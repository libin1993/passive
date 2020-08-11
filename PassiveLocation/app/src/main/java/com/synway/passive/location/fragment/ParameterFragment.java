package com.synway.passive.location.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.synway.passive.location.R;
import com.synway.passive.location.adapter.MainTabLayoutAdapter;
import com.synway.passive.location.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author：Libin on 2020/8/10 18:30
 * Email：1993911441@qq.com
 * Describe：
 */
public class ParameterFragment extends BaseFragment {
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.vp_parameter)
    ViewPager viewPager;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parameter, container, false);

        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        List<String> titleList = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        titleList.add("设备");
        titleList.add("目标");

        fragmentList.add(BluetoothFragment.newInstance());
        fragmentList.add(PhoneInfoFragment.newInstance());

        viewPager.setAdapter(new MainTabLayoutAdapter(getChildFragmentManager(),fragmentList, titleList));
        viewPager.setOffscreenPageLimit(titleList.size());
        tabLayout.setupWithViewPager(viewPager);

        TabLayout.Tab tab;
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tab = tabLayout.getTabAt(i);
            if (tab != null) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_tab_view, null);
                TextView textView = view.findViewById(R.id.tv_tab);
                textView.setText(titleList.get(i));
                tab.setCustomView(view);
            }
        }

        tab = tabLayout.getTabAt(0);
        if (tab != null  && tab.getCustomView() instanceof TextView) {
            ((TextView) tab.getCustomView()).setTextSize(22);
        }


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view instanceof TextView) {
                    ((TextView) view).setTextSize(22);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view instanceof TextView) {
                    ((TextView) view).setTextSize(15);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        selectItem(0);
    }

    /**
     * @param position 切换页面
     */
    public void selectItem(int position){
        viewPager.setCurrentItem(position);
    }


    public static ParameterFragment newInstance() {

        Bundle args = new Bundle();

        ParameterFragment fragment = new ParameterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
