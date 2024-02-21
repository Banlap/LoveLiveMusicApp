package com.banlap.llmusic.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;

import com.banlap.llmusic.receiver.ScreenReceiver;

import java.util.ArrayList;
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

    /**
     * 是否已经开启弹窗权限
     * @return true 已开启
     * */
    public boolean isCanDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    /**
     * 获取当前app版本code
     * @return 版本号
     * */
    public int getAppVersionCode(Context context) {
        int versionCode=0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 判断服务是否开启
     *
     * @param mContext 上下文
     * @param className 服务class名
     * @return true:开启 false:未开启
     */
    public boolean isServiceWorked(Context mContext, String className) {
        ArrayList<ActivityManager.RunningServiceInfo> runningService = getRunningService(mContext);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(className)) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<ActivityManager.RunningServiceInfo> getRunningService(Context mContext) {
        ActivityManager myManager = (ActivityManager) mContext
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
       return (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(30);
    }
}
