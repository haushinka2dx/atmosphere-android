package atmosphere.android.util;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.WindowManager.BadTokenException;
import android.widget.Toast;
import atmosphere.android.activity.view.DownloadProgressDialog;
import atmosphere.android.util.ProgressObserver.ProgressStyle;

public abstract class AbstractProgressTask<Params, Result> extends AsyncTask<Params, AbstractProgressTask.Notification, Result> {
	private DownloadProgressDialog dialog;

	protected final Context context;
	protected final ProgressObserver observer = new ProgressObserverStub();

	private boolean ignoreDialog;
	protected int cancelCode;

	public AbstractProgressTask(Context context) {
		this.context = context;

		createDialog();
	}

	private void createDialog() {
		dialog = new DownloadProgressDialog(context);
		dialog.setCancelable(true);

		dialog.switchSpinProgressBar();
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if (!isCancelled()) {
					cancel(true);
				}
			}
		});
	}

	public AbstractProgressTask<Params, Result> ignoreDialog(boolean ignore) {
		ignoreDialog = ignore;
		return this;
	}

	public boolean getIgnoreDialog() {
		return ignoreDialog;
	}

	protected void internalPublishProgress(Notification... values) {
		if (!ignoreDialog) {
			this.publishProgress(values);
		}
	}

	@Override
	protected void onPreExecute() {
		// TODO pause -> 復帰時にタスクが復元される仕組みを調べる
		if (!isCancelled() && !ignoreDialog) {
			try {
				dialog.show();
			} catch (BadTokenException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onProgressUpdate(Notification... values) {
		if (values == null) {
			return;
		}

		for (Notification notify : values) {
			notify.process();
		}
	}

	@Override
	protected void onCancelled() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

	@Override
	protected void onPostExecute(Result result) {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

	interface Notification {
		void process();
	}

	class ProgressNotification implements Notification {
		ProgressObserver.ProgressStyle style;
		String message;
		String submessage;
		Integer max;
		Integer progress;

		@Override
		public void process() {
			if (dialog == null) {
				return;
			}

			if (style == ProgressStyle.Progress) {
				dialog.switchProgressBar();
			} else if (style == ProgressStyle.Spin) {
				dialog.switchSpinProgressBar();
			}

			if (message != null) {
				dialog.setMessage(message);
			}
			if (submessage != null) {
				dialog.setSubMessage(submessage);
			}

			if (max != null && progress != null) {
				dialog.updateProgress(max, progress);
			} else if (max != null) {
				dialog.setMax(max);
			} else if (progress != null) {
				dialog.setProgress(progress);
			}
		}
	}

	class ToastNotification implements Notification {
		String message;
		int length;

		@Override
		public void process() {
			Toast.makeText(context, message, length).show();
		}
	}

	class CancelNotification implements Notification {

		@Override
		public void process() {
			cancel(true);
		}
	}

	private class ProgressObserverStub implements ProgressObserver {

		@Override
		public void updateStyle(ProgressStyle style) {
			ProgressNotification notification = new ProgressNotification();
			notification.style = style;

			internalPublishProgress(notification);
		}

		@Override
		public void setMessage(String message) {
			ProgressNotification notification = new ProgressNotification();
			notification.message = message;

			internalPublishProgress(notification);
		}

		@Override
		public void setSubMessage(String message) {
			ProgressNotification notification = new ProgressNotification();
			notification.submessage = message;

			internalPublishProgress(notification);
		}

		@Override
		public void update(ProgressStyle style, int max, int progress, String message, String subMesage) {
			ProgressNotification notification = new ProgressNotification();
			notification.style = style;
			notification.progress = progress;
			notification.max = max;
			notification.message = message;
			notification.submessage = subMesage;

			internalPublishProgress(notification);
		}

		@Override
		public void updateProgress(int progress, int max) {
			ProgressNotification notification = new ProgressNotification();
			notification.progress = progress;
			notification.max = max;

			internalPublishProgress(notification);
		}

		@Override
		public void updateProgress(int progress) {
			ProgressNotification notification = new ProgressNotification();
			notification.progress = progress;

			internalPublishProgress(notification);
		}

		@Override
		public void showToast(String message) {
			ToastNotification notification = new ToastNotification();
			notification.message = message;
			notification.length = Toast.LENGTH_LONG;

			internalPublishProgress(notification);
		}

		@Override
		public boolean isCancelled() {
			return AbstractProgressTask.this.isCancelled();
		}

		@Override
		public void cancel() {
			// asyncスレッド通知
			AbstractProgressTask.this.cancel(false);
			// UIスレッド通知（UIスレッドからasyncスレッドへの割り込み用）
			publishProgress(new CancelNotification());
		}

		@Override
		public void cancel(int cancelCode) {
			AbstractProgressTask.this.cancelCode = cancelCode;
			cancel();
		}
	}
}
