package atmosphere.android.manager;

import java.util.Date;
import java.util.List;

import net.arnx.jsonic.JSON;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import atmosphere.android.activity.view.MessageAdapter;
import atmosphere.android.constant.HttpHeaderKey;
import atmosphere.android.dto.MessageResult;

public class TimelineManager {

	public static List<MessageResult> getGlobalTimeline(final Context context, final ListView listView) {
		final List<MessageResult> data = null;
		final Date now = new Date();

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				HttpClient httpClient = null;
				HttpGet get = null;
				try {
					httpClient = new DefaultHttpClient();
					get = new HttpGet("https://atmos.interprism.co.jp/atmos/messages/global_timeline?count=-1&_" + now.getTime());
					get.setHeader("Content-Type", "application/json; charset=UTF-8");
					get.setHeader(HttpHeaderKey.AtmosSessionIdKey, AtmosPreferenceManager.getAtmosSessionId(context));

					HttpResponse response = httpClient.execute(get);
					if (response.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = response.getEntity();
						if (entity != null) {
							AtmosPreferenceManager.setAtmosSessionId(context, response.getFirstHeader(HttpHeaderKey.AtmosSessionIdKey).getValue());
							Log.v("SessionId", response.getFirstHeader(HttpHeaderKey.AtmosSessionIdKey).getValue());
						}

						MessageResult result = JSON.decode(entity.getContent(), MessageResult.class);
						MessageAdapter adapter = new MessageAdapter(context, result.results);
						listView.setAdapter(adapter);
						adapter.notifyDataSetChanged();
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					httpClient.getConnectionManager().shutdown();
				}
				return null;
			}
		}.execute();

		return data;
	}
}
