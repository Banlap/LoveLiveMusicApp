package com.banlap.llmusic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 锁屏广播
 * */
public class ScreenReceiver extends BroadcastReceiver {
    private static final String TAG = ScreenReceiver.class.getSimpleName();

    private Intent intentLockScreenService;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action!=null) {
            switch (action) {
                case Intent.ACTION_SCREEN_OFF:
                    Log.i(TAG, "screen lock");
                    //EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_SCREEN_LOCK));
                    break;
                case Intent.ACTION_SCREEN_ON:
                    Log.i(TAG, "screen unlock");
                    break;
            }
        }
    }
}
