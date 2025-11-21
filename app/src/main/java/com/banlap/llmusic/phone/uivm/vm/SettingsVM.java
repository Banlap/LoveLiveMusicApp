package com.banlap.llmusic.phone.uivm.vm;

import android.app.Application;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.utils.OkhttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Response;

public class SettingsVM extends AndroidViewModel {

    private SettingsCallBack callBack;
    private boolean isDownloadStop = false;  //是否取消下载app

    public SettingsVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(SettingsCallBack callBack) {
        this.callBack = callBack;
    }

    public void viewBack() { callBack.viewBack(); }

    /** 下载新版本App */
    public void downloadUrl(String url) {
        isDownloadStop = false;
        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DOWNLOAD_APP_BY_SETTINGS_START));

        OkhttpUtil.getInstance().request(url, new OkhttpUtil.OkHttpCallBack() {
            @Override
            public void onSuccess(Response response) {
               downloadApp(response);
            }

            @Override
            public void onError(String e) {
                Log.i("ABMediaPlay", "error " + e);
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DOWNLOAD_APP_BY_SETTINGS_ERROR));
            }
        });
    }

    /** 下载新版本App */
    private void downloadApp(Response response) {
        try {
            //数据缓冲
            byte[] bs = new byte[1024];
            int len;
            long total = 0;

            long contentLength = response.body().contentLength();
            InputStream is = response.body().byteStream();

            String path="";
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                path = getApplication().getExternalFilesDir(null).getAbsolutePath() + "/LLMusic.apk";
            } else {
                path = Environment.getExternalStorageDirectory().getPath() + "/LLMusic.apk";
            }
            File file = new File(path);
            if(file.exists()){    //如果目标文件已经存在
                file.delete();    //则删除旧文件
            }
            OutputStream os = new FileOutputStream(file);
            //开始读取
            while((len = is.read(bs)) != -1){
                total += len;
                int progress = (int) (100 * (total / (double) contentLength));
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DOWNLOAD_APP_BY_SETTINGS_LOADING, progress));
                if(isDownloadStop) {
                    file.delete(); //取消下载则删除文件
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DOWNLOAD_APP_BY_SETTINGS_SUCCESS, false));
                    os.close();
                    is.close();
                    return;
                }
                os.write(bs,0,len);
            }
            //完毕关闭所有连接
            os.close();
            is.close();
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DOWNLOAD_APP_BY_SETTINGS_SUCCESS, true, file));
        } catch (Exception e) {
            Log.i("ABMediaPlay", "error " + e.getMessage());
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DOWNLOAD_APP_BY_SETTINGS_ERROR));
        }
    }

    public void changeDownloadApp(boolean status){
        isDownloadStop = status;
    }

    public interface SettingsCallBack {
        void viewBack();
    }
}
