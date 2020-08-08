package com.synway.passive.location.bean;

/**
 * Author：Libin on 2020/8/8 15:14
 * Email：1993911441@qq.com
 * Describe：
 */
public class BluetoothBean {
    private String name;
    private String address;
    private int bondState;
    private int rssi;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getBondState() {
        return bondState;
    }

    public void setBondState(int bondState) {
        this.bondState = bondState;
    }

    public BluetoothBean(String name, String address, int bondState, int rssi) {
        this.name = name;
        this.address = address;
        this.bondState = bondState;
        this.rssi = rssi;
    }

    @Override
    public String toString() {
        return "BluetoothBean{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", bondState='" + bondState + '\'' +
                ", rssi=" + rssi +
                '}';
    }
}
