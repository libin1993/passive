package com.synway.passive.location.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hrst.sdk.HrstSdkCient;
import com.hrst.sdk.callback.RequestCallBack;
import com.hrst.sdk.dto.request.CellSearchRequest;
import com.orhanobut.logger.Logger;
import com.synway.passive.location.R;
import com.synway.passive.location.base.BaseFragment;
import com.synway.passive.location.bean.CellBean;
import com.synway.passive.location.bean.NameListBean;
import com.synway.passive.location.greendao.SQLiteUtils;
import com.synway.passive.location.socket.BluetoothSocketUtils;
import com.synway.passive.location.socket.LteSendManager;
import com.synway.passive.location.socket.MsgType;
import com.synway.passive.location.ui.NameListActivity;
import com.synway.passive.location.utils.CacheManager;
import com.synway.passive.location.utils.FormatUtils;
import com.synway.passive.location.utils.LoadingUtils;
import com.synway.passive.location.utils.LogUtils;
import com.synway.passive.location.utils.ToastUtils;
import com.synway.passive.location.widget.DetectFailedDialog;
import com.synway.passive.location.widget.MyCountDownTimer;
import com.synway.passive.location.widget.RVDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
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
    @BindView(R.id.clToManagePhoneNumberList)
    RelativeLayout clToManagePhoneNumberList;
    @BindView(R.id.rv_similar_number)
    RecyclerView rvNumber;
    @BindView(R.id.et_fcn1)
    EditText etFcn1;
    @BindView(R.id.et_fcn2)
    EditText etFcn2;
    @BindView(R.id.et_fcn3)
    EditText etFcn3;
    @BindView(R.id.ll_fcn)
    LinearLayout llFcn;
    @BindView(R.id.rl_root)
    RelativeLayout rlRoot;


    private List<NameListBean> nameList = new ArrayList<>();
    private BaseQuickAdapter<NameListBean, BaseViewHolder> adapter;

    private MyCountDownTimer myCountDownTimer;

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


        initTabLayout(1, tabLayoutStandard, standardList);
        initTabLayout(2, tabLayoutFcn, fcnList);
        clToManagePhoneNumberList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NameListActivity.class);
                intent.putExtra("flag", 1);
                getParentFragment().getActivity().startActivity(intent);
            }
        });

        etPhoneNumber.addTextChangedListener(textWatcher);


        rvNumber.setLayoutManager(new LinearLayoutManager(getParentFragment().getActivity()));
        rvNumber.addItemDecoration(new RVDividerItemDecoration(getParentFragment().getActivity()));
        adapter = new BaseQuickAdapter<NameListBean, BaseViewHolder>(R.layout.layout_phone_number_item, nameList) {
            @Override
            protected void convert(BaseViewHolder helper, NameListBean item) {
                helper.setText(R.id.tv_similar_number, item.getPhone());
            }
        };
        rvNumber.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                rvNumber.setVisibility(View.GONE);
                selectResult(nameList.get(position));
            }
        });


    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String number = s.toString().trim();
            if (!TextUtils.isEmpty(number)) {

                if (number.length() == 11) {
                    int vendor = FormatUtils.getInstance().isPhoneNumber(number);
                    tabLayoutStandard.getTabAt(vendor - 1).select();
                }

                similarNumber(number);
            } else {
                rvNumber.setVisibility(View.GONE);
            }

        }
    };

    /**
     * @param number 号码输入提示
     */
    private void similarNumber(String number) {
        nameList.clear();
        List<NameListBean> nameListBeans = SQLiteUtils.getInstance().querySimilarNumber(number);
        if (nameListBeans != null && nameListBeans.size() > 0) {
            rvNumber.setVisibility(View.VISIBLE);
            nameList.addAll(nameListBeans);
        } else {
            rvNumber.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();

    }

    private void initTabLayout(final int type, TabLayout tabLayout, List<String> titleList) {
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
        if (tab != null && tab.getCustomView() instanceof TextView) {
            ((TextView) tab.getCustomView()).setTextSize(19);
        }


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view instanceof TextView) {
                    ((TextView) view).setTextSize(19);
                }

                if (type == 1) {
                    CacheManager.vendor = tab.getPosition() + 1;
                } else {
                    if (tabLayoutFcn.getSelectedTabPosition() == 1) {
                        llFcn.setVisibility(View.VISIBLE);
                    } else {
                        llFcn.setVisibility(View.GONE);
                    }
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

    /**
     * 搜索小区
     */
    private void searchCell() {
        if (!BluetoothSocketUtils.getInstance().isConnected()) {
            ToastUtils.getInstance().showToast("请先连接蓝牙");
            return;
        }

        String phoneNumber = etPhoneNumber.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() != 11) {
            ToastUtils.getInstance().showToast("请输入11位手机号");
            return;
        }
        CacheManager.phoneNumber = phoneNumber;

        String lac = etPhoneLac.getText().toString().trim();
        if (TextUtils.isEmpty(lac)) {
            ToastUtils.getInstance().showToast("请输入LAC");
            return;
        }
        CacheManager.lac = lac;


        String cid = etPhoneCid.getText().toString().trim();
        if (TextUtils.isEmpty(cid)) {
            ToastUtils.getInstance().showToast("请输入CID");
            return;
        }
        CacheManager.cid = cid;

        CacheManager.isSearchCell = false;
        CacheManager.isLocation = false;

        int vendor = tabLayoutStandard.getSelectedTabPosition() + 1;
        CacheManager.vendor = vendor;

        int searchMode = tabLayoutFcn.getSelectedTabPosition();
        int[] fcnArray;
        if (searchMode == 0) {
            fcnArray = FormatUtils.getInstance().getDefaultFcn(CacheManager.is5G, vendor);
        } else {
            String fcn1 = etFcn1.getText().toString().trim();
            String fcn2 = etFcn2.getText().toString().trim();
            String fcn3 = etFcn3.getText().toString().trim();

            List<Integer> fcnList = new ArrayList<>();

            if (!TextUtils.isEmpty(fcn1) && Integer.parseInt(fcn1) > 0) {
                fcnList.add(Integer.parseInt(fcn1));
            }
            if (!TextUtils.isEmpty(fcn2) && Integer.parseInt(fcn2) > 0) {
                fcnList.add(Integer.parseInt(fcn2));
            }

            if (!TextUtils.isEmpty(fcn3) && Integer.parseInt(fcn3) > 0) {
                fcnList.add(Integer.parseInt(fcn3));
            }

            if (fcnList.size() > 0) {
                fcnArray = new int[fcnList.size()];
                for (int i = 0; i < fcnList.size(); i++) {
                    fcnArray[i] = fcnList.get(i);
                }
            } else {
                ToastUtils.getInstance().showToast("请输入频点");
                return;
            }

        }


        LoadingUtils.getInstance().showLoading(getParentFragment().getActivity(), "定位中");

        CacheManager.cellMap.clear();
//        if (CacheManager.isSearched()) {
//            LteSendManager.sendData(MsgType.SEND_LOCATION_CMD);
//            new CountDownTimer(10000, 1000) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//                    if (CacheManager.isLocation) {
//                        cancel();
//                    }
//                }
//
//                @Override
//                public void onFinish() {
//                    LoadingUtils.getInstance().dismiss();
//                    ToastUtils.getInstance().showToast("定位失败");
//                }
//            }.start();
//            return;
//        }

        NameListBean nameListBean = SQLiteUtils.getInstance().queryNameList(phoneNumber);   //插入名单
        if (nameListBean != null) {
            if (!nameListBean.getName().equals(etName.getText().toString().trim())) {
                nameListBean.setName(etName.getText().toString().trim());
                SQLiteUtils.getInstance().insertNameList(nameListBean);
            }
        } else {
            NameListBean nameBean = new NameListBean();
            nameBean.setName(etName.getText().toString().trim());
            nameBean.setPhone(phoneNumber);
            nameBean.setVendor(vendor);
            nameBean.setRemark("");

            SQLiteUtils.getInstance().insertNameList(nameBean);
        }

        //5G
        if (CacheManager.is5G) {
            String imsi;
            if (vendor == 1) {
                imsi = "460000000000000";
            } else if (vendor == 2) {
                imsi = "460010000000000";
            } else {
                imsi = "460030000000000";
            }
            CellSearchRequest request = new CellSearchRequest();
            request.setTargetImsi(imsi);
            request.setTargetNumber(phoneNumber);
            request.setVendor(vendor - 1);
            request.setSearchMode(searchMode);
            List<Long> fcnList = new ArrayList<>();
            for (int i : fcnArray) {
                fcnList.add((long) i);
            }
            request.setFreqs(fcnList);

            LogUtils.log(request.toString());

            HrstSdkCient.startCellSearch(request, new RequestCallBack<Integer>() {
                @Override
                public void onAck(Integer integer) {
                    LogUtils.log("搜索小区指令下发结果：" + integer);
                }
            });
        } else {
            LteSendManager.searchCell(vendor, phoneNumber, (byte) searchMode, fcnArray, "", "");
        }


        myCountDownTimer = new MyCountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                LoadingUtils.getInstance().dismiss();
                if (CacheManager.cellMap.size() <= 0) {
                    ToastUtils.getInstance().showToast("未搜索到小区");
                    return;
                }
                DetectFailedDialog detectFailedDialog = new DetectFailedDialog();
                detectFailedDialog.show(getChildFragmentManager(), "searchCell");
            }
        }.start();
    }


    /**
     *
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void searchResult(String result) {
        if (MsgType.SEARCH_SUCCESS.equals(result)) {
            if (myCountDownTimer != null) {
                myCountDownTimer.cancel();
                myCountDownTimer = null;
            }

            CellBean cellBean = CacheManager.getCell();
            if (CacheManager.is5G) {
                long[] cidArr = new long[]{cellBean.getCid()};
                long[] fcnArr = new long[]{cellBean.getFreq()};
                HrstSdkCient.lockMonitorCells(cidArr, fcnArr, new RequestCallBack<Boolean>() {
                    @Override
                    public void onAck(Boolean aBoolean) {
                        LogUtils.log("锁定小区指令下发结果：" + aBoolean);
                        HrstSdkCient.startTargetLocaion(new RequestCallBack<Boolean>() {
                            @Override
                            public void onAck(Boolean aBoolean) {
                                LogUtils.log("目标定位指令下发结果：" + aBoolean);
                                LoadingUtils.getInstance().dismiss();
                                if (aBoolean) {
                                    if (!CacheManager.isLocation) {
                                        EventBus.getDefault().post(MsgType.LOCATION_SUCCESS);
                                    }
                                    CacheManager.isLocation = true;
                                } else {
                                    EventBus.getDefault().post(MsgType.LOCATION_FAIL);
                                    CacheManager.isLocation = false;
                                }
                            }
                        });
                    }
                });
            } else {
                LteSendManager.lockCell(cellBean.getCid(), cellBean.getFreq());
            }


        } else if (MsgType.RESEARCH_CELL.equals(result)) {
            searchCell();
        } else if (MsgType.LOCATION_FAIL.equals(result)) {
            ToastUtils.getInstance().showToast("定位失败");
        }
    }

    /**
     *
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void selectResult(NameListBean nameListBean) {
        if (nameListBean != null) {
            etName.setText(nameListBean.getName());
            etPhoneNumber.removeTextChangedListener(textWatcher);
            etPhoneNumber.setText(nameListBean.getPhone());
            etPhoneNumber.setSelection(nameListBean.getPhone().length());
            etPhoneNumber.addTextChangedListener(textWatcher);
            tabLayoutStandard.getTabAt(nameListBean.getVendor() - 1).select();
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }


    @OnClick({R.id.btn_location, R.id.rl_root})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_location:
                searchCell();
                break;
            case R.id.rl_root:
                if (rvNumber.getVisibility() == View.VISIBLE) {
                    rvNumber.setVisibility(View.GONE);
                }
                break;
        }
    }
}
