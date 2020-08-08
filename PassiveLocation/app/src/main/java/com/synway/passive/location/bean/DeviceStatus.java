package com.synway.passive.location.bean;

/**
 * Author：Libin on 2020/8/8 14:58
 * Email：1993911441@qq.com
 * Describe：设备状态 */
public class DeviceStatus {
    public static final int BLUETOOTH_CLOSED = 1;
    public static final int BLUETOOTH_OPEN = 2;
    public static final int BLUETOOTH_DISCONNECTED = 3;
    public static final int BLUETOOTH_CONNECTED = 4;
    public static final int BLUETOOTH_SOCKET_DISCONNECTED = 5;
    public static final int BLUETOOTH_SOCKET_CONNECTED = 6;
    public static final int SOCKET_DISCONNECTED = 7;
    public static final int SOCKET_CONNECTED = 8;

    public static int deviceStatus = BLUETOOTH_CLOSED;

}
