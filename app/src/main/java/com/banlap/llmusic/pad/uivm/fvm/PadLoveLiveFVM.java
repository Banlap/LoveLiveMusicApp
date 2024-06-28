package com.banlap.llmusic.pad.uivm.fvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class PadLoveLiveFVM extends AndroidViewModel {

    private static PadLoveLiveCallBack mCallBack;

    public PadLoveLiveFVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(PadLoveLiveCallBack callBack) { mCallBack = callBack; }

    public void viewBack() { mCallBack.viewBack(); }


    public interface PadLoveLiveCallBack {
        void viewBack();
    }
}
