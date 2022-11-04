package com.banlap.llmusic.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.banlap.llmusic.request.ThreadEvent;

import org.greenrobot.eventbus.EventBus;

public class BluetoothStateBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action!=null) {
            switch (action) {
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    Log.e("ABMusicPlayer", "bluetooth connect");
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Log.e("ABMusicPlayer", "bluetooth disconnect");
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.BLUETOOTH_DISCONNECT));
                    break;
            }
        }
    }
}
