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
import atmosphere.android.util.internet.GetPath;
import atmosphere.android.util.internet.JsonPath;
import atmosphere.android.util.json.PostTask.PostResultHandler;
import atmsample.android.R;

public class GetTask<Result> extends AbstractProgressTask<GetPath, List<Result>> {

	public GetTask(Context context, Class<Result> resultType, GetResultHandler<Result> handler, LoginResultHandler loginHandler) {
		this(context, resultType, false, handler, loginHandler);
	}

	public GetTask(Context context, Class<Result> resultType, boolean ignoreDialog, GetResultHandler<Result> handler, LoginResultHandler loginHandler) {
		this(context, resultType, ProgressStyle.Spin, ignoreDialog, context.getResources().getString(R.string.connecting), handler, loginHandler);
	}

	public GetTask(Context context, Class<Result> resultType, String message, GetResultHandler<Result> handler, LoginResultHandler loginHandler) {
		this(context, resultType, ProgressStyle.Spin, false, message, handler, loginHandler);
	}

	public GetTask(Context context, Class<Result> resultType, ProgressStyle progressStyle, boolean ignoreDialog, String message, GetResultHandler<Result> handler, LoginResultHandler loginHandler) {
		super(context);
		ignoreDialog(ignoreDialog);
		this.handler = handler;
		this.loginHandler = loginHandler;
		this.resultType = resultType;
		observer.updateStyle(progressStyle);
		observer.setMessage(message);
	}

	GetResultHandler<Result> handler;
	LoginResultHandler loginHandler;
	protected Class<Result> resultType;

	@Override
	protected List<Result> doInBackground(GetPath... args) {
		List<Result> results = new ArrayList<Result>();
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
				} else if (code == HttpURLConnection.HTTP_UNAUTHORIZED) {
					results = null;
					break;
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
		if (result == null) {
			if (AtmosPreferenceManager.getSavePasswordFlag(context) && AtmosPreferenceManager.getLoginTryCount(context) == 0) {
				AtmosPreferenceManager.setLoginTryCount(context, AtmosPreferenceManager.getLoginTryCount(context) + 1);
				LoginRequest param = new LoginRequest();
				param.user_id = AtmosPreferenceManager.getUserId(context);
				param.password = AtmosPreferenceManager.getPassword(context);

				final Dialog loginDialog = new Dialog(context);
				new PostTask<LoginResult>(context, LoginResult.class, createLoginHandler(loginDialog), new PostTask.LoginResultHandler() {
					@Override
					public void handleResult() {
						if (loginHandler != null) {
							loginHandler.handleResult();
						}
					}
				}).execute(JsonPath.paramOf(AtmosUrl.BASE_URL + AtmosUrl.LOGIN_METHOD, param));
			} else {
				final Dialog loginDialog = new Dialog(context);
				DialogHelper.createLoginDialog(context, loginDialog, R.string.login, createLoginHandler(loginDialog));
				loginDialog.show();
			}
		} else if (handler != null) {
			AtmosPreferenceManager.setLoginTryCount(context, 0);
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

	public abstract static class GetResultHandler<Result> {
		public abstract void handleResult(List<Result> results);
	}

	public abstract static class LoginResultHandler {
		public abstract void handleResult();
	}

}
