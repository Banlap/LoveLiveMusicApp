package com.banlap.llmusic.service;

import static com.banlap.llmusic.utils.NotificationHelper.LL_MUSIC_CHARACTER;
import static com.banlap.llmusic.utils.NotificationHelper.LL_MUSIC_FLOATING_LYRIC;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.banlap.llmusic.R;
import com.banlap.llmusic.model.MusicLyric;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.utils.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class LyricService extends Service {
    private static final String TAG = LyricService.class.getSimpleName();

    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;
    /**
     * 当前屏幕宽高
     * */
    private int deviceWidth, deviceHeight;
    private RelativeLayout rlFloatingLyric;
    private LinearLayout llFloatingLyric;
    public static TextView tvLyricLine1, tvLyricLine2;
    private ImageView ivClose;

    public static int currentPosition = 0,  nextPosition = 0, playerCurrentPosition = 0;
    private static List<MusicLyric> musicLyrics;
    public static Handler lyricHandler = new Handler();
    public static Runnable lyricRunnable;
    public static final int DELAY_MILLIS = 200;  //延迟0.2s发送

    private float sx, sy;
    private boolean isMove = false;
    /**
     * 触摸时记录开始时的坐标 用于判断按下事件跟结束事件
     * */
    private int mStartX, mStartY;
    /**
     * 触摸时记录开始时的时间 用于判断按下事件跟结束事件
     * */
    private long mLastTime;


    @Override
    public void onCreate() {
        super.onCreate();
        createView();
        initView();
    }

    private void createView() {
        musicLyrics = new ArrayList<>();

        setWindowManager(100, 150);
        LayoutInflater inflater = LayoutInflater.from(LyricService.this);
        rlFloatingLyric = (RelativeLayout) inflater.inflate(R.layout.view_lyric, null);
        mWindowManager.addView(rlFloatingLyric, wmParams);
    }

    private void initView() {
        llFloatingLyric = rlFloatingLyric.findViewById(R.id.ll_floating_lyric);
        llFloatingLyric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        llFloatingLyric.setOnTouchListener(new ViewTouchListener());

        tvLyricLine1 = rlFloatingLyric.findViewById(R.id.tv_lyric_line1);
        tvLyricLine2 = rlFloatingLyric.findViewById(R.id.tv_lyric_line2);
        ivClose = rlFloatingLyric.findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_CLOSE_FLOATING_LYRIC));
            }
        });
    }

    //设置WindowManager
    private void setWindowManager(int x, int y) {
        wmParams = new WindowManager.LayoutParams();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;
        mWindowManager = (WindowManager) LyricService.this.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        wmParams.format  = PixelFormat.RGBA_8888;
        wmParams.flags   = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
        wmParams.windowAnimations = android.R.style.Animation_Dialog;
        //设置位置
        wmParams.x = x;
        wmParams.y = y;
        //设置宽高
        wmParams.width  = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }



    public class ViewTouchListener implements View.OnTouchListener {

        @SuppressLint("ClickableViewAccessibility") //忽略触摸事件和点击事件同时发生所引起的冲突
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (R.id.ll_floating_lyric == v.getId()) {
                //当前手指的坐标
                float mRawX = event.getRawX();
                float mRawY = event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isMove = false;
                        mLastTime = System.currentTimeMillis();
                        mStartX = (int) event.getRawX();
                        mStartY = (int) event.getRawY();
                        sx = mRawX;
                        sy = mRawY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isMove = true;
                        //手指X轴滑动距离
                        float differenceValueX = mRawX - sx;
                        //手指Y轴滑动距离
                        float differenceValueY = mRawY - sy;
                        //获取手指按下的距离与控件本身X轴的距离
                        float ownX = wmParams.x;
                        //获取手指按下的距离与控件本身Y轴的距离
                        float ownY = wmParams.y;
                        //理论中X轴拖动的距离
                        float endX = ownX + differenceValueX;
                        //理论中Y轴拖动的距离
                        float endY = ownY + differenceValueY;
                        //X轴可以拖动的最大距离
                        float maxX = deviceWidth;
                        //Y轴可以拖动的最大距离
                        float maxY = deviceHeight;
                       /* //X轴边界限制
                        endX = endX < 0 ? 0 : endX > maxX ? maxX : endX;
                        //Y轴边界限制
                        endY = endY < 0 ? 0 : endY > maxY ? maxY : endY;*/
                        //开始移动
                        wmParams.x = (int) endX;
                        wmParams.y = (int) endY;
                        mWindowManager.updateViewLayout(rlFloatingLyric, wmParams);
                        //记录位置
                        sx = mRawX;
                        sy = mRawY;
                        break;
                    case MotionEvent.ACTION_UP:
                        long mCurrentTime = System.currentTimeMillis();
                        int mStopX = (int) event.getRawX();
                        int mStopY = (int) event.getRawY();
                        //判断时间
                        if (mCurrentTime - mLastTime < 500) {
                            //判断移动距离
                            isMove = Math.abs(mStartX - mStopX) >= 10 || Math.abs(mStartY - mStopY) >= 10;
                        } else {
                            isMove = true;
                        }
                        Log.i(TAG, "isMove: " + isMove);
                        break;
                }
                return isMove;
            } else {
                return true;
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_3", "LLMusicFloatingLyric_channel", NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(this)
                    .setChannelId("channel_3")
                    .setContentTitle("歌词服务")
                    .setContentText("运行中..")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_lyric_settings_light_ff)
                    .build();
            startForeground(LL_MUSIC_FLOATING_LYRIC, notification);
        } else {
            Notification notification = new Notification.Builder(this)
                    .setContentTitle("歌词服务")
                    .setContentText("运行中..")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_lyric_settings_light_ff)
                    .build();
            startForeground(LL_MUSIC_FLOATING_LYRIC, notification);
        }
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null != mWindowManager) {
            if(null != rlFloatingLyric) {
                mWindowManager.removeView(rlFloatingLyric);
            }
        }
    }

    public static void updateLyricUI(boolean isShow) {
        stopLyricRunnable();
        if(isShow) {
            if(musicLyrics != null && musicLyrics.size()>0) {
                lyricRunnable = new Runnable() {
                    @Override
                    public void run() {
                        //Log.e(TAG, "updateLyricUI: musicLyrics: " + (musicLyrics!=null));
                        //双重保险，当该服务停止时list参数丢失
                        if(musicLyrics != null && musicLyrics.size()>0) {
                            getMusicLyricPos();
                           // Log.e(TAG, "lyric: " + musicLyrics.get(currentPosition).lyricContext + " cPos: " + currentPosition + " size: " + musicLyrics.size() );

                            if(currentPosition == nextPosition) {
                                setCurrentLyric(true, musicLyrics.get(currentPosition).lyricContext);
                            } else {
                                setCurrentLyric(true, musicLyrics.get(currentPosition).lyricContext);
                                setCurrentLyric(false, musicLyrics.get(currentPosition + 1).lyricContext);
                                nextPosition = currentPosition;
                            }
                        } else {
                            setCurrentLyric(true, "暂无歌词");
                            setCurrentLyric(false, "暂无歌词");
                        }
                        lyricHandler.postDelayed(lyricRunnable, DELAY_MILLIS);
                    }
                };
                lyricHandler.post(lyricRunnable);
            }
        }
    }

    /** 关闭发送广播给小组件的线程 */
    public static boolean stopLyricRunnable() {
        if(lyricRunnable != null) {
            lyricHandler.removeCallbacks(lyricRunnable);
            lyricRunnable = null;
            return true;
        }
        return false;
    }

    /**
     * 设置当前歌曲的歌词
     * */
    public static void setMusicLyrics(List<MusicLyric> list) {
        if(musicLyrics != null) {
            musicLyrics.clear();
            if(list != null) {
                musicLyrics.addAll(list);
            }
        }
    }

    /**
     * 设置歌曲播放进度
     * */
    public static void setMusicPlayerPos(int pos) {
        playerCurrentPosition = pos;
    }

    /**
     * 设置浮动歌词
     * */
    public static void setCurrentLyric(boolean isFirst, String lyricContext) {
        if(tvLyricLine1 != null && tvLyricLine2 != null) {
            if(isFirst) {
                tvLyricLine1.setText(lyricContext);
            } else {
                tvLyricLine2.setText(lyricContext);
            }
        }
    }

    /**
     * 获取当前高亮歌词
     * */
    private static void getMusicLyricPos() {
        try{
            int currentTime = playerCurrentPosition;
            //Log.i(TAG, "currentTime: " + currentTime);
            if(null != musicLyrics) {

                if(!"".equals(musicLyrics.get(1).lyricTime)) {
                    if (currentTime < TimeUtil.timeToMill(musicLyrics.get(0).lyricTime)) {
                        currentPosition = 0;
                        return;
                    }
                } else {
                    return;
                }

                if(!"".equals(musicLyrics.get(musicLyrics.size()-1).lyricTime)) {
                    if (currentTime > TimeUtil.timeToMill(musicLyrics.get(musicLyrics.size()-1).lyricTime)) {
                        currentPosition = musicLyrics.size()-1;
                        return;
                    }
                } else {
                    if(!"".equals(musicLyrics.get(musicLyrics.size()-2).lyricTime)) {
                        if (currentTime > TimeUtil.timeToMill(musicLyrics.get(musicLyrics.size()-2).lyricTime)) {
                            currentPosition = musicLyrics.size()-2;
                            return;
                        }
                    } else {
                        return;
                    }
                }

                for(int i =0; i < musicLyrics.size(); i++) {
                    if(!"".equals(musicLyrics.get(i).lyricTime)&& !"".equals(musicLyrics.get(i).lyricEndTime) ) {
                        if(currentTime >= TimeUtil.timeToMill(musicLyrics.get(i).lyricTime) && currentTime < TimeUtil.timeToMill(musicLyrics.get(i).lyricEndTime)){
                            currentPosition = i;
                            return;
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.i(TAG, "e: " + e.getMessage());
        }
    }
}
