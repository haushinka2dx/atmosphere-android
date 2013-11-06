package atmosphere.android.activity.view;

import interprism.atmosphere.android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import atmosphere.android.manager.AtmosPreferenceManager;

public class SelectAddUserAdapter extends BaseAdapter {

	private Activity activity;
	private List<String> userList;
	private Map<String, Boolean> selectableMap;

	public SelectAddUserAdapter(Activity activity) {
		this.activity = activity;
		this.userList = AtmosPreferenceManager.getUserList(activity);
		this.selectableMap = new HashMap<String, Boolean>();
		for (String userName : userList) {
			selectableMap.put(userName, false);
		}
	}

	@Override
	public int getCount() {
		return userList.size();
	}

	@Override
	public Object getItem(int position) {
		return userList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public List<String> getSelectableUsers() {
		List<String> selectableUsers = new ArrayList<String>();
		if (selectableMap != null) {
			for (String userName : selectableMap.keySet()) {
				if (selectableMap.get(userName)) {
					selectableUsers.add(userName);
				}
			}
		}
		return selectableUsers;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if (convertView != null) {
			view = convertView;
		} else {
			view = LayoutInflater.from(activity).inflate(R.layout.simple_checkbox, null);
		}

		final String userName = userList.get(position);

		CheckBox checkBox = (CheckBox) view.findViewById(R.id.CheckBox);
		checkBox.setText(userName);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				selectableMap.put(userName, isChecked);
			}
		});

		return view;
	}

}
