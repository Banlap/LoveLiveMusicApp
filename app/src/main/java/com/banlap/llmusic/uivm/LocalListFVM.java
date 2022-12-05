package com.banlap.llmusic.uivm;

import android.app.Application;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.viewpager.widget.PagerAdapter;

import com.banlap.llmusic.model.LocalFile;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.utils.FileUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LocalListFVM extends AndroidViewModel {

    private static final String TAG = LocalListFVM.class.getSimpleName();
    private LocalListCallBack callBack;
    /*扫描线程*/
    private Thread scanThread;
    /*定时器  用于定时检测扫描线程的状态*/
    private Timer scanTimer;
    /*检测扫描线程的任务*/
    private TimerTask scanTask;
    private List<LocalFile> localFileList;

    public LocalListFVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(LocalListCallBack callBack) { this.callBack = callBack; }

    public void viewBack() { callBack.viewBack(); }

    public void scan() {

        /*根目录*/
        final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        /*要扫描的文件后缀名*/
        final String endFilter = ".mp3";
        final File dir = new File(rootPath);
        if(null != localFileList) {
            localFileList.clear();
        }
        localFileList = new ArrayList<>();

        scanThread = new Thread(new Runnable() {
            @Override
            public void run() {
                scanFile(dir, endFilter);
            }
        });
        /*判断扫描是否完成 其实就是个定时任务 时间可以自己设置  每2s获取一下扫描线程的状态  如果线程状态为结束就说明扫描完成*/
        scanTimer = new Timer();
        scanTask = new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG,"线程状态: " + scanThread.getState().toString());
                if (scanThread.getState() == Thread.State.TERMINATED) {
                    LocalFile nullLocalFile = new LocalFile();
                    LocalFile nullLocalFile2 = new LocalFile();
                    localFileList.add(nullLocalFile);
                    localFileList.add(nullLocalFile2);
                    /*说明扫描线程结束 扫描完成  更新ui*/
                    EventBus.getDefault().post(new ThreadEvent<LocalFile>(ThreadEvent.SCAN_LOCAL_FILE_SUCCESS, localFileList, ""));
                    cancelTask();
                }
            }
        };

        scanTimer.schedule(scanTask, 0,1000);

        /*开始扫描*/
        scanThread.start();
    }

    /**
     * 扫描文件
     * */
    private void scanFile(File dir, String endFilter) {
        File[] files = dir.listFiles();

        if (files != null && files.length > 0) {

            for (final File file : files) {
                if (file.getName().toUpperCase().endsWith(endFilter.toUpperCase())) {
                    /*是符合后缀名的文件  添加到列表中*/
                    try {
                        String name = file.getName();
                        String path = file.getPath();
                        long totalSpace = file.getTotalSpace();
                        long useSpace = file.getUsableSpace();
                        String absolutePath = file.getAbsolutePath();

                        getMusicData(path, false);

                    } catch (Exception e) {
                        Log.e(TAG, "e: " + e.getMessage());
                    }
                }
                /*是目录*/
                if (file.isDirectory()) {
                    /*递归扫描*/
                    scanFile(file, endFilter);
                }
            }

        }

    }


    /**
     * 选择文件
     * */
    public void selectFile(Intent intent) {
        if(null != localFileList) {
            localFileList.clear();
        }
        localFileList = new ArrayList<>();
        Uri uri = intent.getData();
        if(null != uri) {
            String path = FileUtil.getInstance().getPath(getApplication(), uri);
            getMusicData(path, true);
        }
    }

    private void getMusicData(String path, boolean isPost) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(path);
            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 播放时长单位为毫秒
            Log.d(TAG, "title:" + title + "\n" + "album：" + album + "\n" + "artist：" + artist + "\n" + "duration：" + duration) ;
            byte[] pic = mmr.getEmbeddedPicture();
            String picStr = "";
            if(null != pic) {
                picStr = new String(pic);
            }

            if(null != title) {
                LocalFile localFile = new LocalFile();
                localFile.title = title;
                localFile.album = album;
                localFile.artist = artist;
                localFile.duration = duration;
                localFile.path = path;
                localFile.pic = pic;
                localFile.isDelete = false;
                localFileList.add(localFile);
            }

            if(isPost) {
                EventBus.getDefault().post(new ThreadEvent<LocalFile>(ThreadEvent.SELECT_LOCAL_FILE_SUCCESS, localFileList, ""));
            }

        } catch(Exception e) {
            Log.e(TAG, "e: " + e.getMessage());
        }

    }


    private void cancelTask() {

        Log.i("cancelTask","结束任务");
        if (scanTask!=null){
            scanTask.cancel();
        }

        if (scanTimer!=null){
            scanTimer.purge();
            scanTimer.cancel();;
        }
    }

    public interface LocalListCallBack {
        void viewBack();
    }
}
