package com.banlap.llmusic.base;

import android.app.Application;
import android.content.Context;
import android.net.Uri;


import com.banlap.llmusic.ui.activity.CustomErrorActivity;
import com.banlap.llmusic.ui.activity.WelcomeActivity;
import com.banlap.llmusic.utils.FileUtil;
import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.FileNameGenerator;

import java.io.File;

import cat.ereza.customactivityoncrash.config.CaocConfig;


public class BaseApplication extends Application {
    private HttpProxyCacheServer proxy;

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
