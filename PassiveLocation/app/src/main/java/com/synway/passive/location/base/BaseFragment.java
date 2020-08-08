package com.synway.passive.location.base;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Author：Libin on 2019/05/31 09:34
 * Email：1993911441@qq.com
 * Describe：Fragment基类
 */
public class BaseFragment extends Fragment {
    private BaseActivity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
    }
}



