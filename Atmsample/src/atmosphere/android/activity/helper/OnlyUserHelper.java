package atmosphere.android.activity.helper;

import interprism.atmosphere.android.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import atmosphere.android.activity.view.OnlyUserAdapter;
import atmosphere.android.constant.AtmosConstant;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.dto.SerchRequest;

public class OnlyUserHelper {

	private static View footer;

	public static void showOnlyUserList(final Activity activity, ImageView avatar, final String userName, final Map<String, Bitmap> imageCash) {
		avatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageView avatar = getOnlyUserAvatar(activity);
				if (imageCash.containsKey(userName)) {
					avatar.setImageBitmap(imageCash.get(userName));
				} else {
					AvatarHelper.setAvatar(userName, avatar);
				}

				getOnlyUserName(activity).setText(userName);
				getOnlyUserInfo(activity).setText("Future comments will contain.");

				ListView onlyUserListView = getOnlyUserListView(activity);
				List<MessageDto> list = new ArrayList<MessageDto>();
				final OnlyUserAdapter adapter = new OnlyUserAdapter(activity, list);

				LayoutInflater inflater = LayoutInflater.from(activity);
				if (footer == null) {
					footer = inflater.inflate(R.layout.list_view_footer, null);
				}
				final ProgressBar footerProgressBar = (ProgressBar) footer.findViewById(R.id.ListViewFooterPrograssBar);
				final TextView footerTextView = (TextView) footer.findViewById(R.id.ListViewFooterTextView);

				footer.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (0 < adapter.getCount()) {
							MessageDto lastItem = (MessageDto) adapter.getItem(adapter.getCount() - 1);
							SerchRequest param = new SerchRequest();
							param.created_by = userName;
							param.count = AtmosConstant.NUMBER_OF_MESSAGES;
							param.past_than = lastItem.created_at;

							footerProgressBar.setVisibility(View.VISIBLE);
							footerTextView.setText(R.string.connecting);
							adapter.notifyDataSetChanged();

							MessageHelper.serchMessage(activity, param, adapter, footerProgressBar, footerTextView);
						}
					}
				});

				if (onlyUserListView.getFooterViewsCount() == 0) {
					onlyUserListView.addFooterView(footer);
				}
				onlyUserListView.setAdapter(adapter);

				SerchRequest param = new SerchRequest();
				param.created_by = userName;
				param.count = AtmosConstant.NUMBER_OF_MESSAGES;
				MessageHelper.serchMessage(activity, param, adapter);

				getOnlyUserShowSendGlobalButton(activity).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						SendMessageHelper.initSubmitButton(activity);
						getSendMessageEditText(activity).setText(AtmosConstant.MENTION_START_MARK + userName + AtmosConstant.MENTION_END_MARK);
						getDrawer(activity).openDrawer(GravityCompat.START);
					}
				});

				getOnlyUserShowSendPrivateButton(activity).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						SendMessageHelper.initSubmitPrivateButton(activity);
						getSendPrivateToUserEditText(activity).setText(AtmosConstant.MENTION_START_MARK + userName + AtmosConstant.MENTION_END_MARK);
						getDrawer(activity).openDrawer(GravityCompat.END);
					}
				});

				LinearLayout mainOverlay = getMainOverlay(activity);
				Animation outAnimation = AnimationUtils.loadAnimation(activity, R.anim.slide_out_right);
				mainOverlay.startAnimation(outAnimation);
				mainOverlay.setVisibility(View.GONE);

				LinearLayout onlyUserOverlay = getOnlyUserOverlay(activity);
				Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_right);
				onlyUserOverlay.startAnimation(animation);
				onlyUserOverlay.setVisibility(View.VISIBLE);
			}
		});
	}

	protected static LinearLayout getMainOverlay(Activity activity) {
		return (LinearLayout) activity.findViewById(R.id.main_overlay);
	}

	protected static LinearLayout getOnlyUserOverlay(Activity activity) {
		return (LinearLayout) activity.findViewById(R.id.only_user_overlay);
	}

	protected static ImageView getOnlyUserAvatar(Activity activity) {
		return (ImageView) activity.findViewById(R.id.only_user_avatar);
	}

	protected static TextView getOnlyUserName(Activity activity) {
		return (TextView) activity.findViewById(R.id.only_user_name);
	}

	protected static TextView getOnlyUserInfo(Activity activity) {
		return (TextView) activity.findViewById(R.id.only_user_info);
	}

	protected static ListView getOnlyUserListView(Activity activity) {
		return (ListView) activity.findViewById(R.id.only_user_message_list);
	}

	protected static ImageView getOnlyUserShowSendGlobalButton(Activity activity) {
		return (ImageView) activity.findViewById(R.id.only_user_show_send_global_button);
	}

	protected static ImageView getOnlyUserShowSendPrivateButton(Activity activity) {
		return (ImageView) activity.findViewById(R.id.only_user_show_send_private_button);
	}

	protected static DrawerLayout getDrawer(Activity activity) {
		return (DrawerLayout) activity.findViewById(R.id.Drawer);
	}

	protected static EditText getSendMessageEditText(Activity activity) {
		return (EditText) activity.findViewById(R.id.SendMessageEditText);
	}

	protected static EditText getSendPrivateToUserEditText(Activity activity) {
		return (EditText) activity.findViewById(R.id.SendPrivateToUserEditText);
	}

}
