package atmosphere.android.activity.view;

import interprism.atmosphere.android.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewAnimator;
import atmosphere.android.activity.helper.AvatarHelper;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.util.TimeUtil;

public class MessageAdapter extends MessageBaseAdapter {

	protected Activity activity;
	private Map<String, Bitmap> imageCash;
	private OnItemClickListener mItemClickListener;
	private OnItemLongClickListener mItemLongClickListener;
	private OnItemDoubleClickListener mItemDoubleClickListener;
	private boolean onDoubleTaped;
	private boolean onShowPressed;

	public MessageAdapter(Activity activity, List<MessageDto> list) {
		super(list);
		this.activity = activity;
		this.imageCash = new HashMap<String, Bitmap>();
	}

	public void setOnItemClickListener(OnItemClickListener itemClickListener) {
		mItemClickListener = itemClickListener;
	}

	public void setOnItemLongClickListener(OnItemLongClickListener itemLongClickListener) {
		mItemLongClickListener = itemLongClickListener;
	}

	public void setOnItemDoubleClickListener(OnItemDoubleClickListener itemDoubleClickListener) {
		mItemDoubleClickListener = itemDoubleClickListener;
	}

	public List<MessageDto> getList() {
		// 本当はこれはやりたくない
		return list;
	}

	public Map<String, Bitmap> getImageCash() {
		return this.imageCash;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final View view;
		if (convertView != null) {
			view = convertView;
		} else {
			LayoutInflater inflater = LayoutInflater.from(activity);
			view = inflater.inflate(R.layout.messeges, parent, false);
		}

		final MessageDto data = list.get(position);

		TextView userName = (TextView) view.findViewById(R.id.user_name);
		userName.setText(data.created_by);

		TextView message = (TextView) view.findViewById(R.id.message_timeline);
		message.setText(data.message);

		TextView messageTime = (TextView) view.findViewById(R.id.message_time);
		messageTime.setText(TimeUtil.formatDateFromGMT(data.created_at));

		ImageView avatar = (ImageView) view.findViewById(R.id.user_avatar);
		AvatarHelper.setAndCachAvatar(data, imageCash, avatar);

		viewControl(view, data);

		final ViewAnimator viewAnimator = (ViewAnimator) view.findViewById(R.id.clicked_color_view);
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

	protected void viewControl(View view, MessageDto data) {
		LinearLayout privateLayout = (LinearLayout) view.findViewById(R.id.private_to_user_layout);
		privateLayout.setVisibility(View.GONE);
	}

	protected ListView getDetailListView(Activity activity) {
		return (ListView) activity.findViewById(R.id.detail_message_list);
	}

	protected LinearLayout getDetailOverlay(Activity activity) {
		return (LinearLayout) activity.findViewById(R.id.detail_message_list_overlay);
	}

	protected ViewPager getViewPager(Activity activity) {
		return (ViewPager) activity.findViewById(R.id.ViewPager);
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
