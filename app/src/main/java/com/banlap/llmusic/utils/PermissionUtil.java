package com.banlap.llmusic.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.banlap.llmusic.ui.MainActivity;

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
}
