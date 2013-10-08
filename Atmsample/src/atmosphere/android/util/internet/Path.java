package atmosphere.android.util.internet;

public class Path {
	public String real;
	public String alias;
	public String display;

	public Path(String real, String alias, String display) {
		this.real = real;
		this.alias = alias;
		this.display = display;
	}

	public static Path of(String real) {
		return new Path(real, real, null);
	}

	public static Path of(String real, String alias) {
		return new Path(real, alias, null);
	}

	public static Path of(String real, String alias, String display) {
		return new Path(real, alias, display);
	}

	public static Path displayOf(String real, String display) {
		return new Path(real, real, display);
	}

}
