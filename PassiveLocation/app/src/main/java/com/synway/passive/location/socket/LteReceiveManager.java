package com.synway.passive.location.socket;


import com.synway.passive.location.utils.CacheManager;
import com.synway.passive.location.utils.FormatUtils;
import com.synway.passive.location.utils.LogUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Author：Libin on 2020/6/10 11:05
 * Email：1993911441@qq.com
 * Describe：数据接收
 */
public class LteReceiveManager {
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
        ltePackage.setMagic(tempMagic);
        CacheManager.magic = tempMagic;


        //自增序列
        byte[] tempId = new byte[4];
        System.arraycopy(tempPackage, 4, tempId, 0, 4);
        int id = FormatUtils.getInstance().byteToInt(tempId);
        ltePackage.setId(id);


        //真实数据长度（消息内容部分）
        byte[] tempDataLength = new byte[4];
        System.arraycopy(tempPackage, 8, tempDataLength, 0, 4);
        FormatUtils.getInstance().reverseData(tempDataLength);
        int dataLength = FormatUtils.getInstance().byteToInt(tempDataLength);
        ltePackage.setDataLength(dataLength);

        //消息类型
        byte[] tempType = new byte[2];
        System.arraycopy(tempPackage, 12, tempType, 0, 2);
        FormatUtils.getInstance().reverseData(tempType);
        ltePackage.setType(FormatUtils.getInstance().byteToShort(tempType));


        //crc
        byte[] tempCrc = new byte[4];
        System.arraycopy(tempPackage, 14, tempCrc, 0, 4);
        ltePackage.setCrc(tempCrc);

        //设备编号
        byte[] tempDeviceName = new byte[16];
        System.arraycopy(tempPackage, 28, tempDeviceName, 0, 16);
        ltePackage.setDeviceName(tempDeviceName);
        CacheManager.deviceName = tempDeviceName;

        //GPS信息
        byte[] tempGpsInfo = new byte[32];
        System.arraycopy(tempPackage, 34, tempGpsInfo, 0, 32);
        ltePackage.setGpsInfo(tempGpsInfo);
        CacheManager.gpsInfo = tempGpsInfo;

        //预留位
        byte[] tempReserve = new byte[16];
        System.arraycopy(tempPackage, 66, tempReserve, 0, 16);
        ltePackage.setReserve(tempReserve);

        LogUtils.log("消息类型：" + ltePackage.getType());
        //包内容
        if (dataLength > 0) {
            byte[] tempData = new byte[dataLength];
            System.arraycopy(tempPackage, 82, tempData, 0, dataLength);
            ltePackage.setData(tempData);
            LogUtils.log(FormatUtils.bytesToHexString(tempData));


            if (ltePackage.getType() == MsgType.RCV_SHOW_VERSION) {
                byte[] a = new byte[tempData.length - 1];
                System.arraycopy(tempData, 1, a, 0, tempData.length - 1);
                LogUtils.log(FormatUtils.bytesToHexString(a));
                String b = new String(a, StandardCharsets.UTF_8);
                LogUtils.log(b);

            }
        }
<<<<<<< HEAD

        LogUtils.log("小区个数："+CacheManager.cellMap.size());
    }
=======
>>>>>>> c628df228c0de7242fcb722add8c7a769319314d

        LteSendManager.sendData(MsgType.SEND_SERVER_HEART_BEAT);
        LteSendManager.sendData(MsgType.SEND_SHOW_VERSION);


<<<<<<< HEAD
    /**
     * @param ltePackage 定位数据上报
     */
    public  static void parseLocation(LtePackage ltePackage){
        LocationInfoBean locationBean = new LocationInfoBean();
        byte[] locationBytes = ltePackage.getData();

        locationBean.setFreq(FormatUtils.getInstance().byteToShort(new byte[]{locationBytes[1],locationBytes[0]}) & 0x0FFFF);
        locationBean.setLac(FormatUtils.getInstance().byteToShort(new byte[]{locationBytes[3],locationBytes[2]}) & 0x0FFFF);
        locationBean.setCid(FormatUtils.getInstance().byteToInt(new byte[]{locationBytes[7],locationBytes[6],locationBytes[5],locationBytes[4]}));
        locationBean.setPowerOverFlow(locationBytes[37]);
        List<Short> dbmList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            short dbm = FormatUtils.getInstance().byteToShort(new byte[]{locationBytes[9+2*i],locationBytes[8+2*i]});

            LogUtils.log("dbm："+dbm);
            if (dbm !=0){
                dbmList.add(dbm);
            }

        }
        locationBean.setDbm(dbmList);
        locationBean.setColor(locationBytes[82]);
        locationBean.setPci(FormatUtils.getInstance().byteToShort(new byte[]{locationBytes[84],locationBytes[83]}) & 0x0FFFF);
=======


//        if (ltePackage.getType() == 0xA3){
//            byte[] a = new byte[tempData.length - 4];
//            System.arraycopy(tempData, 4, a, 0, tempData.length - 4);
//            LogUtils.log(FormatUtils.bytesToHexString(a));
//            try {
//                String b= new String(a,"utf-8");
//
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//
//
//            LTESendManager.sendData(MsgType.SEND_SERVER_HEART_BEAT,null);
//
//        }


        LogUtils.log(ltePackage.toString());

>>>>>>> c628df228c0de7242fcb722add8c7a769319314d

    }


    /**
     * 将字节数组转换为String
     *
     * @param tempValue
     * @return
     */
    public static String bytesToString(byte[] tempValue) {
        StringBuffer result = new StringBuffer();
        if (tempValue == null || tempValue.length == 0) {
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
