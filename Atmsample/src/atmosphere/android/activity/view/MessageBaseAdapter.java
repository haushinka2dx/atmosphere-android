package atmosphere.android.activity.view;

import interprism.atmosphere.android.R;

import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ViewAnimator;
import atmosphere.android.dto.MessageDto;

public abstract class MessageBaseAdapter extends BaseAdapter {
	protected List<MessageDto> list;
	private int layoutId;
	private int clickedColorViewId;
	protected Activity activity;
	private OnItemClickListener mItemClickListener;
	private OnItemLongClickListener mItemLongClickListener;
	private OnItemDoubleClickListener mItemDoubleClickListener;
	private boolean onDoubleTaped;
	private boolean onShowPressed;

	public MessageBaseAdapter(Activity activity, List<MessageDto> list, int layoutId, int clickedColorViewId) {
		this.activity = activity;
		this.list = list;
		this.layoutId = layoutId;
		this.clickedColorViewId = clickedColorViewId;
	}

	public void setOnItemClickListener(OnItemClickListener itemClickListener) {
		this.mItemClickListener = itemClickListener;
	}

	public void setOnItemLongClickListener(OnItemLongClickListener itemLongClickListener) {
		this.mItemLongClickListener = itemLongClickListener;
	}

	public void setOnItemDoubleClickListener(OnItemDoubleClickListener itemDoubleClickListener) {
		this.mItemDoubleClickListener = itemDoubleClickListener;
	}

	public void setItems(List<MessageDto> list) {
		this.list = list;
	}

	public void addItem(MessageDto item) {
		if (item != null) {
			this.list.add(item);
		}
	}

	public void addItems(List<MessageDto> list) {
		if (list != null & !list.isEmpty()) {
			this.list.addAll(list);
		}
	}

	public void addBeforeItem(MessageDto item) {
		if (item != null) {
			this.list.add(0, item);
		}
	}

	public void addBeforeItems(List<MessageDto> list) {
		if (list != null && !list.isEmpty()) {
			list.addAll(this.list);
			this.list = list;
		}
	}

	public void removeItem(int position) {
		list.remove(position);
	}

	@Override
	public int getCount() {
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		return this.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final View view;
		if (convertView != null) {
			view = convertView;
		} else {
			LayoutInflater inflater = LayoutInflater.from(activity);
			view = inflater.inflate(layoutId, parent, false);
		}
		final ViewAnimator viewAnimator = (ViewAnimator) view.findViewById(clickedColorViewId);
		final GestureDetector gd = new GestureDetector(activity, new GestureDetector.SimpleOnGestureListener() {

			@Override
			public boolean onDown(MotionEvent e) {
				Log.v("Atmos", "---------------------onDown-------------------");
				onDoubleTaped = false;
				onShowPressed = false;
				return true;
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				Log.v("Atmos", "---------------------onSingleTapUp-------------------");
				if (onShowPressed) {
					goneClickView(viewAnimator);
				} else {
					visibleClickView(viewAnimator);
				}
				return true;
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				Log.v("Atmos", "---------------------onSingleTapConfirmed-------------------");
				goneClickView(viewAnimator);
				if (mItemClickListener != null) {
					mItemClickListener.onItemClick(view, position, getItemId(position));
				}
				return true;
			}

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				Log.v("Atmos", "---------------------onDoubleTap-------------------");
				onDoubleTaped = true;
				visibleClickView(viewAnimator);
				return true;
			}

			@Override
			public boolean onDoubleTapEvent(MotionEvent e) {
				Log.v("Atmos", "---------------------onDoubleTapEvent-------------------");
				goneClickView(viewAnimator);
				if (mItemDoubleClickListener != null && onDoubleTaped) {
					mItemDoubleClickListener.onItemDoubleClick(view, position, getItemId(position));
				}
				return true;
			}

			@Override
			public void onShowPress(MotionEvent e) {
				Log.v("Atmos", "---------------------onShowPress-------------------");
				onShowPressed = true;
				visibleClickView(viewAnimator);
			}

			@Override
			public void onLongPress(MotionEvent e) {
				Log.v("Atmos", "---------------------onLongPress-------------------");
				if (mItemLongClickListener != null) {
					mItemLongClickListener.onItemLongClick(view, position, getItemId(position));
				}
				goneClickView(viewAnimator);
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				Log.v("Atmos", "---------------------onScroll-------------------");
				goneClickView(viewAnimator);
				return true;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				Log.v("Atmos", "---------------------onFling-------------------");
				goneClickView(viewAnimator);
				return true;
			}
		});

		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean t = gd.onTouchEvent(event);
				if (!t) {
					goneClickView(viewAnimator);
				}
				return t;
			}
		});

		return view;
	}

	private void visibleClickView(ViewAnimator viewAnimator) {
		if (viewAnimator.getVisibility() == View.GONE) {
			Animation outAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
			viewAnimator.startAnimation(outAnimation);
			viewAnimator.setVisibility(View.VISIBLE);
		}
	}

	private void goneClickView(ViewAnimator viewAnimator) {
		if (viewAnimator.getVisibility() == View.VISIBLE) {
			Animation outAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_out);
			viewAnimator.startAnimation(outAnimation);
			viewAnimator.setVisibility(View.GONE);
		}
	}

	public interface OnItemClickListener {
		public void onItemClick(View view, int position, long id);
	}

	public interface OnItemLongClickListener {
		public boolean onItemLongClick(View view, int position, long id);
	}

	public interface OnItemDoubleClickListener {
		public boolean onItemDoubleClick(View view, int position, long id);
	}

}
