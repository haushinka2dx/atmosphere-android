package atmosphere.android.activity.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import atmosphere.android.activity.helper.MessageListHelper;
import atmosphere.android.constant.AtmosUrl;
import atmsample.android.R;

public class GlobalTimeLineFragment extends Fragment implements AtmosUrl {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.message_list_view, container, false);
		return MessageListHelper.createListView(getActivity(), view, inflater, GLOBAL_TIMELINE_METHOD);
	}

}
