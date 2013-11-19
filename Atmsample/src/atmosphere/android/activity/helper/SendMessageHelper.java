package atmosphere.android.activity.helper;

import interprism.atmosphere.android.R;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import atmosphere.android.constant.AtmosConstant;
import atmosphere.android.dto.SendMessageRequest;
import atmosphere.android.dto.SendPrivateMessageRequest;

public class SendMessageHelper {

	public static void initSubmitButton(final Activity activity) {
		getSendMessageEditText(activity).setText(AtmosConstant.MESSAGE_CLEAR_TEXT);
		Button submitButton = getSubmitButton(activity);
		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SendMessageRequest param = new SendMessageRequest();
				String message = getSendMessageEditText(activity).getText().toString();
				param.message = message;
				if (message != null && message.length() != 0) {
					MessageHelper.sendMessage(activity, param);
				}
			}
		});
	}

	public static void initSubmitPrivateButton(final Activity activity) {
		getSendPrivateMessageEditText(activity).setText(AtmosConstant.MESSAGE_CLEAR_TEXT);
		getSendPrivateToUserEditText(activity).setText(AtmosConstant.MESSAGE_CLEAR_TEXT);

		Button submitPrivateButton = getSubmitPrivateButton(activity);
		submitPrivateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SendPrivateMessageRequest param = new SendPrivateMessageRequest();
				String message = getSendPrivateMessageEditText(activity).getText().toString();
				param.message = message;
				param.to_user_id = getSendPrivateToUserEditText(activity).getText().toString();
				if (message != null && message.length() != 0) {
					MessageHelper.sendPrivateMessage(activity, param);
				}
			}
		});
	}

	protected static EditText getSendMessageEditText(Activity activity) {
		return (EditText) activity.findViewById(R.id.SendMessageEditText);
	}

	protected static Button getSubmitButton(Activity activity) {
		return (Button) activity.findViewById(R.id.SubmitButton);
	}

	protected static EditText getSendPrivateMessageEditText(Activity activity) {
		return (EditText) activity.findViewById(R.id.SendPrivateMessageEditText);
	}

	protected static EditText getSendPrivateToUserEditText(Activity activity) {
		return (EditText) activity.findViewById(R.id.SendPrivateToUserEditText);
	}

	protected static Button getSubmitPrivateButton(Activity activity) {
		return (Button) activity.findViewById(R.id.SubmitPrivateButton);
	}

}
