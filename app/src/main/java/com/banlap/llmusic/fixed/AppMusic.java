package com.banlap.llmusic.fixed;

import com.banlap.llmusic.sql.room.RoomPlayMusic;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Banlap on 2021/11/30
 */
public class AppMusic {
    public static AppMusic getInstance() { return new AppMusic(); }
    public List<RoomPlayMusic> list;

    public List<RoomPlayMusic> getNullMusicData() {
        list = new ArrayList<>();

        RoomPlayMusic nullMusic1 = new RoomPlayMusic();
        nullMusic1.musicId = 9999991;
        nullMusic1.musicType = " ";
        nullMusic1.musicName = " ";

        RoomPlayMusic nullMusic2 = new RoomPlayMusic();
        nullMusic2.musicId = 9999992;
        nullMusic2.musicType = " ";
        nullMusic2.musicName = " ";

        list.add(nullMusic1);
        list.add(nullMusic2);
        return list;
    }
}
