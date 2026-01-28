package com.banlap.llmusic.phone.ui.activity;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseActivity;
import com.banlap.llmusic.databinding.ActivityLockFullScreenBinding;
import com.banlap.llmusic.service.MusicPlayService;
import com.banlap.llmusic.phone.uivm.vm.LockFullScreenVM;
import com.banlap.llmusic.utils.CountDownHelper;
import com.banlap.llmusic.utils.MyAnimationUtil;
import com.banlap.llmusic.utils.NotificationHelper;
import com.banlap.llmusic.utils.TimeUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class LockFullScreenActivity extends BaseActivity<LockFullScreenVM, ActivityLockFullScreenBinding>
    implements LockFullScreenVM.LockFullScreenCallBack {
    private static final String TAG = LockFullScreenActivity.class.getSimpleName();
    private float startX;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_lock_full_screen;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initWindow() {
        super.initWindow();
        //设置入如下参数才能在设置密码的情况下显示在锁屏页面上
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }


    @Override
    protected void initView() {
        getViewDataBinding().setVm(getViewModel());
        getViewModel().setCallBack(this);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.setStatusBarColor(Color.TRANSPARENT);

        getViewDataBinding().getRoot().setOnTouchListener((view, motionEvent) -> {
            handleTouch(motionEvent);
            return true;
        });

        getViewDataBinding().tvDate.setText(TimeUtil.getCurrentDateByMD());
        setTime();

        if(!TextUtils.isEmpty(MusicPlayService.currentRoomPlayMusic.musicName)) {
            getViewDataBinding().tvMusicName.setText(MusicPlayService.currentRoomPlayMusic.musicName);
        }
        if(!TextUtils.isEmpty(MusicPlayService.currentRoomPlayMusic.musicSinger)) {
            getViewDataBinding().tvMusicSinger.setText(MusicPlayService.currentRoomPlayMusic.musicSinger);
        }

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        //requestOptions.override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL); //关键代码，加载原始大小
        requestOptions.format(DecodeFormat.PREFER_RGB_565); //设置为这种格式去掉透明度通道，可以减少内存占有

        if(MusicPlayService.currentRoomPlayMusic.musicImgByte != null) {
            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(MusicPlayService.currentRoomPlayMusic.musicImgByte)
                    .transform(new RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.ALL))
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(getViewDataBinding().ivMusicImg);
        }
    }

    private void setTime() {
        runOnUiThread(() -> {
            getViewDataBinding().tvTime.setText(TimeUtil.getCurrentDateByTime());
        });
        CountDownHelper.startCountTime(5, new CountDownHelper.CountDownCallBack() {
            @Override
            public void showTime(int countDown, String time) {

            }

            @Override
            public void finish() {
                setTime();
            }
        });
    }


    @Override
    public void onBackPressed() {
        NotificationHelper.getInstance().cancelNotification(getApplicationContext(), NotificationHelper.LL_MUSIC_FULL_SCREEN);
        CountDownHelper.pauseImm();
        super.onBackPressed();
    }

    private void handleTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX() - startX;
                // Move the view as the user swipes
                float viewMove = getViewDataBinding().getRoot().getX() + moveX;
                if(viewMove >=0) {
                    getViewDataBinding().getRoot().setX(viewMove);
                }
                //startX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float endX = event.getX() - startX;
                Log.e(TAG, "startX: " + startX + " endX: " + event.getX() + " value: " + endX);
                if(endX > -100) {
                    CountDownHelper.pauseImm();
                    finish();
                    MyAnimationUtil.objectAnimatorLeftOrRight(this, false, true, getViewDataBinding().llMain);
                    //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                } else {
                    getViewDataBinding().getRoot().setX(0);
                }
                // Handle the touch release event if needed
                break;
        }
    }
}
