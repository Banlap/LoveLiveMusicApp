package com.banlap.llmusic.phone.uivm.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class NewMainVM extends AndroidViewModel {

    private final MutableLiveData<Boolean> mlIsClickMusicController = new MutableLiveData<>(false);

    public NewMainVM(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<Boolean> getMlIsClickMusicController() {
        return mlIsClickMusicController;
    }

    public void toggleMusicControllerSwitch() {
        mlIsClickMusicController.setValue(Boolean.FALSE.equals(mlIsClickMusicController.getValue()));
    }
}
