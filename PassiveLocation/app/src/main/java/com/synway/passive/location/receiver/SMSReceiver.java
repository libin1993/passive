package com.synway.passive.location.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;
import com.synway.passive.location.socket.LteSendManager;


/**
 * Author：Libin on 2020/8/18 18:56
 * Email：1993911441@qq.com
 * Describe：
 */
public class SMSReceiver extends BroadcastReceiver {
    private final static String ACTION_SMS_SEND = "lab.sodino.sms.send";
    private final static String ACTION_SMS_DELIVERY = "lab.sodino.sms.delivery";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action){
            case ACTION_SMS_SEND:
                Logger.d("短信发送结果：发送成功");
                LteSendManager.stopTrigger();
                break;
            case ACTION_SMS_DELIVERY:
                Logger.d("短信发送结果：对方接收成功");

                break;
        }
    }
}
