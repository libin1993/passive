package com.synway.passive.location.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.synway.passive.location.R;


/**
 * Author：Libin on 2019/6/6 09:46
 * Description：
 */
public class LoadingDialog extends Dialog {
    public LoadingDialog(Context context) {
        super(context);
    }

    public LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }


    public static class Builder {
        private Context context;
        private String message;
        private boolean isShowMessage = true;
        private boolean isCancelable = false;
        private boolean isCancelOutside = false;

        public Builder(Context context) {
            this.context = context;
        }

        public LoadingDialog.Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public LoadingDialog.Builder setShowMessage(boolean isShowMessage) {
            this.isShowMessage = isShowMessage;
            return this;
        }

        public LoadingDialog.Builder setCancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        public LoadingDialog.Builder setCancelOutside(boolean isCancelOutside) {
            this.isCancelOutside = isCancelOutside;
            return this;
        }

        public LoadingDialog create() {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            View view = inflater.inflate(R.layout.layout_dialog_loading, null);
            LoadingDialog loadingDialog = new LoadingDialog(this.context, R.style.LoadingDialogStyle);
            TextView msgText = view.findViewById(R.id.tipTextView);
            if (this.isShowMessage) {
                msgText.setText(this.message);
            } else {
                msgText.setVisibility(View.INVISIBLE);
            }

            loadingDialog.setContentView(view);
            loadingDialog.setCancelable(this.isCancelable);
            loadingDialog.setCanceledOnTouchOutside(this.isCancelOutside);
            return loadingDialog;
        }
    }
}
