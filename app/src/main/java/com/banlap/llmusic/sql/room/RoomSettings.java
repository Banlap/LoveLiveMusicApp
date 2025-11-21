package com.banlap.llmusic.sql.room;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
/**
 * 本地数据库表：settings
 * */
@Entity(tableName = "settings")
public class RoomSettings {
    @PrimaryKey(autoGenerate = true)  // 添加 autoGenerate = true
    public int id;
    //保存主题ID
    @ColumnInfo(name = "saveThemeId")
    public String saveThemeId;
    //保存播放模式
    @ColumnInfo(name = "savePlayMode")
    public String savePlayMode;
    //保存控制器场景
    @ColumnInfo(name = "saveControllerScene")
    public String saveControllerScene;

    @Dao
    public interface SettingsDao {
        @Insert
        void insert(RoomSettings roomSettings);

        @Delete
        void delete(RoomSettings roomSettings);

        @Update
        void update(RoomSettings roomSettings);

        @Query("SELECT * FROM settings")
        List<RoomSettings> getAllSettings();
    }
}


