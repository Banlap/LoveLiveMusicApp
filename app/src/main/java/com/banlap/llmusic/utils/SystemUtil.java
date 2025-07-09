package com.banlap.llmusic.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.banlap.llmusic.receiver.ScreenReceiver;

import java.util.ArrayList;
import java.util.List;
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
     * 设备是否是平板
     *
     * @param context 上下文
     * @return 是平板则返回true，反之返回false
     */
    public static boolean isPad(Context context) {
        //新加条件
        boolean isPad = (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y); // 屏幕尺寸

        return isPad || screenInches >= 7.0;
    }

    public static boolean isOrientationPortrait() {

        return true;
    }


    /**
     * 判断底部状态栏是否显示
     *
     */
      public void hasNavigationBar(Activity activity, NavigationBarCallback callback) {

          if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
              boolean hasNavigationBarOld = false;
              int height=0;
              Resources resources = activity.getResources();
              int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
              if (resourceId > 0) {
                  height = resources.getDimensionPixelSize(resourceId);
                  hasNavigationBarOld = height > 0;
              }
              Log.d(TAG, "是否存在底部栏: " + hasNavigationBarOld +", 高度: " + height);
              callback.onResult(hasNavigationBarOld, height);
          } else {
              View decorView = activity.getWindow().getDecorView();
              decorView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {

                  @Override
                  public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                      // 检查导航栏是否可见
                      boolean hasNavigationBar = insets.isVisible(WindowInsets.Type.navigationBars());
                      // 获取导航栏高度
                      int navigationBarHeight = insets.getInsets(WindowInsets.Type.navigationBars()).bottom;
                      int navigationBarLeft = insets.getInsets(WindowInsets.Type.navigationBars()).left;
                      int navigationBarRight = insets.getInsets(WindowInsets.Type.navigationBars()).right;

                      // 你可以根据需要处理导航栏高度，例如存储到成员变量中
                      Log.d(TAG, "是否存在底部栏: " + hasNavigationBar +", 高度: " + navigationBarHeight);
                      Log.d(TAG, "左边: " + navigationBarLeft + " 右边: " + navigationBarRight);
                      callback.onResult(hasNavigationBar, navigationBarHeight);
                      return insets;
                  }
              });
          }

      }

      public  interface NavigationBarCallback {
          void onResult(boolean isShow, int height);
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
     * 获取版本名称
     * */
    public String getAppVersionName(Context context) {
        String versionName=null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
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

    /**
     * 判断应用是否已经启动
     * */
    public boolean isAppRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        return !tasks.isEmpty() && tasks.get(0).topActivity.getPackageName().equals(context.getPackageName());
    }
}
