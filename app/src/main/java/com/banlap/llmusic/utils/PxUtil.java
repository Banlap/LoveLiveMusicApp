package com.banlap.llmusic.utils;

import android.content.Context;

/**
 * 像素转换工具类
 * */
public class PxUtil {

    public static PxUtil getInstance() {
        return new PxUtil();
    }

    /** dp转换px像素 */
    public int dp2px(int value, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
        //return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,context.getResources().getDisplayMetrics() );
    }

    /** px像素转换dp */
    public int px2dp(int value, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (value / scale + 0.5f);
    }

}
