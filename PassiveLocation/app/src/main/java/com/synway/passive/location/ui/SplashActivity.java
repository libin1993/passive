package com.synway.passive.location.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.synway.passive.location.base.BaseActivity;
import com.synway.passive.location.utils.PermissionUtils;
import com.synway.passive.location.utils.SPUtils;

/**
 * Author：Libin on 2020/8/10 16:34
 * Email：1993911441@qq.com
 * Describe：
 */
public class SplashActivity extends BaseActivity {
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SEND_SMS};
    private String[][] permissionArray = {{Manifest.permission.WRITE_EXTERNAL_STORAGE, "读写"},
            {Manifest.permission.ACCESS_COARSE_LOCATION, "定位"},
            {Manifest.permission.READ_PHONE_STATE, "读取手机状态"},
            {Manifest.permission.SEND_SMS, "发送短信"}};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermissions();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        getPermissions();
    }

    private void getPermissions() {
        if (PermissionUtils.getInstance().hasPermission(this, permissions)) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        } else {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (String[] strings : permissionArray) {
                if (!PermissionUtils.getInstance().hasPermission(this, strings[0])) {
                    PermissionUtils.getInstance().showPermissionDialog(SplashActivity.this,
                            strings[0], strings[1], new PermissionUtils.OnPermissionListener() {
                                @Override
                                public void onCancel() {
                                    finish();
                                }

                                @Override
                                public void onReQuest() {
                                    getPermissions();
                                }
                            });
                    return;
                }
            }
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }
}
