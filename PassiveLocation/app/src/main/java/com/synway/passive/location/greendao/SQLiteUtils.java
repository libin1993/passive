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
