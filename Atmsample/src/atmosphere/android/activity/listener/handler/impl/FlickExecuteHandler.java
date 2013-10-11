package atmosphere.android.activity.listener.handler.impl;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import atmosphere.android.activity.listener.handler.OnExecuteHandler;

public class FlickExecuteHandler implements OnExecuteHandler {

	ProgressBar baseProgressBar;
	LinearLayout overLayout;

	public FlickExecuteHandler(ProgressBar baseProgressBar, LinearLayout overLayout) {
		this.baseProgressBar = baseProgressBar;
		this.overLayout = overLayout;
	}

	@Override
	public void execute() {
		baseProgressBar.setVisibility(View.VISIBLE);
		baseProgressBar.setIndeterminate(true);
		overLayout.setVisibility(View.INVISIBLE);
	}

}
