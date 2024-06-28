package com.banlap.llmusic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.service.MusicPlayService;
import com.banlap.llmusic.ui.activity.MainActivity;

import org.greenrobot.eventbus.EventBus;

/**
 * 定时器广播
 * */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();
    public static final String ACTION_ALARM = "com.banlap.llmusic.receiver.AlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action!=null) {
            switch (action) {
                case ACTION_ALARM:
                    Log.i(TAG, "receive alarm");
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_COUNT_DOWN_FINISH));
                    break;
            }
        }
    }
}
