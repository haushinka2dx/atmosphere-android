package atmosphere.android.manager;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.ListView;
import atmosphere.android.constant.AtmosUrl;
import atmosphere.android.dto.LoginRequest;
import atmosphere.android.dto.LoginResult;
import atmosphere.android.util.internet.JsonPath;
import atmosphere.android.util.json.PostTask;
import atmosphere.android.util.json.PostTask.PostResultHandler;

public class LoginManager implements AtmosUrl {

	public static void longin(final Context context, String userId, String password, final ListView listView) {

		LoginRequest prams = new LoginRequest();
		prams.user_id = userId;
		prams.password = password;

		new PostTask<LoginResult>(context, LoginResult.class, new PostResultHandler<LoginResult>() {
			@Override
			public void handleResult(List<LoginResult> results) {
				Log.v("Atmos Login", "login end");
				Log.v("Atmos Timeline", "get Grobal Timeline start");
				// TimelineManager.getGlobalTimeline(context, listView);
				Dialog dialog = new Dialog(context);
				dialog.setTitle(AtmosPreferenceManager.getAtmosSessionId(context));
				dialog.show();
				Log.v("Atmos Timeline", "get Grobal Timeline end");
			}

		}).execute(JsonPath.paramOf(BASE_URL + LOGIN_METHOD, prams));
	}
}
