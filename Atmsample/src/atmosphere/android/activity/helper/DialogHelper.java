package atmosphere.android.activity.helper;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import atmosphere.android.constant.AtmosUrl;
import atmosphere.android.dto.LoginRequest;
import atmosphere.android.dto.LoginResult;
import atmosphere.android.manager.AtmosPreferenceManager;
import atmosphere.android.util.internet.JsonPath;
import atmosphere.android.util.json.PostTask;
import atmosphere.android.util.json.PostTask.PostResultHandler;
import atmsample.android.R;

public class DialogHelper implements AtmosUrl {
	public static Dialog createLoginDialog(final Context context, final Dialog dialog, int titleResId, final PostResultHandler<LoginResult> handler) {
		dialog.setCancelable(false);
		dialog.setTitle(titleResId);

		final View view = LayoutInflater.from(context).inflate(R.layout.login_dialog, null);
		EditText userId = (EditText) view.findViewById(R.id.user_id);
		userId.setText(AtmosPreferenceManager.getUserId(context));

		EditText userPassword = (EditText) view.findViewById(R.id.user_password);
		userPassword.setText(AtmosPreferenceManager.getPassword(context));

		CheckBox savePasswd = (CheckBox) view.findViewById(R.id.save_password_chckBox);
		savePasswd.setChecked(AtmosPreferenceManager.getSavePasswordFlag(context));

		Button okButton = (Button) view.findViewById(R.id.ok_button);
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LoginRequest param = new LoginRequest();
				EditText userId = (EditText) view.findViewById(R.id.user_id);
				param.user_id = userId.getText().toString();
				EditText password = (EditText) view.findViewById(R.id.user_password);
				param.password = password.getText().toString();
				CheckBox savePasswd = (CheckBox) view.findViewById(R.id.save_password_chckBox);
				boolean isSave = savePasswd.isChecked();

				AtmosPreferenceManager.setUserId(context, param.user_id);
				AtmosPreferenceManager.setSavePasswordFlag(context, isSave);
				if (isSave) {
					AtmosPreferenceManager.setPassword(context, param.password);
				} else {
					AtmosPreferenceManager.setPassword(context, "");
				}

				new PostTask<LoginResult>(context, LoginResult.class, handler, null).execute(JsonPath.paramOf(BASE_URL + LOGIN_METHOD, param));
			}
		});

		Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});

		dialog.setContentView(view);

		return dialog;
	}
}
