package com.banlap.llmusic.ui.activity;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseActivity;
import com.banlap.llmusic.databinding.ActivityLockFullScreenBinding;
import com.banlap.llmusic.uivm.vm.LockFullScreenVM;

public class LockFullScreenActivity extends BaseActivity<LockFullScreenVM, ActivityLockFullScreenBinding>
    implements LockFullScreenVM.LockFullScreenCallBack {

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if(keyguardManager!=null) {
                keyguardManager.requestDismissKeyguard(this, null);

            }
        }
       /* getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);*/

    }


    @Override
    protected void initView() {
        getViewDataBinding().setVm(getViewModel());
        getViewModel().setCallBack(this);


    }
}
