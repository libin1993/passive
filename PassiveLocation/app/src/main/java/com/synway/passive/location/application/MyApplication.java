package com.synway.passive.location.application;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.hrst.sdk.HrstSdkCient;
import com.hrst.sdk.dto.report.CellInfosReport;
import com.hrst.sdk.dto.report.LocationInfoReport;
import com.hrst.sdk.dto.report.SysStatusReport;
import com.hrst.sdk.dto.request.CellSearchRequest;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.DiskLogStrategy;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.synway.passive.location.bean.DaoMaster;
import com.synway.passive.location.bean.DaoSession;
import com.synway.passive.location.greendao.MyOpenHelper;

import java.io.File;
import java.lang.reflect.Constructor;


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

        initLogger();



    }

    private void initLogger() {
        String diskPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String folder = diskPath + File.separatorChar + "PassiveLocation/log";
        HandlerThread ht = new HandlerThread("AndroidFileLogger." + folder);
        ht.start();
        try {
            //通过反射实例化DiskLogStrategy中的内部类WriteHandler
            Class<?> clazz = Class.forName("com.orhanobut.logger.DiskLogStrategy$WriteHandler");
            Constructor constructor = clazz.getDeclaredConstructor(Looper.class, String.class, int.class);
            //开启强制访问
            constructor.setAccessible(true);
            //核心：通过构造函数，传入相关属性，得到WriteHandler实例
            Handler handler = (Handler) constructor.newInstance(ht.getLooper(), folder, 10000 * 1024);
            //创建缓存策略
            FormatStrategy strategy = CsvFormatStrategy.newBuilder()
                    .logStrategy(new DiskLogStrategy(handler))
                    .tag("synway")
                    .build();
            DiskLogAdapter adapter = new DiskLogAdapter(strategy);
            Logger.addLogAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            ht.quit();
        }

        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag("synway")                  //（可选）每个日志的全局标记。 默认PRETTY_LOGGER（如上图）
                .showThreadInfo(false)
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
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

