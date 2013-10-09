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

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import atmosphere.android.activity.helper.DialogHelper;
import atmosphere.android.constant.AtmosUrl;
import atmosphere.android.constant.HttpHeaderKey;
import atmosphere.android.dto.LoginRequest;
import atmosphere.android.dto.LoginResult;
import atmosphere.android.manager.AtmosPreferenceManager;
import atmosphere.android.util.AbstractProgressTask;
import atmosphere.android.util.ProgressObserver.ProgressStyle;
import atmosphere.android.util.internet.JsonPath;
import atmsample.android.R;

public class PostTask<Result> extends AbstractProgressTask<JsonPath, List<Result>> {

	public PostTask(Context context, Class<Result> resultType, PostResultHandler<Result> handler, LoginResultHandler loginHandler) {
		this(context, resultType, false, handler, loginHandler);
	}

	public PostTask(Context context, Class<Result> resultType, boolean ignoreDialog, PostResultHandler<Result> handler, LoginResultHandler loginHandler) {
		this(context, resultType, ProgressStyle.Spin, ignoreDialog, context.getResources().getString(R.string.connecting), handler, loginHandler);
	}

	public PostTask(Context context, Class<Result> resultType, String message, PostResultHandler<Result> handler, LoginResultHandler loginHandler) {
		this(context, resultType, ProgressStyle.Spin, false, message, handler, loginHandler);
	}

	public PostTask(Context context, Class<Result> resultType, ProgressStyle progressStyle, boolean ignoreDialog, String message, PostResultHandler<Result> handler, LoginResultHandler loginHandler) {
		super(context);
		ignoreDialog(ignoreDialog);
		this.handler = handler;
		this.loginHandler = loginHandler;
		this.resultType = resultType;
		observer.updateStyle(progressStyle);
		observer.setMessage(message);
	}

	PostResultHandler<Result> handler;
	LoginResultHandler loginHandler;
	protected Class<Result> resultType;

	@Override
	protected List<Result> doInBackground(JsonPath... params) {
		List<Result> results = new ArrayList<Result>();
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

				if (code == HttpURLConnection.HTTP_OK) {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						Result result = JSON.decode(entity.getContent(), resultType);
						results.add(result);
					}
				} else if (code == HttpURLConnection.HTTP_UNAUTHORIZED) {
					results = null;
				} else {
					Log.w("Atmos", "response code:" + code + " [" + response.toString() + "]");
					observer.cancel(code);
					break;
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
		if (result == null) {
			if (AtmosPreferenceManager.getSavePasswordFlag(context)) {
				LoginRequest param = new LoginRequest();
				param.user_id = AtmosPreferenceManager.getUserId(context);
				param.password = AtmosPreferenceManager.getPassword(context);

				final Dialog loginDialog = new Dialog(context);
				new PostTask<LoginResult>(context, LoginResult.class, createLoginHandler(loginDialog), null).execute(JsonPath.paramOf(AtmosUrl.BASE_URL + AtmosUrl.LOGIN_METHOD, param));
				loginDialog.show();
			} else {
				final Dialog loginDialog = new Dialog(context);
				DialogHelper.createLoginDialog(context, loginDialog, R.string.login, createLoginHandler(loginDialog));
				loginDialog.show();
			}
		} else if (handler != null) {
			handler.handleResult(result);
		}
		super.onPostExecute(result);
	}

	private PostResultHandler<LoginResult> createLoginHandler(final Dialog loginDialog) {
		PostResultHandler<LoginResult> handler = new PostResultHandler<LoginResult>() {
			@Override
			public void handleResult(List<LoginResult> results) {
				if (results != null && !results.isEmpty()) {
					LoginResult result = results.get(0);
					if (result.session_id != null && result.session_id.length() != 0) {
						AtmosPreferenceManager.setAtmosSessionId(context, result.session_id);
						loginDialog.dismiss();
						if (loginHandler != null) {
							loginHandler.handleResult();
						}
					} else {
						loginDialog.setTitle("Retry");
					}
				}
			}
		};
		return handler;
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

	public abstract static class LoginResultHandler {
		public abstract void handleResult();
	}

}
