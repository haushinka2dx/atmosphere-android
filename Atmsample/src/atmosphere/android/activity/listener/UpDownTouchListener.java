package atmosphere.android.activity.listener;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class UpDownTouchListener implements View.OnTouchListener {
	private float lastTouchY;
	private float cntY;
	private OnDownListener listener;
	private OnProgressListener progressListener;
	private int limit;

	public UpDownTouchListener(int limit, OnDownListener listener) {
		this.limit = limit;
		this.listener = listener;
	}

	public UpDownTouchListener(OnDownListener listener) {
		this.limit = 500;
		this.listener = listener;
	}

	public UpDownTouchListener(int limit, OnDownListener listener, OnProgressListener progressListener) {
		this.limit = limit;
		this.listener = listener;
		this.progressListener = progressListener;
	}

	public UpDownTouchListener(OnDownListener listener, OnProgressListener progressListener) {
		this.limit = 500;
		this.listener = listener;
		this.progressListener = progressListener;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		ListView listView = (ListView) v;
		if (0 == listView.getChildAt(0).getTop()) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				lastTouchY = event.getY();
				break;

			case MotionEvent.ACTION_UP:
				cntY = event.getY();
				if (cntY > lastTouchY) {
					if (limit < (cntY - lastTouchY)) {
						listener.execute();
						if (progressListener != null) {
							progressListener.compleated();
						}
					} else if (progressListener != null) {
						progressListener.notCompleated();
					}
				} else if (progressListener != null) {
					progressListener.notCompleated();
				}

				break;
			case MotionEvent.ACTION_CANCEL:
				cntY = event.getY();
				if (cntY > lastTouchY) {
					if (limit < (cntY - lastTouchY)) {
						listener.execute();
						if (progressListener != null) {
							progressListener.compleated();
						}
					} else if (progressListener != null) {
						progressListener.notCompleated();
					}
				} else if (progressListener != null) {
					progressListener.notCompleated();
				}

				break;

			case MotionEvent.ACTION_MOVE:
				if (progressListener != null) {
					cntY = event.getY();
					if (cntY > lastTouchY) {
						if ((cntY - lastTouchY) < limit) {
							progressListener.controlProgressBar(limit, (int) (cntY - lastTouchY));
						}
					}
				}
				break;
			}
		} else if (progressListener != null) {
			progressListener.notCompleated();
		}

		return false;
	}

	public interface OnDownListener {
		public void execute();

	}

	public interface OnProgressListener {
		public void controlProgressBar(int limit, int difference);

		public void compleated();

		public void notCompleated();
	}

}
