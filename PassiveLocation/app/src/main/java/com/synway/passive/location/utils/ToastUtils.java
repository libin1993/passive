package com.synway.passive.location.utils;

import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;


import com.synway.passive.location.application.MyApplication;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Author：Libin on 2019/6/4 11:11
 * Email：1993911441@qq.com
 * Describe：Toast重复弹出
 */
public class ToastUtils {
    private static ToastUtils mInstance;
    private Toast mToast;
    private boolean isReflectedHandler = false;

    private ToastUtils() {
    }

    public static ToastUtils getInstance() {
        if (mInstance == null) {
            synchronized (ToastUtils.class) {
                if (mInstance == null) {
                    mInstance = new ToastUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * @param msg
     */
    public void showToast(String msg) {

        if (TextUtils.isEmpty(msg)) {
            return;
        }

        if (mToast == null) {
            mToast = Toast.makeText(MyApplication.getInstance().getApplicationContext(), msg, Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToast.setText(msg);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < Build.VERSION_CODES.O && !isReflectedHandler) {
            reflectTNHandler(mToast);
            //这里为了避免多次反射，使用一个标识来限制
            isReflectedHandler = true;
            return;
        }
        mToast.show();
    }


    /**
     * 防止在android7.x系统会偶发Toast显示异常报BadTokenException
     *
     * @param toast
     */
    private  void reflectTNHandler(Toast toast) {
        try {
            Field tNField = toast.getClass().getDeclaredField("mTN");
            if (tNField == null) {


                return;
            }
            tNField.setAccessible(true);
            Object TN = tNField.get(toast);
            if (TN == null) {
                return;
            }
            Field handlerField = TN.getClass().getDeclaredField("mHandler");
            if (handlerField == null) {
                return;
            }
            handlerField.setAccessible(true);
            handlerField.set(TN, new ProxyTNHandler(TN));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private  class ProxyTNHandler extends Handler {
        private Object tnObject;
        private Method handleShowMethod;
        private Method handleHideMethod;

        ProxyTNHandler(Object tnObject) {
            this.tnObject = tnObject;
            try {
                this.handleShowMethod = tnObject.getClass().getDeclaredMethod("handleShow", IBinder.class);
                this.handleShowMethod.setAccessible(true);
                this.handleHideMethod = tnObject.getClass().getDeclaredMethod("handleHide");
                this.handleHideMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    //SHOW
                    IBinder token = (IBinder) msg.obj;
                    if (handleShowMethod != null) {
                        try {
                            handleShowMethod.invoke(tnObject, token);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (WindowManager.BadTokenException e) {
                            //显示Toast时添加BadTokenException异常捕获
                            e.printStackTrace();
                        }
                    }
                    break;
                }

                case 1: {
                    //HIDE
                    if (handleHideMethod != null) {
                        try {
                            handleHideMethod.invoke(tnObject);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case 2: {
                    //CANCEL
                    if (handleHideMethod != null) {
                        try {
                            handleHideMethod.invoke(tnObject);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }

            }
            super.handleMessage(msg);
        }
    }
}
