package atmosphere.android.util.internet;

public class JsonPath extends Path {
	public Object param;

	public JsonPath(String real, String alias, String display, Object param) {
		super(real, alias, display);
		this.param = param;
	}

	public static JsonPath paramOf(String real, Object param) {
		return new JsonPath(real, real, null, param);
	}

	public static JsonPath paramOf(String real, String display, Object param) {
		return new JsonPath(real, real, display, param);
	}

	public static JsonPath paramOf(String real, String alias, String display, Object param) {
		return new JsonPath(real, alias, display, param);
	}
}
