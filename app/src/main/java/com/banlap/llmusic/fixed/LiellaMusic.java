package com.banlap.llmusic.fixed;

import com.banlap.llmusic.model.Music;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Banlap on 2021/11/30
 */
public class LiellaMusic {
    public static LiellaMusic getInstance() { return new LiellaMusic(); }
    public List<Music> list;

    public List<Music> getMusicData() {
        list = new ArrayList<>();


        return list;
    }

    public List<Music> getNullMusicData() {
        list = new ArrayList<>();

        Music nullMusic1 = new Music();
        nullMusic1.setMusicId(9999991);
        nullMusic1.setMusicType(" ");
        nullMusic1.setMusicName(" ");

        Music nullMusic2 = new Music();
        nullMusic2.setMusicId(9999992);
        nullMusic2.setMusicType(" ");
        nullMusic2.setMusicName(" ");

        list.add(nullMusic1);
        list.add(nullMusic2);
        return list;
    }
}
