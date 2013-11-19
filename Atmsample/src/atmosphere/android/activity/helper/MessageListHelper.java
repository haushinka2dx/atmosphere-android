package atmosphere.android.activity.helper;

import interprism.atmosphere.android.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import atmosphere.android.activity.listener.FlickUpDownListener;
import atmosphere.android.activity.listener.handler.OnFlickHandler;
import atmosphere.android.activity.listener.handler.impl.FlickExecuteHandler;
import atmosphere.android.activity.listener.handler.impl.FlickHandler;
import atmosphere.android.activity.listener.handler.impl.FlickHiranoHandler;
import atmosphere.android.activity.view.DetailMessageAdapter;
import atmosphere.android.activity.view.MessageAdapter;
import atmosphere.android.activity.view.MessageBaseAdapter;
import atmosphere.android.constant.AtmosConstant;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.dto.PastThanRequest;
import atmosphere.android.manager.AtmosPreferenceManager;
import atmosphere.android.util.TimeUtil;
import atmosphere.android.util.Tooltip;

abstract class MessageListHelper {

	protected Activity activity;
	protected String targetMethod;

	private View view;
	private LayoutInflater inflater;
	private MessageAdapter adapter;

	protected MessageListHelper(Activity activity, View view, LayoutInflater inflater, String targetMethod, MessageAdapter adapter) {
		this.activity = activity;
		this.view = view;
		this.inflater = inflater;
		this.targetMethod = targetMethod;
		this.adapter = adapter;
	}

	public View createListView() {
		ListView messageListView = getListView(view);

		View footer = getFooter(inflater);
		final ProgressBar footerProgressBar = (ProgressBar) footer.findViewById(R.id.ListViewFooterPrograssBar);
		final TextView footerTextView = (TextView) footer.findViewById(R.id.ListViewFooterTextView);

		footer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (0 < adapter.getCount()) {
					MessageDto lastItem = (MessageDto) adapter.getItem(adapter.getCount() - 1);
					PastThanRequest params = new PastThanRequest();
					params.count = AtmosConstant.NUMBER_OF_MESSAGES;
					params.past_than = lastItem.created_at;

					footerProgressBar.setVisibility(View.VISIBLE);
					footerTextView.setText(R.string.connecting);
					adapter.notifyDataSetChanged();

					MessageHelper.pastTask(activity, adapter, targetMethod, params, footerProgressBar, footerTextView);
				}
			}
		});
		messageListView.addFooterView(footer);

		adapter.setOnItemClickListener(new MessageBaseAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position, long id) {
				ListView detailListView = getDetailListView(activity);

				List<MessageDto> list = new ArrayList<MessageDto>();
				final MessageDto targetItem = (MessageDto) adapter.getItem(position);
				list.add(targetItem);
				final DetailMessageAdapter detailAdapter = createDetailAdapter(list, targetItem._id);
				detailAdapter.setOnItemLongClickListener(new MessageBaseAdapter.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(View view, int position, long id) {
						final MessageDto detailTargetItem = (MessageDto) detailAdapter.getItem(position);
						Tooltip detailTooltip = createTooltip(position, detailAdapter, detailTargetItem);
						detailTooltip.showTop(view);
						return false;
					}
				});
				LinearLayout detailOverlay = getDetailOverlay(activity);
				detailListView.setAdapter(detailAdapter);

				detailOverlay.setVisibility(View.VISIBLE);
				MessageHelper.serchConversationMessage(activity, targetItem.reply_to, detailAdapter, targetItem._id, adapter.getList());

				ViewPager pager = getViewPager(activity);
				Animation outAnimation = AnimationUtils.loadAnimation(activity, R.anim.slide_out_right);
				pager.startAnimation(outAnimation);
				pager.setVisibility(View.GONE);

				Animation inAnimation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_right);
				detailListView.startAnimation(inAnimation);
				detailListView.setVisibility(View.VISIBLE);
			}
		});

		adapter.setOnItemLongClickListener(new MessageBaseAdapter.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(View view, int position, long id) {
				if (adapter.getCount() - 1 != position) {
					final MessageDto item = (MessageDto) adapter.getItem(position);
					Tooltip tooltip = createTooltip(position, adapter, item);
					tooltip.showTop(view);
				}
				return false;
			}
		});

		adapter.setOnItemDoubleClickListener(new MessageBaseAdapter.OnItemDoubleClickListener() {
			@Override
			public boolean onItemDoubleClick(View view, int position, long id) {
				final MessageDto item = (MessageDto) adapter.getItem(position);
				getSecretUserName(activity).setText(item.created_by);
				ImageView avatar = getSecretUserAvatar(activity);
				Map<String, Bitmap> imageCash = adapter.getImageCash();
				if (imageCash.containsKey(item.created_by)) {
					avatar.setImageBitmap(imageCash.get(item.created_by));
				} else {
					AvatarHelper.setAvatar(item.created_by, avatar);
				}
				getSecretMessageTime(activity).setText(TimeUtil.formaFulltDateFromGMT(item.created_at));
				getSecretMessageTimeLine(activity).setText(item.message);

				if (item.responses != null) {
					if (item.responses.fun != null) {
						getSecretFunTextView(activity).setText(String.valueOf(item.responses.fun.size()));
						getSecretFunLayout(activity).setOnLongClickListener(new View.OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								DialogHelper.showStringListDialog(activity, item.responses.fun, "Fun Users");
								return false;
							}
						});
					}
					if (item.responses.good != null) {
						getSecretGoodTextView(activity).setText(String.valueOf(item.responses.good.size()));
						getSecretGoodLayout(activity).setOnLongClickListener(new View.OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								DialogHelper.showStringListDialog(activity, item.responses.good, "Good Users");
								return false;
							}
						});
					}
					if (item.responses.memo != null) {
						getSecretMemoTextView(activity).setText(String.valueOf(item.responses.memo.size()));
						getSecretMemoLayout(activity).setOnLongClickListener(new View.OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								DialogHelper.showStringListDialog(activity, item.responses.memo, "Memo Users");
								return false;
							}
						});
					}
					if (item.responses.usefull != null) {
						getSecretUsefullTextView(activity).setText(String.valueOf(item.responses.usefull.size()));
						getSecretUsefullLayout(activity).setOnLongClickListener(new View.OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								DialogHelper.showStringListDialog(activity, item.responses.usefull, "Usefull Users");
								return false;
							}
						});
					}
				}

				ViewPager pager = getViewPager(activity);
				Animation outAnimation = AnimationUtils.loadAnimation(activity, R.anim.slide_out_right);
				pager.startAnimation(outAnimation);
				pager.setVisibility(View.GONE);

				LinearLayout secretLayout = getSecretOvarlay(activity);
				Animation inAnimation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_right);
				secretLayout.startAnimation(inAnimation);
				secretLayout.setVisibility(View.VISIBLE);

				return false;
			}
		});

		messageListView.setAdapter(adapter);

		PastThanRequest params = new PastThanRequest();
		params.count = AtmosConstant.NUMBER_OF_MESSAGES;
		MessageHelper.pastTask(activity, adapter, targetMethod, params, true, null, null, getListOverlay(view));
		OnFlickHandler handler;
		if (AtmosPreferenceManager.getViewTheme(activity) == 1) {
			handler = new FlickHiranoHandler(getBaseProgressBar(activity), getFlickProgressLayout(activity), messageListView);
		} else {
			handler = new FlickHandler(getBaseProgressBar(activity), getFlickProgressLayout(activity), getLeftProgressBar(activity), getRightProgressBar(activity));
		}

		messageListView.setOnTouchListener(new FlickUpDownListener(new FlickExecuteHandler(getBaseProgressBar(activity), getFlickProgressLayout(activity)) {
			@Override
			public void execute() {
				super.execute();
				MessageHelper.futureTask(activity, adapter, targetMethod);
			}
		}, handler));

		return view;
	}

	protected DetailMessageAdapter createDetailAdapter(List<MessageDto> list, String orgId) {
		return new DetailMessageAdapter(activity, list, orgId);
	}

	protected Tooltip createTooltip(int position, MessageBaseAdapter detailAdapter, MessageDto detailTargetItem) {
		ResponseTooltipHelper helper = new ResponseTooltipHelper();
		return helper.createResponseTooltip(activity, position, detailAdapter, detailTargetItem, targetMethod);
	}

	protected ListView getListView(View view) {
		return (ListView) view.findViewById(R.id.message_list);
	}

	protected LinearLayout getListOverlay(View view) {
		return (LinearLayout) view.findViewById(R.id.message_list_overlay);
	}

	protected LinearLayout getFlickProgressLayout(Activity activity) {
		return (LinearLayout) activity.findViewById(R.id.flick_progress_layout);
	}

	protected ProgressBar getBaseProgressBar(Activity activity) {
		return (ProgressBar) activity.findViewById(R.id.base_progressBar);
	}

	protected ProgressBar getLeftProgressBar(Activity activity) {
		return (ProgressBar) activity.findViewById(R.id.left_progressBar);
	}

	protected ProgressBar getRightProgressBar(Activity activity) {
		return (ProgressBar) activity.findViewById(R.id.right_progressBar);
	}

	protected View getFooter(LayoutInflater inflater) {
		return inflater.inflate(R.layout.list_view_footer, null);
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

	protected LinearLayout getSecretOvarlay(Activity activity) {
		return (LinearLayout) activity.findViewById(R.id.secret_overlay);
	}

	protected TextView getSecretUserName(Activity activity) {
		return (TextView) activity.findViewById(R.id.secret_user_name);
	}

	protected ImageView getSecretUserAvatar(Activity activity) {
		return (ImageView) activity.findViewById(R.id.secret_user_avatar);
	}

	protected TextView getSecretMessageTime(Activity activity) {
		return (TextView) activity.findViewById(R.id.secret_message_time);
	}

	protected TextView getSecretMessageTimeLine(Activity activity) {
		return (TextView) activity.findViewById(R.id.secret_message_timeline);
	}

	protected TextView getSecretFunTextView(Activity activity) {
		return (TextView) activity.findViewById(R.id.secret_fun_text_view);
	}

	protected TextView getSecretGoodTextView(Activity activity) {
		return (TextView) activity.findViewById(R.id.secret_good_text_view);
	}

	protected TextView getSecretMemoTextView(Activity activity) {
		return (TextView) activity.findViewById(R.id.secret_memo_text_view);
	}

	protected TextView getSecretUsefullTextView(Activity activity) {
		return (TextView) activity.findViewById(R.id.secret_usefull_text_view);
	}

	protected FrameLayout getSecretFunLayout(Activity activity) {
		return (FrameLayout) activity.findViewById(R.id.secret_fun);
	}

	protected FrameLayout getSecretGoodLayout(Activity activity) {
		return (FrameLayout) activity.findViewById(R.id.secret_good);
	}

	protected FrameLayout getSecretMemoLayout(Activity activity) {
		return (FrameLayout) activity.findViewById(R.id.secret_memo);
	}

	protected FrameLayout getSecretUsefullLayout(Activity activity) {
		return (FrameLayout) activity.findViewById(R.id.secret_usefull);
	}
}
