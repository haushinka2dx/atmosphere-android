package atmosphere.android.activity.helper;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
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
import android.widget.ImageButton;
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
import atmosphere.android.constant.AtmosAction;
import atmosphere.android.constant.AtmosUrl;
import atmosphere.android.dto.DestroyRequest;
import atmosphere.android.dto.FutureThanRequest;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.dto.MessageResult;
import atmosphere.android.dto.PastThanRequest;
import atmosphere.android.dto.ResponseRequest;
import atmosphere.android.dto.ResponseResult;
import atmosphere.android.dto.ResponsesDto;
import atmosphere.android.dto.SendMessageRequest;
import atmosphere.android.dto.SendMessageResult;
import atmosphere.android.manager.AtmosPreferenceManager;
import atmosphere.android.util.Tooltip;
import atmosphere.android.util.internet.JsonPath;
import atmosphere.android.util.json.AtmosTask;
import atmosphere.android.util.json.AtmosTask.LoginResultHandler;
import atmosphere.android.util.json.AtmosTask.RequestMethod;
import atmosphere.android.util.json.AtmosTask.ResultHandler;
import atmsample.android.R;

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

					String userId = AtmosPreferenceManager.getUserId(activity);

					final Tooltip tooltip;
					if (item.created_by.equals(userId)) {
						View tooltipView = LayoutInflater.from(activity).inflate(R.layout.reply_to_mine, null);
						tooltip = new Tooltip(activity, tooltipView);

						ImageButton deleteButton = (ImageButton) tooltipView.findViewById(R.id.delete_image_button);
						deleteButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								DestroyRequest param = new DestroyRequest();
								param._id = item._id;
								new AtmosTask.Builder<ResponseResult>(activity, ResponseResult.class, RequestMethod.POST).resultHandler(new ResultHandler<ResponseResult>() {
									@Override
									public void handleResult(List<ResponseResult> results) {
										if (results != null && !results.isEmpty() && results.get(0).status.equals("ok")) {
											adapter.removeItem(position);
											adapter.notifyDataSetChanged();
										}
									}
								}).build().execute(JsonPath.paramOf(BASE_URL + SEND_DESTORY_METHOD, param));
								tooltip.dismiss();
							}
						});

						ImageButton replayButton = (ImageButton) tooltipView.findViewById(R.id.reply_to_mine_image_button);
						replayButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
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
								tooltip.dismiss();
							}
						});

					} else {
						View tooltipView = LayoutInflater.from(activity).inflate(R.layout.reply_to_others, null);
						tooltip = new Tooltip(activity, tooltipView);

						ResponsesDto resDto = item.responses;
						TextView funTextView = (TextView) tooltipView.findViewById(R.id.fun_text_view);
						funTextView.setText(String.valueOf(resDto.fun.size()));

						ImageButton funButton = (ImageButton) tooltipView.findViewById(R.id.fun_image_button);
						if (resDto.fun.contains(userId)) {
							funButton.setEnabled(false);
						} else {
							funButton.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									sendResponse(activity, item, AtmosAction.FUN, adapter);
									tooltip.dismiss();
								}
							});
						}

						TextView goodTextView = (TextView) tooltipView.findViewById(R.id.good_text_view);
						goodTextView.setText(String.valueOf(resDto.good.size()));

						ImageButton goodButton = (ImageButton) tooltipView.findViewById(R.id.good_image_button);
						if (resDto.good.contains(userId)) {
							goodButton.setEnabled(false);
						} else {
							goodButton.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									sendResponse(activity, item, AtmosAction.GOOD, adapter);
									tooltip.dismiss();
								}
							});
						}

						TextView memoTextView = (TextView) tooltipView.findViewById(R.id.memo_text_view);
						memoTextView.setText(String.valueOf(resDto.memo.size()));

						ImageButton memoButton = (ImageButton) tooltipView.findViewById(R.id.memo_image_button);
						if (resDto.memo.contains(userId)) {
							memoButton.setEnabled(false);
						} else {
							memoButton.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									sendResponse(activity, item, AtmosAction.MEMO, adapter);
									tooltip.dismiss();
								}
							});
						}

						TextView usefullTextView = (TextView) tooltipView.findViewById(R.id.usefull_text_view);
						usefullTextView.setText(String.valueOf(resDto.usefull.size()));

						ImageButton usefullButton = (ImageButton) tooltipView.findViewById(R.id.usefull_image_button);
						if (resDto.usefull.contains(userId)) {
							usefullButton.setEnabled(false);
						} else {
							usefullButton.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									sendResponse(activity, item, AtmosAction.USE_FULL, adapter);
									tooltip.dismiss();
								}
							});
						}

						ImageButton replayButton = (ImageButton) tooltipView.findViewById(R.id.reply_to_other_image_button);
						replayButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
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
								tooltip.dismiss();
							}
						});
					}
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
					MessageDto targetItem = (MessageDto) adapter.getItem(position);
					list.add(targetItem);
					detailListView.setAdapter(new DetailMessageAdapter(activity, list));

					ViewPager pager = getViewPager(activity);
					Animation outAnimation = AnimationUtils.loadAnimation(activity, R.anim.slide_out_right);
					pager.startAnimation(outAnimation);
					pager.setVisibility(View.GONE);

					Animation inAnimation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_right);
					detailListView.startAnimation(inAnimation);
					detailListView.setVisibility(View.VISIBLE);

				} else {
					if (0 < adapter.getCount()) {
						MessageDto lastItem = (MessageDto) adapter.getItem(adapter.getCount() - 1);
						PastThanRequest params = new PastThanRequest();
						params.count = 10;
						params.past_than = lastItem.created_at;

						footerProgressBar.setVisibility(View.VISIBLE);
						footerTextView.setText(R.string.connecting);
						adapter.notifyDataSetChanged();

						pastTask(activity, adapter, targetMethod, params, footerProgressBar, footerTextView);
					}
				}
			}
		});

		PastThanRequest params = new PastThanRequest();
		params.count = 10;
		pastTask(activity, adapter, targetMethod, params, false, null, null);

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
				futureTask(activity, adapter, targetMethod);
			}
		}, handler));

		return view;
	}

	private static void pastTask(final Activity activity, final MessageAdapter adapter, final String targetMethod, final PastThanRequest params, ProgressBar footerProgressBar, TextView footerTextView) {
		pastTask(activity, adapter, targetMethod, params, true, footerProgressBar, footerTextView);
	}

	private static void pastTask(final Activity activity, final MessageAdapter adapter, final String targetMethod, final PastThanRequest params, boolean ignoreDialog,
			final ProgressBar footerProgressBar, final TextView footerTextView) {
		new AtmosTask.Builder<MessageResult>(activity, MessageResult.class, RequestMethod.GET).resultHandler(new ResultHandler<MessageResult>() {
			@Override
			public void handleResult(List<MessageResult> results) {
				if (results != null && !results.isEmpty()) {
					adapter.addItems(results.get(0).results);
				}

				if (footerProgressBar != null) {
					footerProgressBar.setVisibility(View.INVISIBLE);
				}
				if (footerTextView != null) {
					footerTextView.setText(R.string.more_load);
				}
				adapter.notifyDataSetChanged();
			}
		}).loginHandler(new LoginResultHandler() {
			@Override
			public void handleResult() {
				pastTask(activity, adapter, targetMethod, params, footerProgressBar, footerTextView);
			}
		}).build().ignoreDialog(ignoreDialog).execute(JsonPath.paramOf(BASE_URL + targetMethod, params));
	}

	private static void futureTask(final Activity activity, final MessageAdapter adapter, final String targetMethod) {
		if (0 < adapter.getCount()) {
			MessageDto firstItem = (MessageDto) adapter.getItem(0);

			FutureThanRequest params = new FutureThanRequest();
			params.count = -1;
			params.future_than = firstItem.created_at;

			new AtmosTask.Builder<MessageResult>(activity, MessageResult.class, RequestMethod.GET).resultHandler(new ResultHandler<MessageResult>() {
				@Override
				public void handleResult(List<MessageResult> results) {
					if (results != null && !results.isEmpty()) {
						adapter.addBeforeItems(results.get(0).results);
						adapter.notifyDataSetChanged();
						getBaseProgressBar(activity).setIndeterminate(false);
					}
				}
			}).loginHandler(new LoginResultHandler() {
				@Override
				public void handleResult() {
					futureTask(activity, adapter, targetMethod);
				}
			}).build().ignoreDialog(true).execute(JsonPath.paramOf(BASE_URL + targetMethod, params));
		}
	}

	private static void sendMessage(final SendMessageRequest param, final Activity activity, final MessageAdapter adapter, final String targetMethod) {
		new AtmosTask.Builder<SendMessageResult>(activity, SendMessageResult.class, RequestMethod.POST).progressMessage("Sending").resultHandler(new ResultHandler<SendMessageResult>() {
			@Override
			public void handleResult(List<SendMessageResult> results) {
				if (results != null && !results.isEmpty() && results.get(0).status.equals("ok")) {
					getSendMessageEditText(activity).setText("");
					getDrawer(activity).closeDrawers();
					futureTask(activity, adapter, targetMethod);
				}
			}
		}).loginHandler(new LoginResultHandler() {
			@Override
			public void handleResult() {
				sendMessage(param, activity, adapter, targetMethod);
			}
		}).build().execute(JsonPath.paramOf(BASE_URL + SEND_MESSAGE_METHOD, param));
	}

	private static void sendResponse(final Activity activity, final MessageDto item, final AtmosAction action, final MessageAdapter adapter) {
		ResponseRequest response = new ResponseRequest();
		response.target_id = item._id;
		response.action = action.getValue();
		new AtmosTask.Builder<ResponseResult>(activity, ResponseResult.class, RequestMethod.POST).resultHandler(new ResultHandler<ResponseResult>() {
			@Override
			public void handleResult(List<ResponseResult> results) {
				if (results != null && !results.isEmpty() && results.get(0).status.equals("ok")) {
					String userId = AtmosPreferenceManager.getUserId(activity);
					if (action == AtmosAction.FUN) {
						item.responses.fun.add(userId);
					} else if (action == AtmosAction.GOOD) {
						item.responses.good.add(userId);
					} else if (action == AtmosAction.MEMO) {
						item.responses.memo.add(userId);
					} else if (action == AtmosAction.USE_FULL) {
						item.responses.usefull.add(userId);
					}
					adapter.notifyDataSetChanged();
				}
			}
		}).build().execute(JsonPath.paramOf(BASE_URL + SEND_RESPONSE_METHOD, response));
	}

	// private static void serchMessage(final Activity activity, final String
	// messageId, final DetailMessageAdapter adapter) {
	// SerchRequest param = new SerchRequest();
	// param.message_ids = messageId;
	// new AtmosTask.Builder<MessageResult>(activity, MessageResult.class,
	// RequestMethod.GET).resultHandler(new ResultHandler<MessageResult>() {
	// @Override
	// public void handleResult(List<MessageResult> results) {
	// if (results != null && !results.isEmpty()) {
	// List<MessageDto> result = results.get(0).results;
	// if (result != null && !result.isEmpty() && result.get(0) != null) {
	// adapter.addItem(result.get(0));
	// adapter.notifyDataSetChanged();
	// serchMessage(activity, result.get(0)._id, adapter);
	// } else {
	//
	// }
	// }
	// }
	// }).loginHandler(new LoginResultHandler() {
	// @Override
	// public void handleResult() {
	// serchMessage(activity, messageId, adapter);
	// }
	// }).build().execute(JsonPath.paramOf(BASE_URL + MESSAGE_SEARCH_METHOD,
	// param));
	// }
	//
	// private static void serchReplyMessage(final Activity activity, final
	// String messageId, final DetailMessageAdapter adapter) {
	// SerchRequest param = new SerchRequest();
	// param.message_ids = messageId;
	// new AtmosTask.Builder<MessageResult>(activity, MessageResult.class,
	// RequestMethod.GET).resultHandler(new ResultHandler<MessageResult>() {
	// @Override
	// public void handleResult(List<MessageResult> results) {
	// if (results != null && !results.isEmpty()) {
	// List<MessageDto> result = results.get(0).results;
	// if (result != null && !result.isEmpty() && result.get(0) != null) {
	// adapter.addItem(result.get(0));
	// adapter.notifyDataSetChanged();
	// serchReplyMessage(activity, result.get(0)._id, adapter);
	// } else {
	//
	// }
	// }
	// }
	// }).loginHandler(new LoginResultHandler() {
	// @Override
	// public void handleResult() {
	// serchReplyMessage(activity, messageId, adapter);
	// }
	// }).build().execute(JsonPath.paramOf(BASE_URL + MESSAGE_SEARCH_METHOD,
	// param));
	// }

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

	protected static ListView getDetailListView(Activity activity) {
		return (ListView) activity.findViewById(R.id.detali_message_list);
	}

	protected static ViewPager getViewPager(Activity activity) {
		return (ViewPager) activity.findViewById(R.id.ViewPager);
	}

}
