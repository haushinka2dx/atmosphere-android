package atmosphere.android.util.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.arnx.jsonic.JSON;
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
import atmosphere.android.util.internet.GET;
import atmosphere.android.util.internet.JsonPath;
import atmosphere.android.util.internet.UrlSession;
import atmsample.android.R;

public class AtmosTask<Result> extends AbstractProgressTask<JsonPath, List<Result>> implements AtmosUrl {

	public enum RequestMethod {
		GET, POST, ;
	}

	protected Class<Result> resultType;
	private RequestMethod requestMethod;

	private ResultHandler<Result> resultHandler = null;
	private LoginResultHandler loginHandler = null;
	private ProgressStyle progressStyle = ProgressStyle.Spin;
	private String progressMessage = null;

	public static class Builder<Result> {
		private Context context;
		private Class<Result> resultType;
		private RequestMethod requestMethod;

		private ResultHandler<Result> resultHandler = null;
		private LoginResultHandler loginHandler = null;
		private ProgressStyle progressStyle = ProgressStyle.Spin;
		private String progressMessage = null;

		public Builder(Context context, Class<Result> resultType, RequestMethod requestMethod) {
			this.context = context;
			this.resultType = resultType;
			this.requestMethod = requestMethod;
			this.progressMessage = context.getResources().getString(R.string.connecting);
		}

		public Builder<Result> progressStyle(ProgressStyle progressStyle) {
			this.progressStyle = progressStyle;
			return this;
		}

		public Builder<Result> progressMessage(String progressMessage) {
			this.progressMessage = progressMessage;
			return this;
		}

		public Builder<Result> resultHandler(ResultHandler<Result> resultHandler) {
			this.resultHandler = resultHandler;
			return this;
		}

		public Builder<Result> loginHandler(LoginResultHandler loginHandler) {
			this.loginHandler = loginHandler;
			return this;
		}

		public AtmosTask<Result> build() {
			if (context != null && resultType != null && requestMethod != null) {
				return new AtmosTask<Result>(this);
			} else {
				return null;
			}
		}
	}

	private AtmosTask(Builder<Result> builder) {
		super(builder.context);
		this.resultType = builder.resultType;
		this.requestMethod = builder.requestMethod;
		this.progressMessage = builder.progressMessage;
		this.resultType = builder.resultType;
		this.requestMethod = builder.requestMethod;

		this.resultHandler = builder.resultHandler;
		this.loginHandler = builder.loginHandler;
		this.progressStyle = builder.progressStyle;
		this.progressMessage = builder.progressMessage;

		super.observer.updateStyle(progressStyle);
		super.observer.setMessage(progressMessage);

	}

	@Override
	protected List<Result> doInBackground(JsonPath... params) {
		List<Result> results = new ArrayList<Result>();
		UrlSession session = null;
		try {

			for (JsonPath path : params) {
				if (isCancelled()) {
					break;
				}

				System.setProperty("http.keepAlive", "false");

				URL url = null;
				if (requestMethod == RequestMethod.POST) {
					url = new URL(path.real);
				} else if (requestMethod == RequestMethod.GET) {
					url = new URL(path.real + GET.encode(path.param));
				}

				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setConnectTimeout(8000);
				con.setRequestMethod(requestMethod.name());
				con.setRequestProperty(HttpHeaderKey.AtmosSessionIdKey, AtmosPreferenceManager.getAtmosSessionId(context));
				con.setDoInput(true);

				if (requestMethod == RequestMethod.POST && path.param != null) {
					con.setDoOutput(true);
					con.setRequestProperty("Content-Type", "application/json");
					OutputStream out = con.getOutputStream();
					JSON.encode(path.param, out, false);
				}

				session = new UrlSession(url, con);

				int code = -1;
				try {
					code = session.con.getResponseCode();
				} catch (IOException e) {
					// TODO
					// HttpURLConnectionでレスポンスコードが401だった場合に接続を切り取得できないため。HttpRequestBaseではPOSTの時のEntityをセット出来ず実現出来ていない。サーバー側でチェック用のロジックを組むかEntityをセットする方法を考えるべき
					code = HttpURLConnection.HTTP_UNAUTHORIZED;
				}

				if (code == HttpURLConnection.HTTP_OK) {
					AtmosPreferenceManager.setAtmosSessionId(context, session.con.getHeaderField(HttpHeaderKey.AtmosSessionIdKey));
					Log.v("SessionId", session.con.getHeaderField(HttpHeaderKey.AtmosSessionIdKey));

					InputStream in = session.con.getInputStream();
					Result result = JSON.decode(in, resultType);
					results.add(result);
					session.con.disconnect();
				} else if (code == HttpURLConnection.HTTP_UNAUTHORIZED) {
					return null;
				} else {
					Log.w("Atmos", "response code:" + code + " [" + session.url + "]");
					observer.cancel(code);
					return results;
				}

			}
		} catch (IOException e) {
			Log.e("Atmos", "throws IOException in Atmos.", e);
			observer.cancel();
			session.con.disconnect();
			return null;
		} finally {
			if (session != null && session.con != null) {
				session.con.disconnect();
			}
		}

		return results;
	}

	@Override
	protected void onPostExecute(List<Result> result) {
		if (result == null || (!result.isEmpty() && result.get(0).getClass().equals(LoginResult.class) && ((LoginResult) result.get(0)).session_id == null)) {
			if (AtmosPreferenceManager.getSavePasswordFlag(context) && AtmosPreferenceManager.getLoginTryCount(context) == 0) {
				AtmosPreferenceManager.setLoginTryCount(context, AtmosPreferenceManager.getLoginTryCount(context) + 1);
				LoginRequest param = new LoginRequest();
				param.user_id = AtmosPreferenceManager.getUserId(context);
				param.password = AtmosPreferenceManager.getPassword(context);

				new AtmosTask.Builder<LoginResult>(context, LoginResult.class, RequestMethod.POST).resultHandler(createLoginHandler(new Dialog(context))).loginHandler(new LoginResultHandler() {
					@Override
					public void handleResult() {
						if (loginHandler != null) {
							loginHandler.handleResult();
						}
					}
				}).build().execute(JsonPath.paramOf(BASE_URL + LOGIN_METHOD, param));
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
