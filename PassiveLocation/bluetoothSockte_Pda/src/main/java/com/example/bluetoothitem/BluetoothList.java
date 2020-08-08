package com.example.bluetoothitem;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.widget.Toast;

public class BluetoothList extends BroadcastReceiver {
	public static  ArrayList<Bluetooth> btList=new ArrayList<Bluetooth>();
	public static boolean setBtList(Context context) {
		btList.clear();
		BluetoothAdapter btAdapter=BluetoothAdapter.getDefaultAdapter();
		if(btAdapter.isEnabled()){
			Toast.makeText(context, "正在搜索......", Toast.LENGTH_SHORT).show();
			btAdapter.startDiscovery();
			return true;
		}else{
			Toast.makeText(context, "请打开蓝牙",  Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	public static ArrayList<Bluetooth> getBtList(){
		return btList;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		try {
			String action = intent.getAction();
			if(BluetoothDevice.ACTION_FOUND.equals(action)){
				BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				short rssi=intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
				btList.add(new Bluetooth(device.getName(), device.getAddress(), rssi+""));
				System.out.println(device.getName());
			}
		} catch (Exception e) {
		}
	}
}
