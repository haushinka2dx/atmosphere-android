package atmosphere.android.util;

public class StringUtil {

	public static boolean isNullOrEmpty(String s) {
		if (s == null || s.length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isBlank(String s) {
		if (s == null || s.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}
}
