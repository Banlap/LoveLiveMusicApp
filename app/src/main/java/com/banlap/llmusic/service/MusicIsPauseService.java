package com.banlap.llmusic.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.banlap.llmusic.request.ThreadEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;

public class MusicIsPauseService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        Log.e("ABMusicPlayer", "StartMusicIsPauseService");
        boolean isPause =  intent.getBooleanExtra("IsPauseMusic", false);
        String musicName = intent.getStringExtra("MusicName");
        String musicSinger = intent.getStringExtra("MusicSinger");
        byte[] bis = intent.getByteArrayExtra("MusicBitmap");
        Bitmap bitmap = bis != null? BitmapFactory.decodeByteArray(bis, 0, bis.length) : null;
        if(isPause) {
            Log.e("ABMusicPlayer", "isPause");
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.MUSIC_IS_PAUSE, musicName, musicSinger, bitmap));
        }
        return super.onStartCommand(intent, flag, startId);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
