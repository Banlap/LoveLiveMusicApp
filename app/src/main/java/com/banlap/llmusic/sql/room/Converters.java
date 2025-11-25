package com.banlap.llmusic.sql.room;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    @TypeConverter
    public static String fromMusicList(List<RoomMusic> musicList) {
        if (musicList == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<RoomMusic>>() {}.getType();
        return gson.toJson(musicList, type);
    }

    @TypeConverter
    public static List<RoomMusic> toMusicList(String musicListString) {
        if (musicListString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<RoomMusic>>() {}.getType();
        return gson.fromJson(musicListString, type);
    }
}