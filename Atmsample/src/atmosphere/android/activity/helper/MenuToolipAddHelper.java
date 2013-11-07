package atmosphere.android.activity.helper;

import interprism.atmosphere.android.R;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import atmosphere.android.constant.AtmosConstant;
import atmosphere.android.manager.AtmosPreferenceManager;
import atmosphere.android.util.Tooltip;

public class MenuToolipAddHelper implements AtmosConstant{

	private enum Menu {
		AddToUser, ;
	}

	public static Tooltip createAddMenuTooltip(final Activity activity, final TextView textView) {
		View view = LayoutInflater.from(activity).inflate(R.layout.simple_list, null);
		final Tooltip tooltip = new Tooltip(activity, view);

		ListView listView = (ListView) view.findViewById(R.id.ListView);

		List<String> menuList = new ArrayList<String>();
		menuList.add(Menu.AddToUser.name());

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.simple_text, menuList);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				tooltip.dismiss();
				if (position == Menu.AddToUser.ordinal()) {
					createAddUsersList(activity, textView);
				}
			}
		});

		return tooltip;
	}

	private static void createAddUsersList(Activity activity, final TextView textView) {
		List<String> userList = AtmosPreferenceManager.getUserList(activity);
		final String[] users = new String[userList.size()];
		final boolean[] flags = new boolean[userList.size()];
		for (int i = 0; i < userList.size(); i++) {
			users[i] = userList.get(i);
			flags[i] = false;
		}
		new AlertDialog.Builder(activity).setTitle("Select To Users").setMultiChoiceItems(users, flags, new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				flags[which] = isChecked;
			}
		}).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String text = textView.getText().toString();
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < flags.length; i++) {
					if (flags[i]) {
						sb.append(AT_MARK);
						sb.append(users[i]);
						sb.append(SPACE);
					}
				}
				textView.setText((text + sb.toString()));
				dialog.dismiss();
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}

}
