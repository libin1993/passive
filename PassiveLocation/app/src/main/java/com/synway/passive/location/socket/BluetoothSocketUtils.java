package com.synway.passive.location.socket;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.hrst.sdk.HrstSdkCient;
import com.hrst.sdk.dto.report.CellInfosReport;
import com.hrst.sdk.dto.report.LocationInfoReport;
import com.hrst.sdk.dto.report.SysStatusReport;
import com.synway.passive.location.bean.BluetoothStatus;
import com.synway.passive.location.bean.CellBean;
import com.synway.passive.location.bean.DeviceStatus;
import com.synway.passive.location.bean.DeviceStatusBean;
import com.synway.passive.location.bean.LocationInfoBean;
import com.synway.passive.location.ui.MainActivity;
import com.synway.passive.location.utils.CacheManager;
import com.synway.passive.location.utils.FormatUtils;
import com.synway.passive.location.utils.LoadingUtils;
import com.synway.passive.location.utils.LogUtils;
import com.synway.passive.location.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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

        if (CacheManager.is5G){
            HrstSdkCient.registerReportListener(new HrstSdkCient.ReportListener() {
                @Override
                public void write(byte[] bytes) {
                    sendData(bytes);
                }

                @Override
                public void reportMcuLog(String s) {

                }

                @Override
                public void reportSysStatus(SysStatusReport sysStatusReport) {
                    if (CacheManager.is5G){
                        DeviceStatusBean deviceStatus = new DeviceStatusBean();
                        deviceStatus.setStatus((byte) sysStatusReport.getDeviceStatus());
                        deviceStatus.setTemperatureDIG((byte) sysStatusReport.getTemperatureDig());
                        deviceStatus.setTemperatureRF((byte) sysStatusReport.getTemperatureRf());
                        deviceStatus.setElectricity((byte) sysStatusReport.getElectricity());
                        deviceStatus.setMsg(sysStatusReport.getMsg());

                        EventBus.getDefault().post(deviceStatus);
                    }

                }

                @Override
                public void reportUsbStauts(boolean b) {

                }

                @Override
                public void reportSyncCellInfo(CellInfosReport cellInfosReport) {
                    if (!CacheManager.is5G){
                        return;
                    }
                    ArrayList<CellInfosReport.CellInfo> cellList = cellInfosReport.getList();
                    if (cellList !=null && cellList.size() >0){
                        for (CellInfosReport.CellInfo cellInfo : cellList) {
                            CellBean cellBean = new CellBean();
                            cellBean.setVendor((byte) cellInfo.getVendor());
                            cellBean.setProtocol((byte) cellInfo.getProtocolType());
                            cellBean.setFreq((int) cellInfo.getFreq());
                            cellBean.setLac((int) cellInfo.getLac());
                            cellBean.setCid((int) cellInfo.getCid());
                            cellBean.setPci(cellInfo.getPci());
                            cellBean.setDbm((short) cellInfo.getRxLevDbm());
                            cellBean.setSnr((byte) cellInfo.getSnr());
                            cellBean.setOffset(cellInfo.getOffset());
                            cellBean.setStatus((byte) cellInfo.getCellStatusType());
                            cellBean.setStateType((byte) cellInfo.getCellStateType());

                            cellBean.setCellColorFlag((byte) 0);
                            cellBean.setCellErrorRate((byte) cellInfo.getCellErrorRate());
                            cellBean.setHit((byte) cellInfo.getHit());

                            LogUtils.log("小区上报："+cellList.size()+":"+cellBean.toString());

                            CacheManager.cellMap.put(cellBean.getLac()+","+cellBean.getCid(),cellBean);

                            if (String.valueOf(cellBean.getLac()).equals(CacheManager.lac) &&  String.valueOf(cellBean.getCid()).equals(CacheManager.cid)){
                                EventBus.getDefault().post(MsgType.SEARCH_SUCCESS);
                            }
                        }
                    }
                }

                @Override
                public void reportLocationInfo(LocationInfoReport locationInfoReport) {
                    if (!CacheManager.is5G){
                        return;
                    }
                    LocationInfoBean locationBean = new LocationInfoBean();

                    locationBean.setFreq((int) locationInfoReport.getTargetARFCN());
                    locationBean.setLac((int) locationInfoReport.getTargetTAC());
                    locationBean.setCid((int) locationInfoReport.getTargetCID());
                    locationBean.setPowerOverFlow((byte) (locationInfoReport.isPowerOverFlow() ? 1:0));
                    List<Short> dbmList = new ArrayList<>();
                    dbmList.add((short) (locationInfoReport.getRxLevInDbm()+140));
                    locationBean.setDbm(dbmList);
                    locationBean.setColor((byte) locationInfoReport.getPowerIndicate());
                    locationBean.setPci(locationInfoReport.getPci());

                    EventBus.getDefault().post(locationBean);

                    LogUtils.log("定位数据上报:"+locationBean.toString());
                }

                @Override
                public void reportHeartBeat(boolean b, boolean b1) {

                }
            });
        }else {
            SocketUtils.getInstance().connect();
        }


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
                Method m = bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
                bluetoothSocket = (BluetoothSocket) m.invoke(bluetoothDevice, 1);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.log("创建BluetoothSocket失败："+e.toString());
            }


            //创建一个socket尝试连接，UUID用正确格式的String来转换而成
//            try {
//
//                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
//            } catch (IOException e) {
//                e.printStackTrace();
//                LogUtils.log("创建BluetoothSocket失败："+e.toString());
//            }
            if (bluetoothSocket == null){
                LoadingUtils.getInstance().dismiss();
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
                    LoadingUtils.getInstance().dismiss();
                    LogUtils.log("连接成功");

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

                        HrstSdkCient.readDeviceData(buf_data);

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.log("连接已断开:"+e.toString());
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
        if(bluetoothSocket == null || !bluetoothSocket.isConnected()) { //防止未连接就发送信息
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
