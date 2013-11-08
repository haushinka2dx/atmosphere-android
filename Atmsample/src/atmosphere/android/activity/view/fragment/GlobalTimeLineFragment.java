package atmosphere.android.activity.view.fragment;

import interprism.atmosphere.android.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import atmosphere.android.activity.helper.MessageListHelper;
import atmosphere.android.constant.AtmosUrl;

public class GlobalTimeLineFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.message_list_view, container, false);
		MessageListHelper helper = new MessageListHelper(getActivity(), view, inflater, AtmosUrl.GLOBAL_TIMELINE_METHOD);
		return helper.createListView();
	}

}
