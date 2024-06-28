package com.banlap.llmusic.pad.uivm.fvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class PadDetailMusicListFVM extends AndroidViewModel {

    private PadDetailMusicListCallBack callBack;

    public PadDetailMusicListFVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(PadDetailMusicListCallBack callBack) { this.callBack = callBack; }

    public void viewBack() { callBack.viewBack(); }

    public interface PadDetailMusicListCallBack {
        void viewBack();
    }
}
