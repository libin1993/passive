package com.synway.passive.location.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.synway.passive.location.R;
import com.synway.passive.location.base.BaseFragment;
import com.synway.passive.location.socket.BluetoothSocketUtils;
import com.synway.passive.location.socket.LteSendManager;
import com.synway.passive.location.socket.MsgType;
import com.synway.passive.location.ui.MainActivity;
import com.synway.passive.location.ui.PhoneNumberManageActivity;
import com.synway.passive.location.utils.CacheManager;
import com.synway.passive.location.utils.FormatUtils;
import com.synway.passive.location.utils.LoadingUtils;
import com.synway.passive.location.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    @BindView(R.id.clToManagePhoneNumberList)
    RelativeLayout clToManagePhoneNumberList;

    private boolean searchSuccess = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_info, container, false);

        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
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
        clToManagePhoneNumberList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PhoneNumberManageActivity.class);
                getParentFragment().getActivity().startActivity(intent);
            }
        });
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



    @OnClick(R.id.btn_location)
    public void onViewClicked() {
        if (BluetoothSocketUtils.getInstance().isConnected()){
            ToastUtils.getInstance().showToast("请先连接蓝牙");
            return;
        }
        LoadingUtils.getInstance().showLoading(getParentFragment().getActivity(),"定位中");
        CacheManager.cellMap.clear();
        searchSuccess = false;
        final String phoneNumber = etPhoneNumber.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() !=11){
            ToastUtils.getInstance().showToast("请输入11位手机号");
            return;
        }
        CacheManager.phoneNumber = phoneNumber;

        String lac = etPhoneLac.getText().toString().trim();
        if (TextUtils.isEmpty(lac)){
            ToastUtils.getInstance().showToast("请输入LAC");
            return;
        }
        CacheManager.lac = lac;


        String cid = etPhoneCid.getText().toString().trim();
        if (TextUtils.isEmpty(cid)){
            ToastUtils.getInstance().showToast("请输入CID");
            return;
        }
        CacheManager.isLocation = false;
        CacheManager.cid =cid;
        int vendor = tabLayoutStandard.getSelectedTabPosition()+1;

        int searchMode = 0;
        int[] fcnArray = FormatUtils.getInstance().getDefaultFcn(vendor);

        LteSendManager.searchCell(vendor,phoneNumber,searchMode,fcnArray,"","");

        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (searchSuccess){
                    cancel();
                    LteSendManager.sendData(MsgType.SEND_LOCATION_CMD);
                }
            }

            @Override
            public void onFinish() {
                LoadingUtils.getInstance().dismiss();
                if (!searchSuccess){
                    if (CacheManager.cellMap.size() <= 0){
                        ToastUtils.getInstance().showToast("未搜索到小区");
                        return;
                    }
                    DetectFailedDialog detectFailedDialog = new DetectFailedDialog();
                    detectFailedDialog.setCancelable(false);
                    detectFailedDialog.show(getChildFragmentManager(),"");
                }
            }
        }.start();



    }

    /**
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void scanResult(String searchResult) {
       if ("searchSuccess".equals(searchResult)){
           searchSuccess = true;
       }else if ("research".equals(searchResult)){
           onViewClicked();
       }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

}
