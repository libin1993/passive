package com.synway.passive.location.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.synway.passive.location.R;
import com.synway.passive.location.adapter.ManagePhoneNumberAdapter;
import com.synway.passive.location.base.BaseActivity;
import com.synway.passive.location.widget.AddPhoneNumberDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PhoneNumberManageActivity extends BaseActivity {
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.rvPhoneNumberList)
    RecyclerView rvPhoneNumberList;
    @BindView(R.id.btnAdd)
    Button btnAdd;

    private Unbinder unbinder;
    private ManagePhoneNumberAdapter phoneNumberAdapter;
    private AddPhoneNumberDialog addPhoneNumberDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_phone_number_list);
        unbinder = ButterKnife.bind(this);
        initVies();
    }

    private void initVies(){
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        phoneNumberAdapter = new ManagePhoneNumberAdapter(R.layout.rv_manage_phone_number_item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rvPhoneNumberList.setLayoutManager(linearLayoutManager);
        phoneNumberAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addPhoneNumberDialog == null){
                    addPhoneNumberDialog = new AddPhoneNumberDialog();
                }
                addPhoneNumberDialog.show(getSupportFragmentManager(),"addPhoneNumber");
            }
        });
    }
}
