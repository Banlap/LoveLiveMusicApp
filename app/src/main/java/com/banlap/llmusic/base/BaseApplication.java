package com.banlap.llmusic.base;

import android.app.Application;
import android.content.Context;
import android.net.Uri;


import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.FileNameGenerator;

import java.io.File;

import jp.wasabeef.glide.transformations.internal.Utils;

public class BaseApplication extends Application {
    private HttpProxyCacheServer proxy;


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


}
