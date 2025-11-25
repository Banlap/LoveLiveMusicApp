package com.banlap.llmusic.sql.room;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Update;

import com.banlap.llmusic.model.Music;

import java.util.List;

@Entity(tableName = "localPlayList")
public class RoomLocalPlayList {
    @PrimaryKey
    @ColumnInfo(name = "playListId")
    public int playListId;
    @ColumnInfo(name = "playListName")
    public String playListName;
    @ColumnInfo(name = "playListCount")
    public int playListCount;
    @ColumnInfo(name = "playListImgByte")
    public byte[] playListImgByte;
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    public String musicListJson; // 将 List<RoomMusic> 序列化为 JSON 字符串
    // 忽略该字段，不存储到数据库
    @ColumnInfo(name = "musicList")
    public List<RoomMusic> musicList;

    @Dao
    public interface LocalPlayListDao {
        @Insert
        void insert(RoomLocalPlayList roomLocalPlayList);

        @Delete
        void delete(RoomLocalPlayList roomLocalPlayList);

        @Update
        void update(RoomLocalPlayList roomLocalPlayList);

        @Query("SELECT * FROM localPlayList")
        List<RoomLocalPlayList> getAllLocalPlayList();
    }
}
