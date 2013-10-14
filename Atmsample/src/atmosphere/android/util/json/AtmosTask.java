package atmosphere.android.util.json;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.arnx.jsonic.JSON;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import atmosphere.android.activity.helper.DialogHelper;
import atmosphere.android.constant.HttpHeaderKey;
import atmosphere.android.dto.LoginRequest;
import atmosphere.android.dto.LoginResult;
import atmosphere.android.manager.AtmosPreferenceManager;
import atmosphere.android.util.AbstractProgressTask;
import atmosphere.android.util.ProgressObserver.ProgressStyle;
import atmosphere.android.util.internet.GET;
import atmosphere.android.util.internet.JsonPath;
import atmsample.android.R;

public class AtmosTask<Result> extends AbstractProgressTask<JsonPath, List<Result>> {

	public enum RequestMethod {
		GET, POST, ;
	}

	protected Class<Result> resultType;
	private RequestMethod requestMethod;

	private ResultHandler<Result> resultHandler = null;
	private LoginResultHandler loginHandler = null;
	private ProgressStyle progressStyle = ProgressStyle.Spin;
	private String progressMessage = null;

	public AtmosTask(Context context, Class<Result> resultType, RequestMethod requestMethod) {
		super(context);
		this.resultType = resultType;
		this.requestMethod = requestMethod;
		progressMessage = context.getResources().getString(R.string.connecting);
	}

	public AtmosTask<Result> progressStyle(ProgressStyle progressStyle) {
		this.progressStyle = progressStyle;
		return this;
	}

	public AtmosTask<Result> progressMessage(String progressMessage) {
		this.progressMessage = progressMessage;
		return this;
	}

	public AtmosTask<Result> resultHandler(ResultHandler<Result> resultHandler) {
		this.resultHandler = resultHandler;
		return this;
	}

	public AtmosTask<Result> loginHandler(LoginResultHandler loginHandler) {
		this.loginHandler = loginHandler;
		return this;
	}

	private void initialize() {
		super.observer.updateStyle(progressStyle);
		super.observer.setMessage(progressMessage);
	}

	@Override
	protected List<Result> doInBackground(JsonPath... params) {
		initialize();
		List<Result> results = new ArrayList<Result>();
		HttpClient httpClient = null;
		HttpRequestBase request = null;
		try {

			for (JsonPath path : params) {
				if (isCancelled()) {
					break;
				}

				System.setProperty("http.keepAlive", "false");
				httpClient = new DefaultHttpClient();

				request = new HttpRequestBase() {
					@Override
					public String getMethod() {
						return requestMethod.name();
					}
				};

				String parmStr = "";
				if (path.param != null) {
					if (requestMethod == RequestMethod.POST) {
						request.setHeader("Content-Type", "application/json; charset=UTF-8");
						parmStr = JSON.encode(path.param);
					} else if (requestMethod == RequestMethod.GET) {
						parmStr = GET.encode(path.param);
					}
				}
				request.setURI(new URI(path.real + parmStr));
				request.setHeader(HttpHeaderKey.AtmosSessionIdKey, AtmosPreferenceManager.getAtmosSessionId(context));

				HttpResponse response = httpClient.execute(request);
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
		} catch (IOException e) {
			Log.e("Atmos", "throws IOException in Atmos.", e);
			observer.cancel();
		} catch (URISyntaxException e) {
			Log.e("Atmos", "throws URISyntaxException in Atmos.", e);
			observer.cancel();
		}

		return results;
	}

	@Override
	protected void onPostExecute(List<Result> result) {
		if (result == null || (result.get(0).getClass().equals(LoginResult.class) && ((LoginResult) result.get(0)).session_id == null)) {
			if (AtmosPreferenceManager.getSavePasswordFlag(context) && AtmosPreferenceManager.getLoginTryCount(context) == 0) {
				AtmosPreferenceManager.setLoginTryCount(context, AtmosPreferenceManager.getLoginTryCount(context) + 1);
				LoginRequest param = new LoginRequest();
				param.user_id = AtmosPreferenceManager.getUserId(context);
				param.password = AtmosPreferenceManager.getPassword(context);

				final Dialog loginDialog = new Dialog(context);
				new AtmosTask<LoginResult>(context, LoginResult.class, RequestMethod.POST).resultHandler(createLoginHandler(loginDialog));
			} else {
				final Dialog loginDialog = new Dialog(context);
				DialogHelper.createLoginDialog(context, loginDialog, R.string.login, createLoginHandler(loginDialog));
				loginDialog.show();
			}
		} else if (resultHandler != null) {
			AtmosPreferenceManager.setLoginTryCount(context, 0);
			resultHandler.handleResult(result);
		}
		super.onPostExecute(result);
	}

	private ResultHandler<LoginResult> createLoginHandler(final Dialog loginDialog) {
		ResultHandler<LoginResult> handler = new ResultHandler<LoginResult>() {
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
		if (resultHandler != null) {
			resultHandler.handleResult(null);
		}
		super.onCancelled();
	}

	public abstract static class ResultHandler<Result> {
		public abstract void handleResult(List<Result> results);
	}

	public abstract static class LoginResultHandler {
		public abstract void handleResult();
	}
}
