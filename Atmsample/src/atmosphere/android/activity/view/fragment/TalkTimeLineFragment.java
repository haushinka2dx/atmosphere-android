package atmosphere.android.activity.view.fragment;

import interprism.atmosphere.android.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import atmosphere.android.activity.helper.MessageListHelper;
import atmosphere.android.constant.AtmosUrl;

public class TalkTimeLineFragment extends Fragment implements AtmosUrl {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.message_list_view, container, false);

		return MessageListHelper.createListView(getActivity(), view, inflater, TALK_TIMELINE_METHOD);
	}

}
