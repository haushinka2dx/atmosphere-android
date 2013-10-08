package atmosphere.android.activity.view;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

public class MessagePagerAdapter extends FragmentPagerAdapter {

	private final Context mContext;
	private final ViewPager mViewPager;
	private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

	/**
	 * タブ内の表示するv4.Fragment、引数、タイトルの保持を行う
	 */
	private static final class TabInfo {
		private final Class<?> clazz;
		private final Bundle args;
		private final int tabTitleId;

		/**
		 * タブ内のActivity、引数を設定する。
		 * 
		 * @param clazz
		 *            タブ内のv4.Fragment
		 * @param args
		 *            タブ内のv4.Fragmentに対する引数
		 * @param tabTitle
		 *            タブに表示するタイトル
		 */
		TabInfo(Class<?> clazz, Bundle args, int tabTitleId) {
			this.clazz = clazz;
			this.args = args;
			this.tabTitleId = tabTitleId;
		}
	}

	public MessagePagerAdapter(FragmentActivity activity, ViewPager pager) {
		super(activity.getSupportFragmentManager());
		mContext = activity;
		mViewPager = pager;
		mViewPager.setAdapter(this);
	}

	public void addTab(Class<?> clazz, int titleId) {
		addTab(clazz, titleId, null);
	}

	/**
	 * タブ内に起動するActivity、引数、タイトルを設定する
	 * 
	 * @param clazz
	 *            起動するv4.Fragmentクラス
	 * @param args
	 *            v4.Fragmentに対する引数
	 * @param tabTitleId
	 *            タブのタイトル
	 */
	public void addTab(Class<?> clazz, int tabTitleId, Bundle args) {
		TabInfo info = new TabInfo(clazz, args, tabTitleId);
		mTabs.add(info);
		notifyDataSetChanged();
	}

	/**
	 * タイトルを返す。 フレームワークからの呼び出しがされるため、任意に呼び出す必要はない。
	 */
	@Override
	public CharSequence getPageTitle(int position) {
		return mContext.getString(mTabs.get(position).tabTitleId);
	}

	/**
	 * タブの総数を返す。 フレームワークからの呼び出しがされるため、任意に呼び出す必要はない。
	 */
	@Override
	public int getCount() {
		return mTabs.size();
	}

	/**
	 * タブ内のv4.Fragmentを返す。 フレームワークからの呼び出しがされるため、任意に呼び出す必要はない。
	 */
	@Override
	public Fragment getItem(int position) {
		TabInfo info = mTabs.get(position);
		return Fragment.instantiate(mContext, info.clazz.getName(), info.args);
	}

	@Override
	public void finishUpdate(ViewGroup container) {
		super.finishUpdate(container);
	}
}
