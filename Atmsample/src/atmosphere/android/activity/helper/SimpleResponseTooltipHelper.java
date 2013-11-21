package atmosphere.android.activity.helper;

import android.app.Activity;
import android.support.v4.view.GravityCompat;
import android.view.View;
import atmosphere.android.activity.view.MessageBaseAdapter;
import atmosphere.android.constant.AtmosConstant;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.dto.SendMessageRequest;
import atmosphere.android.manager.AtmosPreferenceManager;
import atmosphere.android.util.Tooltip;

public class SimpleResponseTooltipHelper extends ResponseTooltipHelper {
	protected View.OnClickListener createReplyListener(final Activity activity, final String userId, final MessageBaseAdapter adapter, final MessageDto item, final String targetMethod,
			final Tooltip tooltip) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String createUser = item.created_by;

				StringBuilder sb = new StringBuilder();
				if (!createUser.equals(AtmosPreferenceManager.getUserId(activity))) {
					sb.append(AtmosConstant.MENTION_START_MARK);
					sb.append(createUser);
					sb.append(AtmosConstant.MENTION_END_MARK);
				}

				if (item.addresses != null && item.addresses.users != null && !item.addresses.users.isEmpty()) {
					for (String replyUser : item.addresses.users) {
						if (!replyUser.equals(userId) && !replyUser.equals(createUser)) {
							sb.append(AtmosConstant.MENTION_START_MARK);
							sb.append(replyUser);
							sb.append(AtmosConstant.MENTION_END_MARK);
						}
					}
				}

				getDrawer(activity).openDrawer(GravityCompat.START);
				getSendMessageEditText(activity).setText(sb.toString());
				getSendMessageEditText(activity).setSelection(sb.length());

				getSubmitButton(activity).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						SendMessageRequest param = new SendMessageRequest();
						param.reply_to = item._id;
						String message = getSendMessageEditText(activity).getText().toString();
						param.message = message;
						if (message != null && message.length() != 0) {
							MessageHelper.sendMessage(activity, param, adapter, targetMethod);
						}
					}
				});
				tooltip.dismiss();
			}
		};
	}
}
