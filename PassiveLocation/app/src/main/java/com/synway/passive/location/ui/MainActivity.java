package com.synway.passive.location.ui;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.synway.passive.location.R;
import com.synway.passive.location.adapter.MainTabLayoutAdapter;
import com.synway.passive.location.application.MyApplication;
import com.synway.passive.location.base.BaseActivity;
import com.synway.passive.location.bean.BluetoothStatus;
import com.synway.passive.location.bean.DeviceStatus;
import com.synway.passive.location.bean.DeviceStatusBean;
import com.synway.passive.location.bean.TabEntity;
import com.synway.passive.location.fragment.LocationFragment;
import com.synway.passive.location.fragment.ParameterFragment;
import com.synway.passive.location.fragment.SetFragment;
import com.synway.passive.location.receiver.SMSReceiver;
import com.synway.passive.location.socket.BluetoothSocketUtils;
import com.synway.passive.location.socket.MsgType;
import com.synway.passive.location.utils.LoadingUtils;
import com.synway.passive.location.utils.OSUtils;
import com.synway.passive.location.utils.ToastUtils;
import com.synway.passive.location.widget.BatteryView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

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
    @BindView(R.id.iv_bluetooth_status)
    ImageView ivBluetoothStatus;
    @BindView(R.id.tv_temperature)
    TextView tvTemperature;
    @BindView(R.id.battery_view)
    BatteryView batteryView;
    @BindView(R.id.tv_battery)
    TextView tvBattery;
    private MainTabLayoutAdapter adapter;
    public static BluetoothSD sd;
    private SMSReceiver smsReceiver;

    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
        start();
        initSMS();
    }

    private void initSMS() {
        smsReceiver = new SMSReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("lab.sodino.sms.send");
        intentFilter.addAction("lab.sodino.sms.delivery");
        registerReceiver(smsReceiver, intentFilter);
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
        entityList.add(new TabEntity("参数", R.mipmap.icon_param_selected, R.mipmap.icon_param_normal));
        entityList.add(new TabEntity("定位", R.mipmap.icon_locate_selected, R.mipmap.icon_locate_normal));
        entityList.add(new TabEntity("设置", R.mipmap.icon_setting_selected, R.mipmap.icon_setting_normal));

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

    /**
     * 定位命令下发成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void locationResult(String result) {
        if (MsgType.LOCATION_SUCCESS.equals(result)) {
            vpMain.setCurrentItem(1);
        }
    }

    /**
     * 蓝牙状态
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bluetoothStatus(BluetoothStatus bluetoothStatus) {
        switch (bluetoothStatus.getStatus()) {
            case DeviceStatus.BLUETOOTH_SOCKET_CONNECTED:
                ivBluetoothStatus.setVisibility(View.VISIBLE);
                break;
            default:
                ivBluetoothStatus.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 设备状态
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deviceStatus(DeviceStatusBean deviceStatusBean) {
        tvTemperature.setVisibility(View.VISIBLE);
        batteryView.setVisibility(View.VISIBLE);
        tvBattery.setVisibility(View.VISIBLE);

        tvTemperature.setText(deviceStatusBean.getTemperatureDIG()+"℃");
        batteryView.setPower(deviceStatusBean.getElectricity());
        tvBattery.setText(deviceStatusBean.getElectricity()+"%");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(smsReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2s则认为是误操作，使用Toast进行提示
                ToastUtils.getInstance().showToast("再按一次退出程序");
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2s则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
