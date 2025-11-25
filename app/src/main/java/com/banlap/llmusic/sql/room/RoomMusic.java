package com.banlap.llmusic.sql.room;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Entity(tableName = "music")
public class RoomMusic {
    @PrimaryKey
    @ColumnInfo(name = "musicId")
    public int musicId;
    @ColumnInfo(name = "musicType")
    public String musicType;
    @ColumnInfo(name = "musicName")
    public String musicName;
    @ColumnInfo(name = "musicSinger")
    public String musicSinger;
    @ColumnInfo(name = "musicURL")
    public String musicURL;
    @ColumnInfo(name = "musicImg")
    public String musicImg;
    @ColumnInfo(name = "musicLyric")
    public String musicLyric;
    @ColumnInfo(name = "isPlaying")
    public boolean isPlaying;
    @ColumnInfo(name = "isLocal")
    public boolean isLocal;
    @ColumnInfo(name = "isDelete")
    public boolean isDelete;
    @ColumnInfo(name = "musicImgByte")
    public byte[] musicImgByte;
    @ColumnInfo(name = "musicBitrate")
    public String musicBitrate;
    @ColumnInfo(name = "musicMime")
    public String musicMime ;
    @ColumnInfo(name = "musicFileSize")
    public String musicFileSize;
    @ColumnInfo(name = "musicQuality")
    public String musicQuality;

    @Dao
    public interface MusicDao {
        @Insert
        void insert(RoomMusic roomMusic);

        @Delete
        void delete(RoomMusic roomMusic);

        @Update
        void update(RoomMusic roomMusic);

        @Query("SELECT * FROM music where musicId = :id")
        RoomMusic getMusicById(String id);

        @Query("SELECT * FROM music")
        List<RoomMusic> getAllMusic();
    }
}
