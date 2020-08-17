package com.synway.passive.location.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.synway.passive.location.application.MyApplication;


/**
 * Author：Libin on 2019/6/6 10:10
 * Description：
 */
public class SPUtils {

    private static SPUtils mInstance;  //单例对象

    public static final String ADMIN_ACCOUNT = "account"; //账号

    public static final String ADMIN_PASSWORD = "password"; //密码
    public static final String TRIGGER_STYLE = "trigger_style";//诱发格式
    public static final String TRIGGER_TIMES = "trigger_times";//诱发次数
    public static final String TRIGGER_TIMEOUT = "trigger_timeout";//诱发超时时间
    public static final String TRIGGER_INTERVAL = "trigger_interval";//诱发间隔
    public static final String DETECT_INTERVAL = "detect_interval";//检测间隔

    private SharedPreferences sp;


    private SPUtils() {
        sp = MyApplication.getInstance().getSharedPreferences(ADMIN_ACCOUNT, Context.MODE_PRIVATE);
    }  //私有化构造方法

    /**
     * 获取单例方式
     *
     * @return
     */
    public static SPUtils getInstance() {
        if (mInstance == null) {
            synchronized (SPUtils.class) {
                if (mInstance == null) {
                    mInstance = new SPUtils();
                }
            }
        }

        return mInstance;
    }


    /**
     * 向指定文件插入值
     *
     * @param key
     * @param object
     */
    public void put(String key, Object object) {

        if (object == null) {
            return;
        }
        LogUtils.log(object.toString());

        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        editor.apply();
    }


    /**
     * 得到指定文件中的key对应的value，如果没有则返回传递的默认值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public Object get(String key, Object defaultObject) {

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getFloat(key, (Long) defaultObject);
        }

        return null;
    }

    public int getTriggerStyle() {
        return sp.getInt(TRIGGER_STYLE, 2);
    }
    public int getTriggerTimes() {
        return sp.getInt(TRIGGER_TIMES, 2);
    }
    public int getTriggerTimeout() {
        return sp.getInt(TRIGGER_TIMEOUT, 0);
    }
    public int getTriggerInterval() {
        return sp.getInt(TRIGGER_INTERVAL, 1);
    }

    public int getDetectInterval() {
        return sp.getInt(DETECT_INTERVAL, 0);
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param key
     */
    public void remove(String key) {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.clear();
        editor.apply();
    }


}
