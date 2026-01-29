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

import com.banlap.llmusic.receiver.DownloadReceiver;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.sql.AppData;
import com.banlap.llmusic.sql.room.RoomDownloadMusic;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

/**
 * 下载管理
 * */
public class DownloadHelper {
    private static final String TAG = DownloadHelper.class.getSimpleName();

    private static Queue<RoomDownloadMusic> downloadMusicQueue = new LinkedList<>();
    private static DownloadManager downloadManager;
    private static DownloadReceiver downloadReceiver;


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
    public static void addDownloadFile(RoomDownloadMusic roomDownloadMusic) {
        downloadMusicQueue = new LinkedList<>();
        downloadMusicQueue.add(roomDownloadMusic);

//        List<DownloadMusic> splist = SPUtil.getListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.DownloadMusicListData, DownloadMusic.class);
//        if(splist.size()>0) {
//            downloadMusicQueue.addAll(splist);
//        }
//        List<DownloadMusic> list = new ArrayList<>(downloadMusicQueue);
//        SPUtil.setListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.DownloadMusicListData, list);

        AppData.roomDownloadMusicList.add(roomDownloadMusic);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                AppData.saveDownloadMusic(roomDownloadMusic);
            }
        });
    }

    /**
     * 获取下载列表
     * */
    public static void getDownloadList() {
        if(!isDownloading()) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    List<RoomDownloadMusic> list = AppData.getDownloadMusicList();
                    if(!list.isEmpty()) {
                        downloadMusicQueue = new LinkedList<>();
                        downloadMusicQueue.addAll(list);
                    }
                }
            });
//            List<DownloadMusic> splist = SPUtil.getListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.DownloadMusicListData, DownloadMusic.class);
//            if(splist.size()>0) {
//                downloadMusicQueue = new LinkedList<>();
//                downloadMusicQueue.addAll(splist);
//            }
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
            if(!downloadMusicQueue.isEmpty()) {
                RoomDownloadMusic downloadMusic = downloadMusicQueue.poll();
                if(downloadMusic != null && (downloadMusic.status.equals(RoomDownloadMusic.DownloadWaiting) || downloadMusic.status.equals(RoomDownloadMusic.Downloading))) {
                    long downloadId = downloadFile(downloadMusic);

                    if(!AppData.roomDownloadMusicList.isEmpty()) {
                        AppData.roomDownloadMusicList.stream().filter(music -> music.id == downloadMusic.id)
                                .findFirst()
                                .ifPresent(music -> {
                                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            AppData.updateRoomDownloadMusic(music.id, roomDownloadMusic->{
                                                roomDownloadMusic.status = RoomDownloadMusic.Downloading;
                                            });
                                        }
                                    });
                                    music.status = RoomDownloadMusic.Downloading;
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
        if(!AppData.roomDownloadMusicList.isEmpty()) {
            AppData.roomDownloadMusicList.stream().filter(music -> music.id == DownloadReceiver.fileId)
                    .findFirst()
                    .ifPresent(music -> {
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                AppData.updateRoomDownloadMusic(music.id, roomDownloadMusic -> {
                                    roomDownloadMusic.status = RoomDownloadMusic.DownloadError;
                                });
                            }
                        });
                        music.status = RoomDownloadMusic.DownloadError;
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DOWNLOAD_MUSIC_UPDATE));

                        DownloadReceiver.downloadId = 0;
                        DownloadReceiver.fileName = "";
                    });
        }
    }


    public static long downloadFile(RoomDownloadMusic roomDownloadMusic) {
        return downloadFile("LLMusic音乐下载管理", roomDownloadMusic);
    }

    /**
     * 下载文件
     * */
    public static long downloadFile(String title, RoomDownloadMusic roomDownloadMusic) {
        if(downloadManager != null) {
            String url = roomDownloadMusic.url;
            int fileId = roomDownloadMusic.id;
            String fileName = roomDownloadMusic.fileName;

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
            DownloadReceiver.fileId = fileId;
            DownloadReceiver.fileName = fileName;
            DownloadReceiver.startHandler();
            //将下载请求添加到任务对列
            return DownloadReceiver.downloadId;
        }
        return -1;
    }
}
