package atmosphere.android.activity.listener.handler.impl;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import atmosphere.android.activity.listener.handler.OnFlickHandler;

public class FlickHiranoHandler implements OnFlickHandler {
	ProgressBar baseProgressBar;
	LinearLayout overLayout;
	ListView listView;

	public FlickHiranoHandler(ProgressBar baseProgressBar, LinearLayout overLayout, ListView listView) {
		this.baseProgressBar = baseProgressBar;
		overLayout.setVisibility(View.INVISIBLE);
		this.listView = listView;
	}

	@Override
	public void controlMoving(int limit, float difference) {
		baseProgressBar.setVisibility(View.INVISIBLE);
		listView.setRotationX(30 * (difference / limit));
	}

	@Override
	public void compleated() {
		baseProgressBar.setVisibility(View.VISIBLE);
		listView.setRotationX(0);
	}

	@Override
	public void notCompleated() {
		baseProgressBar.setVisibility(View.INVISIBLE);
		listView.setRotationX(0);
	}
}
