package atmosphere.android.activity.helper;

import interprism.atmosphere.android.R;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import atmosphere.android.activity.view.SelectAddUserAdapter;
import atmosphere.android.util.Tooltip;

public class AddMenuTooltipHelper {

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
					View usersView = LayoutInflater.from(activity).inflate(R.layout.select_add_users_dialog, null);
					ListView usersListView = (ListView) usersView.findViewById(R.id.add_users_list);

					final SelectAddUserAdapter selectAdapter = new SelectAddUserAdapter(activity);
					usersListView.setAdapter(selectAdapter);

					DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
					int dialogWidth = (int) (metrics.widthPixels * 0.8);
					int dialogHeight = (int) (metrics.heightPixels * 0.6);

					final Dialog dialog = new Dialog(activity);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(usersView);

					WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
					lp.width = dialogWidth;
					lp.height = dialogHeight;
					dialog.getWindow().setAttributes(lp);

					Button okButton = (Button) usersView.findViewById(R.id.ok_button);
					okButton.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							String text = textView.getText().toString();
							StringBuilder sb = new StringBuilder();
							for (String userName : selectAdapter.getSelectableUsers()) {
								sb.append("@");
								sb.append(userName);
								sb.append(" ");
							}
							textView.setText((text + sb.toString()));
							dialog.dismiss();
						}
					});

					Button cancelButton = (Button) usersView.findViewById(R.id.cancel_button);
					cancelButton.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});

					dialog.show();
				}
			}
		});

		return tooltip;
	}

}
