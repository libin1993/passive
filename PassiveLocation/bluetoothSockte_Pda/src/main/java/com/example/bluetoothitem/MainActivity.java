package com.example.bluetoothitem;

import android.os.Bundle;

import hrst.main.BluetoothSD;
import hrst.main.Main;

import java.util.ArrayList;
import java.util.Map;

import com.example.bluetoothsocket.LogUtils;
import com.example.bluetoothsocket.SocketActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	public static BluetoothSD sd;
	
	ListView listView;
	ArrayList<Bluetooth> btlist;
	
	private Button openBtn, closeBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView = (ListView) findViewById(R.id.listView);
		openBtn = (Button) findViewById(R.id.btn_open);
		closeBtn = (Button) findViewById(R.id.btn_close);
		//设置listview条目点击事件
		listView.setOnItemClickListener(new itemClick());
		//注册广播：找到远程蓝牙设备
		IntentFilter iFilter=new IntentFilter();
		iFilter.addAction(BluetoothDevice.ACTION_FOUND);
		registerReceiver(new BluetoothList(), iFilter);
		//注册广播：完成搜索
		IntentFilter iFilter1=new IntentFilter();
		iFilter1.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(new findFinish(), iFilter1);
		//启动搜索，获得搜索结果
		BluetoothList.setBtList(getApplicationContext());
		btlist = BluetoothList.getBtList();
		
		closeBtn.setEnabled(false);
		openBtn.setOnClickListener(this);
		closeBtn.setOnClickListener(this);
		
		// 数据转发对象
		/*sd = new BluetoothSD() {
			
			@Override
			public void sendData(byte[] data) {
				// 蓝牙发送数据
				if (SocketActivity.iSendData != null) {
					SocketActivity.iSendData.sendData(data);
				}	
			}
		};
		
		// 启动
		Main.main(null, sd);*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
        case R.id.search_again:
        	BluetoothList.setBtList(getApplicationContext());
            break;
        case R.id.sevice_jump:
        	Intent i=new Intent(MainActivity.this, SocketActivity.class);
        	i.putExtra("role", "service");
        	startActivity(i);
        	break;
    }
		return false;
    }
	
	/**
	 * listview条目点击事件的实现类（内部类）
	 * @author Administrator
	 *
	 */
	class itemClick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			Map<String, String> infoMap = (Map<String, String>) parent.getItemAtPosition(position);
        	//执行页面跳转
        	Intent i=new Intent(MainActivity.this, SocketActivity.class);
        	//传递数据
        	i.putExtra("btName", infoMap.get("btName"));
        	i.putExtra("btAddress", infoMap.get("btAddress"));
        	i.putExtra("btRSSI", infoMap.get("btRSSI"));
        	i.putExtra("role", "client");
        	startActivity(i);
		}
		
	}
	
	class findFinish extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(context, "搜索完成", 0).show();
			// TODO Auto-generated method stub
			/*
			 * 将数据显示到listview上
			 */
			/**
			 * 创建新的适配器
			 * Context context：getApplicationContext()
			 * List<? extends Map<String, ?>> data：一个以Map为元素的List集合，Map中的键值对对应Item.xml的各个控件
			 * int resource：指出Item中的布局文件
			 * String[] from：与  int[] to 对应，与Map集合里的key相同
			 * int[] to：与 String [] from 对应，与item.xml中的id相同
			 */
			SimpleAdapter saAdapter=new SimpleAdapter(getApplicationContext(), 
														Utils.getListOfMap(btlist),
														R.layout.item,
														new String[] {"btName","btRSSI","btAddress"},
														new int [] {R.id.name,R.id.RSSI,R.id.address});
			listView.setAdapter(saAdapter);//设置listview的适配器为上述适配器
		}
		
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == openBtn.getId()) {
			// 数据转发对象
			sd = new BluetoothSD() {
				@Override
				public void sendData(byte[] data) {
					// 蓝牙发送数据

					if (SocketActivity.iSendData != null) {
						SocketActivity.iSendData.sendData(data);
					}	
				}
			};
			
			// 启动
			Main.main(null, sd);
			
			openBtn.setEnabled(false);
			closeBtn.setEnabled(true);
		} else if (v.getId() == closeBtn.getId()) {
			Main.dispose();
			
			openBtn.setEnabled(true);
			closeBtn.setEnabled(false);
		}
	}


	/**
	 * byte[] 转16进制
	 *
	 * @param tempValue
	 * @return
	 */
	public static String bytesToHexString(byte[] tempValue) {
		StringBuilder stringBuilder = new StringBuilder();
		if (tempValue == null || tempValue.length <= 0) {
			return null;
		}
		for (int i = 0; i < tempValue.length; i++) {
			int v = tempValue[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
}
