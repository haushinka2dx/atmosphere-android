package atmosphere.android.activity.helper;

import interprism.atmosphere.android.R;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import atmosphere.android.activity.view.MessagePagerAdapter;
import atmosphere.android.activity.view.fragment.GlobalTimeLineFragment;
import atmosphere.android.activity.view.fragment.TalkTimeLineFragment;
import atmosphere.android.constant.AtmosUrl;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.dto.PastThanRequest;
import atmosphere.android.manager.AtmosPreferenceManager;
import atmosphere.android.util.Tooltip;

public class MessageListHelper implements AtmosUrl {

	public static void initialize(FragmentActivity activity, ViewPager pager, PagerTabStrip pagerTabStrip) {
		MessagePagerAdapter adapter = new MessagePagerAdapter(activity, pager);

		adapter.addTab(GlobalTimeLineFragment.class, R.string.global_timeline_title);
		adapter.addTab(TalkTimeLineFragment.class, R.string.talk_timeline_title);

		pager.setAdapter(adapter);
	}

	public static View createListView(final Activity activity, final View view, final LayoutInflater inflater, final String targetMethod) {
		ListView messageListView = getListView(view);
		final MessageAdapter adapter = new MessageAdapter(activity, new ArrayList<MessageDto>());

		View footer = getFooter(inflater);
		final ProgressBar footerProgressBar = (ProgressBar) footer.findViewById(R.id.ListViewFooterPrograssBar);
		final TextView footerTextView = (TextView) footer.findViewById(R.id.ListViewFooterTextView);

		messageListView.addFooterView(footer);
		messageListView.setAdapter(adapter);
		messageListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				ListView list = (ListView) parent;
				if (list.getCount() - 1 != position) {
					final MessageDto item = (MessageDto) list.getItemAtPosition(position);
					Tooltip tooltip = ResponseTooltipHelper.createResponseTooltip(activity, view, position, adapter, item, targetMethod);
					tooltip.showTop(view);
				}
				return false;
			}
		});

		messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (adapter.getCount() != position) {

					ListView detailListView = getDetailListView(activity);

					List<MessageDto> list = new ArrayList<MessageDto>();
					final MessageDto targetItem = (MessageDto) adapter.getItem(position);
					DetailMessageAdapter detailAdapter = new DetailMessageAdapter(activity, list);
					list.add(targetItem);
					detailListView.setAdapter(detailAdapter);
					LinearLayout detailOverlay = getDetailOverlay(activity);
					detailOverlay.setVisibility(View.VISIBLE);
					MessageHelper.serchMessage(activity, targetItem.reply_to, detailAdapter, targetItem._id);

					ViewPager pager = getViewPager(activity);
					Animation outAnimation = AnimationUtils.loadAnimation(activity, R.anim.slide_out_right);
					pager.startAnimation(outAnimation);
					pager.setVisibility(View.GONE);

					Animation inAnimation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_right);
					detailListView.startAnimation(inAnimation);
					detailListView.setVisibility(View.VISIBLE);

					detailListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
						@Override
						public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
							Tooltip detailTooltip = ResponseTooltipHelper.createResponseTooltip(activity, view, position, adapter, targetItem, targetMethod);
							detailTooltip.showTop(view);
							return false;
						}
					});

				} else {
					if (0 < adapter.getCount()) {
						MessageDto lastItem = (MessageDto) adapter.getItem(adapter.getCount() - 1);
						PastThanRequest params = new PastThanRequest();
						params.count = 10;
						params.past_than = lastItem.created_at;

						footerProgressBar.setVisibility(View.VISIBLE);
						footerTextView.setText(R.string.connecting);
						adapter.notifyDataSetChanged();

						MessageHelper.pastTask(activity, adapter, targetMethod, params, footerProgressBar, footerTextView);
					}
				}
			}
		});

		PastThanRequest params = new PastThanRequest();
		params.count = 10;
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

	protected static DrawerLayout getDrawer(Activity activity) {
		return (DrawerLayout) activity.findViewById(R.id.Drawer);
	}

	protected static EditText getSendMessageEditText(Activity activity) {
		return (EditText) activity.findViewById(R.id.SendMessageEditText);
	}

	protected static Button getSubmitButton(Activity activity) {
		return (Button) activity.findViewById(R.id.SubmitButton);
	}

	protected static ListView getListView(View view) {
		return (ListView) view.findViewById(R.id.message_list);
	}

	protected static LinearLayout getListOverlay(View view) {
		return (LinearLayout) view.findViewById(R.id.message_list_overlay);
	}

	protected static LinearLayout getFlickProgressLayout(Activity activity) {
		return (LinearLayout) activity.findViewById(R.id.flick_progress_layout);
	}

	protected static ProgressBar getBaseProgressBar(Activity activity) {
		return (ProgressBar) activity.findViewById(R.id.base_progressBar);
	}

	protected static ProgressBar getLeftProgressBar(Activity activity) {
		return (ProgressBar) activity.findViewById(R.id.left_progressBar);
	}

	protected static ProgressBar getRightProgressBar(Activity activity) {
		return (ProgressBar) activity.findViewById(R.id.right_progressBar);
	}

	protected static View getFooter(LayoutInflater inflater) {
		return inflater.inflate(R.layout.list_view_footer, null);
	}

	protected static ListView getDetailListView(Activity activity) {
		return (ListView) activity.findViewById(R.id.detail_message_list);
	}

	protected static LinearLayout getDetailOverlay(Activity activity) {
		return (LinearLayout) activity.findViewById(R.id.detail_message_list_overlay);
	}

	protected static ViewPager getViewPager(Activity activity) {
		return (ViewPager) activity.findViewById(R.id.ViewPager);
	}

}
