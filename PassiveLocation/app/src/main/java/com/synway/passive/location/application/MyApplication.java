package com.synway.passive.location.application;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.synway.passive.location.bean.DaoMaster;
import com.synway.passive.location.bean.DaoSession;
import com.synway.passive.location.greendao.MyOpenHelper;


/**
 * Author：Libin on 2019/7/2 15:52
 * Description：
 */
public class MyApplication extends Application {
    private static MyApplication mContext;

    private DaoSession mDaoSession;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Fresco.initialize(this);



        //调用Application里面的上下文   参数二为数据库名字
        MyOpenHelper helper = new MyOpenHelper(this, "location.db", null);

        SQLiteDatabase database = helper.getWritableDatabase();

        mDaoSession = new DaoMaster(database).newSession();
    }


    public static MyApplication getInstance() {
        return mContext;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    public DaoSession getDaoSession() {
        return mDaoSession;
    }
}

