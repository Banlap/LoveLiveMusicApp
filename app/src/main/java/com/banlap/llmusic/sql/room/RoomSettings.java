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
    @ColumnInfo(name = "save_theme_id")
    public String saveThemeId;
    //保存播放模式
    @ColumnInfo(name = "save_play_mode")
    public String savePlayMode;
    //保存控制器场景
    @ColumnInfo(name = "save_controller_scene")
    public String saveControllerScene;
    //是否背景模式标记
    @ColumnInfo(name = "is_bg_scene")
    public String isBGScene;
    //壁纸URI
    @ColumnInfo(name = "background_uri")
    public String backgroundUri;
    //关闭启动视频标记
    @ColumnInfo(name = "close_launch_video")
    public String closeLaunchVideo;
    //启动视频自定义路径
    @ColumnInfo(name ="launch_video_path")
    public String launchVideoPath;
    //每日推荐的日期
    @ColumnInfo(name = "recommend_date")
    public String recommendDate;
    //定时任务中歌曲播放后是否停止
    @ColumnInfo(name = "task_after_music_switch")
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


