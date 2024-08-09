package com.banlap.llmusic.uivm.vm;


import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.banlap.llmusic.model.DownloadMusic;
import com.banlap.llmusic.receiver.DownloadReceiver;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.utils.DownloadHelper;
import com.banlap.llmusic.utils.LLActivityManager;
import com.banlap.llmusic.utils.SPUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class DownloadVM extends AndroidViewModel {

    private DownloadCallBack callBack;

    public DownloadVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(DownloadCallBack callBack) { this.callBack = callBack; }

    public void viewBack() { callBack.viewBack(); }


    /**
     * 取消下载
     * */
    public void cancel() {
        DownloadHelper.cancelDownload(DownloadReceiver.downloadId);
        DownloadReceiver.stopHandler();
        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DOWNLOAD_MUSIC_CANCEL));
    }



    /**
     * 清空列表
     * */
    public void deleteList() {
        List<DownloadMusic> list = new ArrayList<>();
        SPUtil.setListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.DownloadMusicListData, list);
        callBack.refreshList();
    }

    public interface DownloadCallBack {
        void viewBack();

        void refreshList();
    }
}
