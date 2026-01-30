package com.banlap.llmusic.sql;

import android.util.Log;

import com.banlap.llmusic.base.BaseApplication;
import com.banlap.llmusic.sql.room.RoomCustomPlay;
import com.banlap.llmusic.sql.room.RoomDownloadMusic;
import com.banlap.llmusic.sql.room.RoomFavoriteMusic;
import com.banlap.llmusic.sql.room.RoomLocalFile;
import com.banlap.llmusic.sql.room.RoomPlayMusic;
import com.banlap.llmusic.sql.room.RoomRecommendMusic;
import com.banlap.llmusic.sql.room.RoomSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AppData {
    private static final String TAG = AppData.class.getSimpleName();
    public static RoomSettings roomSettings;
    public static List<RoomPlayMusic> roomPlayMusicList; //播放列表
    public static List<RoomFavoriteMusic> roomFavoriteMusicList; //收藏列表
    public static List<RoomLocalFile> roomLocalFileList; //本地文件列表
    public static List<RoomCustomPlay> roomCustomPlayList; //自建歌单列表
    public static List<RoomRecommendMusic> roomRecommendMusicList; //每日推荐歌单列表
    public static List<RoomDownloadMusic> roomDownloadMusicList; //下载歌曲列表

    /**
     * 初始化参数
     * */
    public static void init() {
        roomSettings = new RoomSettings();
        roomPlayMusicList = new ArrayList<>();
        roomFavoriteMusicList = new ArrayList<>();
        roomLocalFileList = new ArrayList<>();
        roomCustomPlayList = new ArrayList<>();
        roomRecommendMusicList = new ArrayList<>();
        roomDownloadMusicList = new ArrayList<>();
    }

    /**
     * 保存settings各种参数
     * */
    public static <T> void saveRoomSettings(Consumer<RoomSettings> setter) {
        RoomSettings currentSettings = BaseApplication.llMusicDatabase.settingsDao().getFirstData();
        if (currentSettings == null) {
            currentSettings = new RoomSettings();
            currentSettings.id = 1;
            setter.accept(currentSettings);
            BaseApplication.llMusicDatabase.settingsDao().insert(currentSettings);
        } else {
            setter.accept(currentSettings);
            BaseApplication.llMusicDatabase.settingsDao().update(currentSettings);
        }

        // 更新内存缓存
        AppData.roomSettings = BaseApplication.llMusicDatabase.settingsDao().getFirstData();
    }

    /**
     * 直接保存数据
     * */
    public static void saveRoomMusic(List<RoomPlayMusic> roomPlayMusicList) {
        if(!roomPlayMusicList.isEmpty()) {
            for(RoomPlayMusic music : roomPlayMusicList) {
                BaseApplication.llMusicDatabase.musicDao().insert(music);
            }
        }
    }

    /**
     * 删除本地数据库的单个歌曲
     * */
    public static void deleteRoomMusic(RoomPlayMusic roomPlayMusic) {
        BaseApplication.llMusicDatabase.musicDao().delete(roomPlayMusic);
    }

    /**
     * 删除本地数据库的所有歌曲
     * */
    public static void deleteAllRoomMusic() {
        BaseApplication.llMusicDatabase.musicDao().deleteAll();
    }

    /**
     * 获取自建歌单列表数据
     * */
    public static List<RoomCustomPlay> getCustomPlayList() {
        return BaseApplication.llMusicDatabase.customPlayDao().getAllMusic();
    }

    /**
     * 保存自建歌单数据
     * */
    public static void saveRoomCustomPlay(RoomCustomPlay customPlay) {
        if(customPlay.playListId != 0) {
            BaseApplication.llMusicDatabase.customPlayDao().insert(customPlay);
        }
    }

    /**
     * 更新自建歌曲数据
     * */
    public static void updateRoomCustomPlay(int id, Consumer<RoomCustomPlay> setter) {
        RoomCustomPlay roomCustomPlay = BaseApplication.llMusicDatabase.customPlayDao().getCustomPlayById(id);
        if(roomCustomPlay != null) {
            setter.accept(roomCustomPlay);
            BaseApplication.llMusicDatabase.customPlayDao().update(roomCustomPlay);
        } else {
            Log.e(TAG, "更新自建歌单数据失败");
        }
    }

    /**
     * 根据自建歌单id更新歌曲列表数据
     * */
    public static void saveRoomCustomPlayByMusicJson(int playListId, Consumer<RoomCustomPlay> setter) {
        RoomCustomPlay roomCustomPlay = BaseApplication.llMusicDatabase.customPlayDao().getCustomPlayById(playListId);
        if(roomCustomPlay != null) {
            setter.accept(roomCustomPlay);
            BaseApplication.llMusicDatabase.customPlayDao().update(roomCustomPlay);
        } else {
            Log.e(TAG, "更新自建歌单歌曲列表数据失败");
        }
    }

    /**
     * 删除自建歌单
     * */
    public static void deleteCustomPlayList(RoomCustomPlay roomCustomPlay) {
        BaseApplication.llMusicDatabase.customPlayDao().delete(roomCustomPlay);
    }

    /**
     * 删除本地歌曲文件
     * */
    public static void deleteLocalFileMusic(RoomLocalFile localFile) {
        BaseApplication.llMusicDatabase.localFileDao().delete(localFile);
    }


    /**
     * 删除本地歌曲文件
     * */
    public static void deleteFavoriteMusic(RoomFavoriteMusic favoriteMusic) {
        BaseApplication.llMusicDatabase.favoriteMusicDao().delete(favoriteMusic);
    }

    /**
     * 删除所有本地歌曲数据后再保存本地歌曲
     * */
    public static void saveLocalFileMusicList(List<RoomLocalFile> roomLocalFileList) {
        BaseApplication.llMusicDatabase.localFileDao().deleteAll();
        if(!roomLocalFileList.isEmpty()) {
            for(RoomLocalFile localFile : roomLocalFileList) {
                if(localFile.id != 0) {
                    BaseApplication.llMusicDatabase.localFileDao().insert(localFile);
                }
            }
        }
    }

    /**
     * 删除所有歌曲数据后再保存每日推荐歌曲
     * */
    public static void saveRecommendList(List<RoomRecommendMusic> roomRecommendMusicList) {
        BaseApplication.llMusicDatabase.recommendMusicDao().deleteAll();
        if(!roomRecommendMusicList.isEmpty()) {
            for(RoomRecommendMusic recommendMusic : roomRecommendMusicList) {
                BaseApplication.llMusicDatabase.recommendMusicDao().insert(recommendMusic);
            }
        }
    }

    /**
     * 查询收藏音乐列表
     * */
    public static List<RoomFavoriteMusic> getFavoriteMusicList() {
        return BaseApplication.llMusicDatabase.favoriteMusicDao().getAllMusic();
    }

    /**
     * 删除所有收藏歌曲数据后再保存收藏歌曲
     * */
    public static void saveFavoriteList(List<RoomFavoriteMusic> roomFavoriteMusicList) {
        BaseApplication.llMusicDatabase.favoriteMusicDao().deleteAll();
        if(!roomFavoriteMusicList.isEmpty()) {
            for(RoomFavoriteMusic favoriteMusic : roomFavoriteMusicList) {
                if(favoriteMusic.id != 0) {
                    BaseApplication.llMusicDatabase.favoriteMusicDao().insert(favoriteMusic);
                }
            }
        }
    }

    /**
     * 获取下载音乐列表
     * */
    public static List<RoomDownloadMusic> getDownloadMusicList() {
        return BaseApplication.llMusicDatabase.downloadMusicDao().getAllMusic();
    }

    /**
     *
     * 添加一条下载音乐数据
     * */
    public static void saveDownloadMusic(RoomDownloadMusic roomDownloadMusic) {
        BaseApplication.llMusicDatabase.downloadMusicDao().insert(roomDownloadMusic);
    }


    /**
     * 更新下载歌曲数据
     * */
    public static void updateRoomDownloadMusic(int id, Consumer<RoomDownloadMusic> setter) {
        RoomDownloadMusic roomDownloadMusic = BaseApplication.llMusicDatabase.downloadMusicDao().getMusicById(id);
        if(roomDownloadMusic != null) {
            setter.accept(roomDownloadMusic);
            BaseApplication.llMusicDatabase.downloadMusicDao().update(roomDownloadMusic);
        } else {
            Log.e(TAG, "更新下载歌曲数据失败");
        }
    }

    /**
     * 清除所有下载音乐记录
     * */
    public static void deleteAllDownloadMusic() {
        BaseApplication.llMusicDatabase.downloadMusicDao().deleteAll();
    }

    /**
     * 添加空数据 收藏列表
     * */
    public static void addNullDataForFavorite(List<RoomFavoriteMusic> roomFavoriteList, int addSize) {
        for (int i=0; i < addSize; i++) {
            roomFavoriteList.add(new RoomFavoriteMusic());
        }
    }

    /**
     * 添加空数据 本地文件列表
     * */
    public static void addNullDataLocalFile(List<RoomLocalFile> roomLocalFileList, int addSize) {
        for (int i=0; i < addSize; i++) {
            roomLocalFileList.add(new RoomLocalFile());
        }
    }

    /**
     * 添加空数据 自建列表
     * */
    public static void addNullDataCustomPlay(List<RoomCustomPlay> roomCustomPlayList, int addSize) {
        for (int i=0; i < addSize; i++) {
            roomCustomPlayList.add(new RoomCustomPlay());
        }
    }

    /**
     * 添加空数据 下载列表
     * */
    public static void addNullDataDownloadMusic(List<RoomDownloadMusic> roomDownloadMusicList, int addSize) {
        for (int i=0; i < addSize; i++) {
            roomDownloadMusicList.add(new RoomDownloadMusic());
        }
    }


}
