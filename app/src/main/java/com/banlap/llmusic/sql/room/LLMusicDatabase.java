package com.banlap.llmusic.sql.room;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import android.content.Context;

/**
 * 本地数据库
 * */
@Database(
        entities = { RoomSettings.class, RoomPlayMusic.class, RoomCustomPlay.class, RoomFavoriteMusic.class, RoomLocalFile.class, RoomRecommendMusic.class},
        version = LLMusicDatabase.DATABASE_VERSION,
        exportSchema = false
)
@TypeConverters({ Converters.class })
public abstract class LLMusicDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "llmusic_db"; //数据库名称
    public static final int DATABASE_VERSION = 14; //数据库版本号 (结构修改时更新版本号)
    public abstract RoomSettings.SettingsDao settingsDao();
    public abstract RoomPlayMusic.MusicDao musicDao();
    public abstract RoomCustomPlay.CustomPlayDao customPlayDao();
    public abstract RoomFavoriteMusic.FavoriteMusicDao favoriteMusicDao();
    public abstract RoomLocalFile.LocalFileDao localFileDao();
    public abstract RoomRecommendMusic.RecommendMusicDao recommendMusicDao();

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
