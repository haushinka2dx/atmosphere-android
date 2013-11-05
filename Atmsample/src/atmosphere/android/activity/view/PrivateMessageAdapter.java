package atmosphere.android.activity.view;

import interprism.atmosphere.android.R;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import atmosphere.android.activity.helper.DialogHelper;
import atmosphere.android.dto.MessageDto;

public class PrivateMessageAdapter extends MessageAdapter {

	public PrivateMessageAdapter(Activity activity, List<MessageDto> list) {
		super(activity, list);
	}

	@Override
	protected void privateControl(View view, final MessageDto data) {
		LinearLayout privateLayout = (LinearLayout) view.findViewById(R.id.private_to_user_layout);
		StringBuilder sb = new StringBuilder();
		if (data.to_user_id != null) {
			int count = 0;
			String space = "";
			for (String toUser : data.to_user_id) {
				sb.append(space);
				sb.append(toUser);
				space = " ";
				count += (toUser.length() + 1);
				if (24 < count) {
					break;
				}
			}
			TextView point = (TextView) view.findViewById(R.id.point_text_view);
			if (24 < count) {
				point.setVisibility(View.VISIBLE);
			} else {
				point.setVisibility(View.GONE);
			}
		}
		TextView toUsers = (TextView) view.findViewById(R.id.to_user_id_text_view);
		toUsers.setText(sb.toString());
		toUsers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogHelper.createStringListDialog(activity, data.to_user_id);
			}
		});

		privateLayout.setVisibility(View.VISIBLE);
	}

}
