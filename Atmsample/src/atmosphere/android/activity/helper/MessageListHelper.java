package atmosphere.android.activity.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import atmosphere.android.activity.listener.UpDownTouchListener;
import atmosphere.android.activity.view.MessageAdapter;
import atmosphere.android.activity.view.MessagePagerAdapter;
import atmosphere.android.activity.view.fragment.GlobalTimeLineFragment;
import atmosphere.android.activity.view.fragment.TalkTimeLineFragment;
import atmosphere.android.constant.AtmosUrl;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.dto.MessageResult;
import atmosphere.android.dto.SendMessageRequest;
import atmosphere.android.dto.SendMessageResult;
import atmosphere.android.util.internet.GetPath;
import atmosphere.android.util.internet.JsonPath;
import atmosphere.android.util.json.GetTask;
import atmosphere.android.util.json.GetTask.GetResultHandler;
import atmosphere.android.util.json.PostTask;
import atmosphere.android.util.json.PostTask.PostResultHandler;
import atmsample.android.R;

public class MessageListHelper implements AtmosUrl {

	public static void initialize(FragmentActivity activity, ViewPager pager, PagerTabStrip pagerTabStrip) {
		MessagePagerAdapter adapter = new MessagePagerAdapter(activity, pager);

		adapter.addTab(GlobalTimeLineFragment.class, R.string.global_timeline_title);
		adapter.addTab(TalkTimeLineFragment.class, R.string.talk_timeline_title);

		pager.setAdapter(adapter);
	}

	public static View createListView(final Activity activity, final View view, LayoutInflater inflater, final String targetMethod) {
		ListView messageListView = getListView(view);
		final MessageAdapter adapter = new MessageAdapter(activity, new ArrayList<MessageDto>());
		messageListView.addFooterView(getFooter(inflater));
		messageListView.setAdapter(adapter);
		messageListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				ListView list = (ListView) parent;
				if (list.getCount() - 1 != position) {
					final MessageDto item = (MessageDto) list.getItemAtPosition(position);
					getDrawer(activity).openDrawer(GravityCompat.START);
					getSendMessageEditText(activity).setText("@" + item.created_by + " ");
					getSendMessageEditText(activity).setSelection(item.created_by.length() + 2);

					getSubmitButton(activity).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							SendMessageRequest param = new SendMessageRequest();
							param.reply_to = item._id;
							String message = getSendMessageEditText(activity).getText().toString();
							param.message = message;
							if (message != null && message.length() != 0) {
								sendMessage(param, activity, adapter, targetMethod);
							}
						}
					});
				}
				return false;
			}
		});

		messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (adapter.getCount() != position) {

				} else {
					MessageDto lastItem = (MessageDto) adapter.getItem(adapter.getCount() - 1);
					Map<String, List<String>> params = new HashMap<String, List<String>>();

					List<String> countList = new ArrayList<String>();
					countList.add("10");
					params.put("count", countList);

					List<String> timeList = new ArrayList<String>();
					timeList.add(lastItem.created_at);
					params.put("past_than", timeList);
					pastTask(activity, adapter, targetMethod, params);
				}
			}
		});

		Map<String, List<String>> params = new HashMap<String, List<String>>();

		List<String> countList = new ArrayList<String>();
		countList.add("10");
		params.put("count", countList);

		List<String> timeList = new ArrayList<String>();
		timeList.add(String.valueOf(new Date().getTime()));
		params.put("_", timeList);

		pastTask(activity, adapter, targetMethod, params, false);

		getLeftProgressBar(activity).setRotation(180);
		messageListView.setOnTouchListener(new UpDownTouchListener(new UpDownTouchListener.OnDownListener() {
			@Override
			public void execute() {
				getBaseProgressBar(activity).setVisibility(View.VISIBLE);
				getBaseProgressBar(activity).setIndeterminate(true);
				getFlickProgressLayout(activity).setVisibility(View.INVISIBLE);
				futureTask(activity, adapter, targetMethod);
			}
		}, new UpDownTouchListener.OnProgressListener() {
			@Override
			public void controlProgressBar(int limit, int difference) {
				getBaseProgressBar(activity).setVisibility(View.INVISIBLE);
				getFlickProgressLayout(activity).setVisibility(View.VISIBLE);
				getLeftProgressBar(activity).setMax(limit);
				getRightProgressBar(activity).setMax(limit);

				getLeftProgressBar(activity).setProgress(difference);
				getRightProgressBar(activity).setProgress(difference);
			}

			@Override
			public void compleated() {
				getBaseProgressBar(activity).setVisibility(View.VISIBLE);
				getFlickProgressLayout(activity).setVisibility(View.INVISIBLE);
				getLeftProgressBar(activity).setProgress(0);
				getRightProgressBar(activity).setProgress(0);
			}

			@Override
			public void notCompleated() {
				getBaseProgressBar(activity).setVisibility(View.INVISIBLE);
				getFlickProgressLayout(activity).setVisibility(View.VISIBLE);
				getLeftProgressBar(activity).setProgress(0);
				getRightProgressBar(activity).setProgress(0);
			}
		}));

		return view;
	}

	private static void pastTask(final Activity activity, final MessageAdapter adapter, final String targetMethod, final Map<String, List<String>> params) {
		pastTask(activity, adapter, targetMethod, params, true);
	}

	private static void pastTask(final Activity activity, final MessageAdapter adapter, final String targetMethod, final Map<String, List<String>> params, boolean ignoreDialog) {
		new GetTask<MessageResult>(activity, MessageResult.class, ignoreDialog, new GetResultHandler<MessageResult>() {
			@Override
			public void handleResult(List<MessageResult> results) {
				if (results != null && !results.isEmpty()) {
					adapter.addItems(results.get(0).results);
					adapter.notifyDataSetChanged();
				}
			}
		}, new GetTask.LoginResultHandler() {
			@Override
			public void handleResult() {
				pastTask(activity, adapter, targetMethod, params);
			}
		}).execute(GetPath.paramOf(BASE_URL + targetMethod, params));
	}

	private static void futureTask(final Activity activity, final MessageAdapter adapter, final String targetMethod) {
		if (0 < adapter.getCount()) {
			MessageDto firstItem = (MessageDto) adapter.getItem(0);
			Map<String, List<String>> params = new HashMap<String, List<String>>();

			List<String> countList = new ArrayList<String>();
			countList.add("-1");
			params.put("count", countList);

			List<String> timeList = new ArrayList<String>();
			timeList.add(firstItem.created_at);
			params.put("future_than", timeList);

			new GetTask<MessageResult>(activity, MessageResult.class, true, new GetResultHandler<MessageResult>() {
				@Override
				public void handleResult(List<MessageResult> results) {
					if (results != null && !results.isEmpty()) {
						adapter.addBeforeItems(results.get(0).results);
						adapter.notifyDataSetChanged();
						getBaseProgressBar(activity).setIndeterminate(false);
					}
				}
			}, new GetTask.LoginResultHandler() {
				@Override
				public void handleResult() {
					futureTask(activity, adapter, targetMethod);
				}
			}).execute(GetPath.paramOf(BASE_URL + targetMethod, params));
		}
	}

	private static void sendMessage(final SendMessageRequest param, final Activity activity, final MessageAdapter adapter, final String targetMethod) {
		new PostTask<SendMessageResult>(activity, SendMessageResult.class, "Sending", new PostResultHandler<SendMessageResult>() {
			@Override
			public void handleResult(List<SendMessageResult> results) {
				if (results != null && !results.isEmpty() && results.get(0).status.equals("ok")) {
					getSendMessageEditText(activity).setText("");
					getDrawer(activity).closeDrawers();
					futureTask(activity, adapter, targetMethod);
				}
			}
		}, new PostTask.LoginResultHandler() {
			@Override
			public void handleResult() {
				sendMessage(param, activity, adapter, targetMethod);
			}
		}).execute(JsonPath.paramOf(BASE_URL + SEND_MESSAGE_METHOD, param));
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

}
