package com.synway.passive.location.socket;

import android.text.TextUtils;


import com.orhanobut.logger.Logger;
import com.synway.passive.location.utils.CacheManager;
import com.synway.passive.location.utils.FormatUtils;
import com.synway.passive.location.utils.LogUtils;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.synway.passive.location.utils.CacheManager.magic;

/**
 * Author：Libin on 2020/6/10 11:05
 * Email：1993911441@qq.com
 * Describe：App发送指令
 */
public class LteSendManager {
    private static int id = 0;  //消息自增序列


    /**
     * 小区搜索
     *
     * @param vendor            运营商
     * @param targetPhoneNumber 目标手机号
     * @param lac
     * @param cid
     */
    public static void searchCell(int vendor, String targetPhoneNumber, int[] fcns, String lac, String cid) {
        byte[] protocolBytes = new byte[]{(byte) 7};
        byte[] vendorBytes = new byte[]{(byte) vendor};

        byte[] targetNumberBytes = new byte[11];
        for (int i = 0; i < 11; i++) {
            int targetNumber = Integer.parseInt(targetPhoneNumber.substring(i, i + 1));
            targetNumberBytes[i] = (byte) targetNumber;
        }

        String triggerPhoneNumber = FormatUtils.getInstance().getPhoneNumber();
        byte[] triggerNumberBytes = new byte[11];
        for (int i = 0; i < 11; i++) {
            int triggerNumber = Integer.parseInt(triggerPhoneNumber.substring(i, i + 1));
            triggerNumberBytes[i] = (byte) triggerNumber;
        }

        byte[] tmsiBytes = FormatUtils.getInstance().hexStringToBytes("FFFFFFFF");

        Long imsi;
        if (vendor == 1) {
            imsi = 460000000000000L;
        } else if (vendor == 2) {
            imsi = 460010000000000L;
        } else {
            imsi = 460030000000000L;
        }
        byte[] imsiBytes = FormatUtils.getInstance().longToBytes(imsi);
        FormatUtils.getInstance().reverseData(imsiBytes);

        byte[] thresholdBytes = FormatUtils.getInstance().hexStringToBytes("FF");
        byte[] mmecBytes = FormatUtils.getInstance().hexStringToBytes("FF");
        byte[] searchModeBytes = new byte[]{0};
        byte[] freqNumBytes;


        freqNumBytes = new byte[]{(byte) fcns.length};

        byte[] fcnsBytes = new byte[16];
        Arrays.fill(fcnsBytes, (byte) 0x00);
        for (int i = 0; i < fcns.length; i++) {

            byte[] temFcnBytes = FormatUtils.getInstance().hexStringToBytes(FormatUtils.getInstance().intToHexString(fcns[i]));
            fcnsBytes[2 * i] = temFcnBytes[1];
            fcnsBytes[2 * i + 1] = temFcnBytes[0];

        }

        byte[] startModeBytes = new byte[]{0};

//        byte[] searchTimeBytes = FormatUtils.getInstance().shortToByteArray((short) 20);
        byte[] searchTimeBytes = FormatUtils.getInstance().hexStringToBytes("FFFF");
        FormatUtils.getInstance().reverseData(searchTimeBytes);

        byte[] cidBytes = new byte[12];
        Arrays.fill(cidBytes, (byte) 0xFF);

        byte[] tacBytes = new byte[6];
        Arrays.fill(tacBytes, (byte) 0xFF);


        byte[] isAndBytes = new byte[]{0};


        ByteArrayBuffer byteArray = new ByteArrayBuffer(78);
        byteArray.append(protocolBytes, 0, protocolBytes.length);
        byteArray.append(vendorBytes, 0, vendorBytes.length);
        byteArray.append(targetNumberBytes, 0, targetNumberBytes.length);
        byteArray.append(triggerNumberBytes, 0, triggerNumberBytes.length);
        byteArray.append(tmsiBytes, 0, tmsiBytes.length);
        byteArray.append(imsiBytes, 0, imsiBytes.length);
        byteArray.append(thresholdBytes, 0, thresholdBytes.length);
        byteArray.append(mmecBytes, 0, mmecBytes.length);
        byteArray.append(searchModeBytes, 0, searchModeBytes.length);
        byteArray.append(freqNumBytes, 0, freqNumBytes.length);
        byteArray.append(fcnsBytes, 0, fcnsBytes.length);
        byteArray.append(startModeBytes, 0, startModeBytes.length);
        byteArray.append(searchTimeBytes, 0, searchTimeBytes.length);
        byteArray.append(cidBytes, 0, cidBytes.length);
        byteArray.append(tacBytes, 0, tacBytes.length);
        byteArray.append(isAndBytes, 0, isAndBytes.length);

        byte[] bytes = byteArray.toByteArray();

        sendData(MsgType.SEND_CELL_SEARCH, bytes);

    }

    /**
     * 开始诱发
     */
    public static void startTrigger() {
        byte[] triggerModeBytes = new byte[]{1};

        String triggerPhoneNumber = FormatUtils.getInstance().getPhoneNumber();
        byte[] triggerNumberBytes = new byte[11];
        for (int i = 0; i < triggerNumberBytes.length; i++) {
            int triggerNumber = Integer.parseInt(triggerPhoneNumber.substring(i, i + 1));
            triggerNumberBytes[i] = (byte) triggerNumber;
        }

        byte[] flagBytes = new byte[]{0};
        ByteArrayBuffer byteArray = new ByteArrayBuffer(13);
        byteArray.append(triggerModeBytes, 0, triggerModeBytes.length);
        byteArray.append(triggerNumberBytes, 0, triggerNumberBytes.length);
        byteArray.append(flagBytes, 0, flagBytes.length);

        byte[] bytes = byteArray.toByteArray();

        sendData(MsgType.SEND_TRIGGER_START, bytes);

    }

    /**
     * 结束诱发
     */
    public static void stopTrigger() {
        byte[] errorCodeBytes = new byte[4];

        byte[] flagBytes = new byte[]{0};
        ByteArrayBuffer byteArray = new ByteArrayBuffer(5);
        byteArray.append(errorCodeBytes, 0, errorCodeBytes.length);
        byteArray.append(flagBytes, 0, flagBytes.length);

        byte[] bytes = byteArray.toByteArray();

        sendData(MsgType.SEND_TRIGGER_END, bytes);

    }

    /**
     * 监测
     */
    public static void sendMonitor(String phoneNumber) {
        byte[] protocolBytes = new byte[]{(byte) 7};
        byte[] monitorModeBytes = new byte[]{(byte) 1};
        byte[] targetNumberBytes = new byte[11];
        for (int i = 0; i < 11; i++) {
            int targetNumber = Integer.parseInt(phoneNumber.substring(i, i + 1));
            targetNumberBytes[i] = (byte) targetNumber;
        }
        byte[] esnBytes = FormatUtils.getInstance().hexStringToBytes("FFFFFFFF");


        Long imsi;
        if (CacheManager.vendor == 1) {
            imsi = 460000000000000L;
        } else if (CacheManager.vendor == 2) {
            imsi = 460010000000000L;
        } else {
            imsi = 460030000000000L;
        }

        byte[] imsiBytes = FormatUtils.getInstance().longToBytes(imsi);
        FormatUtils.getInstance().reverseData(imsiBytes);


        ByteArrayBuffer byteArray = new ByteArrayBuffer(15);
        byteArray.append(protocolBytes, 0, protocolBytes.length);
        byteArray.append(monitorModeBytes, 0, monitorModeBytes.length);
        byteArray.append(targetNumberBytes, 0, targetNumberBytes.length);
        byteArray.append(esnBytes, 0, esnBytes.length);
        byteArray.append(imsiBytes, 0, imsiBytes.length);

        byte[] bytes = byteArray.toByteArray();

        sendData(MsgType.SEND_MONITOR_CMD, bytes);

    }


    /**
     * 设置增益
     *
     * @param power 0：高增益  1：中增益 2：低增益 3：调试增益
     */
    public static void setPower(byte power) {
        sendData(MsgType.SEND_SET_POWERLEV, new byte[]{power});
    }

    /**
     * 锁定小区
     *
     * @param cid 小区cid
     * @param fcn
     */
    public static void lockCell(int cid,int fcn) {
        byte[] cidsBytes = new byte[12];
        byte[] tempCidBytes = FormatUtils.getInstance().intToByteArray(cid);
        cidsBytes[0] = tempCidBytes[3];
        cidsBytes[1] = tempCidBytes[2];
        cidsBytes[2] = tempCidBytes[1];
        cidsBytes[3] = tempCidBytes[0];

        byte[] fcnsBytes = new byte[6];
        Arrays.fill(fcnsBytes, (byte) 0x00);
        byte[] temFcnBytes = FormatUtils.getInstance().hexStringToBytes(FormatUtils.getInstance().intToHexString(fcn));
        fcnsBytes[0] = temFcnBytes[1];
        fcnsBytes[1] = temFcnBytes[0];


        ByteArrayBuffer byteArray = new ByteArrayBuffer(18);
        byteArray.append(cidsBytes, 0, cidsBytes.length);
        byteArray.append(fcnsBytes, 0, fcnsBytes.length);

        byte[] bytes = byteArray.toByteArray();

        LogUtils.log("锁定小区：cid="+cid+",频点="+fcn);
        sendData(MsgType.SEND_TARGET_CELL_SET, bytes);
    }




    public static void sendData(short msgType) {
        sendData(msgType, null);
    }

    /**
     * @param msgType 发送数据
     * @param data
     */
    public static void sendData(short msgType, byte[] data) {
        if (magic == null) {
            LogUtils.log("网络未连接");
            return;
        }

        byte[] magic = CacheManager.magic;

        //自增序列
        if (id >= 65535) {
            id = 0;
        }

        id++;
        byte[] msgId = FormatUtils.getInstance().intToByteArray(id);
        FormatUtils.getInstance().reverseData(msgId);

        //消息长度
        byte[] msgLengthBytes;
        if (data != null && data.length > 0) {
            msgLengthBytes = FormatUtils.getInstance().intToByteArray(data.length);

        } else {
            msgLengthBytes = new byte[4];
        }
        FormatUtils.getInstance().reverseData(msgLengthBytes);

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


        int packageLength = 82;
        if (data != null && data.length > 0) {
            packageLength += data.length;
        }

        ByteArrayBuffer byteArray = new ByteArrayBuffer(packageLength);

        byteArray.append(magic, 0, magic.length);
        byteArray.append(msgId, 0, msgId.length);
        byteArray.append(msgLengthBytes, 0, msgLengthBytes.length);
        byteArray.append(type, 0, type.length);
        byteArray.append(crc, 0, crc.length);
        byteArray.append(deviceName, 0, deviceName.length);
        byteArray.append(gpsInfo, 0, gpsInfo.length);
        byteArray.append(reserve, 0, reserve.length);

        if (data != null && data.length > 0) {
            byteArray.append(data, 0, data.length);
        }

        byte[] bytes = byteArray.toByteArray();

        Logger.d("发送数据："+FormatUtils.getInstance().bytesToHexString(bytes));

        LogUtils.log("发送数据"+Integer.toHexString(msgType)+":"+FormatUtils.getInstance().bytesToHexString(bytes));

        SocketUtils.getInstance().sendData(bytes);


    }

}
