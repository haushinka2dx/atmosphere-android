package atmosphere.android.activity.listener.handler.impl;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import atmosphere.android.activity.listener.handler.OnFlickHandler;

public class FlickHandler implements OnFlickHandler {
	ProgressBar baseProgressBar;
	LinearLayout overLayout;
	ProgressBar leftProgressBar;
	ProgressBar rightProgressBar;

	public FlickHandler(ProgressBar baseProgressBar, LinearLayout overLayout, ProgressBar leftProgressBar, ProgressBar rightProgressBar) {
		this.baseProgressBar = baseProgressBar;
		this.overLayout = overLayout;
		this.leftProgressBar = leftProgressBar;
		this.rightProgressBar = rightProgressBar;
	}

	@Override
	public void controlMoving(int limit, float difference) {
		baseProgressBar.setVisibility(View.INVISIBLE);
		overLayout.setVisibility(View.VISIBLE);
		leftProgressBar.setMax(limit);
		rightProgressBar.setMax(limit);

		leftProgressBar.setProgress((int) difference);
		rightProgressBar.setProgress((int) difference);

	}

	@Override
	public void compleated() {
		baseProgressBar.setVisibility(View.VISIBLE);
		overLayout.setVisibility(View.INVISIBLE);
		leftProgressBar.setProgress(0);
		rightProgressBar.setProgress(0);

	}

	@Override
	public void notCompleated() {
		baseProgressBar.setVisibility(View.INVISIBLE);
		overLayout.setVisibility(View.VISIBLE);
		leftProgressBar.setProgress(0);
		rightProgressBar.setProgress(0);
	}

}
