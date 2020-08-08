package com.synway.passive.location.socket;


import com.synway.passive.location.utils.FormatUtils;
import com.synway.passive.location.utils.LogUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Zxc on 2018/10/18.
 */

public class LTEDataParse {
    //将字节数暂存
    private ArrayList<Byte> listReceiveBuffer = new ArrayList<Byte>();
    //包头的长度
    private int packageHeadLength = 82;


    //解析数据
    public synchronized void parseData(byte[] bytesReceived, int receiveCount) {
        //将接收到数据存放在列表中
        for (int i = 0; i < receiveCount; i++) {
            listReceiveBuffer.add(bytesReceived[i]);
        }

        while (true) {
            //得到当前缓存中的长度
            int listReceiveCount = listReceiveBuffer.size();

            //如果缓存长度小于82说明最小包都没有收完整
            if (listReceiveCount < packageHeadLength) {
                break;
            }

            //接收到反序数组，取出长度
            byte[] contentLength = {listReceiveBuffer.get(11), listReceiveBuffer.get(10), listReceiveBuffer.get(9), listReceiveBuffer.get(8)};
            int contentLen = FormatUtils.getInstance().byteToInt(contentLength) + 82;


            LogUtils.log("分包大小：" + listReceiveBuffer.size() + "," + contentLen);
            //判断缓存列表中的数据是否达到一个包的数据
            if (listReceiveBuffer.size() < contentLen) {
                LogUtils.log("LTE没有达到整包数:");
                break;
            }

            byte[] tempPackage = new byte[contentLen];
            //取出一个整包
            for (int j = 0; j < contentLen; j++) {
                tempPackage[j] = listReceiveBuffer.get(j);
            }

            //删除内存列表中的数据
            if (contentLen > 0) {
                listReceiveBuffer.subList(0, contentLen).clear();
            }

            //解析包
            parsePackageData(tempPackage);
        }

    }


    //解析成包数据
    private void parsePackageData(byte[] tempPackage) {
        if (tempPackage.length < 82)
            return;

        LTEPackage ltePackage = new LTEPackage();

        //magic  默认
        byte[] tempMagic = new byte[4];
        System.arraycopy(tempPackage, 0, tempMagic, 0, 4);
        reverse(tempMagic);
        ltePackage.setMagic(tempMagic);


        //自增序列
        byte[] tempId = new byte[4];
        System.arraycopy(tempPackage, 4, tempId, 0, 4);
        reverse(tempId);
        int id = FormatUtils.getInstance().byteToInt(tempId);
        ltePackage.setId(id);


        //真实数据长度（消息内容部分）
        byte[] tempDataLength = new byte[4];
        System.arraycopy(tempPackage, 8, tempDataLength, 0, 4);
        reverse(tempDataLength);
        int dataLength = FormatUtils.getInstance().byteToInt(tempDataLength);
        ltePackage.setDataLength(dataLength);

        //消息类型
        byte[] tempType = new byte[2];
        System.arraycopy(tempPackage, 12, tempType, 0, 2);
        reverse(tempType);
        ltePackage.setType(FormatUtils.getInstance().byteToShort(tempType));


        //crc
        byte[] tempCrc = new byte[4];
        System.arraycopy(tempPackage, 14, tempCrc, 0, 4);
        reverse(tempCrc);
        ltePackage.setCrc(tempCrc);

        //设备编号
        byte[] tempDeviceName = new byte[16];
        System.arraycopy(tempPackage, 28, tempDeviceName, 0, 16);
        reverse(tempDeviceName);
        ltePackage.setDeviceName(tempDeviceName);

        //GPS信息
        byte[] tempGpsInfo= new byte[32];
        System.arraycopy(tempPackage, 34, tempGpsInfo, 0, 32);
        reverse(tempGpsInfo);
        ltePackage.setGpsInfo(tempGpsInfo);

        //协议代码
        byte[] tempReserve = new byte[16];
        System.arraycopy(tempPackage, 66, tempReserve, 0, 16);
        ltePackage.setReserve(tempReserve);

        //包内容
        byte[] tempData = new byte[dataLength];
        System.arraycopy(tempPackage, 82, tempData, 0, dataLength);
        ltePackage.setData(tempData);


        LogUtils.log(FormatUtils.bytesToHexString(tempData));
        if (ltePackage.getType() == 0xA3){
            byte[] a = new byte[tempData.length - 4];
            System.arraycopy(tempData, 4, a, 0, tempData.length - 4);
            LogUtils.log(FormatUtils.bytesToHexString(a));
            try {
                String b= new String(a,"utf-8");
                LogUtils.log("包内容："+b);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }



        LogUtils.log(ltePackage.toString());


    }

    private void reverse(byte[] data){
        for (int start = 0, end = data.length - 1; start < end; start++, end--) {
            byte temp = data[end];
            data[end] = data[start];
            data[start] = temp;
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





    public void clearReceiveBuffer() {
        LogUtils.log("clearReceiveBuffer... ...");
        listReceiveBuffer.clear();
    }
}
