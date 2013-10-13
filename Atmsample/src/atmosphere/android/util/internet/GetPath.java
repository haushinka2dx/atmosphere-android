package atmosphere.android.util.internet;

import java.util.List;
import java.util.Map;
import java.util.Set;

import android.util.Log;

public class GetPath extends Path {

	public Map<String, List<String>> param;

	public GetPath(String real, String alias, String display, Map<String, List<String>> param) {
		super(real, alias, display);
		this.param = param;
	}

	public static GetPath paramOf(String real, Map<String, List<String>> param) {
		return new GetPath(real, real, null, param);
	}

	public static GetPath paramOf(String real, String display, Map<String, List<String>> param) {
		return new GetPath(real, real, display, param);
	}

	public static GetPath paramOf(String real, String alias, String display, Map<String, List<String>> param) {
		return new GetPath(real, alias, display, param);
	}

	public static String createUrl(GetPath getPath) {
		String baseUrl = getPath.real;
		Map<String, List<String>> params = getPath.param;
		if (params != null && !params.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append(baseUrl);
			sb.append("?");
			Set<String> keys = params.keySet();
			String andSep = "";
			for (String key : keys) {
				sb.append(andSep);
				sb.append(key);
				sb.append("=");

				List<String> values = params.get(key);
				String commaSep = "";
				for (String value : values) {
					sb.append(commaSep);
					sb.append(value);
					commaSep = ",";
				}
				andSep = "&";
			}
			Log.v("Atmos Get Url", sb.toString());
			return sb.toString();
		} else {
			return baseUrl;
		}
	}
}
