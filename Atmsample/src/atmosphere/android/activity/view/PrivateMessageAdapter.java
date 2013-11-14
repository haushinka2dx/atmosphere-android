package atmosphere.android.activity.view;

import interprism.atmosphere.android.R;

import java.util.List;

import android.app.Activity;
import android.view.View;
import atmosphere.android.activity.helper.PrivateViewHelper;
import atmosphere.android.dto.MessageDto;

public class PrivateMessageAdapter extends MessageAdapter {

	public PrivateMessageAdapter(Activity activity, List<MessageDto> list) {
		super(activity, list);
	}

	@Override
	protected void viewControl(View view, final MessageDto data) {
		PrivateViewHelper.showToUsers(activity, view, data, R.id.private_to_user_layout, R.id.to_user_id_text_view, R.id.point_text_view);
	}

}
