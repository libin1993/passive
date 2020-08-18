package com.synway.passive.location.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lxj.xpopup.core.CenterPopupView;
import com.synway.passive.location.R;
import com.synway.passive.location.utils.FormatUtils;
import com.synway.passive.location.utils.StatusBarUtils;

/**
 * Author：Libin on 2020/8/18 13:00
 * Email：1993911441@qq.com
 * Describe：
 */
public class CustomPopupWindow{
    private Context context;
    private String title = "";
    private String content="";
    private OnConfirmListener onConfirmListener;

    public CustomPopupWindow(Context context) {
        this.context = context;
    }

    public CustomPopupWindow setTitle(String title) {
        this.title = title;
        return this;
    }

    public CustomPopupWindow setContent(String content) {
        this.content = content;
        return this;
    }


    public CustomPopupWindow setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
        return this;
    }


    public void show() {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.layout_custom_popup, null);

        TextView tvTitle = view.findViewById(R.id.tv_popup_title);
        TextView tvContent = view.findViewById(R.id.tv_popup_content);
        TextView tvCancel = view.findViewById(R.id.tv_popup_cancel);
        TextView tvConfirm = view.findViewById(R.id.tv_popup_confirm);

        tvTitle.setText(title);
        tvContent.setText(content);

        final PopupWindow popupWindow = new PopupWindow(view, StatusBarUtils.getInstance()
                .getScreenWidth((Activity) this.context)- FormatUtils.getInstance().dp2px(40),
                ViewGroup.LayoutParams.WRAP_CONTENT);


        //设置Popup具体参数
        popupWindow.setFocusable(true);//点击空白，popup不自动消失
        popupWindow.setTouchable(true);//popup区域可触摸
        popupWindow.setOutsideTouchable(false);//非popup区域可触摸
        popupWindow.setBackgroundDrawable(new BitmapDrawable(this.context.getResources(), (Bitmap) null));
        popupWindow.showAtLocation(((Activity) this.context).getWindow().getDecorView(), Gravity.CENTER, 0, 0);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if (onConfirmListener !=null){
                    onConfirmListener.onConfirm();
                }
            }
        });
    }

    public interface OnConfirmListener{
        void onConfirm();
    }
}
