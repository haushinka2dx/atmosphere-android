package atmosphere.android.activity.view;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import atmosphere.android.constant.AtmosAction;
import atmosphere.android.constant.AtmosUrl;
import atmosphere.android.dto.DestroyRequest;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.dto.ResponseRequest;
import atmosphere.android.dto.ResponseResult;
import atmosphere.android.dto.SendMessageRequest;
import atmosphere.android.dto.SendMessageResult;
import atmosphere.android.manager.AtmosPreferenceManager;
import atmosphere.android.util.TimeUtil;
import atmosphere.android.util.internet.JsonPath;
import atmosphere.android.util.json.AtmosTask;
import atmosphere.android.util.json.AtmosTask.LoginResultHandler;
import atmosphere.android.util.json.AtmosTask.RequestMethod;
import atmosphere.android.util.json.AtmosTask.ResultHandler;
import atmsample.android.R;

public class DetailMessageAdapter extends BaseAdapter implements AtmosUrl {
	private Activity activity;
	private List<MessageDto> list;
	private Map<String, Bitmap> imageCash;
	private String userId;

	public DetailMessageAdapter(Activity activity, List<MessageDto> list) {
		this.activity = activity;
		this.list = list;
		this.imageCash = new HashMap<String, Bitmap>();
		this.userId = AtmosPreferenceManager.getUserId(activity);
	}

	public void setItems(List<MessageDto> list) {
		this.list = list;
	}

	public void addItem(MessageDto item) {
		this.list.add(item);
	}

	public void addItems(List<MessageDto> list) {
		this.list.addAll(list);
	}

	public void addBeforeItems(List<MessageDto> list) {
		list.addAll(this.list);
		this.list = list;
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

		ImageButton funImageButton = (ImageButton) view.findViewById(R.id.detail_fun_image_button);
		ImageButton goodImageButton = (ImageButton) view.findViewById(R.id.detail_good_image_button);
		ImageButton memoImageButton = (ImageButton) view.findViewById(R.id.detail_memo_image_button);
		ImageButton usefullImageButton = (ImageButton) view.findViewById(R.id.detail_usefull_image_button);

		ImageButton deleteButton = (ImageButton) view.findViewById(R.id.detail_delete_image_button);
		deleteButton.setVisibility(View.GONE);

		if (data.created_by.equals(userId)) {
			funImageButton.setEnabled(false);
			goodImageButton.setEnabled(false);
			memoImageButton.setEnabled(false);
			usefullImageButton.setEnabled(false);

			deleteButton.setVisibility(View.VISIBLE);
			deleteButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DestroyRequest param = new DestroyRequest();
					param._id = data._id;
					new AtmosTask.Builder<ResponseResult>(activity, ResponseResult.class, RequestMethod.POST).build().execute(JsonPath.paramOf(BASE_URL + SEND_DESTORY_METHOD, param));
				}
			});
		} else {
			if (data.responses.fun.contains(userId)) {
				funImageButton.setEnabled(false);
			} else {
				funImageButton.setOnClickListener(createRespnseClickListener(activity, data, AtmosAction.FUN));
			}

			if (data.responses.good.contains(userId)) {
				goodImageButton.setEnabled(false);
			} else {
				goodImageButton.setOnClickListener(createRespnseClickListener(activity, data, AtmosAction.GOOD));
			}

			if (data.responses.memo.contains(userId)) {
				memoImageButton.setEnabled(false);
			} else {
				memoImageButton.setOnClickListener(createRespnseClickListener(activity, data, AtmosAction.MEMO));
			}

			if (data.responses.usefull.contains(userId)) {
				usefullImageButton.setEnabled(false);
			} else {
				usefullImageButton.setOnClickListener(createRespnseClickListener(activity, data, AtmosAction.USE_FULL));
			}

		}

		ImageButton replayButton = (ImageButton) view.findViewById(R.id.detail_reply_image_button);
		replayButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getDrawer(activity).openDrawer(GravityCompat.START);
				getSendMessageEditText(activity).setText("@" + data.created_by + " ");
				getSendMessageEditText(activity).setSelection(data.created_by.length() + 2);

				getSubmitButton(activity).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						SendMessageRequest param = new SendMessageRequest();
						param.reply_to = data._id;
						String message = getSendMessageEditText(activity).getText().toString();
						param.message = message;
						if (message != null && message.length() != 0) {
							sendMessage(param, activity);
						}
					}
				});
			}
		});

		return view;
	}

	private void setResponseCount(TextView targetTextView, int count) {
		targetTextView.setText(String.valueOf(count));
	}

	private View.OnClickListener createRespnseClickListener(final Context context, final MessageDto item, final AtmosAction action) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ResponseRequest response = new ResponseRequest();
				response.target_id = item._id;
				response.action = action.getValue();
				new AtmosTask.Builder<ResponseResult>(context, ResponseResult.class, RequestMethod.POST).resultHandler(new ResultHandler<ResponseResult>() {
					@Override
					public void handleResult(List<ResponseResult> results) {
						String userId = AtmosPreferenceManager.getUserId(context);
						if (action == AtmosAction.FUN && !item.responses.fun.contains(userId)) {
							item.responses.fun.add(userId);
						} else if (action == AtmosAction.GOOD && !item.responses.good.contains(userId)) {
							item.responses.good.add(userId);
						} else if (action == AtmosAction.MEMO && !item.responses.memo.contains(userId)) {
							item.responses.memo.add(userId);
						} else if (action == AtmosAction.USE_FULL && !item.responses.usefull.contains(userId)) {
							item.responses.usefull.add(userId);
						}
						notifyDataSetChanged();
					}
				}).build().execute(JsonPath.paramOf(BASE_URL + SEND_RESPONSE_METHOD, response));
			}
		};
	}

	private static void sendMessage(final SendMessageRequest param, final Activity activity) {
		new AtmosTask.Builder<SendMessageResult>(activity, SendMessageResult.class, RequestMethod.POST).progressMessage("Sending").resultHandler(new ResultHandler<SendMessageResult>() {
			@Override
			public void handleResult(List<SendMessageResult> results) {
				if (results != null && !results.isEmpty() && results.get(0).status.equals("ok")) {
					getSendMessageEditText(activity).setText("");
					getDrawer(activity).closeDrawers();
				}
			}
		}).loginHandler(new LoginResultHandler() {
			@Override
			public void handleResult() {
				sendMessage(param, activity);
			}
		}).build().execute(JsonPath.paramOf(BASE_URL + SEND_MESSAGE_METHOD, param));
	}

	protected static DrawerLayout getDrawer(Activity activity) {
		return (DrawerLayout) activity.findViewById(R.id.Drawer);
	}

	protected static EditText getSendMessageEditText(Activity activity) {
		return (EditText) activity.findViewById(R.id.SendMessageEditText);
	}

	protected static Button getSubmitButton(Activity activity) {
		return (Button) activity.findViewById(R.id.SubmitButton);
	}
}
