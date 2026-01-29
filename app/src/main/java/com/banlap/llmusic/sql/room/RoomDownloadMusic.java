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
@Entity(tableName = "download_music")
public class RoomDownloadMusic {
    //
    @PrimaryKey(autoGenerate = true)  // 添加 autoGenerate = true
    public int id;
    @ColumnInfo(name = "download_id")
    public String downloadId;
    //文件名称
    @ColumnInfo(name = "file_name")
    public String fileName;
    //下载链接
    @ColumnInfo(name = "url")
    public String url;
    /**
     * 下载状态: 0=成功 1=正在下载 2=失败 3=等待
     * */
    @ColumnInfo(name = "status")
    public String status;

    /**
     * 下载成功
     * */
    public static final String DownloadSuccess = "0";
    /**
     * 正在下载
     * */
    public static final String Downloading = "1";
    /**
     * 下载失败
     * */
    public static final String DownloadError = "2";
    /**
     * 等待下载
     * */
    public static final String DownloadWaiting = "3";

    @Dao
    public interface DownloadMusicDao {
        @Insert
        void insert(RoomDownloadMusic roomDownloadMusic);

        @Delete
        void delete(RoomDownloadMusic roomDownloadMusic);

        @Query("DELETE FROM download_music")
        void deleteAll();

        @Update
        void update(RoomDownloadMusic roomDownloadMusic);

        @Query("SELECT * FROM download_music ORDER BY id DESC")
        List<RoomDownloadMusic> getAllMusic();

        @Query("SELECT * FROM download_music WHERE id = :id")
        RoomDownloadMusic getMusicById(int id);
    }
}
