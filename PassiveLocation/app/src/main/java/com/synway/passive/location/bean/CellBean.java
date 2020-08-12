package com.synway.passive.location.bean;

/**
 * Author：Libin on 2020/8/11 19:45
 * Email：1993911441@qq.com
 * Describe：
 */
public class CellBean {
    private byte vendor;
    private byte protocol;
    private int freq;
    private int lac;
    private int cid;
    private int pci;
    private short dbm;
    private byte snr;
    private int offset;
    private byte status;
    private byte stateType;
    private byte cellColorFlag;
    private byte cellErrorRate;
    private short union1;
    private short union2;
    private short union3;
    private short union4;
    private byte hit;
    private short lteDecSibErrRate;

    public CellBean() {
    }


    public byte getVendor() {
        return vendor;
    }

    public void setVendor(byte vendor) {
        this.vendor = vendor;
    }

    public byte getProtocol() {
        return protocol;
    }

    public void setProtocol(byte protocol) {
        this.protocol = protocol;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq =  freq;
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

    public int getPci() {
        return pci;
    }

    public void setPci(int pci) {
        this.pci = pci;
    }

    public short getDbm() {
        return dbm;
    }

    public void setDbm(short dbm) {
        this.dbm = dbm;
    }

    public byte getSnr() {
        return snr;
    }

    public void setSnr(byte snr) {
        this.snr = snr;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public byte getStateType() {
        return stateType;
    }

    public void setStateType(byte stateType) {
        this.stateType = stateType;
    }

    public byte getCellColorFlag() {
        return cellColorFlag;
    }

    public void setCellColorFlag(byte cellColorFlag) {
        this.cellColorFlag = cellColorFlag;
    }

    public byte getCellErrorRate() {
        return cellErrorRate;
    }

    public void setCellErrorRate(byte cellErrorRate) {
        this.cellErrorRate = cellErrorRate;
    }

    public short getUnion1() {
        return union1;
    }

    public void setUnion1(short union1) {
        this.union1 = union1;
    }

    public short getUnion2() {
        return union2;
    }

    public void setUnion2(short union2) {
        this.union2 = union2;
    }

    public short getUnion3() {
        return union3;
    }

    public void setUnion3(short union3) {
        this.union3 = union3;
    }

    public short getUnion4() {
        return union4;
    }

    public void setUnion4(short union4) {
        this.union4 = union4;
    }

    public byte getHit() {
        return hit;
    }

    public void setHit(byte hit) {
        this.hit = hit;
    }

    public short getLteDecSibErrRate() {
        return lteDecSibErrRate;
    }

    public void setLteDecSibErrRate(short lteDecSibErrRate) {
        this.lteDecSibErrRate = lteDecSibErrRate;
    }

    @Override
    public String toString() {
        return "CellBean{" +
                "vendor=" + vendor +
                ", protocol=" + protocol +
                ", freq=" + freq +
                ", lac=" + lac +
                ", cid=" + cid +
                ", pci=" + pci +
                ", dbm=" + dbm +
                ", snr=" + snr +
                ", offset=" + offset +
                ", status=" + status +
                ", stateType=" + stateType +
                ", cellColorFlag=" + cellColorFlag +
                ", cellErrorRate=" + cellErrorRate +
                ", union1=" + union1 +
                ", union2=" + union2 +
                ", union3=" + union3 +
                ", union4=" + union4 +
                ", hit=" + hit +
                ", lteDecSibErrRate=" + lteDecSibErrRate +
                '}';
    }
}
