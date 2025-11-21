package com.banlap.llmusic.sql.room;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

/**
 * 本地数据库
 * */
@Database(
        entities = {RoomSettings.class},
        version = 2,
        exportSchema = false
)
public abstract class LLMusicDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "llmusic_db";

    public abstract RoomSettings.SettingsDao settingsDao();

    private static LLMusicDatabase INSTANCE;

    public static synchronized LLMusicDatabase getInstance(Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room
                    .databaseBuilder(
                            context.getApplicationContext(),
                            LLMusicDatabase.class,
                            DATABASE_NAME)
                    .fallbackToDestructiveMigration() // 破坏性迁移
                    .build();
        }
        return INSTANCE;
    }

}
