package com.banlap.llmusic.sql;

import com.banlap.llmusic.base.BaseApplication;
import com.banlap.llmusic.sql.room.RoomSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AppData {
    public static RoomSettings roomSettings;

    /**
     * 保存settings各种参数
     * */
    public static <T> void saveRoomSettings(Consumer<RoomSettings> setter) {
        if (AppData.roomSettings == null) {
            RoomSettings roomSetting = new RoomSettings();
            setter.accept(roomSetting);
            BaseApplication.llMusicDatabase.settingsDao().insert(roomSetting);
        } else {
            setter.accept(AppData.roomSettings);
            BaseApplication.llMusicDatabase.settingsDao().update(AppData.roomSettings);
        }
    }
}
