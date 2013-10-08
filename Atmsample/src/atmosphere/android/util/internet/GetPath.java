package atmosphere.android.util.internet;

import java.util.List;
import java.util.Map;

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
}
