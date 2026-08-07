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
    /**
     * 是否横屏
     * */
    private final MutableLiveData<Boolean> mlIsLandSpace = new MutableLiveData<>(false); //是否横屏

    private NewMainCallBack callBack;
    public void setCallBack(NewMainCallBack callBack) { this.callBack = callBack; }

    public NewMainVM(@NonNull Application application) {
        super(application);
    }


    /**
     * 底部播放控制器点击切换 获取参数
     * */
    public MutableLiveData<Boolean> getMlIsClickMusicController() {
        return mlIsClickMusicController;
    }

    /**
     * 底部播放控制器点击切换 设置参数
     * */
    public void toggleMusicControllerSwitch() {
        mlIsClickMusicController.setValue(Boolean.FALSE.equals(mlIsClickMusicController.getValue()));
    }

    /**
     * 企划列表触摸滑动 获取参数
     * */
    public MutableLiveData<Boolean> getMlIsViewPageTouch() {
        return mlIsViewPageTouch;
    }

    /**
     * 企划列表触摸滑动 设置参数
     * */
    public void toggleViewPageTouch(boolean b) {
        mlIsViewPageTouch.setValue(b);
    }


    /**
     * 是否横屏 获取参数
     * */
    public MutableLiveData<Boolean> getMlIsLandSpace() { return mlIsLandSpace;}

    /**
     * 是否横屏 设置参数
     * */
    public void toggleIsLandSpace(boolean b) { mlIsLandSpace.setValue(b);}

    public interface NewMainCallBack {
    }
}
