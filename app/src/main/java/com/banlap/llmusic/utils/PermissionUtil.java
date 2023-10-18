package com.banlap.llmusic.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

/**
 * 权限检查工具类
 * */
public class PermissionUtil {

    public static PermissionUtil getInstance() { return new PermissionUtil(); }

    /**
     * 检查权限是否开启
     *
     * @param permissions 传递需要检查的权限
     * @return true需要开启权限 / false无需开启权限
     * */
    public boolean checkPermission(Context context, String... permissions) {
        //验证是否许可权限
        for (String str : permissions) {
            if (ContextCompat.checkSelfPermission(context, str) != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                return true;
            }
        }
        return false;
    }

    public void intoMIUIPermission(Context context) {
        if (Build.MANUFACTURER.equals("Xiaomi")) {
            Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", context.getPackageName());
            context.startActivity(intent);
        }
    }
}
