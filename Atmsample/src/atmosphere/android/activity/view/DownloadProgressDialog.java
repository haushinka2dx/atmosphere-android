package atmosphere.android.activity.view;

import interprism.atmosphere.android.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadProgressDialog extends Dialog {
	public DownloadProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		initDialog();
	}

	public DownloadProgressDialog(Context context, int theme) {
		super(context, theme);
		initDialog();
	}

	public DownloadProgressDialog(Context context) {
		super(context);
		initDialog();

		switchSpinProgressBar();
	}

	protected void initDialog() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.progress_dialog);
	}

	protected ProgressBar getSpinProgressBar() {
		ProgressBar view = (ProgressBar) findViewById(R.id.SpinProgressBar);
		return view;
	}

	protected TextView getMessageTextView() {
		TextView view = (TextView) findViewById(R.id.MessageTextView);
		return view;
	}

	protected TextView getSubMessageTextView() {
		TextView view = (TextView) findViewById(R.id.SubMessageTextView);
		return view;
	}

	protected LinearLayout getProgressContainer() {
		LinearLayout view = (LinearLayout) findViewById(R.id.ProgressContainer);
		return view;
	}

	protected ProgressBar getProgressBar() {
		ProgressBar view = (ProgressBar) findViewById(R.id.ProgressBar);
		return view;
	}

	protected TextView getProgressTextView() {
		TextView view = (TextView) findViewById(R.id.ProgressTextView);
		return view;
	}

	public void switchSpinProgressBar() {
		getSpinProgressBar().setVisibility(View.VISIBLE);
		getProgressContainer().setVisibility(View.GONE);
	}

	public void switchProgressBar() {
		getSpinProgressBar().setVisibility(View.GONE);
		getProgressContainer().setVisibility(View.VISIBLE);
	}

	public void setMessage(String message) {
		getMessageTextView().setText(message);
	}

	public void setSubMessage(String message) {
		getSubMessageTextView().setText(message);
	}

	public void setMax(int max) {
		ProgressBar progress = getProgressBar();
		progress.setMax(max);

		getProgressTextView().setText(getContext().getString(R.string.progress_format, progress.getProgress(), max));
	}

	public void setProgress(int progress) {
		ProgressBar primary = getProgressBar();
		primary.setProgress(progress);

		getProgressTextView().setText(getContext().getString(R.string.progress_format, progress, primary.getMax()));
	}

	public void updateProgress(int max, int progress) {
		ProgressBar primary = getProgressBar();
		primary.setMax(max);
		primary.setProgress(progress);

		getProgressTextView().setText(getContext().getString(R.string.progress_format, progress, max));
	}

}
