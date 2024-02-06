package com.banlap.llmusic.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.banlap.llmusic.R;
import com.banlap.llmusic.ui.activity.CustomErrorActivity;
import com.banlap.llmusic.ui.activity.MainActivity;
import com.banlap.llmusic.ui.activity.WelcomeActivity;
import com.banlap.llmusic.utils.FileUtil;
import com.banlap.llmusic.utils.LLActivityManager;
import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.FileNameGenerator;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

import cat.ereza.customactivityoncrash.config.CaocConfig;


public class BaseApplication extends Application {
    private final String TAG = BaseApplication.class.getSimpleName();

    private HttpProxyCacheServer proxy;

    private boolean isInApp = false; //是否在应用前台中
    private long lastChangeTime = 0; //上次截图时间
    private AlertDialog mAlertDialog;                   //弹窗


    ContentObserver contentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            long currentTime = System.currentTimeMillis();
            Log.i(TAG, "isInApp: " + isInApp);

            // 媒体库内容变化，可能有新的截屏图片
            // 在这里进行处理
            if(isInApp && currentTime - lastChangeTime >= 1000) {
                Log.i(TAG, "在LLMusic应用中.. 有截屏呢: " + selfChange);
                // 更新上一次处理的时间
                lastChangeTime = currentTime;

                mAlertDialog = new AlertDialog.Builder(LLActivityManager.getInstance().getTopActivity(), ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT)
                        .setTitle("提示")
                        .setCancelable(false)
                        .setMessage("当前已截图，是否删除图片")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String screenshotsFolderPath = findScreenshotsFolder(Environment.getExternalStorageDirectory(), "Screenshots");

                                if (screenshotsFolderPath != null) {
                                    Toast.makeText(getApplicationContext(), "已删除: " + screenshotsFolderPath, Toast.LENGTH_SHORT).show();
                                    deleteLatestFile(screenshotsFolderPath);
                                } else {
                                    Toast.makeText(getApplicationContext(), "已删除", Toast.LENGTH_SHORT).show();
                                }

                                mAlertDialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAlertDialog.dismiss();
                            }
                        })
                        .create();

                mAlertDialog.show();
            }
        }
    };

    /**
     * 查询本机截图文件夹Screenshots
     * @param folderName 文件名称
     * */
    public String findScreenshotsFolder(File directory, String folderName) {

        File screenshotsFolder = new File(directory, folderName);
        if (screenshotsFolder.exists() && screenshotsFolder.isDirectory()) {
            return screenshotsFolder.getAbsolutePath();
        }

        // 遍历当前目录下的所有子目录
        File[] subDirectories = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });

        if (subDirectories != null) {
            for (File subDirectory : subDirectories) {
                // 递归搜索子目录
                String result = findScreenshotsFolder(subDirectory, folderName);
                if (result != null) {
                    return result;
                }
            }
        }

        // 如果在当前目录及其子目录中未找到 Screenshots 文件夹，则返回 null
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deleteLatestFile(String folderPath) {
        File folder = new File(folderPath);

        // 判断文件夹是否存在
        if (folder.exists() && folder.isDirectory()) {
            // 列出文件夹中的所有文件
            File[] files = folder.listFiles();

            // 按文件最后修改时间排序
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            // 删除最新的文件
            if (files.length > 0) {
                File latestFile = files[files.length - 1];
                if (latestFile.delete()) {
                    Log.i(TAG, "最新截图删除成功： " + latestFile.getAbsolutePath());
                } else {
                    Log.e(TAG, "最新截图删除失败： " + latestFile.getAbsolutePath());
                }
            } else {
                Log.e(TAG, "文件夹为空，无截图可删除： ");
            }
        } else {
            Log.e(TAG, "文件夹不存在或不是一个文件夹");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        CrashHandler crashHandler = new CrashHandler();
//        crashHandler.init();

        CaocConfig.Builder.create()
                .showErrorDetails(false)
                .restartActivity(null)
                .errorActivity(CustomErrorActivity.class)
                .apply();

        //注册ActivityLifecycleCallbacks
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
                Log.i(TAG, "onActivityCreated..");
                isInApp = true;
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                Log.i(TAG, "onActivityStarted..");
                isInApp = true;
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                // 应用切换到前台
                Log.i(TAG, "在LLMusic应用中..");
                isInApp = true;
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                // 应用切换到后台
                Log.i(TAG, "在LLMusic应用外..");
                isInApp = false;
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                Log.i(TAG, "onActivityStopped..");
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
                Log.i(TAG, "onActivitySaveInstanceState..");
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                Log.i(TAG, "onActivityDestroyed..");
                isInApp = false;
            }
        });

        // 注册观察者
//        getContentResolver().registerContentObserver(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                true,
//                contentObserver
//        );

    }

    public static HttpProxyCacheServer getProxy(Context context) {
        BaseApplication baseApplication = (BaseApplication) context.getApplicationContext();
        return baseApplication.proxy == null ? (baseApplication.proxy = baseApplication.newProxy()) : baseApplication.proxy;
    }


    private HttpProxyCacheServer newProxy() {
       /* return new HttpProxyCacheServer(this);*/
        return new HttpProxyCacheServer.Builder(this)
                .cacheDirectory(getAudioCacheDir(this))
                //.fileNameGenerator(new BaseNameGenerator())
                .build();
    }

    public class BaseNameGenerator implements FileNameGenerator { //缓存的命名规则
        public String generate(String url) {
            Uri uri = Uri.parse(url);
            String audioId = uri.getQueryParameter("guid");
            return audioId + ".mp3";
        }
    }


    /**
     * 音乐播放缓存目录的设置
     * @param context
     * @return
     */
    public static File getAudioCacheDir(Context context) {
        return new File(context.getExternalFilesDir("music"), "audio-cache");
    }

    /**
     * 异常捕获线程
     * */
    public static class CrashHandler implements Thread.UncaughtExceptionHandler {
        private Thread.UncaughtExceptionHandler defaultHandler;

        public void init() {
            defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(this);
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            // 处理异常信息，保存日志到本地
            saveCrashLogToFile(e);

            // 调用默认的异常处理器，以便应用程序正常退出
            if (defaultHandler != null) {
                defaultHandler.uncaughtException(t, e);
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }

        private void saveCrashLogToFile(Throwable e) {
            // 将异常信息保存到本地文件
            FileUtil.getInstance().saveCrashLogToFile(e);
        }
    }




}
