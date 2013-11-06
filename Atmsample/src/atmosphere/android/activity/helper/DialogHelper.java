package atmosphere.android.activity.helper;

import java.util.List;

import interprism.atmosphere.android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import atmosphere.android.constant.AtmosUrl;
import atmosphere.android.dto.LoginRequest;
import atmosphere.android.dto.LoginResult;
import atmosphere.android.manager.AtmosPreferenceManager;
import atmosphere.android.util.internet.JsonPath;
import atmosphere.android.util.json.AtmosTask;
import atmosphere.android.util.json.AtmosTask.RequestMethod;
import atmosphere.android.util.json.AtmosTask.ResultHandler;

public class DialogHelper implements AtmosUrl {
	public static Dialog createLoginDialog(final Context context, final Dialog dialog, int titleResId, final ResultHandler<LoginResult> resultHandler) {
		dialog.setCancelable(false);
		dialog.setTitle(titleResId);

		final View view = LayoutInflater.from(context).inflate(R.layout.login_dialog, null);
		EditText userId = (EditText) view.findViewById(R.id.user_id);
		userId.setText(AtmosPreferenceManager.getUserId(context));

		EditText userPassword = (EditText) view.findViewById(R.id.user_password);
		userPassword.setText(AtmosPreferenceManager.getPassword(context));

		CheckBox savePasswd = (CheckBox) view.findViewById(R.id.save_password_checkBox);
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
				CheckBox savePasswd = (CheckBox) view.findViewById(R.id.save_password_checkBox);
				boolean isSave = savePasswd.isChecked();

				AtmosPreferenceManager.setUserId(context, param.user_id);
				AtmosPreferenceManager.setSavePasswordFlag(context, isSave);
				if (isSave) {
					AtmosPreferenceManager.setPassword(context, param.password);
				} else {
					AtmosPreferenceManager.setPassword(context, "");
				}

				new AtmosTask.Builder<LoginResult>(context, LoginResult.class, RequestMethod.POST).resultHandler(resultHandler).build().execute(JsonPath.paramOf(BASE_URL + LOGIN_METHOD, param));
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

	public static void createResponseDialog(Activity activity, String title, String message, DialogInterface.OnClickListener postListener) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(activity).setTitle(title).setMessage(message).setPositiveButton("OK", postListener)
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		dialog.show();
	}

	public static void createStringListDialog(Activity activity, List<String> list) {
		if (list != null && !list.isEmpty()) {

			View view = LayoutInflater.from(activity).inflate(R.layout.simple_list, null);
			ListView listView = (ListView) view.findViewById(R.id.ListView);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.simple_text, list);
			listView.setAdapter(adapter);

			DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
			int dialogWidth = (int) (metrics.widthPixels * 0.8);
			int dialogHeight = (int) (metrics.heightPixels * 0.6);

			Dialog dialog = new Dialog(activity);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(view);

			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
			lp.width = dialogWidth;
			lp.height = dialogHeight;
			dialog.getWindow().setAttributes(lp);

			dialog.show();
		}
	}
}
