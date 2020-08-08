package com.synway.passive.location.ui;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import com.synway.passive.location.fragment.ConfigFragment;
import com.synway.passive.location.fragment.LocationFragment;
import com.synway.passive.location.fragment.SetFragment;
import com.synway.passive.location.inter.OnSocketChangedListener;
import com.synway.passive.location.socket.SocketUtils;
import com.synway.passive.location.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
    public BluetoothSD sd;

    private BluetoothSocket socket;
    private BluetoothDevice bluetoothDevice;

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
               MainActivity.this.sendData(data);
            }
        };

        // 启动
        Main.main(null, sd);
    }

    public void connectBluetoothSocket(BluetoothDevice bluetoothDevice){
        SocketUtils.getInstance().connect();
        this.bluetoothDevice = bluetoothDevice;
        new ClientThread().start();
    }


    /**
     * 客户端，进行连接的线程
     * @author Administrator
     *
     */
    class ClientThread extends Thread {
        @Override
        public void run(){
            try {
                //创建一个socket尝试连接，UUID用正确格式的String来转换而成
                socket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                LogUtils.log("正在连接，请稍后......");
                //该方法阻塞，一直尝试连接
                socket.connect();
                LogUtils.log("连接成功");
                //进行接收线程
                new ReadMsg().start();
            } catch (IOException e) {
                LogUtils.log("连接失败");
                e.printStackTrace();
            }
        }
    }


    /**
     * 循环读取信息的线程
     * @author Administrator
     *
     */
    class ReadMsg extends Thread {
        @Override
        public void run(){

            byte[] buffer = new byte[1024]; // 定义字节数组装载信息
            int bytes; // 定义长度变量
            InputStream in = null;
            try {
                // 使用socket获得输入流
                in = socket.getInputStream();
                // 一直循环接收处理消息
                while(true) {
                    if((bytes = in.read(buffer)) != 0){
                        byte[] buf_data = new byte[bytes];
                        for (int i = 0; i < bytes; i++){
                            buf_data[i] = buffer[i];
                        }
                        if (sd != null) {
                           sd.rcvData(buf_data); // 数据扔过去库
                        }

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.log("连接已断开");
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendData(byte[] data) {
        if(socket == null) { //防止未连接就发送信息
            return;
        }
        try {
            Thread.sleep(100);
            // 使用socket获得outputstream
            OutputStream out = socket.getOutputStream();
            out.write(data); //将消息字节发出
            out.flush(); //确保所有数据已经被写出，否则抛出异常
        }catch(Exception e) {
            e.printStackTrace();
            Log.d("libin", "sendData: "+e.toString());
        }
    }



    private void initView() {
        ArrayList<CustomTabEntity> entityList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();
        titleList.add("配置");
        titleList.add("定位");
        titleList.add("设置");
        fragmentList.add(ConfigFragment.newInstance());
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
