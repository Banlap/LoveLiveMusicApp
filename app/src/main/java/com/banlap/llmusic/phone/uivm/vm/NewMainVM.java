package com.banlap.llmusic.phone.uivm.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.banlap.llmusic.phone.uivm.fvm.MainListFVM;

public class NewMainVM extends AndroidViewModel {

    /**
     * 是否点击底部播放控制器
     * */
    private final MutableLiveData<Boolean> mlIsClickMusicController = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mlIsViewPageTouch = new MutableLiveData<>(false);

    private NewMainCallBack callBack;
    public void setCallBack(NewMainCallBack callBack) { this.callBack = callBack; }

    public NewMainVM(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<Boolean> getMlIsClickMusicController() {
        return mlIsClickMusicController;
    }

    /**
     * 底部播放控制器 点击切换
     * */
    public void toggleMusicControllerSwitch() {
        mlIsClickMusicController.setValue(Boolean.FALSE.equals(mlIsClickMusicController.getValue()));
    }


    public MutableLiveData<Boolean> getMlIsViewPageTouch() {
        return mlIsViewPageTouch;
    }

    /**
     * 企划列表触摸滑动
     * */
    public void toggleViewPageTouch(boolean b) {
        mlIsViewPageTouch.setValue(b);
    }


    public interface NewMainCallBack {
    }
}
