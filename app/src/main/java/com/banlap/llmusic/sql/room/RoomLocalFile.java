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
    @PrimaryKey
    @ColumnInfo(name = "id")
    public long id;
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

        @Query("SELECT * FROM local_file ORDER BY id ASC")
        List<RoomLocalFile> getAllMusic();
    }

    public RoomLocalFile copyWithNewId(long id) {
        RoomLocalFile roomLocalFile = new RoomLocalFile();
        roomLocalFile.id = id;
        roomLocalFile.musicId = this.musicId;
        roomLocalFile.title = this.title;
        roomLocalFile.album = this.album;
        roomLocalFile.artist = this.artist;
        roomLocalFile.duration = this.duration;
        roomLocalFile.path = this.path;
        roomLocalFile.pic = this.pic;
        roomLocalFile.isDelete = this.isDelete;
        return roomLocalFile;
    }

}
