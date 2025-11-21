package com.banlap.llmusic.phone.uivm.fvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.banlap.llmusic.model.Music;

public class MainListFVM extends AndroidViewModel {
    private static MainListCallBack mCallBack;
    //主题id
    private final MutableLiveData<Integer> themeId = new MutableLiveData<>();

    public MainListFVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(MainListCallBack callBack) { mCallBack = callBack; }

    public void viewBack() { mCallBack.viewBack(); }


    public interface MainListCallBack {
        void viewBack();
    }

    /**
     * 获取主题id
     * */
    public MutableLiveData<Integer> getThemeId() {
        return themeId;
    }

    /**
     * 设置主题id
     * */
    public void setThemeId(int rThemeId) {
        themeId.setValue(rThemeId);
    }
}
