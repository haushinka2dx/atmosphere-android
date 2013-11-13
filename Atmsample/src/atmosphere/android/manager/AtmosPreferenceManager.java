package atmosphere.android.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import atmosphere.android.dto.User;
import atmosphere.android.dto.UserListResult;

public class AtmosPreferenceManager {

	public static String getUserId(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("user_id", "");
	}

	public static void setUserId(Context context, String userId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putString("user_id", userId);
		editor.commit();
	}

	public static String getPassword(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("user_password", "");
	}

	public static void setPassword(Context context, String password) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putString("user_password", password);
		editor.commit();
	}

	public static boolean getSavePasswordFlag(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean("save_password_flag", false);
	}

	public static void setSavePasswordFlag(Context context, boolean isSave) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putBoolean("save_password_flag", isSave);
		editor.commit();
	}

	public static String getAtmosSessionId(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("atmos_session_id", "");
	}

	public static void setAtmosSessionId(Context context, String atmosSessionId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putString("atmos_session_id", atmosSessionId);
		editor.commit();
	}

	public static int getLoginTryCount(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getInt("login_try_count", 0);
	}

	public static void setLoginTryCount(Context context, int tryCount) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putInt("login_try_count", tryCount);
		editor.commit();
	}

	public static int getViewTheme(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getInt("view_theme", 0);
	}

	public static void setViewTheme(Context context, int themeId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putInt("view_theme", themeId);
		editor.commit();
	}

	public static List<String> getUserList(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String users = prefs.getString("user_list", "");
		List<String> userList = new ArrayList<String>();
		for (String user : users.split(",")) {
			userList.add(user);
		}
		return userList;
	}

	public static void setUserList(Context context, UserListResult result) {
		if (result != null && result.results != null && !result.results.isEmpty()) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			Editor editor = prefs.edit();

			StringBuilder sb = new StringBuilder();
			String sep = "";
			for (User user : result.results) {
				sb.append(sep);
				sb.append(user.user_id);
				sep = ",";
			}

			editor.putString("user_list", sb.toString());
			editor.commit();
		}
	}

}
