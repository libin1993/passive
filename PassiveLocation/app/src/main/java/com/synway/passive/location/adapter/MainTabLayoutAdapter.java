package com.synway.passive.location.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;



import com.synway.passive.location.base.BaseFragment;

import java.util.List;

/**
 * Author：Libin on 2020/6/2 14:32
 * Email：1993911441@qq.com
 * Describe：
 */
public class MainTabLayoutAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private List<String> mTitles;


    public MainTabLayoutAdapter(FragmentManager fm, List<Fragment> list, List<String> titles) {
        super(fm);
        this.fragmentList = list;
        this.mTitles = titles;

    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }


    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }



}
