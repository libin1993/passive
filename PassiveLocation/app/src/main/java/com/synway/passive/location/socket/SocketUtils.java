package com.synway.passive.location.socket;

import com.synway.passive.location.utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Author：Libin on 2020/8/8 10:33
 * Email：1993911441@qq.com
 * Describe：
 */
public class SocketUtils {
    private static SocketUtils mInstance;
    private Socket mSocket;

    private final static int READ_TIME_OUT = 60000;  //超时时间

    private Timer timer;


    private SocketUtils() {

    }

    //获取单例对象
    public static SocketUtils getInstance() {
        if (mInstance == null) {
            synchronized (SocketUtils.class) {
                if (mInstance == null) {
                    mInstance = new SocketUtils();
                }
            }

        }
        return mInstance;
    }


    /**
     * @param
     */
    public void connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    mSocket = new Socket("127.0.0.1",8818);
                    mSocket.setSoTimeout(READ_TIME_OUT);      //设置超时


                    heartBeat();

                    //数据缓存
                    byte[] bytesReceived = new byte[1024];
                    //接收到流的数量
                    int receiveCount;
                    LteReceiveManager lteDataParse = new LteReceiveManager();

                    //获取输入流
                    InputStream inputStream = mSocket.getInputStream();

                    //循环接收数据
                    while ((receiveCount = inputStream.read(bytesReceived)) != -1) {
                        lteDataParse.parseData(bytesReceived, receiveCount);
                    }

                    LogUtils.log("socket被关闭，读取长度：" + receiveCount);


                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.log("tcp错误："+e.toString());
                    if (timer !=null){
                        timer.cancel();
                        timer = null;
                    }
                }
            }
        }).start();

    }

    /**
     * 心跳
     */
    private void heartBeat() {
        if (timer == null){
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    LteSendManager.sendData(MsgType.SEND_SERVER_HEART_BEAT);
                }
            },1000,10000);

        }
    }


    //关闭socket
    public void closeSocket() {

        if (mSocket != null ) {
            if (!mSocket.isClosed()){
                //关闭socket
                try {
                    mSocket.shutdownInput();
                    mSocket.close();//临时
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    /**
     * 发送数据
     *
     * @param tempByte
     * @return
     */
    public void sendData(final byte[] tempByte) {
        if (mSocket != null && mSocket.isConnected()) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        OutputStream outputStream = mSocket.getOutputStream();
                        outputStream.write(tempByte);
                        outputStream.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.log("socket发送失败："+e.getMessage());
                    }
                }
            }.start();
        }

    }


}
