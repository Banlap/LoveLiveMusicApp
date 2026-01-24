package com.banlap.llmusic.sql.room;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * 播放列表
 * */
@Entity(tableName = "favorite_music")
public class RoomFavoriteMusic {
    @PrimaryKey
    @ColumnInfo(name = "id")
    public long id;
    @ColumnInfo(name = "music_id")
    public int musicId;
    @ColumnInfo(name = "music_type")
    public String musicType;
    @ColumnInfo(name = "music_name")
    public String musicName;
    @ColumnInfo(name = "music_singer")
    public String musicSinger;
    @ColumnInfo(name = "music_url")
    public String musicURL;
    @ColumnInfo(name = "music_img")
    public String musicImg;
    @ColumnInfo(name = "music_lyric")
    public String musicLyric;
    @Ignore
    public int musicFavorite;
    @ColumnInfo(name = "is_local")
    public boolean isLocal;
    @Ignore
    public boolean isDelete;
    @ColumnInfo(name = "music_img_byte")
    public byte[] musicImgByte;
    @Ignore
    public Bitmap musicImgBitmap;
    @ColumnInfo(name = "music_bitrate")
    public String musicBitrate;
    @ColumnInfo(name = "music_mime")
    public String musicMime;
    @ColumnInfo(name = "music_file_size")
    public String musicFileSize;
    @ColumnInfo(name = "music_quality")
    public String musicQuality;

    @Dao
    public interface FavoriteMusicDao {
        @Insert
        void insert(RoomFavoriteMusic roomPlayMusic);

        @Delete
        void delete(RoomFavoriteMusic roomPlayMusic);

        @Query("DELETE FROM favorite_music")
        void deleteAll();

        @Update
        void update(RoomFavoriteMusic roomPlayMusic);

        @Query("SELECT * FROM favorite_music where music_id = :id")
        RoomFavoriteMusic getMusicById(String id);

        @Query("SELECT * FROM favorite_music where music_name = :musicName and music_singer = :musicSinger")
        RoomFavoriteMusic getMusicByNameAndSinger(String musicName, String musicSinger);

        @Query("SELECT * FROM favorite_music ORDER BY id ASC")
        List<RoomFavoriteMusic> getAllMusic();
    }
}
