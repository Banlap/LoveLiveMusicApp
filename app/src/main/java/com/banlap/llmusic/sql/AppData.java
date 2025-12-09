package com.banlap.llmusic.sql;

import android.util.Log;

import com.banlap.llmusic.base.BaseApplication;
import com.banlap.llmusic.sql.room.RoomCustomPlay;
import com.banlap.llmusic.sql.room.RoomFavoriteMusic;
import com.banlap.llmusic.sql.room.RoomLocalFile;
import com.banlap.llmusic.sql.room.RoomPlayMusic;
import com.banlap.llmusic.sql.room.RoomRecommendMusic;
import com.banlap.llmusic.sql.room.RoomSettings;

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


    public static void deleteRoomMusic(RoomPlayMusic roomPlayMusic) {
        BaseApplication.llMusicDatabase.musicDao().delete(roomPlayMusic);
    }


    public static void deleteAllRoomMusic() {
        BaseApplication.llMusicDatabase.musicDao().deleteAll();
    }

    /**
     * 保存自建歌单数据
     * */
    public static void saveRoomCustomPlay(RoomCustomPlay customPlay) {
        BaseApplication.llMusicDatabase.customPlayDao().insert(customPlay);
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
     * 删除所有本地歌曲数据后再保存本地歌曲
     * */
    public static void saveLocalFileMusicList(List<RoomLocalFile> roomLocalFileList) {
        BaseApplication.llMusicDatabase.localFileDao().deleteAll();
        if(!roomLocalFileList.isEmpty()) {
            for(RoomLocalFile localFile : roomLocalFileList) {
                BaseApplication.llMusicDatabase.localFileDao().insert(localFile);
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
}
