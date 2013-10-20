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

	private static final class TabInfo {
		private final Class<?> clazz;
		private final Bundle args;
		private final int tabTitleId;

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

	public void addTab(Class<?> clazz, int tabTitleId, Bundle args) {
		TabInfo info = new TabInfo(clazz, args, tabTitleId);
		mTabs.add(info);
		notifyDataSetChanged();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mContext.getString(mTabs.get(position).tabTitleId);
	}

	@Override
	public int getCount() {
		return mTabs.size();
	}

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
