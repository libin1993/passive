package com.synway.passive.location.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.synway.passive.location.R;
import com.synway.passive.location.bean.PhoneNumberBean;
import com.synway.passive.location.bean.UserBean;

import java.util.List;

public class ManagePhoneNumberAdapter extends BaseQuickAdapter<PhoneNumberBean, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param layoutResId The layout resource id of each item.
     */
    public ManagePhoneNumberAdapter(int layoutResId) {
        super(layoutResId);
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseViewHolder helper, PhoneNumberBean item) {
        helper.setText(R.id.tvOrder,getParentPosition(item));
        helper.setText(R.id.tvName,item.getName());
        helper.setText(R.id.tvPhoneNumber,item.getPhoneNumber());
    }
}