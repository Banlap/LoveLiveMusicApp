package com.banlap.llmusic.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.banlap.llmusic.model.DownloadMusic;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.utils.DownloadHelper;
import com.banlap.llmusic.utils.LLActivityManager;
import com.banlap.llmusic.utils.SPUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 文件下载广播
 * */
public class DownloadReceiver extends BroadcastReceiver {
    private static final String TAG = DownloadReceiver.class.getSimpleName();
    public static long downloadId;  //下载id
    public static String fileName;  //文件名
    public static int currentPos = 0; //记录当前下载数值

    public static Handler downloadHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            //
            try {
                DownloadManager downloadManager = (DownloadManager) LLActivityManager.getInstance().getTopActivity().getSystemService(Context.DOWNLOAD_SERVICE);

                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(DownloadReceiver.downloadId);
                Cursor cursor = downloadManager.query(query);

                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DOWNLOAD_MUSIC_SHOW, 0));
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DOWNLOAD_MUSIC, fileName, currentPos));

                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        int bytesDownloadedIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                        int bytesTotalIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);

                        if (bytesDownloadedIndex != -1 && bytesTotalIndex != -1) {
                            int bytesDownloaded = cursor.getInt(bytesDownloadedIndex);
                            int bytesTotal = cursor.getInt(bytesTotalIndex);

                            if (bytesTotal > 0) {
                                currentPos = (int) ((bytesDownloaded * 100L) / bytesTotal);
                                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DOWNLOAD_MUSIC, fileName, currentPos));
                            }
                        }
                    }
                    cursor.close();
                }

                downloadHandler.sendEmptyMessageDelayed(msg.what, 1000);
            } catch (Exception e) {
                Log.d(TAG, "e: " + e.getMessage());
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        if (id == downloadId) {
            try {
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor cursor = downloadManager.query(query);

                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        int uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);

                        updateList();
                        stopHandler();
                        // 检查列是否存在
                        if (statusIndex != -1 && uriIndex != -1) {
                            int status = cursor.getInt(statusIndex);
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                // 下载成功，处理下载的文件
                                String uriString = cursor.getString(uriIndex);
                                // 处理下载的文件，例如显示通知或打开文件
                                Log.d(TAG, "Download successful: " + uriString);

                                DownloadHelper.startDownload();
                            } else {
                                Log.d(TAG, "Download not successful. Status: " + status);
                            }
                        } else {
                            Log.e(TAG, "Column not found in cursor.");
                        }
                    }
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "e: " + e.getMessage());
            }
        }
    }

    /**
     * 更新列表状态
     * */
    private void updateList() {
        List<DownloadMusic> splist = SPUtil.getListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.DownloadMusicListData, DownloadMusic.class);
        if(splist.size() >0) {
            splist.stream().filter(music -> music.fileName.equals(fileName))
                    .findFirst()
                    .ifPresent(music -> {
                        music.setStatus(DownloadMusic.DownloadSuccess);
                        SPUtil.setListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.DownloadMusicListData, splist);
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DOWNLOAD_MUSIC_UPDATE));
                    });
        }
        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DOWNLOAD_MUSIC_FINISH, fileName, 100));
    }

    /**
     * 开始刷新ui
     * */
    public static void startHandler() {
        Message message = new Message();
        message.what = 0;
        downloadHandler.sendMessage(message);
    }

    /**
     * 停止刷新ui
     * */
    public static void stopHandler() {
        downloadHandler.removeCallbacksAndMessages(null);
    }
}