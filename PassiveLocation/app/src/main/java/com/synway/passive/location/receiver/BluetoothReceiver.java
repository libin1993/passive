package com.synway.passive.location.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.synway.passive.location.bean.BluetoothBean;
import com.synway.passive.location.bean.BluetoothStatus;
import com.synway.passive.location.bean.DeviceStatus;
import com.synway.passive.location.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Author：Libin on 2020/8/8 14:53
 * Email：1993911441@qq.com
 * Describe：
 */
public class BluetoothReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.log(action);
        if (action != null) {
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    LogUtils.log("blueState:"+blueState);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_ON:
                            DeviceStatus.deviceStatus = DeviceStatus.BLUETOOTH_OPEN;
                            EventBus.getDefault().post(new BluetoothStatus(DeviceStatus.BLUETOOTH_OPEN));
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            DeviceStatus.deviceStatus = DeviceStatus.BLUETOOTH_CLOSED;
                            EventBus.getDefault().post(new BluetoothStatus(DeviceStatus.BLUETOOTH_CLOSED));
                            break;
                    }
                    break;

                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    EventBus.getDefault().post(new BluetoothStatus(DeviceStatus.BLUETOOTH_CONNECTED));
                    break;

                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    DeviceStatus.deviceStatus = DeviceStatus.BLUETOOTH_DISCONNECTED;
                    EventBus.getDefault().post(new BluetoothStatus(DeviceStatus.BLUETOOTH_DISCONNECTED));
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                    BluetoothBean bluetoothBean = new BluetoothBean(device.getName(), device.getAddress() ,device.getBondState(),rssi);
                    LogUtils.log("扫描蓝牙："+bluetoothBean.toString());
                    EventBus.getDefault().post(bluetoothBean);
                    break;
            }

        }
    }
}
