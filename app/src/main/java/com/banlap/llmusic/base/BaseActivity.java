package com.banlap.llmusic.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.LayoutRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.banlap.llmusic.utils.LLActivityManager;
import com.banlap.llmusic.utils.NotificationHelper;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;

/**
 * @author Banlap on 2021/11/30
 */
public abstract class BaseActivity<VM extends ViewModel, VDB extends ViewDataBinding> extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();
    protected VM mViewModel;
    protected VDB mViewDataBinding;

    public VM getViewModel() { return mViewModel; }
    public VDB getViewDataBinding() { return mViewDataBinding; }

    protected void beforeOnCreate() {}
    protected void initWindow() {}
    @LayoutRes
    protected abstract int getLayoutId();

    public static long lastTime;
    public static final long TIME_500MS = 500;

    private ActivityResultLauncher<Intent> intentActivityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        beforeOnCreate();
        super.onCreate(savedInstanceState);
        initWindow();
        setContentView(getLayoutId());
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        mViewDataBinding.setLifecycleOwner(this);
        //将Activity实例添加到LLActivityManager的堆栈
        LLActivityManager.getInstance().addActivity(this);


        try {
            if(savedInstanceState!=null) {
                String data = savedInstanceState.getString("SIS");
                //Toast.makeText(this, "SIS: " + data, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "e: " + e.getMessage());
            //Toast.makeText(this, "SIS: " + null, Toast.LENGTH_SHORT).show();
        }

        init();
        initData();
        initView();
        setStatusBar();
    }

    @SuppressWarnings("unchecked")
    protected void init() {
        Class<VM> vmClass = (Class<VM>)((ParameterizedType)(Objects.requireNonNull(this.getClass().getGenericSuperclass()))).getActualTypeArguments()[0];
        mViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(vmClass);
    }

    /**
     * 设置沉浸式状态栏
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setStatusBar() {
        //5.0及以上
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN  //设置为全屏
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); //状态栏字体颜色设置为黑色这个是Android 6.0才出现的属性   默认是白色

        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);//设置为透明色
        window.setNavigationBarColor(Color.TRANSPARENT);
    }

    protected abstract void initData();
    protected abstract void initView();

    @Override
    protected void onResume() {
        super.onResume();
        NotificationHelper.getInstance().cancelNotification(this, NotificationHelper.LL_MUSIC_FULL_SCREEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LLActivityManager.getInstance().removeActivity(this);
    }

    /**
     * 判断是否双击
     * */
    public boolean isDoubleClick() {
        long currentTime = System.currentTimeMillis();
        if(Math.abs(currentTime - lastTime) < TIME_500MS) {
            lastTime = currentTime;
            Log.i(TAG, "当前已双击");
            return true;
        }
        lastTime = currentTime;
        Log.i(TAG, "当前未双击");
        return false;
    }

}
