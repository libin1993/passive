package com.example.bluetoothsocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.example.bluetoothitem.MainActivity;
import com.example.bluetoothitem.R;
import com.example.bluetoothitem.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class SocketActivity extends Activity implements ISendData {
	
	public static ISendData iSendData;
	
	BluetoothAdapter bluetoothAdapter;
	BluetoothDevice device;
	BluetoothSocket socket;
	BluetoothServerSocket serSocket;
	
	TextView infoText1;
	TextView infoText2;
	TextView infoText3;
	TextView infoText4;
	TextView stateText;
	EditText infoEdit;
	
	private static Handler stateHandler;
	
	public static Handler getStateHandler(){
		return  stateHandler;
	}
	
	private static Handler infoHandler;
	
	public static Handler getInfoHandler(){
		return  infoHandler;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_socket);
		
		infoText1 = (TextView) findViewById(R.id.infoText1);
		infoText2 = (TextView) findViewById(R.id.infoText2);
		infoText3 = (TextView) findViewById(R.id.infoText3);
		infoText4 = (TextView) findViewById(R.id.infoText4);
		infoEdit = (EditText) findViewById(R.id.infoEdit);
		stateText = (TextView) findViewById(R.id.stateText);
		
		Intent i = getIntent();
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		//处理消息
		stateHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String s = (String) msg.obj;
				stateText.setText(s);
			}
		};
		
		infoHandler = new Handler() {
			
			@Override
			public void handleMessage(Message msg) {
				String s = (String) msg.obj;
				infoText4.setText(infoText3.getText());
				infoText3.setText(infoText2.getText());
				infoText2.setText(infoText1.getText());
				infoText1.setText(s);
			}
		};
		
		if("service".equals(i.getStringExtra("role"))) {
			new ServiceThread().start();
		} else if("client".equals(i.getStringExtra("role"))) {
			device = bluetoothAdapter.getRemoteDevice(i.getStringExtra("btAddress"));
			if(device.getBondState() == BluetoothDevice.BOND_NONE) {
				device.createBond();
			}
			//若设备未配对则自动配对
			new ClientThread().start();
		}

		iSendData = this;

		SocketUtils.getInstance().startTCP(new OnSocketChangedListener() {
			@Override
			public void onConnect() {

			}

			@Override
			public void onDisconnect() {

			}
		});
	}
	
	public void connect(View v){
		
	}
	
	public void connected(View v){
		
	}
	
	public void sendMsg(View v){
		String info = infoEdit.getText().toString();
		sendMessage(info);
	}
	
	/**
	 * 发送消息
	 * @param msg：发送的消息
	 */
	public void sendMessage(String msg) {
		if(socket == null) { //防止未连接就发送信息
			Toast.makeText(getApplicationContext(), "未建立连接", Toast.LENGTH_SHORT).show();
			return;
		}
		
		try {
			//使用socket获得outputstream
			OutputStream out = socket.getOutputStream();
			out.write(msg.getBytes());//将消息字节发出
			out.flush();//确保所有数据已经被写出，否则抛出异常
			Toast.makeText(getApplicationContext(), "发送:" + msg, Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void sendData(byte[] data) {
		if(socket == null) { //防止未连接就发送信息
			Toast.makeText(getApplicationContext(), "未建立连接", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			 Thread.sleep(100);
			// 使用socket获得outputstream
			OutputStream out = socket.getOutputStream();
			out.write(data); //将消息字节发出
			out.flush(); //确保所有数据已经被写出，否则抛出异常
			// Toast.makeText(getApplicationContext(), "发送:" + msg, 0).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
		} catch(Exception e) {
			e.printStackTrace();
			Log.d("libin", "sendData: "+e.toString());
		}
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
				socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
				Utils.sonUiStateMsg("正在连接，请稍后......");
				//该方法阻塞，一直尝试连接
				socket.connect();
				Utils.sonUiStateMsg("连接成功");
				//进行接收线程
				new ReadMsg().start();
			} catch (IOException e) {
				Utils.sonUiStateMsg("连接失败");
				e.printStackTrace();
			}
		}
	}
	/**
	 * 服务端，接收连接的线程
	 * @author Administrator
	 *
	 */
	class ServiceThread extends Thread{
		@Override
		public void run(){
			try {
				//先用本地蓝牙适配器创建一个serversocket
				serSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(bluetoothAdapter.getName(), UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
				Utils.sonUiStateMsg("正在等待连接");
				if(socket!=null){Utils.sonUiStateMsg("连接成功");}
				//等待连接，该方法阻塞
				socket = serSocket.accept();
				Utils.sonUiStateMsg("连接成功");
				new ReadMsg().start();
			} catch (IOException e) {
				// Toast.makeText(getApplicationContext(), "IOExeption", 1).show();
				System.out.println("IOExeption");
				Utils.sonUiStateMsg("连接失败");
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
		
		/*byte[] oldData = new byte[8192];
		int oldSize = 0;
		
		 // 发送数据到解析
	    Runnable runnable = new Runnable() {
	        @Override
	        public void run() {
	        	try {
	        		byte[] oldData2 = new byte[oldSize];
	 	            byte[] newData = oldData;
	        		System.arraycopy(newData, 0, oldData2, 0, oldData2.length);
		            if (MainActivity.sd != null) {
						MainActivity.sd.rcvData(oldData2); // 数据扔过去库
					}
				} catch (Exception e) {
					// e.printStackTrace();
				} finally {
					oldData = new byte[8192];
		            oldSize = 0;
				}
	        }
	    };*/
		
		@Override
		public void run(){
			//数据缓存
////			byte[] bytesReceived = new byte[1024];
//			//接收到流的数量
//			int receiveCount;
//			LTEDataParse lteDataParse = new LTEDataParse();
//
//			try {
//
//
//				//获取输入流
//				InputStream inputStream = socket.getInputStream();
//				int read = inputStream.read();
//
//				byte[] bytesReceived = new byte[read];
//
//				//循环接收数据
//				while ((receiveCount = inputStream.read(bytesReceived)) != -1) {
//					LogUtils.log("数据长度："+read);
//					LogUtils.log(bytesToHexString(bytesReceived));
////					lteDataParse.parseData(bytesReceived, receiveCount);
//				}
//
//				LogUtils.log("socket被关闭，读取长度：" + receiveCount);
//
//			} catch (IOException ex) {
//				LogUtils.log("读取错误:" + ex.toString());
//			}


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

						if (MainActivity.sd != null) {
							MainActivity.sd.rcvData(buf_data); // 数据扔过去库
						}

					}
					/*try {
						int dd = in.read();
	                    if (oldSize == 0) {
	                        //20毫秒没有数据接收到数据后就向解析发
	                    	stateHandler.postDelayed(runnable, 100);
	                    } else {
	                        //如果一直接收到，且数据超过768，也往解析发
	                        if (oldSize < 768) {
	                        	stateHandler.removeCallbacks(runnable);
	                            stateHandler.postDelayed(runnable, 100);
	                        }
	                    }
	                    oldData[oldSize++] = (byte) dd;
					} catch (Exception e) {

					}*/
				}

			} catch (IOException e) {
				e.printStackTrace();
				Utils.sonUiStateMsg("连接已断开");
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将字节数组转换为String
	 *
	 * @param tempValue
	 * @return
	 */
	public static String bytesToString(byte[] tempValue) {
		StringBuffer result = new StringBuffer();
		if (tempValue == null || tempValue.length == 0){
			return "";
		}
		int length = tempValue.length;

		for (int i = 0; i < length; i++) {
			result.append((char) (tempValue[i] & 0xff));
		}
		return result.toString();
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
