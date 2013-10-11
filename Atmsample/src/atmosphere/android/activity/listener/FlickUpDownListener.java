package atmosphere.android.activity.listener;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import atmosphere.android.activity.listener.handler.OnExecuteHandler;
import atmosphere.android.activity.listener.handler.OnFlickHandler;

public class FlickUpDownListener implements View.OnTouchListener {
	private float lastTouchY;
	private float cntY;
	private OnExecuteHandler executeHandler;
	private OnFlickHandler flickHandler;
	private int limit;
	// TODO ListView の一番下判定が出来たら、コンストラクタ追加
	private boolean isFlickDown = true;

	public FlickUpDownListener(int limit, OnExecuteHandler executeHandler) {
		this.limit = limit;
		this.executeHandler = executeHandler;
	}

	public FlickUpDownListener(OnExecuteHandler executeHandler) {
		this.limit = 500;
		this.executeHandler = executeHandler;
	}

	public FlickUpDownListener(int limit, OnExecuteHandler executeHandler, OnFlickHandler flickHandler) {
		this.limit = limit;
		this.executeHandler = executeHandler;
		this.flickHandler = flickHandler;
	}

	public FlickUpDownListener(OnExecuteHandler executeHandler, OnFlickHandler flickHandler) {
		this.limit = 500;
		this.executeHandler = executeHandler;
		this.flickHandler = flickHandler;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		ListView listView = (ListView) v;
		if (isStart(listView)) {
			if (lastTouchY == 0) lastTouchY = event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				lastTouchY = event.getY();
				break;

			case MotionEvent.ACTION_UP:
				cntY = event.getY();
				if (isMatchDirection()) {
					if (isOverLimit()) {
						executeHandler.execute();
						if (flickHandler != null) {
							flickHandler.compleated();
						}
					} else if (flickHandler != null) {
						flickHandler.notCompleated();
					}
				} else if (flickHandler != null) {
					flickHandler.notCompleated();
				}

				break;
			case MotionEvent.ACTION_CANCEL:
				cntY = event.getY();
				if (isMatchDirection()) {
					if (isOverLimit()) {
						executeHandler.execute();
						if (flickHandler != null) {
							flickHandler.compleated();
						}
					} else if (flickHandler != null) {
						flickHandler.notCompleated();
					}
				} else if (flickHandler != null) {
					flickHandler.notCompleated();
				}

				break;

			case MotionEvent.ACTION_MOVE:
				if (flickHandler != null) {
					cntY = event.getY();
					if (isMatchDirection()) {
						if ((cntY - lastTouchY) < limit) {
							flickHandler.controlMoving(limit, (cntY - lastTouchY));
						}
					}
				}
				break;
			}
		} else if (flickHandler != null) {
			flickHandler.notCompleated();
		}

		return false;
	}

	private boolean isStart(ListView listView) {
		if (isFlickDown) {
			return (0 == listView.getChildAt(0).getTop());
		} else {
			// TODO ListView の一番したにいるかを実装。まだ調べてない
			return false;
		}
	}

	private boolean isMatchDirection() {
		return (isFlickDown == (cntY > lastTouchY));
	}

	private boolean isOverLimit() {
		if (isFlickDown) {
			return (limit < (cntY - lastTouchY));
		} else {
			return (limit < (lastTouchY - cntY));
		}
	}
}
