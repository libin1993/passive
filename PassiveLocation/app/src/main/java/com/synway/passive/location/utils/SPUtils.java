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

    public static final String ADMIN_ACCOUNT= "account"; //账号

    public static final String ADMIN_PASSWORD = "password"; //密码

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


    /**
     * 移除某个key值已经对应的值
     *
     * @param key
     */
    public void remove( String key) {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.clear();
        editor.apply();
    }


}
