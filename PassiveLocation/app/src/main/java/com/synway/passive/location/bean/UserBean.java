package com.synway.passive.location.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Author：Libin on 2020/8/8 13:26
 * Email：1993911441@qq.com
 * Describe：
 */
@Entity(nameInDb = "user")
public class UserBean {
    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "account")
    private String account;

    @Property(nameInDb = "password")
    private String password;

    @Property(nameInDb = "remark")
    private int remark;

    @Generated(hash = 1556085517)
    public UserBean(Long id, String account, String password, int remark) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.remark = remark;
    }

    @Generated(hash = 1203313951)
    public UserBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRemark() {
        return this.remark;
    }

    public void setRemark(int remark) {
        this.remark = remark;
    }


}
