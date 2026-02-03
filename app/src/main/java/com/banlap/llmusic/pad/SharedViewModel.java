package com.banlap.llmusic.pad;

import android.app.Application;

import androidx.annotation.NonNull;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

/**
 * 共享ViewModel pad模式
 * */
public class SharedViewModel extends AndroidViewModel {
    private final MutableLiveData<Boolean> mlIsSearch = new MutableLiveData<>();

    public SharedViewModel(@NonNull Application application) {
        super(application);
    }

    /** 获取是否在搜索歌曲状态 */
    public void setIsSearch(boolean isSearch) {
        mlIsSearch.setValue(isSearch);
    }

    /** 设置是否在搜索歌曲状态 */
    public MutableLiveData<Boolean> getIsSearch() {
        return mlIsSearch;
    }
}