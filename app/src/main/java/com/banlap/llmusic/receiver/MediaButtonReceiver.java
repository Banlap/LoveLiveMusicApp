package com.banlap.llmusic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.banlap.llmusic.ui.activity.MainActivity;

public class MediaButtonReceiver extends BroadcastReceiver {
    private static final String TAG = MediaButtonReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            // 处理媒体按钮事件
            Log.i(TAG, "已接收从桌面外启动应用播放");
            Intent intentMain = new Intent(context, MainActivity.class);
            intentMain.putExtra("IsAutoMusic", "1");
            context.startActivity(intentMain);
        }
    }
}