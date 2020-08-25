package com.synway.passive.location.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.synway.passive.location.R;
import com.synway.passive.location.base.BaseActivity;
import com.synway.passive.location.utils.SPUtils;
import com.synway.passive.location.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author：Libin on 2020/8/8 13:49
 * Email：1993911441@qq.com
 * Describe：
 */
public class LoginActivity extends BaseActivity {
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    private String adminAccount;
    private String adminPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        adminAccount = (String) SPUtils.getInstance().get(SPUtils.ADMIN_ACCOUNT, "admin");
        adminPassword = (String) SPUtils.getInstance().get(SPUtils.ADMIN_PASSWORD, "admin");

        etAccount.setText(adminAccount);
        etAccount.setSelection(adminAccount.length());
        etPassword.setText(adminPassword);
    }

    @OnClick(R.id.btn_login)
    public void onViewClicked() {
        String account = etAccount.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(account)) {
            ToastUtils.getInstance().showToast("请输入账号");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ToastUtils.getInstance().showToast("请输入密码");
            return;
        }


        if (!TextUtils.equals(account, adminAccount) || !TextUtils.equals(password, adminPassword)) {
            ToastUtils.getInstance().showToast("账号或密码错误");
            return;
        }

        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
