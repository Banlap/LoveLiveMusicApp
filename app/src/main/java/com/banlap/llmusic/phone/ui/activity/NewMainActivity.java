package com.banlap.llmusic.phone.ui.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.Observer;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseActivity;
import com.banlap.llmusic.databinding.ActivityNewMainBinding;
import com.banlap.llmusic.phone.uivm.vm.NewMainVM;
import com.banlap.llmusic.utils.LLAnimationUtil;

public class NewMainActivity extends BaseActivity<NewMainVM, ActivityNewMainBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_main;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        initMainView();
        initClickListener();
    }

    /**
     * 初始化主页内容
     * */
    private void initMainView() {
        getViewDataBinding().rlMusicDetailMain.post(new Runnable() {
            @Override
            public void run() {
                int downHeight = getViewDataBinding().rlMusicDetailMain.getHeight() - getViewDataBinding().vDisplay.getHeight() - getViewDataBinding().llMusicDetailTopView.getHeight();
                LLAnimationUtil.objectAnimatorUpOrDown(NewMainActivity.this, true, downHeight, getViewDataBinding().rlMusicDetailMain);
            }
        });

        getViewModel().getMlIsClickMusicController().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                int downHeight = getViewDataBinding().rlMusicDetailMain.getHeight() - getViewDataBinding().vDisplay.getHeight() - getViewDataBinding().llMusicDetailTopView.getHeight();
                LLAnimationUtil.objectAnimatorUpOrDown(NewMainActivity.this, !b, downHeight, getViewDataBinding().rlMusicDetailMain);
            }
        });
    }

    private void initClickListener() {
        getViewDataBinding().llMusicDetailTopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getViewModel().toggleMusicControllerSwitch();
            }
        });
    }


}
