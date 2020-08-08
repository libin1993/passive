package com.synway.passive.location.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Author：Libin on 2019/6/11 17:09
 * Description：
 */
public class DateUtils {
    public static final String standard_time_format = "yyyy-MM-dd HH:mm:ss";  //标准时间格式
    public static final String hour_minute_second = "HH:mm:ss";  //时分秒
    private static DateUtils mIntance;

    private DateUtils() {
    }

    public static DateUtils getInstance() {
        if (mIntance == null) {
            synchronized (DateUtils.class) {
                if (mIntance == null) {
                    mIntance = new DateUtils();
                }
            }
        }
        return mIntance;
    }

    /**
     * @param time
     * @return 字符串转时间戳毫秒
     */
    public long getTime(String type, String time) {

        try {
            Calendar c = Calendar.getInstance();
            c.setTime(new SimpleDateFormat(type).parse(time));
            return c.getTimeInMillis();
        } catch (ParseException e) {

        }
        return 0;
    }


    /**
     * @return 时间格式转换
     */
    public String formatTime(String type, String formatType, String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }

        String format = time.replaceAll("T", " ");
        long time1 = getTime(type, format);
        return getDate(formatType, time1);
    }


    /**
     * @param type
     * @param time
     * @return 获取日期
     */
    public String getDate(String type, long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(type, Locale.CHINA);

        return sdf.format(new Date(time));
    }

}
