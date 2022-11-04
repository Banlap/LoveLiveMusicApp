package com.banlap.llmusic.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.ui.MainActivity;

import org.greenrobot.eventbus.EventBus;

public class MusicNextService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        Log.e("ABMusicPlayer", "StartMusicNextService");
        boolean isNext =  intent.getBooleanExtra("NextMusic", false);
        if(isNext) {
            Log.e("ABMusicPlayer", "isNext");
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.MUSIC_IS_NEXT));
        }
        return super.onStartCommand(intent, flag, startId);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
