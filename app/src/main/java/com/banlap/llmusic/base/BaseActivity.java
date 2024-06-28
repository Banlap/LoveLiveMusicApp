package com.banlap.llmusic.base;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.LayoutRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.adapters.ViewBindingAdapter;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.banlap.llmusic.R;
import com.banlap.llmusic.pad.ui.activity.PadMainActivity;
import com.banlap.llmusic.service.MusicPlayService;
import com.banlap.llmusic.ui.activity.LockFullScreenActivity;
import com.banlap.llmusic.utils.LLActivityManager;
import com.banlap.llmusic.utils.NotificationHelper;
import com.banlap.llmusic.widget.LLMusicAlphaWidgetProvider;
import com.banlap.llmusic.widget.LLMusicWidgetProvider;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
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




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AppWidgetManager appWidgetManager = getSystemService(AppWidgetManager.class);
            if (appWidgetManager.isRequestPinAppWidgetSupported()) {
                // 系统支持添加小部件的操作
                // 进行添加小部件操作的处理
//                new Handler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
//
//                        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com/"));
//
//                        AppWidgetHost host = new AppWidgetHost(BaseActivity.this, 1);
//                        host.startListening();
//                        int nextId = host.allocateAppWidgetId();
//
//
//                        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
//                        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, nextId);
//
//                                // 获取ShortcutManager服务
//                        ShortcutInfo shortcut = new ShortcutInfo.Builder(BaseActivity.this, "unique_shortcut_id")
//                                .setShortLabel(getString(R.string.shortcuts_title)) // 快捷方式的简短标签
//                                .setLongLabel(getString(R.string.shortcuts_title_long)) // 快捷方式的完整标签
//                                .setIcon(Icon.createWithResource(BaseActivity.this, R.mipmap.ic_llmp_small_1)) // 快捷方式的图标
//                                .setIntent(pickIntent) // 点击快捷方式时启动的Activity
//                                .build();
//                        shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut));
//                    }
//                });

            }

        }

        try {
            if(savedInstanceState!=null) {
                String data = savedInstanceState.getString("SIS");
                Toast.makeText(this, "SIS: " + data, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "e: " + e.getMessage());
            Toast.makeText(this, "SIS: " + null, Toast.LENGTH_SHORT).show();
        }

        init();
        initData();
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBar();
        }
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
            return true;
        }
        lastTime = currentTime;
        return false;
    }

}
