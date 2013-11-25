package atmosphere.android.activity.view;

import interprism.atmosphere.android.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import atmosphere.android.activity.helper.AvatarHelper;
import atmosphere.android.activity.helper.OnlyUserHelper;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.util.TimeUtil;

public class MessageAdapter extends MessageBaseAdapter {

	private Map<String, Bitmap> imageCash;

	public MessageAdapter(Activity activity, List<MessageDto> list) {
		super(activity, list, R.layout.messeges, R.id.clicked_color_view);
		this.imageCash = new HashMap<String, Bitmap>();
	}

	public List<MessageDto> getList() {
		// 本当はこれはやりたくない
		return list;
	}

	public Map<String, Bitmap> getImageCash() {
		return this.imageCash;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final View view = super.getView(position, convertView, parent);
		final MessageDto data = list.get(position);

		TextView userName = (TextView) view.findViewById(R.id.user_name);
		userName.setText(data.created_by);

		TextView message = (TextView) view.findViewById(R.id.message_timeline);
		message.setText(data.message);

		TextView messageTime = (TextView) view.findViewById(R.id.message_time);
		messageTime.setText(TimeUtil.formatDateFromGMT(data.created_at));

		ImageView avatar = (ImageView) view.findViewById(R.id.user_avatar);
		AvatarHelper.setAndCachAvatar(data, imageCash, avatar);
		OnlyUserHelper.showOnlyUserList(activity, avatar, data.created_by, imageCash);

		viewControl(view, data);

		return view;
	}

	protected void viewControl(View view, MessageDto data) {
		LinearLayout privateLayout = (LinearLayout) view.findViewById(R.id.private_to_user_layout);
		privateLayout.setVisibility(View.GONE);
	}
}
