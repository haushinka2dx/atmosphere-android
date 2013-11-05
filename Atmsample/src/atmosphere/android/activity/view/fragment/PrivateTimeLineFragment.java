package atmosphere.android.activity.view.fragment;

import interprism.atmosphere.android.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import atmosphere.android.activity.helper.PrivateMessageListHelper;
import atmosphere.android.constant.AtmosUrl;

public class PrivateTimeLineFragment extends Fragment implements AtmosUrl {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.message_list_view, container, false);

		PrivateMessageListHelper helper = new PrivateMessageListHelper(getActivity(), view, inflater, PRIVATE_TIMELINE_METHOD);
		return helper.createListView();
	}
}
