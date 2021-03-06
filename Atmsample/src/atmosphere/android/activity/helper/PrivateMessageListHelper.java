package atmosphere.android.activity.helper;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import atmosphere.android.activity.view.DetailMessageAdapter;
import atmosphere.android.activity.view.MessageBaseAdapter;
import atmosphere.android.activity.view.PrivateDetailMessageAdapter;
import atmosphere.android.activity.view.PrivateMessageAdapter;
import atmosphere.android.constant.AtmosUrl;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.util.Tooltip;

public class PrivateMessageListHelper extends MessageListHelper {

	public PrivateMessageListHelper(Activity activity, View view, LayoutInflater inflater, String targetMethod) {
		super(activity, view, inflater, targetMethod, new PrivateMessageAdapter(activity, new ArrayList<MessageDto>()));
	}

	@Override
	protected Tooltip createTooltip(int position, MessageBaseAdapter detailAdapter, MessageDto detailTargetItem) {
		PrivateResponseTooltipHelper helper = new PrivateResponseTooltipHelper();
		return helper.createResponseTooltip(activity, position, detailAdapter, detailTargetItem, targetMethod, AtmosUrl.SEND_PRIVATE_RESPONSE_METHOD, AtmosUrl.SEND_PRIVATE_DESTORY_METHOD);
	}

	@Override
	protected DetailMessageAdapter createDetailAdapter(List<MessageDto> list, String orgId) {
		return new PrivateDetailMessageAdapter(activity, list, orgId);
	}
}
