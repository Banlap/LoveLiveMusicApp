package com.banlap.llmusic.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据缓存工具类
 * @author Banlap on 2021/8/19
 */
public class SPUtil {
    public static final String SaveThemeId = "SaveThemeId";  //保存主题ID
    public static final String SavePlayMode = "SavePlayMode";  //保存播放模式
    public static final String SaveControllerScene = "SaveControllerScene"; //保存控制器场景
    public static final String isBGScene = "isBGScene"; //是否背景模式标记
    public static final String BackgroundUri = "BackgroundUri";  //壁纸URI
    public static final String CloseLaunchVideo = "CloseLaunchVideo"; //关闭启动视频标记
    public static final String LaunchVideoPath = "LaunchVideoPath"; //启动视频自定义路径
    public static final String RecommendDate = "RecommendDate";  //每日推荐的日期
    public static final String TaskAfterMusicSwitch = "TaskAfterMusicSwitch"; //定时任务中歌曲播放后是否停止

    public static final String LocalPlayListData = "LocalPlayListData"; //自建歌单缓存列表
    public static final String LocalListData = "LocalListData"; //本地歌曲缓存列表
    public static final String FavoriteListData = "FavoriteListData"; //最爱歌曲缓存列表
    public static final String PlayListData = "PlayListData";  //播放的歌曲缓存列表
    public static final String RecommendListData = "RecommendListData"; //每日歌曲缓存列表
    public static final String DownloadMusicListData = "DownloadMusicListData"; //音乐下载列表

    //由于滑动按钮频繁存储数据，以下功能使用缓存存储数据
    public static final String DefaultLyricSizeData = "DefaultLyricSizeData"; //默认歌词字体大小
    public static final String SingleLyricSizeData = "SingleLyricSizeData"; //滚动行歌词字体大小
    public static final String DetailLyricSizeData = "DetailLyricSizeData"; //明细歌词字体大小

    /**
     * 各团歌曲总数
     * */
    public static final String MusicNewAllTotalByLiella = "MusicNewAllTotalByLiella";
    public static final String MusicNewAllTotalByLiyuu = "MusicNewAllTotalByLiyuu";
    public static final String MusicNewAllTotalBySunnyPassion = "MusicNewAllTotalBySunnyPassion";
    public static final String MusicNewAllTotalByNIJIGASAKI = "MusicNewAllTotalByNIJIGASAKI";
    public static final String MusicNewAllTotalByAqours = "MusicNewAllTotalByAqours";
    public static final String MusicNewAllTotalByUS = "MusicNewAllTotalByUS";
    public static final String MusicNewAllTotalByHASUNOSORA = "MusicNewAllTotalByHASUNOSORA";
    public static final String MusicNewAllTotalBySAINTSNOW = "MusicNewAllTotalBySAINTSNOW";
    public static final String MusicNewAllTotalByARISE = "MusicNewAllTotalByARISE";
    public static final String MusicNewAllTotalByOther = "MusicNewAllTotalByOther";


    /**
     * ---------默认变量值----------------
     * */
    public static final String SaveControllerSceneValue_DefaultScene = "DefaultScene";
    public static final String SaveControllerSceneValue_NewScene = "NewScene";
    public static final String SaveControllerSceneValue_FloatingScene = "FloatingScene";

    private static final String TAG = SPUtil.class.getSimpleName();

    public static String getStrValue(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static void setStrValue(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static int getIntValue(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    public static void setIntValue(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /*
     * 获取List<T>本地数据
     * */
    public static <T> List<T> getListValue(Context context, String key, Class<T> cls) {
        List<T> list = new ArrayList<>();
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

            String listJson = sharedPreferences.getString(key, null);
            if(listJson == null) {
                return list;
            }
            Gson gson = new Gson();
            JsonArray array = new JsonParser().parse(listJson).getAsJsonArray();
            for (JsonElement element : array) {
                list.add(gson.fromJson(element,cls));
            }
        } catch (Exception e) {
            Log.e(TAG, "获取本地数据异常(同时重置数据)：" + e.getMessage());
            setListValue(context, key, list);
        }
        return list;
    }

    /*
     * 将List<T> 保存到本地
     * */
    public static <T> void setListValue(Context context, String key, List<T> list) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String listJson = gson.toJson(list);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, listJson);
            editor.apply();
        } catch (Exception e) {
            Log.e(TAG, "设置本地数据异常：" + e.getMessage());
        }
    }

}
