package atmosphere.android.activity.view;

import interprism.atmosphere.android.R;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import atmosphere.android.dto.MessageDto;

public class SimpleMessageAdapter extends MessageAdapter {

	public SimpleMessageAdapter(Activity activity, List<MessageDto> list) {
		super(activity, list);
	}

	@Override
	protected void viewControl(View view, MessageDto data) {
		LinearLayout privateLayout = (LinearLayout) view.findViewById(R.id.private_to_user_layout);
		privateLayout.setVisibility(View.GONE);
	}
}
