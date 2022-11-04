package com.banlap.llmusic.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseActivity;
import com.banlap.llmusic.databinding.ActivityWelcomeBinding;
import com.banlap.llmusic.uivm.WelcomeVM;

public class WelcomeActivity extends BaseActivity<WelcomeVM, ActivityWelcomeBinding>
    implements WelcomeVM.WelcomeCallBack {

    @Override
    protected int getLayoutId() { return R.layout.activity_welcome; }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        getViewDataBinding().setVm(getViewModel());
        getViewModel().setCallBack(this);

        initVideo();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }

    /** 循环播放欢迎页视频 */
    private void initVideo() {
        getViewDataBinding().vvWelcomeVideo.setVideoURI(
                Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.welcomevideo));
        getViewDataBinding().vvWelcomeVideo.start();
        getViewDataBinding().vvWelcomeVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e("LogByAB", "onSuccess");
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
                Log.e("LogByAB", "what: " + what + " extra: " + extra);
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
