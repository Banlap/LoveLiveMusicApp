package com.banlap.llmusic.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


import java.util.ArrayList;
import java.util.List;

/**
 * @author Banlap on 2021/8/19
 */
public class SPUtil {

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


    /*
     * 获取List<T>本地数据
     * */
    public static <T> List<T> getListValue(Context context, String key, Class<T> cls) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        List<T> list = new ArrayList<>();
        String listJson = sharedPreferences.getString(key, null);
        if(listJson == null) {
            return list;
        }
        Gson gson = new Gson();
        JsonArray array = new JsonParser().parse(listJson).getAsJsonArray();
        for (JsonElement element : array) {
            list.add(gson.fromJson(element,cls));
        }
        return list;
    }

    /*
     * 将List<T> 保存到本地
     * */
    public static <T> void setListValue(Context context, String key, List<T> list) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String listJson = gson.toJson(list);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, listJson);
        editor.apply();
    }
}
