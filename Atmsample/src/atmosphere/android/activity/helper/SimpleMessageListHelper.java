package atmosphere.android.activity.helper;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import atmosphere.android.activity.view.DetailMessageAdapter;
import atmosphere.android.activity.view.MessageBaseAdapter;
import atmosphere.android.activity.view.SimpleMessageAdapter;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.util.Tooltip;

public class SimpleMessageListHelper extends MessageListHelper {

	public SimpleMessageListHelper(Activity activity, View view, LayoutInflater inflater, String targetMethod) {
		super(activity, view, inflater, targetMethod, new SimpleMessageAdapter(activity, new ArrayList<MessageDto>()));
	}

	@Override
	protected DetailMessageAdapter createDetailAdapter(List<MessageDto> list, String orgId) {
		return new DetailMessageAdapter(activity, list, orgId);
	}

	@Override
	protected Tooltip createTooltip(int position, MessageBaseAdapter detailAdapter, MessageDto detailTargetItem) {
		SimpleResponseTooltipHelper helper = new SimpleResponseTooltipHelper();
		return helper.createResponseTooltip(activity, position, detailAdapter, detailTargetItem, targetMethod);
	}
}
