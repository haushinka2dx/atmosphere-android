package atmosphere.android.util.json;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.arnx.jsonic.JSON;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.Log;
import atmosphere.android.constant.HttpHeaderKey;
import atmosphere.android.manager.AtmosPreferenceManager;
import atmosphere.android.util.AbstractProgressTask;
import atmosphere.android.util.ProgressObserver.ProgressStyle;
import atmosphere.android.util.internet.GetPath;
import atmsample.android.R;

public class GetTask<Result> extends AbstractProgressTask<GetPath, List<Result>> {
	public GetTask(Context context, Class<Result> resultType, GetResultHandler<Result> handler) {
		super(context);
		ignoreDialog(true);
		this.handler = handler;
		this.resultType = resultType;
	}

	GetResultHandler<Result> handler;
	protected Class<Result> resultType;

	@Override
	protected List<Result> doInBackground(GetPath... args) {
		List<Result> results = new ArrayList<Result>();
		observer.updateStyle(ProgressStyle.Spin);
		observer.setMessage(context.getResources().getString(R.string.connecting));
		HttpClient httpClient = null;
		HttpGet get = null;
		try {

			for (GetPath path : args) {
				if (isCancelled()) {
					break;
				}

				System.setProperty("http.keepAlive", "false");
				httpClient = new DefaultHttpClient();
				get = new HttpGet(createGetPath(path));
				get.setHeader("Content-Type", "application/json; charset=UTF-8");
				get.setHeader(HttpHeaderKey.AtmosSessionIdKey, AtmosPreferenceManager.getAtmosSessionId(context));

				HttpResponse response = httpClient.execute(get);
				int code = response.getStatusLine().getStatusCode();
				if (code == HttpURLConnection.HTTP_OK) {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						AtmosPreferenceManager.setAtmosSessionId(context, response.getFirstHeader(HttpHeaderKey.AtmosSessionIdKey).getValue());
						Log.v("SessionId", response.getFirstHeader(HttpHeaderKey.AtmosSessionIdKey).getValue());
						Result result = JSON.decode(entity.getContent(), resultType);
						results.add(result);
					}
				} else if (code == 401) {
					results = null;
				}
			}

		} catch (Exception e) {
			Log.e("Atmos", "throws IOException in Atmos.", e);
			observer.cancel();
		}

		return results;
	}

	private String createGetPath(GetPath getPath) {
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

	@Override
	protected void onPostExecute(List<Result> result) {
		if (handler != null) {
			handler.handleResult(result);
		}
		super.onPostExecute(result);
	}

	@Override
	protected void onCancelled() {
		if (handler != null) {
			handler.handleResult(null);
		}
		super.onCancelled();
	}

	public abstract static class GetResultHandler<Result> {
		public abstract void handleResult(List<Result> results);
	}

}
