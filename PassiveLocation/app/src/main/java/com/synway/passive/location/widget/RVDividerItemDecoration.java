package com.synway.passive.location.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.synway.passive.location.R;


/**
 * Author：Libin on 2019/6/11 16:48
 * Description：
 */
public class RVDividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int padding = 0;
    private int headerCount = 0;


    public RVDividerItemDecoration(Context context, int padding) {
        this.mDivider = ContextCompat.getDrawable(context, R.drawable.rv_divider_white);
        this.padding = padding;
    }
    public RVDividerItemDecoration(Context context, int padding,int dividerColor) {
        this.mDivider = ContextCompat.getDrawable(context, dividerColor);
        this.padding = padding;
    }


    public RVDividerItemDecoration(Context context) {
        this.mDivider = ContextCompat.getDrawable(context, R.drawable.rv_divider_white);
    }


    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int left = parent.getPaddingLeft() + padding;
        int right = parent.getWidth() - parent.getPaddingRight() - padding;
        int childCount = parent.getChildCount();

        for (int i = headerCount; i < childCount - 1; ++i) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + this.mDivider.getIntrinsicHeight();
            this.mDivider.setBounds(left, top, right, bottom);
            this.mDivider.draw(c);
        }
    }
}
