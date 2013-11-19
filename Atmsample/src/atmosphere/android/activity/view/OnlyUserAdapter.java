package atmosphere.android.activity.view;

import interprism.atmosphere.android.R;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import atmosphere.android.dto.MessageDto;
import atmosphere.android.util.TimeUtil;

public class OnlyUserAdapter extends MessageBaseAdapter {

	private Activity activity;

	public OnlyUserAdapter(Activity activity, List<MessageDto> list) {
		super(list);
		this.activity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View view;
		if (convertView != null) {
			view = convertView;
		} else {
			LayoutInflater inflater = LayoutInflater.from(activity);
			view = inflater.inflate(R.layout.only_user_message, parent, false);
		}

		final MessageDto data = list.get(position);

		TextView create_at = (TextView) view.findViewById(R.id.only_user_message_time);
		create_at.setText(TimeUtil.formaFulltDateFromGMT(data.created_at));

		TextView message = (TextView) view.findViewById(R.id.only_user_message_timeline);
		message.setText(data.message);

		TextView funTextView = (TextView) view.findViewById(R.id.only_user_fun_text_view);
		setResponseCount(funTextView, data.responses.fun.size());

		TextView goodTextView = (TextView) view.findViewById(R.id.only_user_good_text_view);
		setResponseCount(goodTextView, data.responses.good.size());

		TextView memoTextView = (TextView) view.findViewById(R.id.only_user_memo_text_view);
		setResponseCount(memoTextView, data.responses.memo.size());

		TextView usefullTextView = (TextView) view.findViewById(R.id.only_user_usefull_text_view);
		setResponseCount(usefullTextView, data.responses.usefull.size());

		return view;
	}

	private void setResponseCount(TextView targetTextView, int count) {
		targetTextView.setText(String.valueOf(count));
	}

}
