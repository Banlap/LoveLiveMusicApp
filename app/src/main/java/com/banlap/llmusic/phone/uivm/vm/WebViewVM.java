package com.banlap.llmusic.phone.uivm.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class WebViewVM extends AndroidViewModel {

    private MessageCallBack callBack;

    public WebViewVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(MessageCallBack callBack) { this.callBack = callBack; }

    public void viewBack() { callBack.viewBack(); }

    public interface MessageCallBack {
        void viewBack();
    }
}
