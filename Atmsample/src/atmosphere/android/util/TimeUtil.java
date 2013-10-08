package atmosphere.android.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.util.Log;

public class TimeUtil {

	static public final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

	public static String formatDateFromGMT(String gmtString) {
		String result = null;
		Date date = getFormatDateFromGMT(gmtString);
		SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateTimeInstance();
		format.applyPattern("yyyy/MM/dd HH:mm:ss");
		String formatedDate = format.format(date);
		Date today = new Date();
		SimpleDateFormat onlyDay = (SimpleDateFormat) DateFormat.getDateTimeInstance();
		onlyDay.applyPattern("yyyy/MM/dd");
		String todayStr = onlyDay.format(today);

		if (todayStr.equals(formatedDate.substring(0, 10))) {
			result = formatedDate.substring(11, 16);
		} else {
			result = formatedDate.substring(0, 10);
		}

		return result;
	}

	public static Date getFormatDateFromGMT(String gmtStr) {
		Date result = null;
		SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateTimeInstance();
		sdf.applyPattern(DATE_PATTERN);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			result = sdf.parse(gmtStr);
		} catch (ParseException e) {
			Log.e("Atmos TImeUtil", e.getMessage());
		}
		return result;
	}

}
