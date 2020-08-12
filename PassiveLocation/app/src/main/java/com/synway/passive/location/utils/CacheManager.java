package com.synway.passive.location.utils;

import com.synway.passive.location.bean.CellBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：Libin on 2020/8/10 08:51
 * Email：1993911441@qq.com
 * Describe：
 */
public class CacheManager {
    public static byte[] magic;
    public static byte[] deviceName;
    public static byte[] gpsInfo;
    public static List<CellBean> cellList = new ArrayList<>();
}
