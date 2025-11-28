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

@Entity(tableName = "custom_play")
public class RoomCustomPlay {
    @PrimaryKey
    @ColumnInfo(name = "play_list_id")
    public int playListId;
    @ColumnInfo(name = "play_list_name")
    public String playListName;
    @ColumnInfo(name = "play_list_count")
    public int playListCount;
    @ColumnInfo(name = "play_list_img_byte")
    public byte[] playListImgByte;
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT, name = "music_list_json")
    public String musicListJson; // 将 List<RoomMusic> 序列化为 JSON 字符串

    @Dao
    public interface CustomPlayDao {
        @Insert
        void insert(RoomCustomPlay roomCustomPlay);

        @Delete
        void delete(RoomCustomPlay roomCustomPlay);

        @Update
        void update(RoomCustomPlay roomCustomPlay);

        @Query("SELECT * FROM custom_play")
        List<RoomCustomPlay> getAllCustomPlay();

        @Query("SELECT * FROM custom_play WHERE play_list_id = :id")
        RoomCustomPlay getCustomPlayById(int id);
    }
}
