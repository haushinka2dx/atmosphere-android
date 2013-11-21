package atmosphere.android.activity.view;

import interprism.atmosphere.android.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import atmosphere.android.activity.helper.AvatarHelper;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.util.TimeUtil;

public class DetailMessageAdapter extends MessageBaseAdapter {
	private Map<String, Bitmap> imageCash;
	private String orgId;

	public DetailMessageAdapter(Activity activity, List<MessageDto> list, String orgId) {
		super(activity, list, R.layout.detail_message, R.id.detail_clicked_color_view);
		this.imageCash = new HashMap<String, Bitmap>();
		this.orgId = orgId;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final View view = super.getView(position, convertView, parent);
		final MessageDto data = list.get(position);

		LinearLayout detailLayout = (LinearLayout) view.findViewById(R.id.detail_layout);
		int color;
		if (data._id.equals(orgId)) {
			color = activity.getResources().getColor(R.drawable.detail_focus_color);
		} else {
			color = activity.getResources().getColor(R.drawable.none);
		}
		detailLayout.setBackgroundColor(color);

		TextView userName = (TextView) view.findViewById(R.id.detail_user_name);
		userName.setText(data.created_by);

		TextView message = (TextView) view.findViewById(R.id.detail_message_timeline);
		message.setText(data.message);

		TextView messageTime = (TextView) view.findViewById(R.id.detail_message_time);
		messageTime.setText(TimeUtil.formatDateFromGMT(data.created_at));

		ImageView avatar = (ImageView) view.findViewById(R.id.detail_user_avatar);
		AvatarHelper.setAndCachAvatar(data, imageCash, avatar);

		TextView funTextView = (TextView) view.findViewById(R.id.detail_fun_text_view);
		setResponseCount(funTextView, data.responses.fun.size());

		TextView goodTextView = (TextView) view.findViewById(R.id.detail_good_text_view);
		setResponseCount(goodTextView, data.responses.good.size());

		TextView memoTextView = (TextView) view.findViewById(R.id.detail_memo_text_view);
		setResponseCount(memoTextView, data.responses.memo.size());

		TextView usefullTextView = (TextView) view.findViewById(R.id.detail_usefull_text_view);
		setResponseCount(usefullTextView, data.responses.usefull.size());

		viewControl(view, data);

		return view;
	}

	private void setResponseCount(TextView targetTextView, int count) {
		targetTextView.setText(String.valueOf(count));
	}

	protected void viewControl(View view, MessageDto data) {
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
