package atmosphere.android.activity.helper;

import interprism.atmosphere.android.R;
import android.app.Activity;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import atmosphere.android.activity.view.MessageBaseAdapter;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.dto.SendMessageRequest;
import atmosphere.android.manager.AtmosPreferenceManager;
import atmosphere.android.util.Tooltip;

public class PrivateResponseTooltipHelper extends ResponseTooltipHelper {
	@Override
	protected OnClickListener createReplayListener(final Activity activity, final String userId, final MessageBaseAdapter adapter, final MessageDto item, final String targetMethod,
			final Tooltip tooltip) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String atMark = "@";
				String createUser = item.created_by;
				String space = " ";

				StringBuilder sb = new StringBuilder();
				if (!createUser.equals(AtmosPreferenceManager.getUserId(activity))) {
					sb.append(atMark);
					sb.append(createUser);
					sb.append(space);
				}

				if (item.to_user_id != null && !item.to_user_id.isEmpty()) {
					for (String replayUser : item.to_user_id) {
						if (!replayUser.equals(userId) && !replayUser.equals(createUser)) {
							sb.append(atMark);
							sb.append(replayUser);
							sb.append(space);
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
