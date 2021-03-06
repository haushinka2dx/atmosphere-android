package atmosphere.android.activity.helper;

import interprism.atmosphere.android.R;
import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import atmosphere.android.activity.view.MessageBaseAdapter;
import atmosphere.android.constant.AtmosAction;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.dto.ResponsesDto;
import atmosphere.android.manager.AtmosPreferenceManager;
import atmosphere.android.util.Tooltip;

class ResponseTooltipHelper {

	public Tooltip createResponseTooltip(final Activity activity, final int position, final MessageBaseAdapter adapter, final MessageDto item, final String targetMethod, final String responseMethod,
			final String deleteMethod) {
		final Tooltip tooltip;
		final String userId = AtmosPreferenceManager.getUserId(activity);
		if (item.created_by.equals(userId)) {
			View tooltipView = LayoutInflater.from(activity).inflate(R.layout.reply_to_mine, null);
			tooltip = new Tooltip(activity, tooltipView);

			ImageButton deleteButton = (ImageButton) tooltipView.findViewById(R.id.delete_image_button);
			deleteButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DialogHelper.showResponseDialog(activity, "Delete This Message?", item.message, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							MessageHelper.sendDelete(activity, item, adapter, position, deleteMethod);
						}
					});
					tooltip.dismiss();
				}
			});

			ImageButton replyButton = (ImageButton) tooltipView.findViewById(R.id.reply_to_mine_image_button);
			replyButton.setOnClickListener(createReplyListener(activity, userId, adapter, item, targetMethod, tooltip));

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
						DialogHelper.showResponseDialog(activity, "Are you sure to response 'fun' ?", item.message, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								MessageHelper.sendResponse(activity, item, AtmosAction.FUN, adapter, responseMethod);
							}
						});
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
						DialogHelper.showResponseDialog(activity, "Are you sure to response 'good' ?", item.message, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								MessageHelper.sendResponse(activity, item, AtmosAction.GOOD, adapter, responseMethod);
							}
						});
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
						DialogHelper.showResponseDialog(activity, "Are you sure to response 'memo' ?", item.message, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								MessageHelper.sendResponse(activity, item, AtmosAction.MEMO, adapter, responseMethod);
							}
						});
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
						DialogHelper.showResponseDialog(activity, "Are you sure to response 'usefull' ?", item.message, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								MessageHelper.sendResponse(activity, item, AtmosAction.USE_FULL, adapter, responseMethod);
							}
						});
						tooltip.dismiss();
					}
				});
			}

			ImageButton replyButton = (ImageButton) tooltipView.findViewById(R.id.reply_to_other_image_button);
			replyButton.setOnClickListener(createReplyListener(activity, userId, adapter, item, targetMethod, tooltip));
		}
		return tooltip;
	}

	protected View.OnClickListener createReplyListener(final Activity activity, final String userId, final MessageBaseAdapter adapter, final MessageDto item, final String targetMethod,
			final Tooltip tooltip) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tooltip.dismiss();
			}
		};
	}

	protected DrawerLayout getDrawer(Activity activity) {
		return (DrawerLayout) activity.findViewById(R.id.Drawer);
	}

	protected EditText getSendMessageEditText(Activity activity) {
		return (EditText) activity.findViewById(R.id.SendMessageEditText);
	}

	protected Button getSubmitButton(Activity activity) {
		return (Button) activity.findViewById(R.id.SubmitButton);
	}

}
