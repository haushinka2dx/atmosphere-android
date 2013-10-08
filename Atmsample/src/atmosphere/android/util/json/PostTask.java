package atmosphere.android.util.json;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import net.arnx.jsonic.JSON;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.Log;
import atmosphere.android.constant.HttpHeaderKey;
import atmosphere.android.manager.AtmosPreferenceManager;
import atmosphere.android.util.AbstractProgressTask;
import atmosphere.android.util.ProgressObserver.ProgressStyle;
import atmosphere.android.util.internet.JsonPath;
import atmsample.android.R;

public class PostTask<Result> extends AbstractProgressTask<JsonPath, List<Result>> {

	public PostTask(Context context, Class<Result> resultType, PostResultHandler<Result> handler) {
		super(context);
		ignoreDialog(true);
		this.handler = handler;
		this.resultType = resultType;
	}

	PostResultHandler<Result> handler;
	protected Class<Result> resultType;

	@Override
	protected List<Result> doInBackground(JsonPath... params) {
		List<Result> results = new ArrayList<Result>();
		observer.updateStyle(ProgressStyle.Spin);
		observer.setMessage(context.getResources().getString(R.string.connecting));
		HttpClient httpClient = null;
		HttpPost post = null;
		try {

			for (JsonPath path : params) {
				if (isCancelled()) {
					break;
				}

				System.setProperty("http.keepAlive", "false");

				httpClient = new DefaultHttpClient();
				post = new HttpPost(path.real);

				if (path.param != null) {
					post.setHeader("Content-Type", "application/json; charset=UTF-8");
					post.setHeader(HttpHeaderKey.AtmosSessionIdKey, AtmosPreferenceManager.getAtmosSessionId(context));
					String parmStr = JSON.encode(path.param);
					StringEntity entity = new StringEntity(parmStr, "UTF-8");
					post.setEntity(entity);

				}

				HttpResponse response = httpClient.execute(post);
				int code = response.getStatusLine().getStatusCode();

				if (code != HttpURLConnection.HTTP_OK) {
					Log.w("Atmos", "response code:" + code + " [" + response.toString() + "]");
					observer.cancel(code);
					break;
				}

				HttpEntity entity = response.getEntity();
				if (entity != null) {
					Result result = JSON.decode(entity.getContent(), resultType);
					results.add(result);
				}
			}

		} catch (Exception e) {
			Log.e("Atmos", "throws IOException in Atmos.", e);
			observer.cancel();
		}

		return results;
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

	public abstract static class PostResultHandler<Result> {
		public abstract void handleResult(List<Result> results);
	}

}
