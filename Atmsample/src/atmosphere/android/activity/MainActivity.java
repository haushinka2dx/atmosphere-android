package atmosphere.android.activity;

import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import atmosphere.android.activity.helper.MessageListHelper;
import atmosphere.android.constant.AtmosUrl;
import atmosphere.android.dto.SendMessageRequest;
import atmosphere.android.dto.SendMessageResult;
import atmosphere.android.dto.WhoAmIResult;
import atmosphere.android.manager.AtmosPreferenceManager;
import atmosphere.android.util.internet.JsonPath;
import atmosphere.android.util.json.AtmosTask;
import atmosphere.android.util.json.AtmosTask.LoginResultHandler;
import atmosphere.android.util.json.AtmosTask.RequestMethod;
import atmosphere.android.util.json.AtmosTask.ResultHandler;
import atmsample.android.R;

public class MainActivity extends FragmentActivity implements AtmosUrl {

	private ActionBarDrawerToggle drawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final FragmentActivity activity = this;
		new AtmosTask.Builder<WhoAmIResult>(this, WhoAmIResult.class, RequestMethod.GET).resultHandler(new ResultHandler<WhoAmIResult>() {
			@Override
			public void handleResult(List<WhoAmIResult> results) {
				if (results != null && !results.isEmpty()) {
					MessageListHelper.initialize(activity, getViewPager(), getPagerTabStrip());
				}
			}
		}).loginHandler(new LoginResultHandler() {
			@Override
			public void handleResult() {
				MessageListHelper.initialize(activity, getViewPager(), getPagerTabStrip());
			}
		}).build().execute(JsonPath.paramOf(BASE_URL + USER_WHO_AM_I_METHOD, null));

		drawerToggle = new ActionBarDrawerToggle(this, getDrawer(), R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View drawerView) {
				initSubmitButton();
				Log.i("MainActivity", "onDrawerClosed");
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				Log.i("MainActivity", "onDrawerOpened");
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				// ActionBarDrawerToggleクラス内の同メソッドにてアイコンのアニメーションの処理をしている。
				// overrideするときは気を付けること。
				super.onDrawerSlide(drawerView, slideOffset);
				Log.i("MainActivity", "onDrawerSlide : " + slideOffset);
			}

			@Override
			public void onDrawerStateChanged(int newState) {
				// 表示済み、閉じ済みの状態：0
				// ドラッグ中状態:1
				// ドラッグを放した後のアニメーション中：2
				if (newState == 2) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getDrawer().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
				Log.i("MainActivity", "onDrawerStateChanged  new state : " + newState);
			}

		};

		getDrawer().setDrawerListener(drawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		initSubmitButton();
	}

	private void initSubmitButton() {
		getSendMessageEditText().setText("");
		Button submitButton = getSubmitButton();
		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SendMessageRequest param = new SendMessageRequest();
				String message = getSendMessageEditText().getText().toString();
				param.message = message;
				if (message != null && message.length() != 0) {
					sendMessage(param);
				}
			}
		});
	}

	private void sendMessage(SendMessageRequest param) {
		new AtmosTask.Builder<SendMessageResult>(this, SendMessageResult.class, RequestMethod.POST).progressMessage("Sending").resultHandler(new ResultHandler<SendMessageResult>() {
			@Override
			public void handleResult(List<SendMessageResult> results) {
				if (results != null && !results.isEmpty() && results.get(0).status.equals("ok")) {
					getSendMessageEditText().setText("");
					getDrawer().closeDrawers();
				}
			}
		}).loginHandler(new LoginResultHandler() {
			@Override
			public void handleResult() {
				getSendMessageEditText().setText("");
				getDrawer().closeDrawers();
			}
		}).build().ignoreDialog(false).execute(JsonPath.paramOf(BASE_URL + SEND_MESSAGE_METHOD, param));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.add(Menu.NONE, 0, Menu.NONE, "Defalt");
		menu.add(Menu.NONE, 1, Menu.NONE, "Hirano View");
		return true;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		AtmosPreferenceManager.setViewTheme(this, item.getItemId());

		// ActionBarDrawerToggleにandroid.id.home(up ナビゲーション)を渡す。
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		ListView detailListView = getDetailListView();
		if (detailListView.getVisibility() == View.VISIBLE && keyCode == KeyEvent.KEYCODE_BACK) {
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
			detailListView.startAnimation(animation);
			detailListView.setVisibility(View.GONE);

			ViewPager pager = getViewPager();
			Animation outAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
			pager.startAnimation(outAnimation);
			pager.setVisibility(View.VISIBLE);

			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	protected ViewPager getViewPager() {
		return (ViewPager) findViewById(R.id.ViewPager);
	}

	protected PagerTabStrip getPagerTabStrip() {
		return (PagerTabStrip) findViewById(R.id.PagerTabStrip);
	}

	protected DrawerLayout getDrawer() {
		return (DrawerLayout) findViewById(R.id.Drawer);
	}

	protected EditText getSendMessageEditText() {
		return (EditText) findViewById(R.id.SendMessageEditText);
	}

	protected Button getSubmitButton() {
		return (Button) findViewById(R.id.SubmitButton);
	}

	protected ListView getDetailListView() {
		return (ListView) findViewById(R.id.detali_message_list);
	}
}
