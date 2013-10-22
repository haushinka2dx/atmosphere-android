package atmosphere.android.activity.helper;

import interprism.atmosphere.android.R;

import java.util.List;

import android.app.Activity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import atmosphere.android.activity.view.MessageBaseAdapter;
import atmosphere.android.constant.AtmosAction;
import atmosphere.android.constant.AtmosUrl;
import atmosphere.android.dto.DestroyRequest;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.dto.ResponseResult;
import atmosphere.android.dto.ResponsesDto;
import atmosphere.android.dto.SendMessageRequest;
import atmosphere.android.manager.AtmosPreferenceManager;
import atmosphere.android.util.Tooltip;
import atmosphere.android.util.internet.JsonPath;
import atmosphere.android.util.json.AtmosTask;
import atmosphere.android.util.json.AtmosTask.RequestMethod;
import atmosphere.android.util.json.AtmosTask.ResultHandler;

public class ResponseTooltipHelper implements AtmosUrl {

	public static Tooltip createResponseTooltip(final Activity activity, View view, final int position, final MessageBaseAdapter adapter, final MessageDto item, final String targetMethod) {
		final Tooltip tooltip;
		String userId = AtmosPreferenceManager.getUserId(activity);
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
								MessageHelper.sendMessage(param, activity, adapter, targetMethod);
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
						MessageHelper.sendResponse(activity, item, AtmosAction.FUN, adapter);
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
						MessageHelper.sendResponse(activity, item, AtmosAction.GOOD, adapter);
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
						MessageHelper.sendResponse(activity, item, AtmosAction.MEMO, adapter);
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
						MessageHelper.sendResponse(activity, item, AtmosAction.USE_FULL, adapter);
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
								MessageHelper.sendMessage(param, activity, adapter, targetMethod);
							}
						}
					});
					tooltip.dismiss();
				}
			});
		}
		return tooltip;
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

}