package com.banlap.llmusic.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.banlap.llmusic.receiver.BluetoothStateBroadcastReceiver;

/**
 * 蓝牙工具类
 * */
public class BluetoothUtil {

    private BluetoothStateBroadcastReceiver mReceiver;

    public static BluetoothUtil getInstance() { return new BluetoothUtil(); }

    //注册蓝牙广播
    public void registerBluetoothReceiver(Context context) {
        mReceiver = new BluetoothStateBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        context.registerReceiver(mReceiver, intentFilter);
    }

    public void unRegisterBluetoothReceiver(Context context) {
        if(mReceiver!=null) {
            context.unregisterReceiver(mReceiver);
        }

    }
}
