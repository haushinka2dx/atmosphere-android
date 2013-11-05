package atmosphere.android.activity.view;

import interprism.atmosphere.android.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.util.TimeUtil;

public class DetailMessageAdapter extends MessageBaseAdapter {
	protected Activity activity;
	private Map<String, Bitmap> imageCash;

	public DetailMessageAdapter(Activity activity, List<MessageDto> list) {
		super(list);
		this.activity = activity;
		this.imageCash = new HashMap<String, Bitmap>();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final View view;
		if (convertView != null) {
			view = convertView;
		} else {
			LayoutInflater inflater = LayoutInflater.from(activity);
			view = inflater.inflate(R.layout.detail_message, parent, false);
		}

		final MessageDto data = list.get(position);

		TextView userName = (TextView) view.findViewById(R.id.detail_user_name);
		userName.setText(data.created_by);

		TextView message = (TextView) view.findViewById(R.id.detail_message_timeline);
		message.setText(data.message);

		TextView messageTime = (TextView) view.findViewById(R.id.detail_message_time);
		messageTime.setText(TimeUtil.formatDateFromGMT(data.created_at));

		ImageView avator = (ImageView) view.findViewById(R.id.detail_user_avator);

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
					ImageView avator = (ImageView) view.findViewById(R.id.detail_user_avator);
					avator.setImageBitmap(result);
					if (15 < imageCash.size()) {
						imageCash.clear();
					}
					imageCash.put(data.created_by, result);
				};

			}.execute();
		}

		TextView funTextView = (TextView) view.findViewById(R.id.detail_fun_text_view);
		setResponseCount(funTextView, data.responses.fun.size());

		TextView goodTextView = (TextView) view.findViewById(R.id.detail_good_text_view);
		setResponseCount(goodTextView, data.responses.good.size());

		TextView memoTextView = (TextView) view.findViewById(R.id.detail_memo_text_view);
		setResponseCount(memoTextView, data.responses.memo.size());

		TextView usefullTextView = (TextView) view.findViewById(R.id.detail_usefull_text_view);
		setResponseCount(usefullTextView, data.responses.usefull.size());

		privateControl(view, data);

		return view;
	}

	private void setResponseCount(TextView targetTextView, int count) {
		targetTextView.setText(String.valueOf(count));
	}

	protected void privateControl(View view, MessageDto data) {
		LinearLayout privateLayout = (LinearLayout) view.findViewById(R.id.private_detail_to_user_layout);
		privateLayout.setVisibility(View.GONE);
	}

	protected DrawerLayout getDrawer(Activity activity) {
		return (DrawerLayout) activity.findViewById(R.id.Drawer);
	}

	protected EditText getSendMessageEditText(Activity activity) {
		return (EditText) activity.findViewById(R.id.SendMessageEditText);
	}

	protected static Button getSubmitButton(Activity activity) {
		return (Button) activity.findViewById(R.id.SubmitButton);
	}
}
