package com.synway.passive.location.bean;

/**
 * Author：Libin on 2020/8/12 12:59
 * Email：1993911441@qq.com
 * Describe：
 */
public class DeviceStatusBean {
    private byte status;
    private byte temperatureDIG;
    private byte temperatureRF;
    private byte electricity;
    private String msg;

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public byte getTemperatureDIG() {
        return temperatureDIG;
    }

    public void setTemperatureDIG(byte temperatureDIG) {
        this.temperatureDIG = temperatureDIG;
    }

    public byte getTemperatureRF() {
        return temperatureRF;
    }

    public void setTemperatureRF(byte temperatureRF) {
        this.temperatureRF = temperatureRF;
    }

    public byte getElectricity() {
        return electricity;
    }

    public void setElectricity(byte electricity) {
        this.electricity = electricity;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "DeviceStatusBean{" +
                "status=" + status +
                ", temperatureDIG=" + temperatureDIG +
                ", temperatureRF=" + temperatureRF +
                ", electricity=" + electricity +
                ", msg='" + msg + '\'' +
                '}';
    }
}
