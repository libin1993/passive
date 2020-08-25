package com.synway.passive.location.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hrst.sdk.HrstSdkCient;
import com.orhanobut.logger.Logger;
import com.synway.passive.location.socket.LteSendManager;
import com.synway.passive.location.socket.MsgType;
import com.synway.passive.location.utils.CacheManager;
import com.synway.passive.location.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;


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
                LogUtils.log("短信发送结果：发送成功");
                if (CacheManager.is5G){
                    HrstSdkCient.sendTriggerEnd(0);
                    EventBus.getDefault().post(MsgType.TRIGGER_SUCCESS);
                }else {
                    LteSendManager.stopTrigger();
                }

                break;
            case ACTION_SMS_DELIVERY:
                LogUtils.log("短信发送结果：已接收");
                EventBus.getDefault().post(MsgType.DETECT_SEND_MSG);
                break;
        }
    }
}
