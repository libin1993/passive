package com.synway.passive.location.greendao;


import com.synway.passive.location.application.MyApplication;
import com.synway.passive.location.bean.NameListBean;
import com.synway.passive.location.bean.NameListBeanDao;
import com.synway.passive.location.bean.UserBean;
import com.synway.passive.location.bean.UserBeanDao;

import java.util.List;


/**
 * Author：Li Bin on 2019/7/18 17:22
 * Description：
 */
public class SQLiteUtils {
    private static SQLiteUtils instance;

    private UserBeanDao userBeanDao;
    private NameListBeanDao nameListBeanDao;

    private SQLiteUtils() {
        userBeanDao = MyApplication.getInstance().getDaoSession().getUserBeanDao();
        nameListBeanDao = MyApplication.getInstance().getDaoSession().getNameListBeanDao();
    }

    public static SQLiteUtils getInstance() {
        if (instance == null) {
            synchronized (SQLiteUtils.class) {
                if (instance == null) {
                    instance = new SQLiteUtils();
                }
            }
        }
        return instance;
    }

    public void insertUser(UserBean userBean) {
        userBeanDao.insertOrReplace(userBean);
    }

    public void insertUsers(List<UserBean> userList) {
        userBeanDao.insertOrReplaceInTx(userList);
    }


    public void updateUser(UserBean userBean) {
        userBeanDao.update(userBean);
    }

    public void insertNameList(NameListBean nameListBean){
        nameListBeanDao.insertOrReplace(nameListBean);
    }
    public NameListBean queryNameList(String phone){
       return nameListBeanDao.queryBuilder().where(NameListBeanDao.Properties.Phone.eq(phone)).unique();
    }

    public List<NameListBean> queryAllNameList(){
        return nameListBeanDao.queryBuilder().list();
    }

    public void deleteNameList(NameListBean nameListBean){
         nameListBeanDao.delete(nameListBean);
    }

    public List<NameListBean> querySimilarNumber(String phone){
        return nameListBeanDao.queryBuilder().where(NameListBeanDao.Properties.Phone.like("%"+phone+"%")).list();
    }

}
