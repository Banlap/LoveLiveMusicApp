package com.banlap.llmusic.phone.uivm.vm;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.banlap.llmusic.receiver.DownloadReceiver;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.sql.AppData;
import com.banlap.llmusic.utils.AppExecutors;
import com.banlap.llmusic.utils.DownloadHelper;

import org.greenrobot.eventbus.EventBus;

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
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                AppData.deleteAllDownloadMusic();
                callBack.refreshList();
            }
        });

    }

    public interface DownloadCallBack {
        void viewBack();

        void refreshList();
    }
}
