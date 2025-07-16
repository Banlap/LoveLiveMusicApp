package com.banlap.llmusic.phone.uivm.fvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MainListFVM extends AndroidViewModel {

    private static MainListCallBack mCallBack;

    public MainListFVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(MainListCallBack callBack) { mCallBack = callBack; }

    public void viewBack() { mCallBack.viewBack(); }


    public interface MainListCallBack {
        void viewBack();
    }
}
