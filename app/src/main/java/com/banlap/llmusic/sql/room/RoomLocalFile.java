package com.banlap.llmusic.sql.room;

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

@Entity(tableName = "local_file")
public class RoomLocalFile {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "music_id")
    public int musicId;
    @ColumnInfo(name = "title")
    public String title;
    @ColumnInfo(name = "album")
    public String album;
    @ColumnInfo(name = "artist")
    public String artist;
    @ColumnInfo(name = "duration")
    public String duration;
    @ColumnInfo(name = "path")
    public String path;
    @ColumnInfo(name = "pic")
    public byte[] pic;
    @ColumnInfo(name = "pic_str")
    public String picStr;
    @Ignore
    public boolean isDelete;

    @Dao
    public interface LocalFileDao {
        @Insert
        void insert(RoomLocalFile roomPlayMusic);

        @Delete
        void delete(RoomLocalFile roomPlayMusic);

        // 使用 @Query 删除所有数据
        @Query("DELETE FROM local_file")
        void deleteAll();

        @Update
        void update(RoomLocalFile roomPlayMusic);

        @Query("SELECT * FROM local_file where music_id = :id")
        RoomLocalFile getMusicById(String id);


        @Query("SELECT * FROM local_file")
        List<RoomLocalFile> getAllMusic();
    }

}
