package atmosphere.android.activity.helper;

import interprism.atmosphere.android.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import atmosphere.android.constant.AtmosUrl;
import atmosphere.android.dto.MessageDto;

public class AvatarHelper {
	public static void setAndCachAvatar(final MessageDto data, final Map<String, Bitmap> imageCash, final ImageView avatar) {
		avatar.setImageResource(R.drawable.no_image);
		if (imageCash.containsKey(data.created_by)) {
			avatar.setImageBitmap(imageCash.get(data.created_by));
		} else {
			final String urlString = AtmosUrl.BASE_URL + AtmosUrl.USER_AVATAR_METHOD + "?user_id=" + data.created_by;
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
					avatar.setImageBitmap(result);
					if (15 < imageCash.size()) {
						imageCash.clear();
					}
					imageCash.put(data.created_by, result);
				};
			}.execute();
		}
	}

	public static void setAvatar(String userName, final ImageView avatar) {
		avatar.setImageResource(R.drawable.no_image);
		final String urlString = AtmosUrl.BASE_URL + AtmosUrl.USER_AVATAR_METHOD + "?user_id=" + userName;
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
				avatar.setImageBitmap(result);
			};
		}.execute();
	}
}
