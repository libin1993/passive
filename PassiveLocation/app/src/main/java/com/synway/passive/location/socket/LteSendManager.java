package com.synway.passive.location.socket;

import android.text.TextUtils;


import com.synway.passive.location.utils.CacheManager;
import com.synway.passive.location.utils.FormatUtils;
import com.synway.passive.location.utils.LogUtils;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Author：Libin on 2020/6/10 11:05
 * Email：1993911441@qq.com
 * Describe：App发送指令
 */
public class LteSendManager {
    private static int id = 0;  //消息自增序列


    /**  小区搜索
     * @param protocol  制式
     * @param vendor    运营商
     * @param targetPhoneNumber 目标手机号
     * @param triggerPhoneNumber
     * @param searchMode  搜索模式
     * @param lac
     * @param cid
     */
    public static void searchCell(int protocol, int vendor,String targetPhoneNumber, String triggerPhoneNumber,String searchMode,String lac, String cid) {
        byte[] protocolBytes =new byte[]{(byte) protocol};
        byte[] vendorBytes = new byte[]{(byte) vendor};

        byte[] targetNumberBytes = new byte[targetPhoneNumber.length()];
        for (int i = 0; i < targetNumberBytes.length; i++) {
            targetNumberBytes[i] = (byte) targetPhoneNumber.charAt(i);
        }

        byte[] triggerNumberBytes = new byte[triggerPhoneNumber.length()];
        for (int i = 0; i < triggerNumberBytes.length; i++) {
            triggerNumberBytes[i] = (byte) triggerPhoneNumber.charAt(i);
        }

        byte[] tmsiBytes = FormatUtils.getInstance().hexStringToBytes("0xffffffff");
        byte[] imsiBytes = new byte[16];
        byte[] thresholdBytes = new byte[]{5};
        byte[] mmecBytes = FormatUtils.getInstance().hexStringToBytes("0xff");
        byte[] searchModeBytes = searchMode.getBytes();
//        byte[] freqNumBytes = new by;
    }


    public static void sendData(short msgType) {
        sendData(msgType, null);
    }

    /**
     * @param msgType 发送数据
     * @param data
     */
    public static void sendData(short msgType, byte[] data) {
        if (CacheManager.magic == null) {
            LogUtils.log("网络未连接");
            return;
        }
        int packageLength = 82;  //包长度
        byte[] magic = CacheManager.magic;

        //自增序列
        if (id >= 65535) {
            id = 0;
        }

        id++;
        byte[] msgId = FormatUtils.getInstance().intToByteArray(id);
        FormatUtils.getInstance().reverseData(msgId);

        //消息长度
        if (data != null && data.length > 0) {
            packageLength += data.length;
        }
        byte[] msgLength = FormatUtils.getInstance().intToByteArray(packageLength);
        FormatUtils.getInstance().reverseData(msgLength);

        //消息类型
        byte[] type = FormatUtils.getInstance().shortToByteArray(msgType);
        FormatUtils.getInstance().reverseData(type);

        //crc
        byte[] crc = new byte[4];
        //设备编号
        byte[] deviceName = CacheManager.deviceName;
        //坐标信息
        byte[] gpsInfo = CacheManager.gpsInfo;
        //预留位
        byte[] reserve = new byte[16];


        ByteArrayBuffer byteArray = new ByteArrayBuffer(packageLength);

        byteArray.append(magic, 0, magic.length);
        byteArray.append(msgId, 0, msgId.length);
        byteArray.append(msgLength, 0, msgLength.length);
        byteArray.append(type, 0, type.length);
        byteArray.append(crc, 0, crc.length);
        byteArray.append(deviceName, 0, deviceName.length);
        byteArray.append(gpsInfo, 0, gpsInfo.length);
        byteArray.append(reserve, 0, reserve.length);

        if (data != null && data.length > 0) {
            byteArray.append(data, 0, data.length);
        }


        byte[] bytes = byteArray.toByteArray();

        SocketUtils.getInstance().sendData(bytes);

    }

}
