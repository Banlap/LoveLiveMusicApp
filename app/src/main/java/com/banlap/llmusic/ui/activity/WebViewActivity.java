package com.banlap.llmusic.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseActivity;
import com.banlap.llmusic.databinding.ActivityMessageBinding;
import com.banlap.llmusic.databinding.ActivityWebviewBinding;
import com.banlap.llmusic.databinding.ItemMessageListBinding;
import com.banlap.llmusic.uivm.vm.WebViewVM;

public class WebViewActivity extends BaseActivity<WebViewVM, ActivityWebviewBinding>
    implements WebViewVM.MessageCallBack {


    @Override
    protected int getLayoutId() { return R.layout.activity_webview; }

    @Override
    protected void initData(){ }

    @Override
    protected void initView() {
        getViewDataBinding().wvUrl.loadUrl("https://banlap-db.oss-cn-guangzhou.aliyuncs.com/music/lovelive/app/LLMusicPlayer_official_v1.3.1_2022-05-22_16-53.apk");
        //getViewDataBinding().wvUrl.loadUrl("https://www.baidu.com");
        getViewDataBinding().wvUrl.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        getViewDataBinding().wvUrl.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        getViewDataBinding().wvUrl.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        getViewDataBinding().wvUrl.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        getViewDataBinding().wvUrl.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        getViewDataBinding().wvUrl.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        //getViewDataBinding().wvUrl.getSettings().setAppCacheEnabled(true);//是否使用缓存
        getViewDataBinding().wvUrl.getSettings().setDomStorageEnabled(true);//DOM Storage

        getViewDataBinding().wvUrl.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url){
                try {
                    // 以下固定写法,表示跳转到第三方应用
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } catch (Exception e) {
                    // 防止没有安装的情况
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

    @Override
    public void viewBack() { finish(); }



}
