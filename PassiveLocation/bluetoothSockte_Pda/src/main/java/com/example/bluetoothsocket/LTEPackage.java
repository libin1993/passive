package com.example.bluetoothsocket;

import java.util.Arrays;

/**
 * Author：Libin on 2020/6/9 15:44
 * Email：1993911441@qq.com
 * Describe：数据包
 */
public class LTEPackage {
    private byte[] magic = new byte[4];  //默认：00 FF FF 00   4字节
    private int id;     //自增序列   4字节
    private int dataLength;    //数据长度（消息内容部分）   4字节
    private short type = 0xA1;   //消息类型    2字节
    private byte[] crc = new byte[4];   //校验码     4字节
    private byte[] deviceName = new byte[16];    //采集设备（板卡）编号  16字节
    private byte[] gpsInfo = new byte[32];  //gps
    private byte[] reserve = new byte[16];  //预留字段   16字节

    private byte[]  data;  //包内容   可变长度


    public static final int HEAD_SIZE = 82;  //包头长度  82字节

    public LTEPackage() {
    }


    public byte[] getMagic() {
        return magic;
    }

    public void setMagic(byte[] magic) {
        this.magic = magic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public byte[] getCrc() {
        return crc;
    }

    public void setCrc(byte[] crc) {
        this.crc = crc;
    }

    public byte[] getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(byte[] deviceName) {
        this.deviceName = deviceName;
    }

    public byte[] getGpsInfo() {
        return gpsInfo;
    }

    public void setGpsInfo(byte[] gpsInfo) {
        this.gpsInfo = gpsInfo;
    }

    public byte[] getReserve() {
        return reserve;
    }

    public void setReserve(byte[] reserve) {
        this.reserve = reserve;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LTEPackage{" +
                "magic=" + Arrays.toString(magic) +
                ", id=" + id +
                ", dataLength=" + dataLength +
                ", type=" + type +
                ", crc=" + Arrays.toString(crc) +
                ", deviceName=" + Arrays.toString(deviceName) +
                ", gpsInfo=" + Arrays.toString(gpsInfo) +
                ", reserve=" + Arrays.toString(reserve) +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
