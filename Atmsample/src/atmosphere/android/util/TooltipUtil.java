package atmosphere.android.util;

import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class TooltipUtil {

	public static final int ViewTypeDefault = 0;
	public static final int ViewTypeOnView = 1;

	public static Tooltip register(View view, int layoutResId) {
		return register(view, layoutResId, ViewTypeDefault, null, 0);
	}

	public static Tooltip register(View view, int layoutResId, int viewType, int textViewId) {
		return register(view, layoutResId, viewType, view.getContentDescription(), textViewId);
	}

	public static Tooltip register(View view, int layoutResId, int viewType, CharSequence text, int textViewId) {
		Tooltip tooltip = new Tooltip(view.getContext(), layoutResId);
		if (0 < textViewId) {
			TextView textView = (TextView) tooltip.findViewById(textViewId);
			textView.setText(text);
		}
		view.setOnTouchListener(new TooltipOnTouchListener(view, tooltip, viewType));
		return tooltip;
	}

	static class TooltipOnTouchListener implements View.OnTouchListener {
		private GestureDetector gestureDetector;
		private TooltipGestureListener listener;

		public TooltipOnTouchListener(View view, Tooltip tooltip, int viewType) {
			listener = new TooltipGestureListener(view, tooltip, viewType);
			this.gestureDetector = new GestureDetector(view.getContext(), listener);
		}

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			gestureDetector.onTouchEvent(event);
			if (event.getAction() == MotionEvent.ACTION_UP) {
				listener.dismiss();
			}
			return false;
		}
	}

	static class TooltipGestureListener implements OnGestureListener {
		private View view;
		private Tooltip tooltip;
		private int viewType;

		private TooltipGestureListener(View view, Tooltip tooltip, int viewType) {
			this.view = view;
			this.tooltip = tooltip;
			this.viewType = viewType;
		}

		public void show(MotionEvent event) {
			if (viewType == ViewTypeOnView) {
				tooltip.showOnTop(view);
			} else {
				tooltip.show(view, event);
			}
		}

		public void dismiss() {
			tooltip.dismiss();
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			show(e);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

	}

}
