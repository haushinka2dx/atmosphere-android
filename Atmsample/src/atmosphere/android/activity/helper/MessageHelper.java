package atmosphere.android.activity.helper;

import interprism.atmosphere.android.R;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import atmosphere.android.activity.view.DetailMessageAdapter;
import atmosphere.android.activity.view.MessageAdapter;
import atmosphere.android.activity.view.MessageBaseAdapter;
import atmosphere.android.constant.AtmosAction;
import atmosphere.android.constant.AtmosConstant;
import atmosphere.android.constant.AtmosUrl;
import atmosphere.android.dto.FutureThanRequest;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.dto.MessageResult;
import atmosphere.android.dto.PastThanRequest;
import atmosphere.android.dto.ResponseRequest;
import atmosphere.android.dto.ResponseResult;
import atmosphere.android.dto.SendMessageRequest;
import atmosphere.android.dto.SendMessageResult;
import atmosphere.android.dto.SendPrivateMessageRequest;
import atmosphere.android.dto.SerchRequest;
import atmosphere.android.manager.AtmosPreferenceManager;
import atmosphere.android.util.internet.JsonPath;
import atmosphere.android.util.json.AtmosTask;
import atmosphere.android.util.json.AtmosTask.LoginResultHandler;
import atmosphere.android.util.json.AtmosTask.RequestMethod;
import atmosphere.android.util.json.AtmosTask.ResultHandler;

public class MessageHelper {

	public static void pastTask(final Activity activity, final MessageAdapter adapter, final String targetMethod, final PastThanRequest params, ProgressBar footerProgressBar, TextView footerTextView) {
		pastTask(activity, adapter, targetMethod, params, true, footerProgressBar, footerTextView, null);
	}

	public static void pastTask(final Activity activity, final MessageAdapter adapter, final String targetMethod, final PastThanRequest params, final boolean ignoreDialog,
			final ProgressBar footerProgressBar, final TextView footerTextView, final LinearLayout overlay) {
		if (ignoreDialog && overlay != null) {
			overlay.setVisibility(View.VISIBLE);
		}
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
				if (ignoreDialog && overlay != null) {
					overlay.setVisibility(View.GONE);
				}
			}
		}).loginHandler(new LoginResultHandler() {
			@Override
			public void handleResult() {
				pastTask(activity, adapter, targetMethod, params, footerProgressBar, footerTextView);
			}
		}).build().ignoreDialog(ignoreDialog).execute(JsonPath.paramOf(AtmosUrl.BASE_URL + targetMethod, params));
	}

	public static void futureTask(final Activity activity, final MessageBaseAdapter adapter, final String targetMethod) {
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
			}).build().ignoreDialog(true).execute(JsonPath.paramOf(AtmosUrl.BASE_URL + targetMethod, params));
		}
	}

	public static void sendMessage(final Activity activity, final SendMessageRequest param) {
		sendMessage(activity, param, null, null);
	}

	public static void sendMessage(final Activity activity, final SendMessageRequest param, final MessageBaseAdapter adapter, final String targetMethod) {
		new AtmosTask.Builder<SendMessageResult>(activity, SendMessageResult.class, RequestMethod.POST).progressMessage("Sending").resultHandler(new ResultHandler<SendMessageResult>() {
			@Override
			public void handleResult(List<SendMessageResult> results) {
				if (results != null && !results.isEmpty() && results.get(0).status.equals("ok")) {
					getSendMessageEditText(activity).setText(AtmosConstant.SEND_MESSAGE_CLEAR_TEXT);
					getDrawer(activity).closeDrawer(GravityCompat.START);
					if (adapter != null && targetMethod != null) {
						futureTask(activity, adapter, targetMethod);
					}
				}
			}
		}).loginHandler(new LoginResultHandler() {
			@Override
			public void handleResult() {
				sendMessage(activity, param, adapter, targetMethod);
			}
		}).build().execute(JsonPath.paramOf(AtmosUrl.BASE_URL + AtmosUrl.SEND_MESSAGE_METHOD, param));
	}

	public static void sendPrivateMessage(final Activity activity, final SendPrivateMessageRequest param) {
		sendPrivateMessage(activity, param, null, null);
	}

	public static void sendPrivateMessage(final Activity activity, final SendPrivateMessageRequest param, final MessageBaseAdapter adapter, final String targetMethod) {
		new AtmosTask.Builder<SendMessageResult>(activity, SendMessageResult.class, RequestMethod.POST).progressMessage("Sending").resultHandler(new ResultHandler<SendMessageResult>() {
			@Override
			public void handleResult(List<SendMessageResult> results) {
				if (results != null && !results.isEmpty() && results.get(0).status.equals("ok")) {
					getSendPrivateMessageEditText(activity).setText(AtmosConstant.SEND_MESSAGE_CLEAR_TEXT);
					getSendPrivateToUserEditText(activity).setText(AtmosConstant.SEND_MESSAGE_CLEAR_TEXT);
					getDrawer(activity).closeDrawer(GravityCompat.END);
					if (adapter != null && targetMethod != null) {
						futureTask(activity, adapter, targetMethod);
					}
				}
			}
		}).loginHandler(new LoginResultHandler() {
			@Override
			public void handleResult() {
				sendPrivateMessage(activity, param, adapter, targetMethod);
			}
		}).build().execute(JsonPath.paramOf(AtmosUrl.BASE_URL + AtmosUrl.SEND_PRIVATE_MESSAGE_METHOD, param));
	}

	public static void sendResponse(final Activity activity, final MessageDto item, final AtmosAction action, final BaseAdapter adapter) {
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
		}).build().execute(JsonPath.paramOf(AtmosUrl.BASE_URL + AtmosUrl.SEND_RESPONSE_METHOD, response));
	}

	public static void serchMessage(final Activity activity, final String replyId, final DetailMessageAdapter adapter, final String messageId, final List<MessageDto> orgList) {
		final List<MessageDto> addBeforeList = new ArrayList<MessageDto>();
		serchMessage(activity, replyId, adapter, messageId, orgList, addBeforeList);
	}

	private static void serchMessage(final Activity activity, final String replyId, final DetailMessageAdapter adapter, final String messageId, final List<MessageDto> orgList,
			final List<MessageDto> addBeforeList) {

		if (replyId != null && replyId.length() != 0) {
			boolean skipConnect = false;
			String targetReplyId = replyId;

			if (orgList != null && !orgList.isEmpty()) {
				for (MessageDto messageDto : orgList) {
					if (messageDto._id.equals(targetReplyId)) {
						addBeforeList.add(0, messageDto);
						targetReplyId = messageDto.reply_to;
					}
				}

				if (targetReplyId == null || targetReplyId.length() == 0) {
					skipConnect = true;
				}
			}

			if (skipConnect) {
				serchReplyMessage(activity, messageId, adapter, orgList, addBeforeList);
			} else {
				SerchRequest param = new SerchRequest();
				param.message_ids = targetReplyId;
				new AtmosTask.Builder<MessageResult>(activity, MessageResult.class, RequestMethod.GET).resultHandler(new ResultHandler<MessageResult>() {
					@Override
					public void handleResult(List<MessageResult> results) {
						if (results != null && !results.isEmpty()) {
							List<MessageDto> result = results.get(0).results;
							if (result != null && !result.isEmpty() && result.get(0) != null) {
								addBeforeList.add(0, result.get(0));
								serchMessage(activity, result.get(0).reply_to, adapter, messageId, null, addBeforeList);
							} else {
								serchReplyMessage(activity, messageId, adapter, orgList, addBeforeList);
							}

						} else {
							serchReplyMessage(activity, messageId, adapter, orgList, addBeforeList);
						}
					}
				}).loginHandler(new LoginResultHandler() {
					@Override
					public void handleResult() {
						serchMessage(activity, replyId, adapter, messageId, orgList, addBeforeList);
					}
				}).build().ignoreDialog(true).execute(JsonPath.paramOf(AtmosUrl.BASE_URL + AtmosUrl.MESSAGE_SEARCH_METHOD, param));
			}
		} else {
			serchReplyMessage(activity, messageId, adapter, orgList, addBeforeList);
		}
	}

	private static void serchReplyMessage(final Activity activity, final String messageId, final DetailMessageAdapter adapter, final List<MessageDto> orgList, final List<MessageDto> addBeforeList) {
		final List<MessageDto> addList = new ArrayList<MessageDto>();
		serchReplyMessage(activity, messageId, adapter, orgList, addBeforeList, addList);
	}

	private static void serchReplyMessage(final Activity activity, final String messageId, final DetailMessageAdapter adapter, final List<MessageDto> orgList, final List<MessageDto> addBeforeList,
			final List<MessageDto> addList) {
		if (messageId != null && messageId.length() != 0) {
			boolean skipConnect = false;
			String targetMessageId = messageId;

			if (orgList != null && !orgList.isEmpty()) {
				for (int i = orgList.size() - 1; 0 <= i; i--) {
					MessageDto messageDto = orgList.get(i);
					if (messageDto.reply_to != null && messageDto.reply_to.equals(targetMessageId)) {
						addList.add(messageDto);
						targetMessageId = messageDto._id;
					}
				}

				if (targetMessageId == null || targetMessageId.length() == 0) {
					skipConnect = true;
				}
			}

			if (skipConnect) {
				finishDetail(activity, adapter, addBeforeList, addList);
			} else {
				SerchRequest param = new SerchRequest();
				param.reply_to_message_id = targetMessageId;
				new AtmosTask.Builder<MessageResult>(activity, MessageResult.class, RequestMethod.GET).resultHandler(new ResultHandler<MessageResult>() {
					@Override
					public void handleResult(List<MessageResult> results) {
						if (results != null && !results.isEmpty()) {
							List<MessageDto> result = results.get(0).results;
							if (result != null && !result.isEmpty() && result.get(0) != null) {
								addList.add(result.get(0));
								serchReplyMessage(activity, result.get(0)._id, adapter, null, addBeforeList, addList);
							} else {
								finishDetail(activity, adapter, addBeforeList, addList);
							}
						} else {
							finishDetail(activity, adapter, addBeforeList, addList);
						}
					}
				}).loginHandler(new LoginResultHandler() {
					@Override
					public void handleResult() {
						serchReplyMessage(activity, messageId, adapter, orgList, addBeforeList, addList);
					}
				}).build().ignoreDialog(true).execute(JsonPath.paramOf(AtmosUrl.BASE_URL + AtmosUrl.MESSAGE_SEARCH_METHOD, param));
			}
		} else {
			finishDetail(activity, adapter, addBeforeList, addList);
		}
	}

	private static void finishDetail(Activity activity, DetailMessageAdapter adapter, List<MessageDto> addBeforeList, List<MessageDto> addList) {
		LinearLayout overlay = getDetailOverlay(activity);
		if (overlay.getVisibility() == View.VISIBLE) {
			overlay.setVisibility(View.GONE);
			adapter.addBeforeItems(addBeforeList);
			adapter.addItems(addList);
			adapter.notifyDataSetChanged();
		}
	}

	protected static DrawerLayout getDrawer(Activity activity) {
		return (DrawerLayout) activity.findViewById(R.id.Drawer);
	}

	protected static EditText getSendMessageEditText(Activity activity) {
		return (EditText) activity.findViewById(R.id.SendMessageEditText);
	}

	protected static EditText getSendPrivateMessageEditText(Activity activity) {
		return (EditText) activity.findViewById(R.id.SendPrivateMessageEditText);
	}

	protected static EditText getSendPrivateToUserEditText(Activity activity) {
		return (EditText) activity.findViewById(R.id.SendPrivateToUserEditText);
	}

	protected static ProgressBar getBaseProgressBar(Activity activity) {
		return (ProgressBar) activity.findViewById(R.id.base_progressBar);
	}

	protected static LinearLayout getDetailOverlay(Activity activity) {
		return (LinearLayout) activity.findViewById(R.id.detail_message_list_overlay);
	}

}
