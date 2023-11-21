package com.banlap.llmusic.utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 倒计时帮助类
 * */
public class CountDownHelper {
    public static Timer timer;

    public static int countDown;  //倒计时值

    /**
     * 开始倒计时
     * @param time 倒计时值
     * @param callBack 回调
     * */
    public static void startCountTime(int time, CountDownCallBack callBack) {
        pauseImm();
        timer = new Timer();
        countDown = time;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                countDown--;
                if(countDown >0) {
                    callBack.showTime(countDown, rebuildTime(countDown));
                } else {
                    timer.cancel();
                    callBack.finish();
                }

            }
        }, 0, 1000);
    }

    public static void pauseImm() {
        if(timer != null) {
            timer.cancel();
        }
    }

    /**
     * 时间转换 格式 00:00:00
     * */
    public static String rebuildTime(int countDown) {
        int hour = countDown /3600;
        int min = countDown /60 %60;
        int sec = countDown %60;
        String hourStr = hour <10? "0" + hour : "" + hour;
        String minStr = min <10 ? "0" + min : "" + min;
        String secStr = sec <10 ? "0" + sec : "" + sec;
        return hourStr + ":" + minStr + ":" + secStr;
    }

    public interface CountDownCallBack {
        void showTime(int countDown, String time);
        void finish();
    }
}
