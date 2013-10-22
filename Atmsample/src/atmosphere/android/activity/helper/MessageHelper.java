package atmosphere.android.activity.helper;

import interprism.atmosphere.android.R;

import java.util.List;

import android.app.Activity;
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
import atmosphere.android.constant.AtmosUrl;
import atmosphere.android.dto.FutureThanRequest;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.dto.MessageResult;
import atmosphere.android.dto.PastThanRequest;
import atmosphere.android.dto.ResponseRequest;
import atmosphere.android.dto.ResponseResult;
import atmosphere.android.dto.SendMessageRequest;
import atmosphere.android.dto.SendMessageResult;
import atmosphere.android.dto.SerchRequest;
import atmosphere.android.manager.AtmosPreferenceManager;
import atmosphere.android.util.internet.JsonPath;
import atmosphere.android.util.json.AtmosTask;
import atmosphere.android.util.json.AtmosTask.LoginResultHandler;
import atmosphere.android.util.json.AtmosTask.RequestMethod;
import atmosphere.android.util.json.AtmosTask.ResultHandler;

public class MessageHelper implements AtmosUrl {

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
		}).build().ignoreDialog(ignoreDialog).execute(JsonPath.paramOf(BASE_URL + targetMethod, params));
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
			}).build().ignoreDialog(true).execute(JsonPath.paramOf(BASE_URL + targetMethod, params));
		}
	}

	public static void sendMessage(final SendMessageRequest param, final Activity activity, final MessageBaseAdapter adapter, final String targetMethod) {
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
		}).build().execute(JsonPath.paramOf(BASE_URL + SEND_RESPONSE_METHOD, response));
	}

	public static void serchMessage(final Activity activity, final String replyId, final DetailMessageAdapter adapter, final String messageId) {
		if (replyId != null && replyId.length() != 0) {
			SerchRequest param = new SerchRequest();
			param.message_ids = replyId;
			new AtmosTask.Builder<MessageResult>(activity, MessageResult.class, RequestMethod.GET).resultHandler(new ResultHandler<MessageResult>() {
				@Override
				public void handleResult(List<MessageResult> results) {
					if (results != null && !results.isEmpty()) {
						List<MessageDto> result = results.get(0).results;
						if (result != null && !result.isEmpty() && result.get(0) != null) {
							adapter.addBeforeItem(result.get(0));
							serchMessage(activity, result.get(0).reply_to, adapter, messageId);
						} else {
							serchReplyMessage(activity, messageId, adapter);
						}

					} else {
						serchReplyMessage(activity, messageId, adapter);
					}
				}
			}).loginHandler(new LoginResultHandler() {
				@Override
				public void handleResult() {
					serchMessage(activity, replyId, adapter, messageId);
				}
			}).build().ignoreDialog(true).execute(JsonPath.paramOf(BASE_URL + MESSAGE_SEARCH_METHOD, param));
		} else {
			serchReplyMessage(activity, messageId, adapter);
		}
	}

	public static void serchReplyMessage(final Activity activity, final String messageId, final DetailMessageAdapter adapter) {
		if (messageId != null && messageId.length() != 0) {
			SerchRequest param = new SerchRequest();
			param.reply_to_message_id = messageId;
			new AtmosTask.Builder<MessageResult>(activity, MessageResult.class, RequestMethod.GET).resultHandler(new ResultHandler<MessageResult>() {
				@Override
				public void handleResult(List<MessageResult> results) {
					if (results != null && !results.isEmpty()) {
						List<MessageDto> result = results.get(0).results;
						if (result != null && !result.isEmpty() && result.get(0) != null) {
							adapter.addItem(result.get(0));
							serchReplyMessage(activity, result.get(0)._id, adapter);
						} else {
							getDetailOverlay(activity).setVisibility(View.GONE);
							adapter.notifyDataSetChanged();
						}
					} else {
						getDetailOverlay(activity).setVisibility(View.GONE);
						adapter.notifyDataSetChanged();
					}
				}
			}).loginHandler(new LoginResultHandler() {
				@Override
				public void handleResult() {
					serchReplyMessage(activity, messageId, adapter);
				}
			}).build().ignoreDialog(true).execute(JsonPath.paramOf(BASE_URL + MESSAGE_SEARCH_METHOD, param));
		} else {
			getDetailOverlay(activity).setVisibility(View.GONE);
			adapter.notifyDataSetChanged();
		}
	}

	protected static DrawerLayout getDrawer(Activity activity) {
		return (DrawerLayout) activity.findViewById(R.id.Drawer);
	}

	protected static EditText getSendMessageEditText(Activity activity) {
		return (EditText) activity.findViewById(R.id.SendMessageEditText);
	}

	protected static ProgressBar getBaseProgressBar(Activity activity) {
		return (ProgressBar) activity.findViewById(R.id.base_progressBar);
	}

	protected static LinearLayout getDetailOverlay(Activity activity) {
		return (LinearLayout) activity.findViewById(R.id.detail_message_list_overlay);
	}

}
