package com.banlap.llmusic.utils;

import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 * */
public class TimeUtil {

    private static final String TAG = TimeUtil.class.getSimpleName();

    public static String getCurrentDateByTime() {
        return getCurrentDateStr("HH:mm");
    }

    public static String getCurrentDateByMD() {
        return getCurrentDateStr("YYYY年MM月dd日");
    }

    /**
     * 获取当前时间
     * @return 日期 格式为 yyyy-MM-dd HH:mm:ss
     * */
    public static String getCurrentDateStr() {
        return getCurrentDateStr("yyyy-MM-dd HH:mm:ss");
    }
    /**
     * 获取当前时间
     * @param type 根具类型获取时间
     * */
    public static String getCurrentDateStr(String type) {
        String currentTimeStr = "";
        try {
            SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(type, Locale.CHINA);
            String date = DEFAULT_DATE_FORMAT.format(new Date());
            if(!TextUtils.isEmpty(date)) {
                currentTimeStr = date;
            }
        } catch (Exception e) {
            Log.i(TAG, "e: " + e.getMessage());
        }
        return currentTimeStr;
    }

    /**
     * 判断当前时间与传递的时间差 小时制 （格式 yyyy-MM-dd HH:mm:ss）
     * @param time 要判断的时间
     * @param hour 判断的小时
     * @return true = 超过  false = 不超过
     * */
    public static boolean isCheckTime(String time, int hour) {
        if(!TextUtils.isEmpty(time)) {
            try {
                SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                Date lastDate = DEFAULT_DATE_FORMAT.parse(time);
                if(lastDate != null) {
                    long lastHours = lastDate.getTime() / (1000 * 3600);
                    long currentHours = System.currentTimeMillis() / (1000 * 3600);
                    if(Math.abs(currentHours - lastHours) > hour) {
                        return true;
                    }
                }
            } catch (ParseException e) {
                Log.i(TAG, "e: " + e.getMessage());
            }
        }
        return false;
    }


    /**
     * 转换成时间格式 long类型转换
     * @return 分:秒  mm:ss
     * */
    public static String rebuildTime(long position) {
        long minLong = position /1000/60;
        long secLong = position /1000%60;
        String minStr = minLong <10 ? "0"+minLong : ""+minLong;
        String secStr = secLong <10 ? "0"+secLong : ""+secLong;
        return minStr + ":" + secStr;
    }

    /**
     * 转换为秒 long类型转换
     * @return s
     * */
    public static int showSec(long position) {
        long minLong = position /1000/60;
        long secLong = position /1000%60;
        String minStr = minLong <10 ? "0"+minLong : ""+minLong;
        String secStr = secLong <10 ? "0"+secLong : ""+secLong;
        if(minLong >0) {
            return (Integer.parseInt(secStr) + (Integer.parseInt(minStr) * 60));
        }
        return Integer.parseInt(secStr);
    }



}
