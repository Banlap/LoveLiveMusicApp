package com.banlap.llmusic.model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * @author Banlap on 2021/11/30
 */
public class Music implements Serializable {
    public int musicId;
    public String musicType;
    public String musicName;
    public String musicSinger;
    public String musicURL;
    public String musicImg;
    public String musicLyric;
    public int musicFavorite;
    public boolean isPlaying;
    public boolean isLocal;
    public boolean isDelete;
    public byte[] musicImgByte;
    public Bitmap musicImgBitmap;
    public String musicBitrate;
    public String musicMime ;
    public String musicFileSize;
    public String musicQuality;

    public int getMusicId() { return musicId; }
    public void setMusicId(int musicId) { this.musicId = musicId; }

    public String getMusicType() { return musicType; }
    public void setMusicType(String musicType) { this.musicType = musicType; }

    public String getMusicName() { return musicName; }
    public void setMusicName(String musicName) { this.musicName = musicName; }

    public String getMusicSinger() { return musicSinger; }
    public void setMusicSinger(String musicSinger) { this.musicSinger = musicSinger; }

    public String getMusicURL() { return musicURL; }
    public void setMusicURL(String musicURL) { this.musicURL = musicURL; }

    public String getMusicImg() { return musicImg; }
    public void setMusicImg(String musicImg) { this.musicImg = musicImg; }

    public String getMusicLyric() { return musicLyric; }
    public void setMusicLyric(String musicLyric) { this.musicLyric = musicLyric; }

    public int getMusicFavorite() { return musicFavorite; }
    public void setMusicFavorite(int musicFavorite) { this.musicFavorite = musicFavorite; }

    public boolean isLocal() { return isLocal; }
    public void setLocal(boolean local) { isLocal = local; }

    public byte[] getMusicImgByte() { return musicImgByte; }
    public void setMusicImgByte(byte[] musicImgByte) { this.musicImgByte = musicImgByte; }

    public Bitmap getMusicImgBitmap() { return musicImgBitmap; }
    public void setMusicImgBitmap(Bitmap musicImgBitmap) { this.musicImgBitmap = musicImgBitmap; }

    public String getMusicBitrate() { return musicBitrate; }
    public void setMusicBitrate(String musicBitrate) { this.musicBitrate = musicBitrate; }

    public String getMusicMime() { return musicMime; }
    public void setMusicMime(String musicMime) { this.musicMime = musicMime; }

    public String getMusicFileSize() { return musicFileSize; }
    public void setMusicFileSize(String musicFileSize) { this.musicFileSize = musicFileSize; }

    public String getMusicQuality() { return musicQuality; }
    public void setMusicQuality(String musicQuality) { this.musicQuality = musicQuality; }
}
