package com.banlap.llmusic.utils;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.banlap.llmusic.ui.SelectImgActivity;

import java.io.File;

/**
 * 裁剪图片启动类
 * */
public class SelectImgHelper {

    private final String TAG = SelectImgHelper.class.getSimpleName();
    public static SelectImgHelper getInstance() { return new SelectImgHelper(); }

    public static final String DEFAULT_URL = "file://" + Environment.getExternalStorageDirectory().getPath() + "/tmp.png";

    /**
     * 选择图片并裁剪
     * */
    public void startSelectImg(Context context, SelectImgListener listener) {
        SelectImgActivity.start(context, listener);
    }

    public interface SelectImgListener {
        void onSuccess(Bitmap bitmap);  //选择图片并裁剪成功
        void onError();
        void onNeedPermission(Context context, String[] permission, SelectImgListener listener);  //需要获取权限
    }
}
