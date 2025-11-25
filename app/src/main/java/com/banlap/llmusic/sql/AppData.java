package com.banlap.llmusic.sql;

import com.banlap.llmusic.base.BaseApplication;
import com.banlap.llmusic.sql.room.RoomLocalPlayList;
import com.banlap.llmusic.sql.room.RoomMusic;
import com.banlap.llmusic.sql.room.RoomSettings;

import java.util.List;
import java.util.function.Consumer;

public class AppData {
    public static RoomSettings roomSettings;
    public static List<RoomLocalPlayList> roomLocalPlayList;
    public static List<RoomMusic> roomMusicList;

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

    public static void saveRoomLocalPlayList(Consumer<RoomLocalPlayList> consumer) {

    }
}
