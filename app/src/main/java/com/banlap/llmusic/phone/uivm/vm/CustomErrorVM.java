package com.banlap.llmusic.phone.uivm.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class CustomErrorVM extends AndroidViewModel {

    private CustomErrorCallBack callBack;

    public CustomErrorVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(CustomErrorCallBack callBack) { this.callBack = callBack; }

    public void viewBack() { callBack.viewBack(); }

    public interface CustomErrorCallBack {
        void viewBack();
    }
}
