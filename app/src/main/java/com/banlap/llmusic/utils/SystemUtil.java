package com.banlap.llmusic.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.DisplayMetrics;

import com.banlap.llmusic.receiver.ScreenReceiver;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 系统工具类
 * */
public class SystemUtil {
    private final String TAG = SystemUtil.class.getSimpleName();
    private ScreenReceiver screenReceiver;
    public static SystemUtil getInstance() {
        return new SystemUtil();
    }

    /**
     * 获取DisplayMetrics
     * */
    public DisplayMetrics getDM(Activity activity) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            return dm;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取设备最小宽度
     *<P>
     * 小屏幕手机：一般在 4 英寸到 4.5 英寸之间。
     * <P>
     * 最小宽度范围：320dp - 360dp
     *<P>
     * 中等屏幕手机：一般在 5 英寸到 5.5 英寸之间。
     * <P>
     * 最小宽度范围：360dp - 400dp
     *<P>
     * 大屏幕手机、小屏幕平板：一般在 6 英寸到 7 英寸之间。
     * <P>
     * 最小宽度范围：400dp - 480dp
     *<P>
     * 中等屏幕平板：一般在 8 英寸到 9 英寸之间。
     * <P>
     * 最小宽度范围：600dp - 720dp
     *<P>
     * 大屏幕平板：一般在 10 英寸以上。
     * <P>
     * 最小宽度范围：720dp 以上
     * */
    public int getScreenWidthDp(Activity activity) {
        try {
            DisplayMetrics dm = getDM(activity);
            return (int) (dm.widthPixels / dm.density);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 是否小屏幕手机
     * <P>
     * 最小宽度范围：320dp - 360dp
     * */
    public boolean isSmallScaleDevice() {
        int getScreenWidthDp = getScreenWidthDp(LLActivityManager.getInstance().getTopActivity());
        if(getScreenWidthDp == -1) return false;
        return (getScreenWidthDp(LLActivityManager.getInstance().getTopActivity()) <= 360);
    }

    /**
     * 注册屏幕监测广播
     * */
    public void registerScreenReceiver(Context context) {
        screenReceiver = new ScreenReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        context.registerReceiver(screenReceiver, intentFilter);
    }

    /**
     * 解除屏幕监测广播
     * */
    public void unRegisterScreenReceiver(Context context) {
        if(screenReceiver != null) {
            context.unregisterReceiver(screenReceiver);
        }
    }


}
