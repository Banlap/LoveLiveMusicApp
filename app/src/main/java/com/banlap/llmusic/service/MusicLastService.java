package com.banlap.llmusic.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.banlap.llmusic.request.ThreadEvent;

import org.greenrobot.eventbus.EventBus;

public class MusicLastService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        Log.i("ABMusicPlayer", "StartMusicLastService");
        boolean isLast =  intent.getBooleanExtra("LastMusic", false);
        if(isLast) {
            Log.i("ABMusicPlayer", "isLast");
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.MUSIC_IS_LAST));
        }
        return super.onStartCommand(intent, flag, startId);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
