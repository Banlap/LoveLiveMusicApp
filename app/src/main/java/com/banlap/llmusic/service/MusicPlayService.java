package com.banlap.llmusic.service;

import static com.banlap.llmusic.utils.NotificationHelper.LL_MUSIC_PLAYER;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import com.banlap.llmusic.model.Music;
import com.banlap.llmusic.model.MusicLyric;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.utils.FileUtil;
import com.banlap.llmusic.utils.NotificationHelper;
import com.danikula.videocache.HttpProxyCacheServer;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 音乐播放服务
 * @author Banlap on 2021/12/6
 */
public class MusicPlayService extends Service {

    public MediaPlayer mediaPlayer;
    private boolean isStop = false;
    private final Object lock = new Object();
    private List<MusicLyric> mMusicLyricList = new ArrayList<>();
    public static int mStartPosition;  //当前歌曲播放时间
    public static int mAllPosition;    //当前歌曲总时间
    public static Handler appWidgetHandler = new Handler();
    public static Runnable appWidgetRunnable;
    public static final int DELAY_MILLIS = 1000;


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                while (true) {
                    while (isStop) {
                        synchronized (lock) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_CURRENT_TIME,  rebuildTime(currentPosition)));
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_SEEK_BAR_POS, currentPosition));

                    mStartPosition = currentPosition;

                }
            }
        }
    };

    public static MusicPlayService getInstance() { return new MusicPlayService(); }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {

        boolean isPlayMusic = intent.getBooleanExtra("IsPlayMusic", false);
        Log.e("ABMusicPlayer", "isPlayMusic: " + isPlayMusic);
        if(!isPlayMusic) {
            Notification notification = NotificationHelper.getInstance().createNotificationReturn(this, "LLMusic", "Singer", null, true);
            startForeground(LL_MUSIC_PLAYER, notification);
        } else {
            String musicName = intent.getStringExtra("MusicName");
            String musicSinger = intent.getStringExtra("MusicSinger");
            byte[] res = intent.getByteArrayExtra("MusicBitmap");

            Bitmap bitmap = null;
            if(res != null) {
                bitmap = BitmapFactory.decodeByteArray(res, 0, res.length);
            }
            Notification notification = NotificationHelper.getInstance().createNotificationReturn(this, musicName, musicSinger, bitmap, false);
            startForeground(LL_MUSIC_PLAYER, notification);
        }
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("ABMusicPlayer", "StopForeground");
        stopForeground(true);
    }

    public class MusicBinder extends Binder {

        /** 播放歌曲整体流程1：获取歌词 */
        public void showLyric(final Music dataSource, final boolean isLoop) {
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_MUSIC_LYRIC, dataSource, isLoop));
        }

        /** 播放歌曲整体流程2：播放歌曲 */
        public void player(final Music dataSource, final boolean isLoop, final HttpProxyCacheServer proxyCacheServer, final List<MusicLyric> musicLyrics) {
            try {
                stop();
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_SEEK_BAR_RESUME));

                mediaPlayer = new MediaPlayer();

                if(null != mMusicLyricList) {
                    mMusicLyricList.clear();
                } else {
                    mMusicLyricList = new ArrayList<>();
                }
                mMusicLyricList.addAll(musicLyrics);

                //设置当前歌曲加载后存入缓存（目前先下载再缓存）
                String url = proxyCacheServer.getProxyUrl(dataSource.musicURL);
                if(dataSource.isLocal) { //判断当前歌曲是否本地，则不使用缓存方法
                    url = dataSource.musicURL;
                }

                mediaPlayer.setDataSource(url);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        isStop = false;
                        mediaPlayer.start();
                        mAllPosition = mediaPlayer.getDuration();
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAUSE, isStop));
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_MUSIC_MSG, dataSource, mediaPlayer.getDuration()));
                        new Thread(runnable).start();
                    }
                });
                mediaPlayer.setLooping(isLoop);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.e("ABMediaPlay","isDown");
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.PLAY_FINISH_SUCCESS));
                    }
                });
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Log.e("ABMediaPlay","isError");
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.PLAY_ERROR));
                        return true;
                    }
                });

            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        /** 播放或暂停 */
        public void pause(Context context, String musicName, String musicSinger, Bitmap bitmap) {
            if(mediaPlayer!=null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    isStop = true;
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAUSE, true));
                    NotificationHelper.getInstance().createRemoteViews(context, musicName, musicSinger, bitmap, true);
                } else {
                    mediaPlayer.start();
                    resumeThread();
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAUSE, false));
                    NotificationHelper.getInstance().createRemoteViews(context, musicName, musicSinger, bitmap, false);
                }
                updateWidgetUI(mediaPlayer.isPlaying(), true);
            } else {
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.PLAY_LIST_FIRST));
            }
        }

        /** 立即播放 */
        public void playImm(Context context, String musicName, String musicSinger, Bitmap bitmap) {
            if (mediaPlayer != null) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    resumeThread();
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAUSE, false));
                    NotificationHelper.getInstance().createRemoteViews(context, musicName, musicSinger, bitmap, false);
                }
            }

        }

        /** 立即暂停 */
        public void pauseImm(Context context, String musicName, String musicSinger, Bitmap bitmap) {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    isStop = true;
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAUSE, true));
                    NotificationHelper.getInstance().createRemoteViews(context, musicName, musicSinger, bitmap, true);
                }
            }
        }

        /** 是否单曲循环 */
        public void setSingePlayMode(boolean isOpen) {
            if(mediaPlayer!=null) {
                mediaPlayer.setLooping(isOpen);
            }
        }

        /** 跳转 */
        public void seekTo(SeekBar seekBar) {
            if(mediaPlayer!=null) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        }

        /** 线程锁 */
        public void posLock(boolean isLock) {
            if(isLock) {
                isStop = true;
            } else {
                resumeThread();
            }
        }

        /** 当前是否播放音乐 */
        public boolean isPlay() {
            if(mediaPlayer!=null) {
                return mediaPlayer.isPlaying();
            } else {
                return false;
            }
        }

        public void clearMedia() {
            stop();
        }

        public MediaPlayer getMediaPlayer() {
            if(null == mediaPlayer) {
                return null;
            }
            return mediaPlayer;
        }
    }

    /** 恢复线程 */
    public void resumeThread() {
        isStop = false;
        synchronized (lock) {
            lock.notify();
        }
    }


    /** 关闭 */
    public void stop() {
        isStop = true;
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /** 转换成时间格式*/
    public static String rebuildTime(long position) {
        long minLong = position /1000/60;
        long secLong = position /1000%60;
        String minStr = minLong <10 ? "0"+minLong : ""+minLong;
        String secStr = secLong <10 ? "0"+secLong : ""+secLong;
        return minStr + ":" + secStr;
    }

    /** 转换为秒 */
    public static int showSec(long position) {
        long minLong = position /1000/60;
        long secLong = position /1000%60;
        String minStr = minLong <10 ? "0"+minLong : ""+minLong;
        String secStr = secLong <10 ? "0"+secLong : ""+secLong;
        if(minLong >0) {
            return (Integer.parseInt(secStr) + (Integer.parseInt(minStr) * 60));
        }
        return Integer.parseInt(secStr);
    }

    /** 查询当前时间是否有对应歌词 */
    public boolean searchLyricListPos(String currentTime) {
        boolean isFind = false;
        if(mMusicLyricList.size()>0) {
            for(int i=0; i< mMusicLyricList.size(); i++) {
                if(currentTime.equals(mMusicLyricList.get(i).lyricTime)) {
                    isFind = true;
                    break;
                }
            }
        }
        return isFind;
    }

    /** 更新小组件UI */
    private void updateWidgetUI(boolean isPlaying, boolean isLoading) {
        if(isPlaying) {
            stopAppWidgetRunnable();
            appWidgetRunnable = new Runnable() {
                @Override
                public void run() {
                    sendWidgetBroadcastReceiver();
                    appWidgetHandler.postDelayed(appWidgetRunnable, DELAY_MILLIS);
                }
            };

            appWidgetHandler.post(appWidgetRunnable);
        } else {
            if(stopAppWidgetRunnable()) {
                sendWidgetBroadcastReceiver();
            }
        }
    }

    /** 关闭发送广播给小组件的线程 */
    public static boolean stopAppWidgetRunnable() {
        if(null != appWidgetRunnable) {
            appWidgetHandler.removeCallbacks(appWidgetRunnable);
            appWidgetRunnable = null;
            return true;
        }
        return false;
    }

    /** 发送广播给小组件 更新视图 */
    private void sendWidgetBroadcastReceiver() {
        Intent intent = new Intent("WIDGET_PROVIDER_REFRESH_MUSIC_MSG");
        intent.setPackage(getPackageName());
        intent.putExtra("IsLoading", false);

        String startTime = rebuildTime(mStartPosition);
        if(!TextUtils.isEmpty(startTime)) {
            intent.putExtra("StartTime", startTime);
        }
        String allTime = rebuildTime(mAllPosition);
        if(!TextUtils.isEmpty(allTime)) {
            intent.putExtra("AllTime", allTime);
        }

        BigDecimal bd1 = new BigDecimal(showSec(mStartPosition));
        BigDecimal bd2 = new BigDecimal(showSec(mAllPosition));

        int progress = bd1.divide(bd2, 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).intValue();
        intent.putExtra("MusicProgress", progress);
        //Log.e("LogByAB", "mStartPosition: " + showSec(mStartPosition) + " mAllPosition: " +  showSec(mAllPosition) + " /: " +  bd1.divide(bd2, 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)));
        sendBroadcast(intent);
    }


}
