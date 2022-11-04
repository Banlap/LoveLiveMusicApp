package com.banlap.llmusic.uivm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MusicControllerFVM extends AndroidViewModel {

    private MusicControllerCallBack callBack;

    public MusicControllerFVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(MusicControllerCallBack callBack) { this.callBack = callBack; }

    public void viewBack() { callBack.viewBack(); }

    public interface MusicControllerCallBack {
        void viewBack();
    }
}
