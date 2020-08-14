package com.synway.passive.location.socket;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.synway.passive.location.bean.BluetoothStatus;
import com.synway.passive.location.bean.DeviceStatus;
import com.synway.passive.location.ui.MainActivity;
import com.synway.passive.location.utils.LoadingUtils;
import com.synway.passive.location.utils.LogUtils;
import com.synway.passive.location.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * Author：Libin on 2020/8/12 13:16
 * Email：1993911441@qq.com
 * Describe：
 */
public class BluetoothSocketUtils {
    private static BluetoothSocketUtils mInstance;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;

    private final static int READ_TIME_OUT = 60000;  //超时时间


    private BluetoothSocketUtils() {

    }

    //获取单例对象
    public static BluetoothSocketUtils getInstance() {
        if (mInstance == null) {
            synchronized (BluetoothSocketUtils.class) {
                if (mInstance == null) {
                    mInstance = new BluetoothSocketUtils();
                }
            }

        }
        return mInstance;
    }

    public void connectBluetoothSocket(BluetoothDevice bluetoothDevice){
        if (bluetoothSocket !=null && bluetoothSocket.isConnected()){
            LoadingUtils.getInstance().dismiss();
            return;
        }

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
            //创建一个socket尝试连接，UUID用正确格式的String来转换而成
            try {
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bluetoothSocket == null){
                ToastUtils.getInstance().showToastOnThread("蓝牙连接失败");

                return;
            }

            int count = 0;
            while (true){
                try {
                    LogUtils.log("正在连接，请稍后......");
                    //该方法阻塞，一直尝试连接

                    bluetoothSocket.connect();

                    DeviceStatus.deviceStatus = DeviceStatus.BLUETOOTH_SOCKET_CONNECTED;
                    EventBus.getDefault().post(new BluetoothStatus(DeviceStatus.BLUETOOTH_SOCKET_CONNECTED));

                    LogUtils.log("连接成功");
                    LoadingUtils.getInstance().dismiss();
                    //进行接收线程
                    new ReadMsg().start();
                    break;
                } catch (IOException e) {
                    LogUtils.log("连接失败:"+e.toString());
                    count++;
                    if (count >=20){
                        LoadingUtils.getInstance().dismiss();
                        ToastUtils.getInstance().showToastOnThread("蓝牙连接失败");
                        break;
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                }
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
                in = bluetoothSocket.getInputStream();
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
        if(bluetoothSocket == null) { //防止未连接就发送信息
            return;
        }
        try {
            Thread.sleep(100);
            // 使用socket获得outputstream
            OutputStream out = bluetoothSocket.getOutputStream();
            out.write(data); //将消息字节发出
            out.flush(); //确保所有数据已经被写出，否则抛出异常
        }catch(Exception e) {
            e.printStackTrace();
            Log.d("libin", "sendData: "+e.toString());
        }
    }

    public boolean  isConnected(){
        return bluetoothDevice !=null && bluetoothSocket !=null && bluetoothSocket.isConnected();
    }

}
