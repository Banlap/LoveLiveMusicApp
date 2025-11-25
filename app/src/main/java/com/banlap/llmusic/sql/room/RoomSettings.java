package com.banlap.llmusic.sql.room;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Update;

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
    //是否背景模式标记
    @ColumnInfo(name = "isBGScene")
    public String isBGScene;
    //壁纸URI
    @ColumnInfo(name = "backgroundUri")
    public String backgroundUri;
    //关闭启动视频标记
    @ColumnInfo(name = "closeLaunchVideo")
    public String closeLaunchVideo;
    //启动视频自定义路径
    @ColumnInfo(name ="launchVideoPath")
    public String launchVideoPath;
    //每日推荐的日期
    @ColumnInfo(name = "recommendDate")
    public String recommendDate;
    //定时任务中歌曲播放后是否停止
    @ColumnInfo(name = "taskAfterMusicSwitch")
    public String taskAfterMusicSwitch;

    @Dao
    public interface SettingsDao {
        @Insert
        void insert(RoomSettings roomSettings);

        @Delete
        void delete(RoomSettings roomSettings);

        @Update
        void update(RoomSettings roomSettings);

        @Query("SELECT * FROM settings LIMIT 1")
        RoomSettings getFirstData();

    }
}


