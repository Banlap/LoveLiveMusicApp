package com.banlap.llmusic.uivm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MusicPanelFVM extends AndroidViewModel {

    private static MusicPanelCallBack callBack;

    public MusicPanelFVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(MusicPanelCallBack callBack) { MusicPanelFVM.callBack = callBack; }

    public void viewBack() { callBack.viewBack(); }

    /** 转换成时间格式*/
    public String rebuildTime(long position) {
        long minLong = position /1000/60;
        long secLong = position /1000%60;
        String minStr = minLong <10 ? "0"+minLong : ""+minLong;
        String secStr = secLong <10 ? "0"+secLong : ""+secLong;
        return minStr + ":" + secStr;
    }


    public interface MusicPanelCallBack {
        void viewBack();
    }
}
