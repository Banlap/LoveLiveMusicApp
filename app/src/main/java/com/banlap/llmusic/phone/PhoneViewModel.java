package com.banlap.llmusic.phone;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

/**
 * 全局ViewModel (单例) phone模式
 * */
public class PhoneViewModel extends AndroidViewModel {
    private static PhoneViewModel instance;
    private final MutableLiveData<String> globalData = new MutableLiveData<>();

    public static synchronized PhoneViewModel getInstance(Application application) {
        if (instance == null) {
            instance = new ViewModelProvider.AndroidViewModelFactory(application).create(PhoneViewModel.class);
        }
        return instance;
    }

    private PhoneViewModel(@NonNull Application application) {
        super(application);
    }
}