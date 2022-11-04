package com.banlap.llmusic.uivm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MusicMainDownFVM extends AndroidViewModel {

    private MusicMainDownCallBack callBack;

    public MusicMainDownFVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(MusicMainDownCallBack callBack) { this.callBack = callBack; }

    public void viewBack() { callBack.viewBack(); }

    public interface MusicMainDownCallBack {
        void viewBack();
    }
}
