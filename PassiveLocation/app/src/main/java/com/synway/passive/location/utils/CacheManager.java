package com.synway.passive.location.utils;

import com.synway.passive.location.bean.CellBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Author：Libin on 2020/8/10 08:51
 * Email：1993911441@qq.com
 * Describe：
 */
public class CacheManager {
    public static byte[] magic;
    public static byte[] deviceName;
    public static byte[] gpsInfo;
    public static Map<String, CellBean> cellMap = new HashMap<>();

    public static String phoneNumber;
    public static String lac;
    public static String cid;

    public static  boolean isLocation = false;
}
