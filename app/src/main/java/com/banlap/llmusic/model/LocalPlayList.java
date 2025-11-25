package com.banlap.llmusic.model;

import java.util.List;

public class LocalPlayList {
    public int playListId;
    public String playListName;
    public int playListCount;
    public byte[] playListImgByte;
    public List<Music> musicList;

    public int getPlayListId() {return playListId;}
    public void setPlayListId(int playListId) {this.playListId = playListId;}

    public String getPlayListName() { return playListName; }
    public void setPlayListName(String playListName) { this.playListName = playListName; }

    public int getPlayListCount() { return playListCount; }
    public void setPlayListCount(int playListCount) { this.playListCount = playListCount; }

    public byte[] getPlayListImgByte() { return playListImgByte; }
    public void setPlayListImgByte(byte[] playListImgByte) { this.playListImgByte = playListImgByte; }

    public List<Music> getMusicList() { return musicList; }
    public void setMusicList(List<Music> musicList) { this.musicList = musicList; }
}
