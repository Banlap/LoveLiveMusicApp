package com.banlap.llmusic.service;

import static com.banlap.llmusic.utils.NotificationHelper.LL_MUSIC_PLAYER;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.SeekBar;

import androidx.media.session.MediaButtonReceiver;
import androidx.media3.session.MediaSession;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.banlap.llmusic.base.BaseActivity;
import com.banlap.llmusic.base.BaseApplication;
import com.banlap.llmusic.model.Music;
import com.banlap.llmusic.model.MusicLyric;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.ui.activity.LockFullScreenActivity;
import com.banlap.llmusic.ui.activity.MainActivity;
import com.banlap.llmusic.utils.LLActivityManager;
import com.banlap.llmusic.utils.NotificationHelper;
import com.banlap.llmusic.utils.SystemUtil;
import com.banlap.llmusic.utils.TimeUtil;
import com.danikula.videocache.HttpProxyCacheServer;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 音乐播放服务
 * @author Banlap on 2021/12/6
 */
public class MusicPlayService extends MediaBrowserServiceCompat {

    public static final String TAG = MusicPlayService.class.getSimpleName();
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";
    //用于旧版本通知栏点击、小组件点击action
    public static final String INTENT_ACTION_PLAY = "INTENT_ACTION_PLAY";
    public static final String INTENT_ACTION_PLAY_NEXT = "INTENT_ACTION_PLAY_NEXT";
    public static final String INTENT_ACTION_PLAY_LAST = "INTENT_ACTION_PLAY_LAST";

    public static MediaPlayer mediaPlayer;
    private boolean isStop = false;  //是否暂停
    private static boolean isUpdateWidgetUI = false; //是否在刷新小组件
    private final Object lock = new Object();  //线程锁
    private List<MusicLyric> mMusicLyricList = new ArrayList<>();
    public static int mStartPosition;  //当前歌曲播放时间
    public static int mAllPosition;    //当前歌曲总时间

    public static Handler appWidgetHandler = new Handler();
    public static Runnable appWidgetRunnable;
    public static Handler musicNotificationHandler = new Handler();
    public static Runnable musicNotificationRunnable;

    public static final int DELAY_MILLIS = 1000;  //延迟1s发送
    public static int mAudioSessionId;

    //使用MediaSession、MediaController处理
    public static MediaSessionCompat mMediaSession;
    public static MediaControllerCompat mediaController;
    public static PlaybackStateCompat.Builder stateBuilder;

    //当前歌曲信息
    public static Music currentMusic; //当前播放音乐的总信息
    private Thread musicPosThread;

    private static final ExecutorService musicExecutor = Executors.newFixedThreadPool(1); // 单线程
    private static final MediaMetadataRetriever retriever = new MediaMetadataRetriever();

    Runnable musicPosRunnable = new Runnable() {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted() && mediaPlayer != null) {
                synchronized (lock) {
                    while (isStop) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Log.e(TAG, "InterruptedException: " + e.getMessage());
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }

                }

                int currentPosition = mediaPlayer.getCurrentPosition();
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_SEEK_BAR_POS, currentPosition));

                mStartPosition = currentPosition;

            }
        }
    };

    public static MusicPlayService getInstance() { return new MusicPlayService(); }



    @Override
    public void onCreate() {
        super.onCreate();
        SystemUtil.getInstance().registerScreenReceiver(getApplication());
        initMediaSession();
    }

    /** 初始化MediaSession */
    private void initMediaSession() {
        //通过MediaSession处理歌曲的播放、暂停、拖动进度条
        mMediaSession = new MediaSessionCompat(this, getPackageName());
        mMediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public boolean onMediaButtonEvent(@NonNull Intent intent) {  //蓝牙按键控制
                Log.i(TAG, "onMediaButtonEvent");
                if (mMediaSession == null) {
                    return false;
                }
                if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
                    KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                    if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                        LLActivityManager.getInstance().getTopActivity().onKeyDown(event.getKeyCode(), event);
                        LLActivityManager.getInstance().getTopActivity().onKeyUp(event.getKeyCode(), event);
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void onPlay() { //播放歌曲 统一处理
                super.onPlay();
                if(mediaPlayer != null) {
                    mediaPlayer.start();
                    resumeThread();
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAUSE, false));
                    updateMusicNotification(mediaPlayer.isPlaying());
                    updatePlaybackState(PlaybackStateCompat.STATE_PLAYING, mMediaSession);
                    NotificationHelper.getInstance().createRemoteViews(MusicPlayService.this, currentMusic.musicName, currentMusic.musicSinger, currentMusic.musicImgBitmap, false);
                }
            }

            @Override
            public void onPause() { //暂停歌曲 统一处理
                super.onPause();
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                    isStop = true;
                    updateMusicNotification(mediaPlayer.isPlaying());
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAUSE, true));
                    updatePlaybackState(PlaybackStateCompat.STATE_PAUSED, mMediaSession);
                    NotificationHelper.getInstance().createRemoteViews(MusicPlayService.this, currentMusic.musicName, currentMusic.musicSinger, currentMusic.musicImgBitmap, true);
                }
            }

            @Override
            public void onSeekTo(long pos) { //进度条处理 用于通知栏
                super.onSeekTo(pos);
                if (mediaPlayer != null) {
                    Log.i(TAG, "onSeekTo");
                    mediaPlayer.seekTo((int) pos);
                    updatePlaybackStateByMP(mMediaSession);
                    NotificationHelper.getInstance().createRemoteViews(MusicPlayService.this, currentMusic.musicName, currentMusic.musicSinger, currentMusic.musicImgBitmap, false);
                }
            }

            @Override
            public void onSkipToNext() { //下一首 用于通知栏
                super.onSkipToNext();
                Log.i(TAG, "isNextNew");
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.MUSIC_IS_NEXT));
            }

            @Override
            public void onSkipToPrevious() { //上一首 用于通知栏
                super.onSkipToPrevious();
                Log.i(TAG, "isPauseNew");
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.MUSIC_IS_LAST));
            }
        });
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        );

        stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(
                PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE |
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                PlaybackStateCompat.ACTION_SEEK_TO );
        mMediaSession.setPlaybackState(stateBuilder.build());

        //应用未初始化时系统检测媒体按钮并进入
        Intent intent =  new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.setComponent(new ComponentName("com.banlap.llmusic", "androidx.media.session.MediaButtonReceiver"));
        intent.setPackage("com.banlap.llmusic");

        PendingIntent mediaButtonPendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(),
                0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        // 设置 MediaButtonReceiver
        mMediaSession.setMediaButtonReceiver(mediaButtonPendingIntent);

        mMediaSession.setActive(true);

        currentMusic = new Music();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        String action = intent.getAction();
        if(!TextUtils.isEmpty(action)) {
            switch (action) {
                case INTENT_ACTION_PLAY:
                    Log.i(TAG, "StartMusicIsPauseService");
                    boolean isPause = intent.getBooleanExtra("IsPauseMusic", false);
                    String musicName = intent.getStringExtra("MusicName");
                    String musicSinger = intent.getStringExtra("MusicSinger");
                    byte[] bis = intent.getByteArrayExtra("MusicBitmap");
                    Bitmap bitmap = bis != null ? BitmapFactory.decodeByteArray(bis, 0, bis.length) : null;
                    if (isPause) {
                        Log.i(TAG, "isPause");
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.MUSIC_IS_PAUSE, musicName, musicSinger, bitmap));
                    }
                    break;
                case INTENT_ACTION_PLAY_NEXT:
                    Log.i(TAG, "StartMusicNextService");
                    boolean isNext = intent.getBooleanExtra("NextMusic", false);
                    if (isNext) {
                        Log.i(TAG, "isNext");
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.MUSIC_IS_NEXT));
                    }
                    break;
                case INTENT_ACTION_PLAY_LAST:
                    Log.i(TAG, "StartMusicLastService");
                    boolean isLast = intent.getBooleanExtra("LastMusic", false);
                    if (isLast) {
                        Log.i(TAG, "isLast");
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.MUSIC_IS_LAST));
                    }
                    break;
            }
            return super.onStartCommand(intent, flag, startId);
        } else {
            boolean isPlayMusic = intent.getBooleanExtra("IsPlayMusic", false);
            Log.i(TAG, "isPlayMusic: " + isPlayMusic);
            if(!isPlayMusic) {
                Notification notification = NotificationHelper.getInstance().createRemoteViews(this, "LLMusic", "Singer", null, true);
                startForeground(LL_MUSIC_PLAYER, notification);
            } else {
                String musicName = intent.getStringExtra("MusicName");
                String musicSinger = intent.getStringExtra("MusicSinger");
                byte[] res = intent.getByteArrayExtra("MusicBitmap");

                currentMusic.setMusicName(musicName);
                currentMusic.setMusicSinger(musicSinger);

                Bitmap bitmap = null;
                if(res != null) {
                    bitmap = BitmapFactory.decodeByteArray(res, 0, res.length);
                    currentMusic.setMusicImgBitmap(bitmap);
                }
                Notification notification = NotificationHelper.getInstance().createRemoteViews(this, musicName, musicSinger, bitmap, false);
                startForeground(LL_MUSIC_PLAYER, notification);
            }
            return START_REDELIVER_INTENT;
        }


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable @org.jetbrains.annotations.Nullable Bundle rootHints) {

        if (isAllowedPackage(clientPackageName, clientUid)) {
            // 允许客户端访问，返回允许的根节点（root id）
            return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
        } else {
            // 不允许客户端访问，可以返回null或其他限制状态
            return null;
        }
    }

    private boolean isAllowedPackage(String clientPackageName, int clientUid) {
        return true;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>(); // 根据parentId加载媒体项的逻辑
        result.sendResult(mediaItems);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "StopForeground");
        stopForeground(true);
        SystemUtil.getInstance().unRegisterScreenReceiver(getApplication());
    }

    public class MusicBinder extends Binder {

        public MusicPlayService getService() {
            return MusicPlayService.this;
        }

        /** 播放歌曲整体流程1：获取歌词 */
        public void showLyric(final Music dataSource, final boolean isLoop) {
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_MUSIC_LYRIC, dataSource, isLoop));
        }

        /** 播放歌曲整体流程2：播放歌曲 */
        public void player(final Music dataSource, final boolean isLoop, final HttpProxyCacheServer proxyCacheServer, final List<MusicLyric> musicLyrics) {
            try {
                stop();
                //延迟0.2秒处理进度条等ui内容，同时线程休眠0.2秒
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_SEEK_BAR_RESUME));
                    }
                }, 200);
                Thread.sleep(200);

                mediaPlayer = new MediaPlayer();


                if(null != mMusicLyricList) {
                    mMusicLyricList.clear();
                } else {
                    mMusicLyricList = new ArrayList<>();
                }
                mMusicLyricList.addAll(musicLyrics);

                //设置当前音乐总信息
                currentMusic = dataSource;

                //设置缓存时的歌曲名称
                BaseApplication.setCacheMusicName(dataSource.musicName);
                //设置当前歌曲加载后存入缓存（目前先下载再缓存）
                String url = proxyCacheServer.getProxyUrl(dataSource.musicURL);
                if(dataSource.isLocal) { //判断当前歌曲是否本地，则不使用缓存方法
                    url = dataSource.musicURL;
                }

                if(dataSource.musicImgByte != null) {
                    currentMusic.setMusicImgBitmap(BitmapFactory.decodeByteArray(dataSource.musicImgByte, 0, dataSource.musicImgByte.length));
                }

                mediaPlayer.setDataSource(url);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepareAsync();

                //处理歌曲信息
                currentMusic.setMusicBitrate("--");
                currentMusic.setMusicMime("--");
                currentMusic.setMusicFileSize("-- MB");

                //获取MediaMeta内容 后台线程处理，网络缓慢的情况下会卡住
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_MUSIC_METADATA, dataSource));

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        isStop = false;
                        mediaPlayer.start();
                        mAllPosition = mediaPlayer.getDuration();
                        mAudioSessionId = mediaPlayer.getAudioSessionId();
                        Log.i(TAG, "mAudioSessionId: " + mAudioSessionId + " isStop: " + isStop);
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAUSE, isStop));
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_MUSIC_MSG, dataSource, mediaPlayer.getDuration()));
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_SHOW_VISUALIZER));

                        updateMetadata(dataSource);


                        updateMusicNotification(mediaPlayer.isPlaying());

                        musicPosThread = new Thread(musicPosRunnable);
                        musicPosThread.start();

                    }
                });
                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        Log.i(TAG, "mp: " + mp.isPlaying() + "music buffer: " + percent);

                    }
                });
                mediaPlayer.setLooping(isLoop);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.i("ABMediaPlay","isDown");
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.PLAY_FINISH_SUCCESS));
                    }
                });

                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Log.i("ABMediaPlay","isError");
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.PLAY_ERROR));
                        return true;
                    }
                });

            } catch(Exception e) {
                e.printStackTrace();
            }
        }


        /** 播放或暂停 */
        public void pause(Context context, String musicName, String musicSinger, Bitmap bitmap) {
            currentMusic.setMusicName(musicName);
            currentMusic.setMusicSinger(musicSinger);
            if(currentMusic.musicImgByte != null) {
                currentMusic.setMusicImgBitmap(BitmapFactory.decodeByteArray(currentMusic.musicImgByte, 0, currentMusic.musicImgByte.length));
            } else if(bitmap != null) {
                currentMusic.setMusicImgBitmap(bitmap);
            }

            if(mediaPlayer!=null) {
                if (mediaPlayer.isPlaying()) {
                    mediaController.getTransportControls().pause();
                } else {
                    mediaController.getTransportControls().play();
                }

//                if(!isUpdateWidgetUI) {
//                    updateWidgetUI(context, true);
//                }
                //updateMusicNotification(mediaPlayer.isPlaying());
            } else {
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.PLAY_LIST_FIRST));
            }
        }

        /** 立即播放 */
        public void playImm(Context context, String musicName, String musicSinger, Bitmap bitmap) {
            if (mediaPlayer != null) {
                currentMusic.setMusicName(musicName);
                currentMusic.setMusicSinger(musicSinger);
                if(currentMusic.musicImgByte != null) {
                    currentMusic.setMusicImgBitmap(BitmapFactory.decodeByteArray(currentMusic.musicImgByte, 0, currentMusic.musicImgByte.length));
                } else if(bitmap != null) {
                    currentMusic.setMusicImgBitmap(bitmap);
                }
                if (!mediaPlayer.isPlaying()) {
                    mediaController.getTransportControls().play();
                }
            }

        }

        /** 立即暂停 */
        public void pauseImm(Context context, String musicName, String musicSinger, Bitmap bitmap) {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaController.getTransportControls().pause();
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
            if(mediaPlayer != null) {
                return mediaPlayer.isPlaying();
            } else {
                return false;
            }
        }

        /** 清除并销毁 */
        public void clearMedia() {
            stop();
        }


        /** 获取MediaController*/
        public MediaControllerCompat getMediaController() {
            return mediaController;
        }

        public void setMediaController(MediaControllerCompat mcc) {
             mediaController = mcc;
        }

        /** 更新数据源 */
        public void updateMetadata(Music music) {
            //需要设置数据源，保证高版本显示进度条时更新歌曲进度
            mMediaSession.setMetadata(new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, music.musicName)
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, music.musicSinger)
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, music.musicType)
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ART,  MusicPlayService.currentMusic.musicImgBitmap)
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer.getDuration()) // 音乐总时长
                    .putString("IsLocal", music.isLocal()? "1" : "0")
                    .putString("Path", music.musicURL)
                    .build()
            );
        }

    }

    public MediaSessionCompat.Token getSessionToken() {
        return mMediaSession.getSessionToken();
    }

    /** 获取AudioSessionId */
    public static int getAudioSessionId() {
        return mAudioSessionId;
    }


    /**
     * 获取歌曲MediaMeta的信息
     * */
    public static Map<String, String> getMediaMeta(Music dataSource) {
        Map<String, String> map = new HashMap<>();
        String bitrate = "0";
        String mime ="";
        String quality="--";
        int bitrateInt = 0;
        try {
            if(dataSource.isLocal) {
                retriever.setDataSource(dataSource.musicURL);
            } else {
                retriever.setDataSource(dataSource.musicURL, new HashMap<>());
            }
            // 获取音频采样率
            bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
            bitrateInt = Integer.parseInt(bitrate)/1000;
            if(bitrateInt>192) {
                quality = "SQ";
            } else {
                quality = "HQ";
            }
            bitrate = ""+bitrateInt;
            mime = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            if(mime.equals("audio/flac")) {
                mime = "FLAC";
            } else {
                mime = "MP3";
            }
            map.put("Quality", quality);
            map.put("Bitrate", bitrate);
            map.put("Mime", mime);

            Log.d(TAG, "Audio bitrate: " + bitrate + " mime: " + mime);
        } catch (Exception e) {
            Log.d(TAG, "Audio bitrate error: " + e.getMessage());
        }

        return map;
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
        if(musicPosThread != null) {
            musicPosThread.interrupt();
        }
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
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
    public static void updateWidgetUI(Context context, boolean isLoading) {
        if(mediaPlayer !=null && mediaPlayer.isPlaying()) {
            stopAppWidgetRunnable();
            appWidgetRunnable = new Runnable() {
                @Override
                public void run() {
                    isUpdateWidgetUI = true;
                    sendWidgetBroadcastReceiver(context, isLoading);
                    appWidgetHandler.postDelayed(appWidgetRunnable, DELAY_MILLIS);
                }
            };
            appWidgetHandler.post(appWidgetRunnable);
        } else {
            if(stopAppWidgetRunnable()) {
                sendWidgetBroadcastReceiver(context, isLoading);
            }
            isUpdateWidgetUI = false;
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
    public static void sendWidgetBroadcastReceiver(Context context, boolean isLoading) {
        Intent intent = new Intent("WIDGET_PROVIDER_REFRESH_MUSIC_MSG");
        intent.setPackage(context.getPackageName());
        intent.putExtra("IsLoading", isLoading);

        String startTime = TimeUtil.rebuildTime(mStartPosition);
        if(!TextUtils.isEmpty(startTime)) {
            intent.putExtra("StartTime", startTime);
        }
        String allTime = TimeUtil.rebuildTime(mAllPosition);
        if(!TextUtils.isEmpty(allTime)) {
            intent.putExtra("AllTime", allTime);
        }

        BigDecimal bd1 = new BigDecimal(TimeUtil.showSec(mStartPosition));
        BigDecimal bd2 = new BigDecimal(TimeUtil.showSec(mAllPosition));

        int progress = bd1.divide(bd2, 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).intValue();
        intent.putExtra("MusicProgress", progress);
        //Log.i(TAG, "mStartPosition: " + showSec(mStartPosition) + " mAllPosition: " +  showSec(mAllPosition) + " /: " +  bd1.divide(bd2, 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)));
        context.sendBroadcast(intent);
    }


    /**  更新音乐通知栏进度 */
    private void updateMusicNotification(boolean isPlaying) {

        if(isPlaying) {
            stopMusicNotificationRunnable();
            Log.i(TAG, "updateMusicNotification: ");
            musicNotificationRunnable = new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer !=null && mediaPlayer.isPlaying()) {
                        //mediaController.getTransportControls().seekTo(mediaPlayer.getCurrentPosition());
                        updatePlaybackStateByMP(mMediaSession);
                        //NotificationHelper.setProgressByValue(mediaPlayer.getDuration(), mediaPlayer.getCurrentPosition());
                        musicNotificationHandler.postDelayed(musicNotificationRunnable, DELAY_MILLIS);
                    }
                }
            };
            musicNotificationHandler.post(musicNotificationRunnable);
        } else {
            if(stopMusicNotificationRunnable()) {
                updatePlaybackStateByMP(mMediaSession);
            }
        }
    }

    /**  关闭音乐通知栏进度线程 */
    public static boolean stopMusicNotificationRunnable() {
        if(musicNotificationRunnable != null) {
            musicNotificationHandler.removeCallbacks(musicNotificationRunnable);
            musicNotificationRunnable = null;
            return true;
        }
        return false;
    }

    public void updatePlaybackStateByMP(MediaSessionCompat mediaSession) {
        int state = mediaPlayer.isPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
        updatePlaybackState(state, mediaSession);
    }

    public void updatePlaybackState(int state, MediaSessionCompat mediaSession) {
        //Log.e(TAG, "mediaPlayer.getCurrentPosition(): " + mediaPlayer.getCurrentPosition());
        stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(
                PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE |
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                PlaybackStateCompat.ACTION_SEEK_TO );
        stateBuilder.setState(state, mediaPlayer.getCurrentPosition(), mediaPlayer.isPlaying()? 1.0f : 0f, SystemClock.elapsedRealtime());
        mediaSession.setPlaybackState(stateBuilder.build());
    }

    /** 清除媒体会话参数 */
    public static void clearMediaSession(){
        if (mMediaSession != null) {
            mMediaSession.setCallback(null);
            mMediaSession.setActive(false);
            mMediaSession.release();
            mMediaSession = null;
        }
    }

}
