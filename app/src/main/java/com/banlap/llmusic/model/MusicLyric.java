package com.banlap.llmusic.model;

public class MusicLyric {
    public int lyricId;
    public String lyricTime;
    public String lyricEndTime;
    public String lyricContext;
    public String lyricContext2;
    public boolean isChangeLyricColor;

    public int getLyricId() { return lyricId; }

    public void setLyricId(int lyricId) { this.lyricId = lyricId; }

    public String getLyricTime() { return lyricTime; }

    public void setLyricTime(String lyricTime) { this.lyricTime = lyricTime; }

    public String getLyricEndTime() {return lyricEndTime;}

    public void setLyricEndTime(String lyricEndTime) {this.lyricEndTime = lyricEndTime;}

    public String getLyricContext() { return lyricContext; }

    public void setLyricContext(String lyricContext) { this.lyricContext = lyricContext; }

    public String getLyricContext2() {return lyricContext2;}

    public void setLyricContext2(String lyricContext2) {this.lyricContext2 = lyricContext2;}
}
