package com.banlap.llmusic.uivm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MessageVM extends AndroidViewModel {

    private MessageCallBack callBack;

    public MessageVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(MessageCallBack callBack) { this.callBack = callBack; }

    public void viewBack() { callBack.viewBack(); }

    public interface MessageCallBack {
        void viewBack();
    }
}
