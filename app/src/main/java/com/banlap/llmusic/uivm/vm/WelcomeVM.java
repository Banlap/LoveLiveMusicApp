package com.banlap.llmusic.uivm.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class WelcomeVM extends AndroidViewModel {

    private WelcomeCallBack callBack;

    public WelcomeVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(WelcomeCallBack callBack) { this.callBack = callBack; }

    public interface WelcomeCallBack { }
}
