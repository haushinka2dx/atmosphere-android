package atmosphere.android.activity.helper;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import atmosphere.android.constant.AtmosConstant;
import atmosphere.android.dto.MessageDto;

public class PrivateViewHelper {
	public static void showToUsers(final Activity activity, View view, final MessageDto data, int layoutId, int toUserTextId, int pointTextId) {
		LinearLayout privateLayout = (LinearLayout) view.findViewById(layoutId);
		StringBuilder sb = new StringBuilder();
		if (data.to_user_id != null) {
			int count = 0;
			String sep = AtmosConstant.BLANK;
			for (String toUser : data.to_user_id) {
				sb.append(sep);
				sb.append(toUser);
				sep = AtmosConstant.SPACE;
				count += (toUser.length() + 1);
				if (24 < count) {
					break;
				}
			}
			TextView point = (TextView) view.findViewById(pointTextId);
			if (24 < count) {
				point.setVisibility(View.VISIBLE);
			} else {
				point.setVisibility(View.GONE);
			}
		}
		TextView toUsers = (TextView) view.findViewById(toUserTextId);
		toUsers.setText(sb.toString());
		toUsers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogHelper.showStringListDialog(activity, data.to_user_id);
			}
		});

		privateLayout.setVisibility(View.VISIBLE);
	}
}
