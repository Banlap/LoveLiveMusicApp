package com.banlap.llmusic.uivm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MusicListFVM extends AndroidViewModel {

    private static MusicListCallBack mCallBack;

    public MusicListFVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(MusicListCallBack callBack) { mCallBack = callBack; }

    public void viewBack() { mCallBack.viewBack(); }

    public static void viewRefreshMusicList() {
        mCallBack.viewRefreshMusicList();
    }

    public interface MusicListCallBack {
        void viewBack();
        void viewRefreshMusicList();
    }
}
