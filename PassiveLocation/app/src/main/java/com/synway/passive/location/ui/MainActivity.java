package com.synway.passive.location.ui;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.synway.passive.location.R;
import com.synway.passive.location.adapter.MainTabLayoutAdapter;
import com.synway.passive.location.base.BaseActivity;
import com.synway.passive.location.bean.BluetoothStatus;
import com.synway.passive.location.bean.DeviceStatus;
import com.synway.passive.location.bean.TabEntity;
import com.synway.passive.location.fragment.BluetoothFragment;
import com.synway.passive.location.fragment.LocationFragment;
import com.synway.passive.location.fragment.ParameterFragment;
import com.synway.passive.location.fragment.SetFragment;
import com.synway.passive.location.socket.BluetoothSocketUtils;
import com.synway.passive.location.socket.LteReceiveManager;
import com.synway.passive.location.socket.SocketUtils;
import com.synway.passive.location.utils.FormatUtils;
import com.synway.passive.location.utils.LogUtils;
import com.synway.passive.location.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import hrst.main.BluetoothSD;
import hrst.main.Main;

/**
 * Author：Libin on 2020/8/8 13:49
 * Email：1993911441@qq.com
 * Describe：
 */
public class MainActivity extends BaseActivity {
    @BindView(R.id.vp_main)
    ViewPager vpMain;
    @BindView(R.id.ctl_main)
    CommonTabLayout ctlMain;
    private MainTabLayoutAdapter adapter;
    public static BluetoothSD sd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        start();
    }

    private void start() {
        // 数据转发对象
        sd = new BluetoothSD() {
            @Override
            public void sendData(byte[] data) {
                // 蓝牙发送数据
                BluetoothSocketUtils.getInstance().sendData(data);
            }
        };

        // 启动
        Main.main(null, sd);

    }

    private void initView() {
        ArrayList<CustomTabEntity> entityList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();
        titleList.add("参数");
        titleList.add("定位");
        titleList.add("设置");
        fragmentList.add(ParameterFragment.newInstance());
        fragmentList.add(LocationFragment.newInstance());
        fragmentList.add(SetFragment.newInstance());
        entityList.add(new TabEntity("配置", R.mipmap.ic_launcher, R.mipmap.ic_launcher));
        entityList.add(new TabEntity("定位", R.mipmap.ic_launcher, R.mipmap.ic_launcher));
        entityList.add(new TabEntity("设置", R.mipmap.ic_launcher, R.mipmap.ic_launcher));

        adapter = new MainTabLayoutAdapter(getSupportFragmentManager(), fragmentList, titleList);
        vpMain.setOffscreenPageLimit(titleList.size());
        vpMain.setAdapter(adapter);

        ctlMain.setTabData(entityList);

        ctlMain.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                vpMain.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        vpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ctlMain.setCurrentTab(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vpMain.setCurrentItem(0);
    }

}
