package com.banlap.llmusic.pad.uivm.fvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class PadDetailMusicListFVM extends AndroidViewModel {
    private PadDetailMusicListCallBack callBack;
    public MutableLiveData<Boolean> mlIsSearch = new MutableLiveData<>();

    public PadDetailMusicListFVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(PadDetailMusicListCallBack callBack) { this.callBack = callBack; }

    public void viewBack() { callBack.viewBack(); }

    public interface PadDetailMusicListCallBack {
        void viewBack();
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
