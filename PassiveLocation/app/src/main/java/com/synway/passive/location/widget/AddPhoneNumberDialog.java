package com.synway.passive.location.widget;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.synway.passive.location.R;
import com.synway.passive.location.bean.NameListBean;
import com.synway.passive.location.greendao.SQLiteUtils;
import com.synway.passive.location.socket.MsgType;
import com.synway.passive.location.utils.CacheManager;
import com.synway.passive.location.utils.FormatUtils;
import com.synway.passive.location.utils.SPUtils;
import com.synway.passive.location.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddPhoneNumberDialog extends DialogFragment {

    @BindView(R.id.et_target_name)
    EditText etTargetName;
    @BindView(R.id.et_target_phone)
    EditText etTargetPhone;
    @BindView(R.id.spinner_vendor)
    Spinner spinnerVendor;
    @BindView(R.id.et_target_remark)
    EditText etTargetRemark;
    @BindView(R.id.btn_cancel_edit)
    Button btnCancelEdit;
    @BindView(R.id.btn_edit_name_list)
    Button btnEditNameList;
    private Unbinder unbinder;

    private NameListBean nameListBean;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_phone_number, container, false);
        unbinder = ButterKnife.bind(this, view);
        intiView();
        return view;
    }

    private void intiView() {
        nameListBean = (NameListBean) getArguments().getSerializable("target");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_select_item);

        for (int i = 0; i < CacheManager.vendorArr.length; i++) {
            adapter.add(String.valueOf(CacheManager.vendorArr[i]));
        }
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerVendor.setAdapter(adapter);

        if (nameListBean != null) {
            etTargetName.setText(nameListBean.getName());
            etTargetPhone.setText(nameListBean.getPhone());
            etTargetRemark.setText(nameListBean.getRemark());
            spinnerVendor.setSelection(nameListBean.getVendor() - 1);
        }


        etTargetPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String number = s.toString().trim();
                if (!TextUtils.isEmpty(number) && number.length() == 11) {
                    int vendor = FormatUtils.getInstance().isPhoneNumber(number);
                    spinnerVendor.setSelection(vendor - 1);
                }
            }
        });

    }

    public static AddPhoneNumberDialog newInstance(NameListBean nameListBean) {

        Bundle args = new Bundle();

        AddPhoneNumberDialog fragment = new AddPhoneNumberDialog();
        if (nameListBean != null) {
            args.putSerializable("target", nameListBean);
        }
        fragment.setArguments(args);
        return fragment;
    }


    private void edit() {
        String name = etTargetName.getText().toString().trim();
        String phone = etTargetPhone.getText().toString().trim();
        String remark = etTargetRemark.getText().toString().trim();
        int vendor = spinnerVendor.getSelectedItemPosition() + 1;

        if (TextUtils.isEmpty(phone) || phone.length() != 11) {
            ToastUtils.getInstance().showToast("请输入11位手机号");
            return;
        }


        NameListBean nameList = SQLiteUtils.getInstance().queryNameList(phone);

        if (nameListBean !=null){

            if (nameList !=null && !nameListBean.getId().equals(nameList.getId())){
                ToastUtils.getInstance().showToast("该手机号已存在");
                return;
            }else {
                nameListBean.setName(name);
                nameListBean.setPhone(phone);
                nameListBean.setVendor(vendor);
                nameListBean.setRemark(remark);

                SQLiteUtils.getInstance().insertNameList(nameListBean);
            }
        }else {

            if (nameList !=null){
                ToastUtils.getInstance().showToast("该手机号已存在");
                return;
            }else {
                NameListBean nameBean = new NameListBean();
                nameBean.setName(name);
                nameBean.setPhone(phone);
                nameBean.setVendor(vendor);
                nameBean.setRemark(remark);

                SQLiteUtils.getInstance().insertNameList(nameBean);
            }
        }

        EventBus.getDefault().post(MsgType.QUERY_NAME_LIST);
        dismiss();


    }


    @OnClick({R.id.btn_cancel_edit, R.id.btn_edit_name_list})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel_edit:
                dismiss();
                break;
            case R.id.btn_edit_name_list:

                edit();
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

