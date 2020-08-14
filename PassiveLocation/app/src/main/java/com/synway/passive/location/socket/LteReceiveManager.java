package com.synway.passive.location.socket;


import com.synway.passive.location.bean.CellBean;
import com.synway.passive.location.bean.DeviceStatus;
import com.synway.passive.location.bean.DeviceStatusBean;
import com.synway.passive.location.bean.LocationInfoBean;
import com.synway.passive.location.utils.CacheManager;
import com.synway.passive.location.utils.FormatUtils;
import com.synway.passive.location.utils.LogUtils;
import com.synway.passive.location.utils.ToastUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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


        LtePackage ltePackage = new LtePackage();

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

        LogUtils.log("接收数据类型：" + Integer.toHexString(ltePackage.getType()));
        LogUtils.log("接收数据："+FormatUtils.getInstance().bytesToHexString(tempPackage));

        //包内容
        if (dataLength > 0) {
            byte[] tempData = new byte[dataLength];
            System.arraycopy(tempPackage, 82, tempData, 0, dataLength);
            ltePackage.setData(tempData);

        }


        LogUtils.log(ltePackage.toString());
        msgType(ltePackage);

    }

    /**
     * @param ltePackage  消息类型
     */
    public void msgType(LtePackage ltePackage){
            switch (ltePackage.getType()){
                case MsgType.RCV_SERVER_HEART_BEAT:
                    break;
                case MsgType.RCV_SHOW_VERSION:
                    parseVersion(ltePackage);
                    break;
                case MsgType.RCV_DEVICE_STATUS:
                    parseDeviceStatus(ltePackage);
                    break;
                case MsgType.RCV_CELL_SEARCH:
                    parseCommon(ltePackage);
                    break;
                case MsgType.RCV_CELL_INFO:
                    parseCell(ltePackage);
                    break;
                case MsgType.RCV_LOCATION_CMD:
                    parseCommon(ltePackage);

//                    LteSendManager.stopTrigger();
                    break;
                case MsgType.RCV_LOCATION_INFO:
                    parseLocation(ltePackage);
                    break;
                case MsgType.RCV_TRIGGER_ACK:
                    parseCommon(ltePackage);
                    break;
                case MsgType.RCV_SET_POWERLEV:
                    parseCommon(ltePackage);
                    break;
            }

    }

    /**
     * 常规消息接收
     */
    private void parseCommon(LtePackage ltePackage){
        LogUtils.log(ltePackage.getType()+":"+FormatUtils.getInstance().byteToInt(ltePackage.getData()));
    }

    /**
     * @param ltePackage
     */
    private void parseCell(LtePackage ltePackage){
        byte[] data = ltePackage.getData();

        byte[] tempCellNum = new byte[4];
        System.arraycopy(data, 0, tempCellNum, 0, 4);
        FormatUtils.getInstance().reverseData(tempCellNum);
        int cellNum = FormatUtils.getInstance().byteToInt(tempCellNum);
        LogUtils.log("小区数量："+cellNum);

        byte[] cellBytes = new byte[ltePackage.getDataLength() - 4];
        System.arraycopy(data, 4, cellBytes, 0, ltePackage.getDataLength() - 4);

        for (int i = 0; i < cellNum; i++) {
            CellBean cellBean = new CellBean();
            cellBean.setVendor(cellBytes[21*i]);
            cellBean.setProtocol(cellBytes[21*i+1]);
            cellBean.setFreq(FormatUtils.getInstance().byteToShort(new byte[]{cellBytes[21*i+3],cellBytes[21*i+2]}) & 0x0FFFF);
            cellBean.setLac(FormatUtils.getInstance().byteToShort(new byte[]{cellBytes[21*i+5],cellBytes[21*i+4]})& 0x0FFFF);
            cellBean.setCid(FormatUtils.getInstance().byteToInt(new byte[]{cellBytes[21*i+9],cellBytes[21*i+8],cellBytes[21*i+7],cellBytes[21*i+6]}));
            cellBean.setPci(FormatUtils.getInstance().byteToShort(new byte[]{cellBytes[21*i+11],cellBytes[21*i+10]})& 0x0FFFF);
            cellBean.setDbm(FormatUtils.getInstance().byteToShort(new byte[]{cellBytes[21*i+13],cellBytes[21*i+12]}));
            cellBean.setSnr(cellBytes[21*i+14]);
            cellBean.setOffset(FormatUtils.getInstance().byteToInt(new byte[]{cellBytes[21*i+18],cellBytes[21*i+17],cellBytes[21*i+16],cellBytes[21*i+15]}));
            cellBean.setStatus(cellBytes[21*i+19]);
            cellBean.setStateType(cellBytes[21*i+20]);

            cellBean.setCellColorFlag(cellBytes[i+840]);
            cellBean.setCellErrorRate(cellBytes[i+880]);
            cellBean.setHit(cellBytes[i+1120]);

            LogUtils.log(cellBean.toString());

           CacheManager.cellMap.put(cellBean.getLac()+","+cellBean.getCid(),cellBean);

        }

        LogUtils.log("小区个数："+CacheManager.cellMap.size());
    }



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

        LogUtils.log("定位数据上报:"+locationBean.toString());
    }

    /**
     * @param ltePackage 版本
     */
    private void parseVersion(LtePackage ltePackage){
        byte[] versionBytes = new byte[ltePackage.getDataLength() - 1];
        System.arraycopy(ltePackage.getData(), 1, versionBytes, 0, ltePackage.getDataLength() - 1);
        String version = new String(versionBytes, StandardCharsets.UTF_8);
        LogUtils.log("版本："+version);
    }

    /**
     * @param ltePackage 设备状态
     */
    private void parseDeviceStatus(LtePackage ltePackage){
        byte[] data = ltePackage.getData();
        DeviceStatusBean deviceStatus = new DeviceStatusBean();
        deviceStatus.setStatus(data[0]);
        deviceStatus.setTemperatureDIG(data[1]);
        deviceStatus.setTemperatureRF(data[2]);
        deviceStatus.setElectricity(data[3]);


        byte[] msgBytes = new byte[ltePackage.getDataLength()-4];
        System.arraycopy(ltePackage.getData(), 4, msgBytes, 0, ltePackage.getDataLength() - 4);
        String msg = new String(msgBytes, StandardCharsets.UTF_8);
        deviceStatus.setMsg(msg);
        LogUtils.log("设备状态："+deviceStatus.toString());
    }


    public void clearReceiveBuffer() {
        LogUtils.log("clearReceiveBuffer... ...");
        listReceiveBuffer.clear();
    }
}
