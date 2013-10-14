package atmosphere.android.util.internet;

import java.lang.reflect.Field;
import java.util.List;

import android.util.Log;

public class GET {

	public static String encode(Object source) {
		if (source != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("?");

			Field[] fields = source.getClass().getFields();
			String sepAnd = "";
			for (Field field : fields) {
				field.setAccessible(true);
				Object result = null;
				try {
					result = field.get(source);
					if (result != null) {
						sb.append(sepAnd);
						sb.append(field.getName());
						sb.append("=");
						String sepComma = "";
						if (result instanceof List<?>) {
							for (Object obj : (List<?>) result) {
								sb.append(sepComma);
								sb.append(String.valueOf(obj));
								sepComma = ",";
							}
						} else {
							sb.append(String.valueOf(result));
						}
						sepAnd = "&";
					}

				} catch (IllegalArgumentException e) {
					Log.e("Atmos", "throws IllegalArgumentException in Atmos.", e);
				} catch (IllegalAccessException e) {
					Log.e("Atmos", "throws IllegalAccessException in Atmos.", e);
				}
			}

			return sb.toString();
		} else {
			return "";
		}
	}

}
