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
    public static int vendor = 1;

    public static  boolean isLocation = false;

    public static boolean is5G = false;

    public static String[] styleArr ={"制式一","制式二","制式三"};
    public static String[] vendorArr ={"移动","联通","电信"};
    public static Integer[] timesArr ={1,2,5,10,20,30};
    public static Integer[] timeoutArr ={30,40,50,60,90};
    public static Integer[] intervalArr ={3,5,10,15,20,25};
    public static Integer[] detectArr ={30,60,90,120};


    public static boolean isSearched(){
        for (Map.Entry<String, CellBean> entry : CacheManager.cellMap.entrySet()) {
            if (entry.getKey().equals(lac+","+cid)){
                return true;
            }
        }

        return false;
    }

    public static CellBean getCell(){
        for (Map.Entry<String, CellBean> entry : CacheManager.cellMap.entrySet()) {
            if (entry.getKey().equals(lac+","+cid)){
                return entry.getValue();
            }
        }

        return null;
    }
}
