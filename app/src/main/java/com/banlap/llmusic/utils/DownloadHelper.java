package com.banlap.llmusic.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.mbms.DownloadRequest;
import android.util.Log;

import androidx.annotation.NonNull;

import com.banlap.llmusic.model.DownloadMusic;
import com.banlap.llmusic.receiver.DownloadReceiver;
import com.banlap.llmusic.request.ThreadEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 下载管理
 * */
public class DownloadHelper {
    private static final String TAG = DownloadHelper.class.getSimpleName();

    public static Queue<DownloadMusic> downloadMusicQueue = new LinkedList<>();
    public static DownloadManager downloadManager;
    public static DownloadReceiver downloadReceiver;


    /**
     * 初始化下载管理
     * */
    public static void init(Context context) {
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        // 注册广播接收器
        downloadReceiver = new DownloadReceiver();
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(downloadReceiver, filter);
    }

    /**
     * 添加下载任务
     * */
    public static void addDownloadFile(DownloadMusic downloadMusic) {
        downloadMusicQueue = new LinkedList<>();
        downloadMusicQueue.add(downloadMusic);
        List<DownloadMusic> splist = SPUtil.getListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.DownloadMusicListData, DownloadMusic.class);
        if(splist.size()>0) {
            downloadMusicQueue.addAll(splist);
        }
        List<DownloadMusic> list = new ArrayList<>(downloadMusicQueue);
        SPUtil.setListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.DownloadMusicListData, list);
    }

    /**
     * 获取下载列表
     * */
    public static void getDownloadList() {
        if(!isDownloading()) {
            List<DownloadMusic> splist = SPUtil.getListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.DownloadMusicListData, DownloadMusic.class);
            if(splist.size()>0) {
                downloadMusicQueue = new LinkedList<>();
                downloadMusicQueue.addAll(splist);
            }
        }
    }

    /**
     * 是否正在下载
     * */
    public static boolean isDownloading() {
        if(downloadManager != null) {
            DownloadManager.Query query = new DownloadManager.Query();
            Cursor cursor = downloadManager.query(query);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if(statusIndex != -1) {
                        int status = cursor.getInt(statusIndex);
                        if (status == DownloadManager.STATUS_PENDING || status == DownloadManager.STATUS_RUNNING) {
                            cursor.close();
                            return true;
                        }
                    }

                }
                cursor.close();
            }
        }
        return false;
    }

    /**
     * 开始下载任务
     * */
    public static void startDownload() {
        if(downloadManager != null && !downloadMusicQueue.isEmpty() && !isDownloading()) {
            if(downloadMusicQueue.size()>0) {
                DownloadMusic downloadMusic = downloadMusicQueue.poll();
                if(downloadMusic != null && (downloadMusic.status.equals(DownloadMusic.DownloadWaiting) || downloadMusic.status.equals(DownloadMusic.Downloading))) {
                    long downloadId = downloadFile(downloadMusic.fileName, downloadMusic.url);
                    List<DownloadMusic> splist = SPUtil.getListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.DownloadMusicListData, DownloadMusic.class);
                    if(splist.size() >0) {
                        splist.stream().filter(music -> music.fileName.equals(downloadMusic.fileName))
                                .findFirst()
                                .ifPresent(music -> {
                                    music.setStatus(DownloadMusic.Downloading);
                                    music.setDownloadId(downloadId + "");
                                    SPUtil.setListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.DownloadMusicListData, splist);
                                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DOWNLOAD_MUSIC_UPDATE));
                                });
                    }
                } else {
                    startDownload();
                }
            } else {
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DOWNLOAD_MUSIC_CANCEL));
            }
        } else {
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DOWNLOAD_MUSIC_CANCEL));
        }
    }

    /**
     * 取消下载
     * */
    public static void cancelDownload(long downloadId) {
        if(downloadManager != null) {
            downloadManager.remove(downloadId);
            updateList();
        }
    }

    /**
     * 更新列表状态
     * */
    public static void updateList() {
        List<DownloadMusic> splist = SPUtil.getListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.DownloadMusicListData, DownloadMusic.class);
        if(splist.size() >0) {
            splist.stream().filter(music -> music.fileName.equals(DownloadReceiver.fileName))
                    .findFirst()
                    .ifPresent(music -> {
                        music.setStatus(DownloadMusic.DownloadError);
                        SPUtil.setListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.DownloadMusicListData, splist);
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DOWNLOAD_MUSIC_UPDATE));

                        DownloadReceiver.downloadId = 0;
                        DownloadReceiver.fileName = "";
                    });
        }
    }


    public static long downloadFile(String fileName, String url) {
        return downloadFile("LLMusic音乐下载管理", fileName, url);
    }

    /**
     * 下载文件
     * */
    public static long downloadFile(String title, String fileName, String url) {
        if(downloadManager != null) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

            // 检查并创建目标目录
            File downloadDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "LLMusicDownload");
            if (!downloadDirectory.exists()) {
                downloadDirectory.mkdirs();
            }

            //将重复的文件删除
            File musicFile = new File(downloadDirectory, fileName + ".mp3");
            if(musicFile.exists()) {
                musicFile.delete();
            }

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "LLMusicDownload/" + fileName + ".mp3");
            request.setTitle(title);
            request.setVisibleInDownloadsUi(false);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            DownloadReceiver.downloadId = downloadManager.enqueue(request);
            DownloadReceiver.fileName = fileName;
            DownloadReceiver.startHandler();
            //将下载请求添加到任务对列
            return DownloadReceiver.downloadId;
        }
        return -1;
    }
}
