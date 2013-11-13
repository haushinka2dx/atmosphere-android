package atmosphere.android.activity.view;

import interprism.atmosphere.android.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import atmosphere.android.activity.helper.AvatarHelper;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.util.TimeUtil;

public class MessageAdapter extends MessageBaseAdapter {

	protected Activity activity;
	private Map<String, Bitmap> imageCash;

	public MessageAdapter(Activity activity, List<MessageDto> list) {
		super(list);
		this.activity = activity;
		this.imageCash = new HashMap<String, Bitmap>();
	}

	public List<MessageDto> getList() {
		// 本当はこれはやりたくない
		return list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View view;
		if (convertView != null) {
			view = convertView;
		} else {
			LayoutInflater inflater = LayoutInflater.from(activity);
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

		ImageView avatar = (ImageView) view.findViewById(R.id.user_avatar);
		AvatarHelper.setAvatar(view, data, imageCash, avatar);

		privateControl(view, data);

		return view;
	}

	protected void privateControl(View view, MessageDto data) {
		LinearLayout privateLayout = (LinearLayout) view.findViewById(R.id.private_to_user_layout);
		privateLayout.setVisibility(View.GONE);
	}

	protected ListView getDetailListView(Activity activity) {
		return (ListView) activity.findViewById(R.id.detail_message_list);
	}

	protected LinearLayout getDetailOverlay(Activity activity) {
		return (LinearLayout) activity.findViewById(R.id.detail_message_list_overlay);
	}

	protected ViewPager getViewPager(Activity activity) {
		return (ViewPager) activity.findViewById(R.id.ViewPager);
	}

}
