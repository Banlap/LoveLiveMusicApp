package com.banlap.llmusic.ui.activity;


import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseActivity;
import com.banlap.llmusic.databinding.ActivityCustomErrorBinding;
import com.banlap.llmusic.uivm.vm.CustomErrorVM;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;

public class CustomErrorActivity extends BaseActivity<CustomErrorVM, ActivityCustomErrorBinding> {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_custom_error;
    }

    @Override
    protected void initData() { }

    @Override
    protected void initView() {
        getViewDataBinding().errorDetails.setText(CustomActivityOnCrash.getStackTraceFromIntent(getIntent()));
        CaocConfig config = CustomActivityOnCrash.getConfigFromIntent(getIntent());
        if (config == null) {
            finish();
            return;
        }
        if (config.isShowRestartButton() && config.getRestartActivityClass() != null) {
            getViewDataBinding().restartButton.setOnClickListener(v -> CustomActivityOnCrash.restartApplication(CustomErrorActivity.this, config));
        } else {
            getViewDataBinding().restartButton.setOnClickListener(v -> CustomActivityOnCrash.closeApplication(CustomErrorActivity.this, config));
        }
    }

}