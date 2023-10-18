package com.banlap.llmusic.uivm.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class LockFullScreenVM extends AndroidViewModel {

    private LockFullScreenCallBack callBack;

    public LockFullScreenVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(LockFullScreenCallBack callBack) { this.callBack = callBack; }

    public interface LockFullScreenCallBack { }
}
