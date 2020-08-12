package com.synway.passive.location.bean;

import java.util.List;

/**
 * Author：Libin on 2020/8/12 18:51
 * Email：1993911441@qq.com
 * Describe：
 */
public class LocationInfoBean {
    private int freq;
    private int lac;
    private int cid;
    private byte powerOverFlow;
    private List<Float> power;
    private byte color;
    private int pci;

    public int getFreq() {
        return freq;
    }

    public byte getPowerOverFlow() {
        return powerOverFlow;
    }

    public void setPowerOverFlow(byte powerOverFlow) {
        this.powerOverFlow = powerOverFlow;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public int getLac() {
        return lac;
    }

    public void setLac(int lac) {
        this.lac = lac;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public List<Float> getPower() {
        return power;
    }

    public void setPower(List<Float> power) {
        this.power = power;
    }

    public byte getColor() {
        return color;
    }

    public void setColor(byte color) {
        this.color = color;
    }

    public int getPci() {
        return pci;
    }

    public void setPci(int pci) {
        this.pci = pci;
    }

    @Override
    public String toString() {
        return "LocationInfoBean{" +
                "freq=" + freq +
                ", lac=" + lac +
                ", cid=" + cid +
                ", powerOverFlow=" + powerOverFlow +
                ", power=" + power +
                ", color=" + color +
                ", pci=" + pci +
                '}';
    }
}
