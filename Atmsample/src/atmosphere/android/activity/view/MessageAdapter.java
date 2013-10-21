package atmosphere.android.activity.view;

import interprism.atmosphere.android.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import atmosphere.android.constant.AtmosUrl;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.util.TimeUtil;

public class MessageAdapter extends BaseAdapter implements AtmosUrl {

	private Context context;
	private List<MessageDto> list;
	private Map<String, Bitmap> imageCash;

	public MessageAdapter(Context context, List<MessageDto> list) {
		this.context = context;
		this.list = list;
		this.imageCash = new HashMap<String, Bitmap>();
	}

	public void setItems(List<MessageDto> list) {
		this.list = list;
	}

	public void addItems(List<MessageDto> list) {
		this.list.addAll(list);
	}

	public void addBeforeItems(List<MessageDto> list) {
		list.addAll(this.list);
		this.list = list;
	}

	public void removeItem(int position) {
		list.remove(position);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View view;
		if (convertView != null) {
			view = convertView;
		} else {
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(R.layout.messeges, parent, false);
		}

		final MessageDto data = list.get(position);

		TextView userName = (TextView) view.findViewById(R.id.user_name);
		userName.setText(data.created_by);

		TextView message = (TextView) view.findViewById(R.id.message_timeline);
		message.setText(data.message);
		// LinkUtil.autoLink(message, new LinkUtil.OnClickListener() {
		// @Override
		// public void onLinkClicked(String link) {
		// }
		//
		// @Override
		// public void onClicked() {
		// listView.dispatchSetActivated(true);
		// }
		// });

		TextView messageTime = (TextView) view.findViewById(R.id.message_time);
		messageTime.setText(TimeUtil.formatDateFromGMT(data.created_at));

		ImageView avator = (ImageView) view.findViewById(R.id.user_avator);

		if (imageCash.containsKey(data.created_by)) {
			avator.setImageBitmap(imageCash.get(data.created_by));
		} else {
			final String urlString = BASE_URL + USER_AVATOR_METHOD + "?user_id=" + data.created_by;
			new AsyncTask<Void, Void, Bitmap>() {

				@Override
				protected Bitmap doInBackground(Void... params) {
					URL url = null;
					InputStream istream = null;
					Bitmap bitmap = null;
					try {
						url = new URL(urlString);
						istream = url.openStream();
						bitmap = BitmapFactory.decodeStream(istream);
						istream.close();
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (istream != null) {
							try {
								istream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					return bitmap;
				}

				protected void onPostExecute(Bitmap result) {
					ImageView avator = (ImageView) view.findViewById(R.id.user_avator);
					avator.setImageBitmap(result);
					if (15 < imageCash.size()) {
						imageCash.clear();
					}
					imageCash.put(data.created_by, result);
				};

			}.execute();
		}

		return view;
	}

}
