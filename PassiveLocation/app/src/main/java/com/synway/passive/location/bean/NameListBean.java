package com.synway.passive.location.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Author：Libin on 2020/8/18 10:34
 * Email：1993911441@qq.com
 * Describe：
 */
@Entity(nameInDb = "name_list")
public class NameListBean implements Serializable {
    private static final long serialVersionUID = -6459818205932625733L;
    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "name")
    private String name;

    @Property(nameInDb = "phone")
    private String phone;

    @Property(nameInDb = "vendor")
    private int vendor;

    @Property(nameInDb = "remark")
    private String remark;

    @Generated(hash = 54643855)
    public NameListBean(Long id, String name, String phone, int vendor,
            String remark) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.vendor = vendor;
        this.remark = remark;
    }

    @Generated(hash = 888774045)
    public NameListBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getVendor() {
        return this.vendor;
    }

    public void setVendor(int vendor) {
        this.vendor = vendor;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "NameListBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", vendor=" + vendor +
                ", remark='" + remark + '\'' +
                '}';
    }
}
