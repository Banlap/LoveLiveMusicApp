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
 * 每日列表
 * */
@Entity(tableName = "recommend_music")
public class RoomRecommendMusic {
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
    @ColumnInfo(name = "music_bitrate")
    public String musicBitrate;
    @ColumnInfo(name = "music_mime")
    public String musicMime;
    @ColumnInfo(name = "music_file_size")
    public String musicFileSize;
    @ColumnInfo(name = "music_quality")
    public String musicQuality;

    @Dao
    public interface RecommendMusicDao {
        @Insert
        void insert(RoomRecommendMusic roomRecommendMusic);

        @Delete
        void delete(RoomRecommendMusic roomRecommendMusic);

        @Query("DELETE FROM recommend_music")
        void deleteAll();

        @Update
        void update(RoomRecommendMusic roomRecommendMusic);

        @Query("SELECT * FROM recommend_music where music_id = :id")
        RoomRecommendMusic getMusicById(String id);

        @Query("SELECT * FROM recommend_music where music_name = :musicName and music_singer = :musicSinger")
        RoomRecommendMusic getMusicByNameAndSinger(String musicName, String musicSinger);

        @Query("SELECT * FROM recommend_music ORDER BY id ASC")
        List<RoomRecommendMusic> getAllMusic();
    }
}
