package atmosphere.android.activity.listener;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class FlickTouchListener implements View.OnTouchListener {

	private float lastTouchX;
	private float cntX;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastTouchX = event.getX();
			break;

		case MotionEvent.ACTION_UP:
			cntX = event.getX();
			if (lastTouchX < cntX) {
				Log.d("tag", "------------------Proceed------------------");
			} else if (lastTouchX > cntX) {
				Log.d("tag", "------------------Back------------------");
			}

			break;
		case MotionEvent.ACTION_CANCEL:
			cntX = event.getX();
			if (lastTouchX < cntX) {
				Log.d("tag", "------------------Proceed------------------");
			} else if (lastTouchX > cntX) {
				Log.d("tag", "------------------Back------------------");
			}

			break;
		}
		return true;
	}
}
