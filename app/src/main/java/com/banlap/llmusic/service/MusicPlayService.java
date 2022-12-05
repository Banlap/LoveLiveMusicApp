package com.banlap.llmusic.service;

import static com.banlap.llmusic.utils.NotificationHelper.LL_MUSIC_PLAYER;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import com.banlap.llmusic.model.Music;
import com.banlap.llmusic.model.MusicLyric;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.utils.FileUtil;
import com.banlap.llmusic.utils.NotificationHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Banlap on 2021/12/6
 */
public class MusicPlayService extends Service {

    public MediaPlayer mediaPlayer;
    private boolean isStop = false;
    private final Object lock = new Object();
    private List<MusicLyric> mMusicLyricList = new ArrayList<>();
    private int scrollTo=0;
    private String[] currentLyric;
    private String currentTime="";
    public static final int ARRAY_LENGTH = 5;
    private int[] cLyricIntArray = new int[ARRAY_LENGTH];
    private String[] cLyricStrArray = new String[ARRAY_LENGTH];

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

                    String currentTime = rebuildTime(currentPosition);
                    if(mMusicLyricList.size()>0) {
                       if(searchLyricListPos(currentTime)) {
                           EventBus.getDefault().post(new ThreadEvent(ThreadEvent.SCROLL_LYRIC, scrollTo));
                       }
                    }
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

            Bitmap bitmap=null;
            if(res!=null) {
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

        public void showLyric(final Music dataSource, final boolean isLoop) {
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_MUSIC_LYRIC, dataSource, isLoop));
        }

        public void player(final Music dataSource, final boolean isLoop, final String lyric, final List<MusicLyric> musicLyrics) {
            try {

                stop();
                //callBack.viewSeekBarResume();
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_SEEK_BAR_RESUME));

                mediaPlayer = new MediaPlayer();
                //当前歌词
                currentLyric = !lyric.equals("") ? lyric.split("\n") : null;
                currentTime="";
                scrollTo=0;
                cLyricIntArray = new int[ARRAY_LENGTH];
                cLyricStrArray = new String[ARRAY_LENGTH];

                if(null != mMusicLyricList) {
                    mMusicLyricList.clear();
                } else {
                    mMusicLyricList = new ArrayList<>();
                }
                mMusicLyricList.addAll(musicLyrics);

                mediaPlayer.setDataSource(dataSource.musicURL);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        isStop = false;
                        mediaPlayer.start();
                        //callBack.viewPause(isLoop);
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAUSE, isStop));
                        //callBack.viewMusicMsg(dataSource, mediaPlayer.getDuration());
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
                    //callBack.viewPause(true);
                } else {
                    mediaPlayer.start();
                    resumeThread();
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAUSE, false));
                    NotificationHelper.getInstance().createRemoteViews(context, musicName, musicSinger, bitmap, false);
                    //callBack.viewPause(false);
                }
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

    /** 播放或暂停 */
    public void pause() {
        if(mediaPlayer!=null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isStop = true;
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAUSE, true));
                //callBack.viewPause(true);
            } else {
                mediaPlayer.start();
                resumeThread();
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAUSE, false));
                //callBack.viewPause(false);
            }
        } else {
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.PLAY_LIST_FIRST));
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
    public String rebuildTime(long position) {
        long minLong = position /1000/60;
        long secLong = position /1000%60;
        String minStr = minLong <10 ? "0"+minLong : ""+minLong;
        String secStr = secLong <10 ? "0"+secLong : ""+secLong;
        return minStr + ":" + secStr;
    }

    /** 转换为秒 */
    public int showSec(long position) {
        long secLong = position /1000%60;
        String secStr = secLong <10 ? "0"+secLong : ""+secLong;
        return Integer.parseInt(secStr);
    }

    /** 查询是否存在对应时间的歌词 */
    public int[] isSearchLyric(String time, String[] ly) {
        int isSearch = -1;
        int[] row = new int[ARRAY_LENGTH];
        int maxSize = 0;
        if(ly!=null) {
            if(!currentTime.equals(time)) {
                currentTime = time;
                for(int i=0; i<ly.length; i++) {
                   /* if(ly[i].contains(time)) {
                        isSearch = i;
                        break;
                    }*/
                    if(ly[i].contains(time)) {
                        if(maxSize <row.length) {
                            row[maxSize] = i;
                            maxSize++;
                        }
                    }
                }
            }
        }
        //Log.e("ABMusicPlayer", "isSearch: " + isSearch + " time: " + time);
        return row;
    }

    /** 查询当前时间是否有对应歌词 */
    public boolean searchLyricListPos(String currentTime) {
        boolean isFind = false;
        if(mMusicLyricList.size()>0) {
            for(int i=0; i< mMusicLyricList.size(); i++) {
                if(currentTime.equals(mMusicLyricList.get(i).lyricTime)) {
                    scrollTo = i;
                    isFind = true;
                    break;
                }
            }
        }
        return isFind;
    }
}
