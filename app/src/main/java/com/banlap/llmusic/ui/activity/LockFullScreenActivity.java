package com.banlap.llmusic.ui.activity;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseActivity;
import com.banlap.llmusic.databinding.ActivityLockFullScreenBinding;
import com.banlap.llmusic.uivm.vm.LockFullScreenVM;
import com.banlap.llmusic.utils.CountDownHelper;
import com.banlap.llmusic.utils.NotificationHelper;
import com.banlap.llmusic.utils.TimeUtil;
import com.banlap.llmusic.widget.SildingFinishLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class LockFullScreenActivity extends BaseActivity<LockFullScreenVM, ActivityLockFullScreenBinding>
    implements LockFullScreenVM.LockFullScreenCallBack {

    private static final String TAG = LockFullScreenActivity.class.getSimpleName();
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


        getViewDataBinding().sflMain.setOnSildingFinishListener(new SildingFinishLayout.OnSildingFinishListener() {
            @Override
            public void onSildingFinish() {
                NotificationHelper.getInstance().cancelNotification(getApplicationContext(), NotificationHelper.LL_MUSIC_FULL_SCREEN);
                CountDownHelper.pauseImm();
                finish();
            }
        });
        getViewDataBinding().sflMain.setTouchView(getViewDataBinding().sflMain);

        getViewDataBinding().tvDate.setText(TimeUtil.getCurrentDateByMD());
        setTime();

        if(!TextUtils.isEmpty(MainActivity.currentMusicName)) {
            getViewDataBinding().tvMusicName.setText(MainActivity.currentMusicName);
        }
        if(!TextUtils.isEmpty(MainActivity.currentMusicSinger)) {
            getViewDataBinding().tvMusicSinger.setText(MainActivity.currentMusicSinger);
        }

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        //requestOptions.override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL); //关键代码，加载原始大小
        requestOptions.format(DecodeFormat.PREFER_RGB_565); //设置为这种格式去掉透明度通道，可以减少内存占有

        if(MainActivity.currentBitmap != null) {
            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(MainActivity.currentBitmap)
                    .transform(new RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.ALL))
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(getViewDataBinding().ivMusicImg);
        }
    }

    private void setTime() {
        runOnUiThread(() -> {
            Toast.makeText(this, "已刷新", Toast.LENGTH_SHORT).show();
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
}
