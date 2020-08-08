package com.synway.passive.location.utils;


import com.synway.passive.location.application.MyApplication;
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

    private SQLiteUtils() {
        userBeanDao = MyApplication.getInstance().getDaoSession().getUserBeanDao();
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

//    /**
//     * 查询最新五条数据
//     *
//     * @return
//     */
//    public List<HistoryAddress> query(int installType) {
//        return historyAddressDao.queryBuilder().where(HistoryAddressDao.Properties.User_id.eq(
//                SPUtils.getInstance().getAccountId()),HistoryAddressDao.Properties.Install_type
//                .eq(installType)).orderDesc(HistoryAddressDao.Properties.Id).limit(5).list();
//    }
//
//    public HistoryAddress queryAddress(int communityId) {
//        return historyAddressDao.queryBuilder().where(HistoryAddressDao.Properties.Community_id.eq(communityId),
//                HistoryAddressDao.Properties.User_id.eq(SPUtils.getInstance().getAccountId())).unique();
//    }
//
//
//    public void delete(HistoryAddress historyAddress) {
//        historyAddressDao.delete(historyAddress);
//    }

}
