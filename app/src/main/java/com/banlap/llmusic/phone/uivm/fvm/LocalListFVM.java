package com.banlap.llmusic.phone.uivm.fvm;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.banlap.llmusic.model.LocalFile;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.phone.uivm.vm.MainVM;
import com.banlap.llmusic.utils.BitmapUtil;
import com.banlap.llmusic.utils.FileUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
        final String[] fileType = { ".mp3", ".flac" };
        final File dir = new File(rootPath);
        if(null != localFileList) {
            localFileList.clear();
        }
        localFileList = new ArrayList<>();

        scanThread = new Thread(new Runnable() {
            @Override
            public void run() {
                scanFile(dir, fileType);
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
     * @param fileType 文件后缀类型，可设置多个。如：{".mp3",".wav"}
     * */
    private void scanFile(File dir, String... fileType) {
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (final File file : files) {
                for (String s : fileType) {
                    if (file.getName().toUpperCase().endsWith(s.toUpperCase())) {
                        /*是符合后缀名的文件  添加到列表中*/
                        try {
                            String name = file.getName();
                            String path = file.getPath();
                            long totalSpace = file.getTotalSpace();
                            long useSpace = file.getUsableSpace();
                            String absolutePath = file.getAbsolutePath();

                            getMusicData(path, false);

                        } catch (Exception e) {
                            Log.i(TAG, "e: " + e.getMessage());
                        }
                    }
                }
                /*是目录*/
                if (file.isDirectory()) {
                    /*递归扫描*/
                    scanFile(file, fileType);
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

    /** 获取音乐信息 */
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

            Bitmap picBitmap = pic != null? BitmapFactory.decodeByteArray(pic, 0, pic.length) : null;
            if(null != picBitmap) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if (BitmapUtil.getBitmapSize(picBitmap) >= 900000) {  //本地导入的歌曲图片过大需要压缩质量
                    //重新压缩图片
                    BitmapFactory.Options optionsNew = new BitmapFactory.Options();
                    optionsNew.inJustDecodeBounds = false;
                    optionsNew.inSampleSize = 4;//宽和高变为原来的1/4，即图片压缩为原来的1/16
                    Bitmap bitmapNew = BitmapFactory.decodeByteArray(pic, 0, pic.length, optionsNew);
                    bitmapNew.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                } else {
                    //使用工厂把网络的输入流生产Bitmap
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = 1; // 1 不压缩, 4 为宽和高变为原来的1/4，即图片压缩为原来的1/16
                    Bitmap bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length, options);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                }
                pic = baos.toByteArray();
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
            Log.i(TAG, "e: " + e.getMessage());
        }

    }

    /** 取消扫描任务 */
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
