package atmosphere.android.activity.helper;

import interprism.atmosphere.android.R;
import android.app.Activity;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import atmosphere.android.activity.view.MessageBaseAdapter;
import atmosphere.android.constant.AtmosConstant;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.dto.SendMessageRequest;
import atmosphere.android.manager.AtmosPreferenceManager;
import atmosphere.android.util.Tooltip;

public class PrivateResponseTooltipHelper extends ResponseTooltipHelper implements AtmosConstant {
	@Override
	protected OnClickListener createReplyListener(final Activity activity, final String userId, final MessageBaseAdapter adapter, final MessageDto item, final String targetMethod,
			final Tooltip tooltip) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String createUser = item.created_by;

				StringBuilder sb = new StringBuilder();
				if (!createUser.equals(AtmosPreferenceManager.getUserId(activity))) {
					sb.append(AT_MARK);
					sb.append(createUser);
					sb.append(SPACE);
				}

				if (item.to_user_id != null && !item.to_user_id.isEmpty()) {
					for (String replyUser : item.to_user_id) {
						if (!replyUser.equals(userId) && !replyUser.equals(createUser)) {
							sb.append(AT_MARK);
							sb.append(replyUser);
							sb.append(SPACE);
						}
					}
				}

				getDrawer(activity).openDrawer(GravityCompat.END);
				getSendPrivateToUserEditText(activity).setText(sb.toString());

				getSubmitPrivateButton(activity).setOnClickListener(new View.OnClickListener() {
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
		};
	}

	protected EditText getSendPrivateToUserEditText(Activity activity) {
		return (EditText) activity.findViewById(R.id.SendPrivateToUserEditText);
	}

	protected EditText getSendPrivateMessageEditText(Activity activity) {
		return (EditText) activity.findViewById(R.id.SendPrivateMessageEditText);
	}

	protected Button getSubmitPrivateButton(Activity activity) {
		return (Button) activity.findViewById(R.id.SubmitPrivateButton);
	}

}
