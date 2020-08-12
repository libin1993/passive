package com.synway.passive.location.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.synway.passive.location.R;
import com.synway.passive.location.base.BaseFragment;
import com.synway.passive.location.socket.LteSendManager;
import com.synway.passive.location.socket.MsgType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Author：Libin on 2020/8/10 18:34
 * Email：1993911441@qq.com
 * Describe：
 */
public class PhoneInfoFragment extends BaseFragment {
    Unbinder unbinder;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_phone_number)
    EditText etPhoneNumber;
    @BindView(R.id.et_phone_lac)
    EditText etPhoneLac;
    @BindView(R.id.et_phone_cid)
    EditText etPhoneCid;
    @BindView(R.id.tab_layout_standard)
    TabLayout tabLayoutStandard;
    @BindView(R.id.tab_layout_fcn)
    TabLayout tabLayoutFcn;
    @BindView(R.id.btn_location)
    Button btnLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_info, container, false);

        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        List<String> standardList = new ArrayList<>();
        standardList.add("移动");
        standardList.add("联通");
        standardList.add("电信");



        List<String> fcnList = new ArrayList<>();
        fcnList.add("智能");
        fcnList.add("手动");


        initTabLayout(tabLayoutStandard,standardList);
        initTabLayout(tabLayoutFcn,fcnList);

    }

    private void initTabLayout(TabLayout tabLayout,List<String> titleList){
        for (String title : titleList) {
            tabLayout.addTab(tabLayout.newTab().setText(title));
        }


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
            ((TextView) tab.getCustomView()).setTextSize(19);
        }


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view instanceof TextView) {
                    ((TextView) view).setTextSize(19);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view instanceof TextView) {
                    ((TextView) view).setTextSize(14);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    public static PhoneInfoFragment newInstance() {

        Bundle args = new Bundle();

        PhoneInfoFragment fragment = new PhoneInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_location)
    public void onViewClicked() {

        int[] fcn = new int[]{37900,38098,38350};
        LteSendManager.searchCell(1,"15167168495",1,fcn,"","");

        //增益
        LteSendManager.setPower((byte) 0);

        LteSendManager.sendData(MsgType.SEND_SERVER_HEART_BEAT);
        LteSendManager.sendData(MsgType.SEND_SHOW_VERSION);
    }
}
