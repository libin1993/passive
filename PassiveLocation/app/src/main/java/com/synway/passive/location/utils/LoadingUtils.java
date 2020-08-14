package com.synway.passive.location.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

import com.synway.passive.location.widget.LoadingDialog;


/**
 * Author：Libin on 2019/6/6 09:56
 * Description：
 */
public class LoadingUtils {
    private static LoadingUtils mInstance;

    private LoadingDialog.Builder mLoadBuilder;
    private LoadingDialog mDialog;


    private LoadingUtils() {
    }

    public static LoadingUtils getInstance() {
        if (mInstance == null) {
            synchronized (LoadingUtils.class) {
                if (mInstance == null) {
                    mInstance = new LoadingUtils();
                }
            }
        }
        return mInstance;
    }


    /**
     * 显示等待框
     */
    public void showLoading(Context context, String message) {
        dismiss();

        mLoadBuilder = new LoadingDialog.Builder(context)
                .setMessage(message)
                .setCancelable(true)
                .setCancelOutside(false);
        mDialog = mLoadBuilder.create();
        mDialog.show();
    }


    /**
     * 隐藏等待框
     */
    public void dismiss() {
        if (mDialog == null) {
            return;
        }

        Context context = ((ContextWrapper) mDialog.getContext()).getBaseContext();

        if (context instanceof Activity) {
            if (((Activity) context).isFinishing())  //如果当前Activity已被销毁，跳出
                return;
        }

        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }


}
