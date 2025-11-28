package com.banlap.llmusic.sql.room;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    @TypeConverter
    public static String fromPlayMusicList(List<RoomPlayMusic> musicList) {
        if (musicList == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<RoomPlayMusic>>() {}.getType();
        return gson.toJson(musicList, type);
    }

    @TypeConverter
    public static List<RoomPlayMusic> toPlayMusicList(String musicListString) {
        if (musicListString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<RoomPlayMusic>>() {}.getType();
        return gson.fromJson(musicListString, type);
    }

    @TypeConverter
    public static String fromFavoriteMusicList(List<RoomFavoriteMusic> musicList) {
        if (musicList == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<RoomFavoriteMusic>>() {}.getType();
        return gson.toJson(musicList, type);
    }

    @TypeConverter
    public static List<RoomFavoriteMusic> toFavoriteMusicList(String musicListString) {
        if (musicListString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<RoomFavoriteMusic>>() {}.getType();
        return gson.fromJson(musicListString, type);
    }
}