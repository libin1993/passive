package com.synway.passive.location.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.synway.passive.location.R;
import com.synway.passive.location.base.BaseFragment;
import com.synway.passive.location.bean.BluetoothBean;
import com.synway.passive.location.bean.BluetoothStatus;
import com.synway.passive.location.bean.DeviceStatus;
import com.synway.passive.location.receiver.BluetoothReceiver;
import com.synway.passive.location.ui.MainActivity;
import com.synway.passive.location.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.annotation.Entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author：Libin on 2020/8/8 14:27
 * Email：1993911441@qq.com
 * Describe：配置
 */
public class ConfigFragment extends BaseFragment {
    @BindView(R.id.cb_bluetooth)
    CheckBox cbBluetooth;
    @BindView(R.id.rv_bluetooth)
    RecyclerView rvBluetooth;
    private Unbinder unbinder;

    private BluetoothReceiver bluetoothReceiver;
    private List<BluetoothBean> bluetoothList = new ArrayList<>();
    private BaseQuickAdapter<BluetoothBean, BaseViewHolder> adapter;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice device;
    private boolean hasBond = false;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_config, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        registerBroadcastReceiver();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initView();
        return view;
    }

    private void registerBroadcastReceiver() {
        bluetoothReceiver = new BluetoothReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        getActivity().registerReceiver(bluetoothReceiver, intentFilter);
    }

    private void initView() {

        rvBluetooth.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BaseQuickAdapter<BluetoothBean, BaseViewHolder>(R.layout.layout_bluetooth_item,bluetoothList) {
            @Override
            protected void convert(BaseViewHolder helper, BluetoothBean item) {
                helper.setText(R.id.tv_bluetooth_info,item.getName()+","+item.getAddress()+","+item.getRssi()+","+item.getBondState()) ;
            }
        };
        rvBluetooth.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BluetoothBean bluetoothBean = bluetoothList.get(position);
                device = bluetoothAdapter.getRemoteDevice(bluetoothBean.getAddress());

                if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                    if ((getActivity()) !=null) {
                        hasBond = true;
                        ((MainActivity)getActivity()).connectBluetoothSocket(device); // 数据扔过去库
                    }
                }else {
                    hasBond = false;
                    device.createBond();
                }


            }
        });

        cbBluetooth.setOnCheckedChangeListener(checkedChangeListener);
        cbBluetooth.setChecked(true);

    }

    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                scanBluetooth();
            }else {
                bluetoothAdapter.disable();
            }
        }
    };

    /**
     * 扫描蓝牙
     */
    private void scanBluetooth(){
        if(bluetoothAdapter.isEnabled()){
            bluetoothAdapter.startDiscovery();
        }else {
            bluetoothAdapter.enable();
        }
    }




    /**
     * @param bluetoothBean 扫描蓝牙结果
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void scanResult(BluetoothBean bluetoothBean){
        for (BluetoothBean bean : bluetoothList) {
            if (bean.getAddress().equals(bluetoothBean.getAddress())){
                return;
            }
        }

        bluetoothList.add(bluetoothBean);
        Collections.sort(bluetoothList, new Comparator<BluetoothBean>() {
            @Override
            public int compare(BluetoothBean o1, BluetoothBean o2) {
                return Integer.valueOf(o2.getRssi()).compareTo(o1.getRssi());
            }
        });

        adapter.notifyDataSetChanged();
    }


    /**
     * 蓝牙状态
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bluetoothStatus(BluetoothStatus bluetoothStatus){
        switch (bluetoothStatus.getStatus()){
            case DeviceStatus.BLUETOOTH_CLOSED:
                setBluetoothStatus(false);
                break;
            case DeviceStatus.BLUETOOTH_OPEN:
                setBluetoothStatus(true);
                break;
            case DeviceStatus.BLUETOOTH_DISCONNECTED:
                break;
            case DeviceStatus.BLUETOOTH_CONNECTED:
                if ((getActivity()) !=null) {
                    if (!hasBond)
                    ((MainActivity)getActivity()).connectBluetoothSocket(device); // 数据扔过去库
                }
                break;
        }
    }



    /**
     * @param isOpen 蓝牙开关
     */
    private void setBluetoothStatus(boolean isOpen){
        cbBluetooth.setOnCheckedChangeListener(null);
        cbBluetooth.setChecked(isOpen);
        cbBluetooth.setOnCheckedChangeListener(checkedChangeListener);
        if (isOpen){
            scanBluetooth();
        }else {
            bluetoothList.clear();
            adapter.notifyDataSetChanged();
        }
    }


    public static ConfigFragment newInstance() {
        Bundle args = new Bundle();
        ConfigFragment fragment = new ConfigFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        getActivity().unregisterReceiver(bluetoothReceiver);
        EventBus.getDefault().unregister(this);
    }


}
