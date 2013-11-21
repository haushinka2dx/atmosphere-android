package atmosphere.android.activity;

import interprism.atmosphere.android.R;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import atmosphere.android.activity.helper.MenuToolipAddHelper;
import atmosphere.android.activity.helper.SendMessageHelper;
import atmosphere.android.activity.view.MessagePagerAdapter;
import atmosphere.android.activity.view.fragment.GlobalTimeLineFragment;
import atmosphere.android.activity.view.fragment.PrivateTimeLineFragment;
import atmosphere.android.activity.view.fragment.TalkTimeLineFragment;
import atmosphere.android.constant.AtmosUrl;
import atmosphere.android.dto.UserListResult;
import atmosphere.android.manager.AtmosPreferenceManager;
import atmosphere.android.util.Tooltip;
import atmosphere.android.util.internet.JsonPath;
import atmosphere.android.util.json.AtmosTask;
import atmosphere.android.util.json.AtmosTask.LoginResultHandler;
import atmosphere.android.util.json.AtmosTask.RequestMethod;
import atmosphere.android.util.json.AtmosTask.ResultHandler;

public class MainActivity extends FragmentActivity {

	private ActionBarDrawerToggle drawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final Activity activity = this;

		init(activity);

		drawerToggle = new ActionBarDrawerToggle(this, getDrawer(), R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View drawerView) {
				SendMessageHelper.initSubmitButton(activity);
				SendMessageHelper.initSubmitPrivateButton(activity);
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

		getGlobalReplyShowView().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getDrawer().openDrawer(GravityCompat.START);
			}
		});

		getPrivateReplyShowView().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getDrawer().openDrawer(GravityCompat.END);
			}
		});

		if (AtmosPreferenceManager.getViewTheme(this) == 1) {
			getReplyButtonLayout().setVisibility(View.VISIBLE);
		} else {
			getReplyButtonLayout().setVisibility(View.GONE);
		}

		final ImageButton addButton = getAddButton();
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Tooltip tooltip = MenuToolipAddHelper.createAddMenuTooltip(activity, getSendMessageEditText());
				tooltip.showBottom(addButton);
			}
		});

		final ImageButton privateAddButton = getPrivateAddButton();
		privateAddButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Tooltip tooltip = MenuToolipAddHelper.createAddMenuTooltip(activity, getSendPrivateToUserEditText());
				tooltip.showBottom(privateAddButton);
			}
		});

		SendMessageHelper.initSubmitButton(activity);
		SendMessageHelper.initSubmitPrivateButton(activity);
	}

	private void init(final Activity activity) {
		new AtmosTask.Builder<UserListResult>(this, UserListResult.class, RequestMethod.GET).resultHandler(new ResultHandler<UserListResult>() {
			@Override
			public void handleResult(List<UserListResult> results) {
				if (results != null && !results.isEmpty()) {
					AtmosPreferenceManager.setUserList(activity, results.get(0));
					messagesInitialize();
					goneTitleView(activity);
				}
			}
		}).loginHandler(new LoginResultHandler() {
			@Override
			public void handleResult() {
				init(activity);
			}
		}).build().ignoreDialog(true).execute(JsonPath.paramOf(AtmosUrl.BASE_URL + AtmosUrl.USER_LIST_METHOD, null));
	}

	private void goneTitleView(Activity activity) {
		LinearLayout titleView = getTitleViewOverlay();
		Animation outAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_out);
		outAnimation.setDuration(2000);
		titleView.startAnimation(outAnimation);
		titleView.setVisibility(View.GONE);
	}

	private void messagesInitialize() {
		ViewPager pager = getViewPager();
		MessagePagerAdapter adapter = new MessagePagerAdapter(this, pager);

		adapter.addTab(GlobalTimeLineFragment.class, R.string.global_timeline_title);
		adapter.addTab(TalkTimeLineFragment.class, R.string.talk_timeline_title);
		adapter.addTab(PrivateTimeLineFragment.class, R.string.private_timeline_title);

		pager.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.add(Menu.NONE, 0, Menu.NONE, "Default");
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

		LinearLayout detailOverlay = getDetailOverlay();
		if (detailOverlay.getVisibility() == View.VISIBLE && keyCode == KeyEvent.KEYCODE_BACK) {
			detailOverlay.setVisibility(View.GONE);
		}

		LinearLayout secretOvarlay = getSecretOvarlay();
		LinearLayout onlyUserOverlay = getOnlyUserOverlay();
		ListView detailListView = getDetailListView();
		if (secretOvarlay.getVisibility() == View.VISIBLE && keyCode == KeyEvent.KEYCODE_BACK) {
			closeTargetOverlay(secretOvarlay);
			return true;
		} else if (onlyUserOverlay.getVisibility() == View.VISIBLE && keyCode == KeyEvent.KEYCODE_BACK) {
			closeTargetOverlay(onlyUserOverlay);
			return true;
		} else if (detailListView.getVisibility() == View.VISIBLE && keyCode == KeyEvent.KEYCODE_BACK) {
			closeTargetOverlay(detailListView);
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private void closeTargetOverlay(View targetView) {
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
		targetView.startAnimation(animation);
		targetView.setVisibility(View.GONE);

		LinearLayout mainOverlay = getMainOverlay();
		Animation outAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
		mainOverlay.startAnimation(outAnimation);
		mainOverlay.setVisibility(View.VISIBLE);
	}

	protected LinearLayout getMainOverlay() {
		return (LinearLayout) findViewById(R.id.main_overlay);
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

	protected ImageButton getAddButton() {
		return (ImageButton) findViewById(R.id.AddButton);
	}

	protected EditText getSendPrivateToUserEditText() {
		return (EditText) findViewById(R.id.SendPrivateToUserEditText);
	}

	protected ImageButton getPrivateAddButton() {
		return (ImageButton) findViewById(R.id.PrivateAddButton);
	}

	protected ListView getDetailListView() {
		return (ListView) findViewById(R.id.detail_message_list);
	}

	protected LinearLayout getDetailOverlay() {
		return (LinearLayout) findViewById(R.id.detail_message_list_overlay);
	}

	protected LinearLayout getReplyButtonLayout() {
		return (LinearLayout) findViewById(R.id.reply_button_layout);
	}

	protected ImageView getGlobalReplyShowView() {
		return (ImageView) findViewById(R.id.global_reply_button);
	}

	protected ImageView getPrivateReplyShowView() {
		return (ImageView) findViewById(R.id.private_reply_button);
	}

	protected LinearLayout getTitleViewOverlay() {
		return (LinearLayout) findViewById(R.id.title_overlay);
	}

	protected LinearLayout getSecretOvarlay() {
		return (LinearLayout) findViewById(R.id.secret_overlay);
	}

	protected LinearLayout getOnlyUserOverlay() {
		return (LinearLayout) findViewById(R.id.only_user_overlay);
	}
}
