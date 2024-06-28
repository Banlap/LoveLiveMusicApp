package com.banlap.llmusic.ui.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.splashscreen.SplashScreen;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseActivity;
import com.banlap.llmusic.databinding.ActivityWelcomeBinding;
import com.banlap.llmusic.pad.ui.activity.PadMainActivity;
import com.banlap.llmusic.uivm.vm.WelcomeVM;
import com.banlap.llmusic.utils.SPUtil;
import com.banlap.llmusic.utils.SystemUtil;

/**
 * 欢迎页
 * */
public class WelcomeActivity extends BaseActivity<WelcomeVM, ActivityWelcomeBinding>
    implements WelcomeVM.WelcomeCallBack {

    private static final String TAG = WelcomeActivity.class.getSimpleName();
    @Override
    protected int getLayoutId() { return R.layout.activity_welcome; }

    @Override
    protected void beforeOnCreate() {
        //显示闪屏页
        SplashScreen.installSplashScreen(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        getViewDataBinding().setVm(getViewModel());
        getViewModel().setCallBack(this);

        launchVideo();
    }

    /** 判断是否显示启动动画 */
    private void launchVideo() {
        //pad版本跳过启动视频，看后续是否需要添加
        if(SystemUtil.isPad(getApplication())) {
            Intent intent = new Intent(getApplication(), PadMainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        //是否显示启动动画
        String isLaunchVideo = SPUtil.getStrValue(getApplicationContext(), SPUtil.CloseLaunchVideo);
        if(!TextUtils.isEmpty(isLaunchVideo) && "0".equals(isLaunchVideo)) {
            runActivity();
            return;
        }
        //启动动画
        initVideo();
        new Handler().postDelayed(this::runActivity, 5000);
    }

    /**
     * 进入页面
     * */
    private void runActivity() {

        Log.i(TAG, "isPad: " + SystemUtil.isPad(getApplication()));
//        Log.i(TAG, "isNavBarVisible: " + SystemUtil.isNavBarVisible(getWindow()));

        //判断设备是否pad，ps需要考虑通知栏点击和小组件点击 也需要判断
        //Intent intent = new Intent(getApplication(), SystemUtil.isPad(getApplication())? PadMainActivity.class : MainActivity.class);
        Intent intent = new Intent(getApplication(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    /** 视频保持宽高比 */
    private void setDimension() {
        float videoProportion = getVideoProportion();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        float screenProportion = (float) screenHeight / (float) screenWidth;
        android.view.ViewGroup.LayoutParams lp = getViewDataBinding().vvWelcomeVideo.getLayoutParams();
        if (videoProportion < screenProportion) {
            lp.height= screenHeight;
            lp.width = (int) ((float) screenHeight / videoProportion);
        } else {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth * videoProportion);
        }
        getViewDataBinding().vvWelcomeVideo.setLayoutParams(lp);
    }

    private float getVideoProportion(){
        return 1.5f;
    }

    /** 循环播放欢迎页视频 */
    private void initVideo() {
        setDimension();
        //判断是否使用了自定义启动动画
        String launchVideoPath = SPUtil.getStrValue(getApplicationContext(), SPUtil.LaunchVideoPath);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.welcomeliella);
        if(!TextUtils.isEmpty(launchVideoPath)) {
            uri = Uri.parse(launchVideoPath);
        }
        getViewDataBinding().vvWelcomeVideo.setVideoURI(uri);
        getViewDataBinding().vvWelcomeVideo.start();
        getViewDataBinding().vvWelcomeVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i(TAG, "onSuccess");
                mp.setVolume(0f,0f);
            }
        });

        getViewDataBinding().vvWelcomeVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                /*
                * what int: 标记的错误类型:
                *   MEDIA_ERROR_UNKNOWN  未知错误 值: 1 (0x00000001)
                *   MEDIA_ERROR_SERVER_DIED 媒体服务器挂掉了。此时，程序必须释放MediaPlayer 对象，并重新new 一个新的。值: 100 (0x00000064)
                * extra int: 标记的错误类型.
                *   MEDIA_ERROR_IO   文件不存在或错误，或网络不可访问错误 值: -1004 (0xfffffc14)
                *   MEDIA_ERROR_MALFORMED 流不符合有关标准或文件的编码规范 值: -1007 (0xfffffc11)
                *   MEDIA_ERROR_UNSUPPORTED 比特流符合相关编码标准或文件的规格，但媒体框架不支持此功能 值: -1010 (0xfffffc0e)
                *   MEDIA_ERROR_TIMED_OUT 一些操作使用了过长的时间，也就是超时了，通常是超过了3-5秒 值: -110 (0xffffff92)
                *   MEDIA_ERROR_SYSTEM (-2147483648) - low-level system error.
                * */
                Log.i(TAG, "what: " + what + " extra: " + extra);
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
