package com.banlap.llmusic.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.banlap.llmusic.request.ThreadEvent;

import org.greenrobot.eventbus.EventBus;

public class BluetoothStateBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = BluetoothStateBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action!=null) {
            switch (action) {
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    Log.i(TAG, "bluetooth connect");
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Log.i(TAG, "bluetooth disconnect");
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_BLUETOOTH_DISCONNECT));
                    break;
            }
        }
    }
}
