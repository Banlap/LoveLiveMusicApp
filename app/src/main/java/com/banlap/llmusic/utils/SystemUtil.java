package com.banlap.llmusic.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

public class SystemUtil {
    private final String TAG = SystemUtil.class.getSimpleName();
    public static SystemUtil getInstance() {
        return new SystemUtil();
    }

    /**
     * 获取DisplayMetrics
     * */
    public DisplayMetrics getDM(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
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
        DisplayMetrics dm = getDM(activity);
        return (int) (dm.widthPixels / dm.density);
    }

    /**
     * 是否小屏幕手机
     * <P>
     * 最小宽度范围：320dp - 360dp
     * */
    public boolean isSmallScaleDevice() {
        return (getScreenWidthDp(LLActivityManager.getInstance().getTopActivity()) <= 360);
    }
}
