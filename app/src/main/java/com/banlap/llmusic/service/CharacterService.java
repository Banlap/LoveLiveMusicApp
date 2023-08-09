package com.banlap.llmusic.service;

import static com.banlap.llmusic.utils.NotificationHelper.LL_MUSIC_CHARACTER;

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
import android.os.Looper;
import android.os.Message;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.banlap.llmusic.R;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.uivm.MainVM;
import com.banlap.llmusic.utils.CharacterHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;
import java.util.Random;

/**
 * 角色服务
 * */
public class CharacterService extends Service {

    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;

    private RelativeLayout rlCharacter;
    private ConstraintLayout clCharacter;
    private ImageView ivCharacter;
    private static LinearLayout llCharacterTalk;
    private LinearLayout llSayHello, llSayGood, llGame;
    private LinearLayout llLastMusic, llPlayMusic, llNextMusic;
    private static ImageView ivPlayMusic;
    private TextView tvCharacterTalk;
    //当前屏幕宽高
    private int deviceWidth, deviceHeight;

    private String mCharacterName;   //当前角色
    //角色视图
    private float sx;
    private float sy;
    private boolean isMove = false;
    //触摸时记录开始时的坐标 用于判断按下事件跟结束事件
    private int mStartX, mStartY;
    //触摸时记录开始时的时间 用于判断按下事件跟结束事件
    private long mLastTime;

    //文本倒计时
    private static final Handler talkHandler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            //正常操作
            if(msg.what == 0) {
                hideCharacterTalk();
            }
            talkHandler.sendEmptyMessageDelayed(msg.what -1, 1000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        createView();
        initView();
    }


    private void createView() {
        setWindowManager(100, 150);
        LayoutInflater inflater = LayoutInflater.from(CharacterService.this);
        rlCharacter = (RelativeLayout) inflater.inflate(R.layout.view_character_img, null);
        mWindowManager.addView(rlCharacter, wmParams);

    }

    //设置WindowManager
    private void setWindowManager(int x, int y) {
        wmParams = new WindowManager.LayoutParams();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;
        mWindowManager = (WindowManager) CharacterService.this.getSystemService(Context.WINDOW_SERVICE);
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
        wmParams.width  = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    private void initView() {
        //MainVM.initAnimatedCharacter();
        clCharacter = rlCharacter.findViewById(R.id.cl_character);
        ivCharacter = rlCharacter.findViewById(R.id.iv_character);
        llCharacterTalk = rlCharacter.findViewById(R.id.ll_character_talk);
        llSayHello = rlCharacter.findViewById(R.id.ll_say_hello);
        llSayGood = rlCharacter.findViewById(R.id.ll_say_good);
        llGame = rlCharacter.findViewById(R.id.ll_game);
        llLastMusic = rlCharacter.findViewById(R.id.ll_last_music);
        llPlayMusic = rlCharacter.findViewById(R.id.ll_play_music);
        ivPlayMusic = rlCharacter.findViewById(R.id.iv_play_music);
        llNextMusic = rlCharacter.findViewById(R.id.ll_next_music);
        tvCharacterTalk = rlCharacter.findViewById(R.id.tv_character_talk);
        //将角色传递到CharacterHelper
        CharacterHelper.initCharacter(ivCharacter);
        llCharacterTalk.setVisibility(View.GONE);
        llSayHello.setVisibility(View.GONE);
        llSayGood.setVisibility(View.GONE);
        llLastMusic.setVisibility(View.GONE);
        llPlayMusic.setVisibility(View.GONE);
        llNextMusic.setVisibility(View.GONE);
        llSayHello.setOnClickListener(new ButtonClickListener());
        llSayGood.setOnClickListener(new ButtonClickListener());
        llGame.setOnClickListener(new ButtonClickListener());
        llLastMusic.setOnClickListener(new ButtonClickListener());
        llPlayMusic.setOnClickListener(new ButtonClickListener());
        llNextMusic.setOnClickListener(new ButtonClickListener());
        ivCharacter.setOnClickListener(new ButtonClickListener());
        ivCharacter.setOnTouchListener(new ViewTouchListener());
    }

    public class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.iv_character) {
                if(llSayHello.getVisibility() == View.GONE && llSayGood.getVisibility() == View.GONE) {
                    llSayHello.setVisibility(View.VISIBLE);
                    llSayGood.setVisibility(View.VISIBLE);
                    llLastMusic.setVisibility(View.VISIBLE);
                    llPlayMusic.setVisibility(View.VISIBLE);
                    llNextMusic.setVisibility(View.VISIBLE);
                } else {
                    llSayHello.setVisibility(View.GONE);
                    llSayGood.setVisibility(View.GONE);
                    llLastMusic.setVisibility(View.GONE);
                    llPlayMusic.setVisibility(View.GONE);
                    llNextMusic.setVisibility(View.GONE);
                }
            } else if(v.getId() == R.id.ll_say_hello) {
                if(llCharacterTalk.getVisibility() == View.GONE) {
                    llCharacterTalk.setVisibility(View.VISIBLE);
                    tvCharacterTalk.setText(CharacterHelper.sayHelloContent(mCharacterName));
                    showContent();
                }
            } else if(v.getId() == R.id.ll_say_good) {
                if (llCharacterTalk.getVisibility() == View.GONE) {
                    llCharacterTalk.setVisibility(View.VISIBLE);
                    tvCharacterTalk.setText(CharacterHelper.sayGoodContent(mCharacterName));
                    showContent();
                }
            } else if(v.getId() == R.id.ll_game) {

            } else if(v.getId() == R.id.ll_last_music) {
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.MUSIC_IS_LAST));
            } else if(v.getId() == R.id.ll_play_music) {
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.PLAY_MUSIC));
            } else if(v.getId() == R.id.ll_next_music) {
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.MUSIC_IS_NEXT));
            }

        }
    }

    //发送显示文本
    public static void showContent() {
        android.os.Message message = new Message();
        message.what = 3;
        talkHandler.sendMessage(message);
    }

    //发送显示文本
    public static void isPlayMusic(boolean isPlay) {
        ivPlayMusic.setBackgroundResource(isPlay? R.drawable.ic_pause_2_gray : R.drawable.ic_play_2_gray);
    }

    public class ViewTouchListener implements View.OnTouchListener {

        @SuppressLint("ClickableViewAccessibility") //忽略触摸事件和点击事件同时发生所引起的冲突
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (R.id.iv_character == v.getId()) {
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
                        mWindowManager.updateViewLayout(rlCharacter, wmParams);
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
                        Log.i("LogByAB", "isMove: " + isMove);
                        break;
                }
                return isMove;
            } else {
                return true;
            }
        }
    }

    public static void hideCharacterTalk() {
        llCharacterTalk.setVisibility(View.GONE);
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        boolean isPlayMusic = intent.getBooleanExtra("IsPlayMusic", false);
        isPlayMusic(isPlayMusic);
        //更新角色图
        mCharacterName = intent.getStringExtra("CharacterName");
        if(null == mCharacterName) {  //为null或空白时 赋予默认角色
            mCharacterName = CharacterHelper.CHARACTER_NAME_KEKE;
        } else if ("".equals(mCharacterName)) {
            mCharacterName = CharacterHelper.CHARACTER_NAME_KEKE;
        }

        //根据播放或暂停 对角色状态变更
        MainVM.stopHandler();
        if(isPlayMusic) {
            MainVM.animatedListenCharacter(mCharacterName);
        } else {
            MainVM.initAnimatedCharacter(mCharacterName);
        }
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_2", "LLMusicCharacter_channel", NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(this)
                    .setChannelId("channel_2")
                    .setContentTitle("角色服务")
                    .setContentText("运行中..")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_llmp_small_2)
                    .build();
            startForeground(LL_MUSIC_CHARACTER, notification);
        } else {
            Notification notification = new Notification.Builder(this)
                    .setContentTitle("角色服务")
                    .setContentText("运行中..")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_llmp_small_2)
                    .build();
            startForeground(LL_MUSIC_CHARACTER, notification);
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
            if(null != rlCharacter) {
                mWindowManager.removeView(rlCharacter);
            }
        }
    }
}
