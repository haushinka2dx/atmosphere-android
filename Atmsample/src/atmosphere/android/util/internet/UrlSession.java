package atmosphere.android.util.internet;

import java.net.HttpURLConnection;
import java.net.URL;

public class UrlSession {
	public final URL url;
	public final HttpURLConnection con;

	public UrlSession(URL url, HttpURLConnection con) {
		this.url = url;
		this.con = con;
	}
}
