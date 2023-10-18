package com.banlap.llmusic.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.banlap.llmusic.utils.NotificationHelper;

public class LockScreenService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        NotificationHelper.getInstance().createFullScreen(this);
        return START_NOT_STICKY;
    }
}
