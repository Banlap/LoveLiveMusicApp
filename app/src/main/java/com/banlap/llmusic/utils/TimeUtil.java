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

    /**
     * 获取当前时间
     * @return 日期 格式为 yyyy-MM-dd HH:mm:ss
     * */
    public static String getCurrentDateStr() {
        String currentTimeStr = "";
        try {
            SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String date = DEFAULT_DATE_FORMAT.format(new Date());
            if(!TextUtils.isEmpty(date)) {
                currentTimeStr = date;
            }
        } catch (Exception e) {
            Log.e(TAG, "e: " + e.getMessage());
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
                Log.e(TAG, "e: " + e.getMessage());
            }
        }
        return false;
    }



}
