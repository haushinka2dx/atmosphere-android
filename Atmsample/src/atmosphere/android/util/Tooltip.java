package atmosphere.android.util;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import atmsample.android.R;

public class Tooltip {

	final static int MarginX = 0;
	final static int MarginY = -80;

	private Context context;
	private PopupWindow popupWindow;
	private View container;

	public Tooltip(Context context, View view) {
		this(context);
		setContentView(view);
	}

	public Tooltip(Context context, int layoutResId) {
		this(context);

		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(layoutResId, null);
		setContentView(view);
	}

	public Tooltip(Context context) {
		this.context = context;

		popupWindow = new PopupWindow(context);

		popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					popupWindow.dismiss();

					return true;
				}

				return false;
			}
		});
	}

	public void setContentView(View view) {
		container = view;
		popupWindow.setContentView(container);
	}

	public View findViewById(int resId) {
		return container.findViewById(resId);
	}

	public void show(View view, MotionEvent event) {
		if (isShowing()) {
			return;
		}
		container.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int width = container.getMeasuredWidth();
		int height = container.getMeasuredHeight();

		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Point size = new Point();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		manager.getDefaultDisplay().getSize(size);
		manager.getDefaultDisplay().getMetrics(displayMetrics);
		int displayWidth = size.x;
		int displayHeight = size.y;

		int posX = (int) (event.getRawX() - (width / 2) + MarginX);
		int posY = (int) (event.getRawY() - height + MarginY);

		if (displayWidth < posX + width) {
			posX = displayWidth - width;
		}
		posX = posX < 0 ? 0 : posX;

		if (displayHeight < posY + height) {
			posY = displayHeight - height;
		}
		int statusBarHeight = (int) (25 * displayMetrics.density);
		posY = posY < statusBarHeight ? statusBarHeight : posY;

		popupWindow.setAnimationStyle(R.style.TooltipAnimation);
		popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, posX, posY);
	}

	public void showOnTop(View view) {
		if (isShowing()) {
			return;
		}
		int[] location = new int[2];
		view.getLocationOnScreen(location);

		popupWindow.setWidth(view.getWidth());
		popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1]);
	}

	public void showTop(View view) {
		showTop(view, null);
	}

	public void showTop(View view, Integer height) {
		if (isShowing()) {
			return;
		}
		int[] location = new int[2];
		view.getLocationOnScreen(location);

		if (height == null) {
			container.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			popupWindow.setWidth(container.getMeasuredWidth());
			height = container.getMeasuredHeight();
		}
		popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] - height);
	}

	public void showBottom(View view) {
		if (isShowing()) {
			return;
		}
		int[] location = new int[2];
		view.getLocationOnScreen(location);

		container.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		popupWindow.setWidth(container.getMeasuredWidth());
		popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] + view.getHeight());
	}

	public void showAsDropDown(View view) {
		if (isShowing()) {
			return;
		}
		popupWindow.showAsDropDown(view);
	}

	public boolean isShowing() {
		return popupWindow.isShowing();
	}

	public void dismiss() {
		popupWindow.dismiss();
	}

	public void setOnDismissListener(OnDismissListener onDismissListener) {
		popupWindow.setOnDismissListener(onDismissListener);
	}
}
