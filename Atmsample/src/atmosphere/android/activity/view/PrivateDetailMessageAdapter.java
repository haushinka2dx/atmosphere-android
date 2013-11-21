package atmosphere.android.activity.view;

import interprism.atmosphere.android.R;

import java.util.List;

import android.app.Activity;
import android.view.View;
import atmosphere.android.activity.helper.PrivateViewHelper;
import atmosphere.android.dto.MessageDto;

public class PrivateDetailMessageAdapter extends DetailMessageAdapter {

	public PrivateDetailMessageAdapter(Activity activity, List<MessageDto> list, String orgId) {
		super(activity, list, orgId);
	}

	@Override
	protected void viewControl(View view, final MessageDto data) {
		PrivateViewHelper.showToUsers(activity, view, data, R.id.private_detail_to_user_layout, R.id.detail_to_user_id_text_view, R.id.detail_point_text_view);
	}
}
