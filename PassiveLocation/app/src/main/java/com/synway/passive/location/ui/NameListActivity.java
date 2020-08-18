package com.synway.passive.location.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.synway.passive.location.R;
import com.synway.passive.location.base.BaseActivity;
import com.synway.passive.location.bean.NameListBean;
import com.synway.passive.location.greendao.SQLiteUtils;
import com.synway.passive.location.socket.MsgType;
import com.synway.passive.location.utils.CacheManager;
import com.synway.passive.location.utils.LoadingUtils;
import com.synway.passive.location.utils.StatusBarUtils;
import com.synway.passive.location.widget.AddPhoneNumberDialog;
import com.synway.passive.location.widget.CustomPopupWindow;
import com.synway.passive.location.widget.RVDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.NameList;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author：Libin on 2020/8/18 10:32
 * Email：1993911441@qq.com
 * Describe：
 */
public class NameListActivity extends BaseActivity {
    @BindView(R.id.view_status_bar)
    View viewStatusBar;
    @BindView(R.id.tv_title_name)
    TextView tvTitleName;
    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.rv_name_list)
    RecyclerView rvNameList;
    @BindView(R.id.btn_add_account)
    Button btnAddAccount;

    private List<NameListBean> nameList = new ArrayList<>();
    private BaseQuickAdapter<NameListBean, BaseViewHolder> adapter;

    private int flag = 0; //是否从目标页面跳转过来

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_list);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
        initData();
    }

    private void initData() {
        nameList.clear();
        List<NameListBean> nameListBeans = SQLiteUtils.getInstance().queryAllNameList();
        nameList.addAll(nameListBeans);
        adapter.notifyDataSetChanged();

    }

    private void initView() {
        StatusBarUtils.getInstance().setStatusBarHeight(viewStatusBar);
        tvTitleName.setText("名单管理");

        flag = getIntent().getIntExtra("flag",0);

        rvNameList.setLayoutManager(new LinearLayoutManager(this));
        rvNameList.addItemDecoration(new RVDividerItemDecoration(this, 0,
                R.drawable.rv_divider_black_horrizontal));
        adapter = new BaseQuickAdapter<NameListBean, BaseViewHolder>(R.layout.layout_name_list_item, nameList) {
            @Override
            protected void convert(BaseViewHolder helper, NameListBean item) {
                helper.setText(R.id.tv_target_name, item.getName());
                helper.setText(R.id.tv_target_phone, item.getPhone());
                helper.setText(R.id.tv_phone_vendor, CacheManager.vendorArr[item.getVendor() - 1]);
                helper.setText(R.id.tv_target_remark, item.getRemark());

                helper.addOnClickListener(R.id.tv_edit_number);
                helper.addOnClickListener(R.id.tv_delete_number);
                helper.addOnClickListener(R.id.ll_name_list_info);
            }
        };
        rvNameList.setAdapter(adapter);



        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.tv_edit_number:
                        editNameList(nameList.get(position));
                        break;
                    case R.id.tv_delete_number:
                        deleteNameList(nameList.get(position));
                        break;
                    case R.id.ll_name_list_info:
                        if (flag == 1){
                            EventBus.getDefault().post(nameList.get(position));
                            finish();
                        }
                        break;
                }
            }


        });
    }

    private void deleteNameList(final NameListBean nameListBean) {

        new CustomPopupWindow(this)
                .setTitle("删除手机号")
                .setContent("您确定要删除该号码吗？")
                .setOnConfirmListener(new CustomPopupWindow.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        SQLiteUtils.getInstance().deleteNameList(nameListBean);
                        initData();
                    }
                })
                .show();

    }

    private void editNameList(NameListBean nameListBean){
        AddPhoneNumberDialog dialog = AddPhoneNumberDialog.newInstance(nameListBean);
        dialog.show(getSupportFragmentManager(),"editNameList");
    }

    @OnClick({R.id.iv_title_back, R.id.btn_add_account})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.btn_add_account:
                editNameList(null);
                break;
        }
    }

    /**
     * 检测成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void editSuccess(String result) {
        if (MsgType.QUERY_NAME_LIST.equals(result)) {
            initData();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
