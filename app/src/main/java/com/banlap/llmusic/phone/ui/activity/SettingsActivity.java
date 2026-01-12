package com.banlap.llmusic.phone.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseActivity;
import com.banlap.llmusic.base.BaseApplication;
import com.banlap.llmusic.databinding.ActivitySettingsBinding;
import com.banlap.llmusic.databinding.DialogDefaultBinding;
import com.banlap.llmusic.databinding.DialogDownloadBinding;
import com.banlap.llmusic.databinding.DialogMessageBinding;
import com.banlap.llmusic.databinding.DialogPermissionBinding;
import com.banlap.llmusic.databinding.DialogSettingVideoBinding;
import com.banlap.llmusic.databinding.DialogSettingViewModeBinding;
import com.banlap.llmusic.receiver.DownloadReceiver;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.service.LyricService;
import com.banlap.llmusic.phone.ui.ThemeHelper;
import com.banlap.llmusic.phone.uivm.vm.SettingsVM;
import com.banlap.llmusic.sql.room.RoomSettings;
import com.banlap.llmusic.sql.AppData;
import com.banlap.llmusic.utils.AppExecutors;
import com.banlap.llmusic.utils.CacheUtil;
import com.banlap.llmusic.utils.FileUtil;
import com.banlap.llmusic.utils.LLActivityManager;
import com.banlap.llmusic.utils.PermissionUtil;
import com.banlap.llmusic.utils.SPUtil;
import com.banlap.llmusic.utils.SystemUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/**
 * 设置页面
 * */
public class SettingsActivity extends BaseActivity<SettingsVM, ActivitySettingsBinding>
    implements SettingsVM.SettingsCallBack {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    private AlertDialog mAlertDialog;                   //弹窗
    private DialogDownloadBinding downloadBinding;
    private String newVersionUrl = "";          //新版本路径
    private String newVersionTitle = "";        //新版本标题
    private String newVersionContent = "";      //新版本内容

    private DialogSettingVideoBinding settingVideoBinding;
    private ActivityResultLauncher<Intent> intentActivityResultLauncher;
    private ActivityResultLauncher<Intent> intentFloatingLyricResultLauncher;
    private String mNormalLaunchVideoUrl; //默认启动动画UI
    private String mNormalLaunchVideoUrl2; //启动动画UI 5nd

    private int rThemeId =0;
    private static final int REQUEST_CODE_DOWNLOAD_APP_2 = 201;
    private static final int REQUEST_CODE_SELECT_VIDEO_FILE_2 = 202;

    private boolean isRestartAfterClean = false; //清理缓存后重启app的标志

    @Override
    protected int getLayoutId() { return R.layout.activity_settings; }


    @Override
    protected void initWindow() {
        super.initWindow();
    }



    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        mNormalLaunchVideoUrl = "android.resource://" + getPackageName() + "/" + R.raw.welcomeliella;
        mNormalLaunchVideoUrl2 = "android.resource://" + getPackageName() + "/" + R.raw.welcomeliella5nd;
        //EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.THREAD_ROOM_GET_THEME_ID, SettingsActivity.class.getSimpleName()));
        if(AppData.roomSettings != null && !TextUtils.isEmpty(AppData.roomSettings.saveThemeId)) {
            try {
                changeTheme(Integer.parseInt(AppData.roomSettings.saveThemeId));
            } catch (Exception e) {
                Log.e(TAG, "saveThemeId转换失败,使用默认参数");
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {
        getViewDataBinding().setVm(getViewModel());
        getViewModel().setCallBack(this);
        boolean isExistNewVersion = getIntent().getBooleanExtra("IsExistNewVersion", false);
        newVersionUrl = getIntent().getStringExtra("NewVersionUrl");
        newVersionTitle = getIntent().getStringExtra("NewVersionTitle");
        newVersionContent = getIntent().getStringExtra("NewVersionContent");

        getViewDataBinding().llThemeNormal.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llThemeBlue.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llThemeDark.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llThemeLight.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llThemeWhite.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llThemeOrange.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llThemeRed.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llThemeStars.setOnClickListener(new ButtonClickListener());


        getViewDataBinding().llSettingWelcomeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingVideo();
            }
        });

        getViewDataBinding().llSettingViewMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showViewMode();
            }
        });

        getViewDataBinding().tvVersionValue.setText("V" + SystemUtil.getInstance().getAppVersionName(this));
        getViewDataBinding().tvNewVersion.setVisibility(isExistNewVersion ? View.VISIBLE : View.GONE);
        getViewDataBinding().llVersionMain.setEnabled(isExistNewVersion);
        getViewDataBinding().llVersionMain.setOnClickListener(new View.OnClickListener() { //版本
            @Override
            public void onClick(View view) {
                showUpgradeApp();
            }
        });


        String cacheAllSize = CacheUtil.getTotalCacheSize(this);
        getViewDataBinding().tvCacheValue.setText(cacheAllSize);
        getViewDataBinding().llCleanCache.setOnClickListener(new View.OnClickListener() { //清除缓存
            @Override
            public void onClick(View v) {
                cleanCache();
            }
        });

        getViewDataBinding().llErrorLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showErrorLog();
            }
        });

        getViewDataBinding().llPermission.setOnClickListener(new View.OnClickListener() { //关于
            @Override
            public void onClick(View v) {
                showPermission();
            }
        });

        getViewDataBinding().llAbout.setOnClickListener(new View.OnClickListener() { //关于
            @Override
            public void onClick(View v) {
                showAboutApp();
            }
        });

        intentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(null != result.getData()) {
                            Intent intent = result.getData();
                            Uri uri = intent.getData();
                            if(null != uri) {
                                String path = FileUtil.getInstance().getPath(getApplication(), uri);
                                Log.i(TAG, "path: " + path);
                                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_SETTING_LAUNCH_VIDEO_SUCCESS, path));
                                return;
                            }
                        }
                        EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_SETTING_LAUNCH_VIDEO_ERROR));
                    }
                });


        getViewDataBinding().llFloatingLyric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否已经授权显示悬浮窗
                if(SystemUtil.getInstance().isCanDrawOverlays(SettingsActivity.this)) {
                    if(SystemUtil.getInstance().isServiceWorked(SettingsActivity.this, LyricService.class.getPackage().getName()
                            + "." + LyricService.class.getSimpleName())) {
                        LyricService.updateLyricUI(false);
                        stopService(MainActivity.intentLyricService);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(MainActivity.intentLyricService);
                        } else {
                            startService(MainActivity.intentLyricService);
                        }
                        //
                        refreshFloatingLyric();
                    }
                } else {
                    DialogDefaultBinding defaultBinding = DataBindingUtil.inflate(LayoutInflater.from(SettingsActivity.this),
                            R.layout.dialog_default, null, false);

                    defaultBinding.dialogSelectTitle.setText("开启悬浮窗权限以展示悬浮歌词");

                    //取消
                    defaultBinding.btSelectIconCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAlertDialog.dismiss();
                        }
                    });

                    //授权开启
                    defaultBinding.btSelectIconCommit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            }
                            intentFloatingLyricResultLauncher.launch(intent);
                        }
                    });

                    mAlertDialog = new AlertDialog.Builder(SettingsActivity.this)
                            .setView(defaultBinding.getRoot())
                            .create();
                    Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_3);
                    mAlertDialog.show();
                    mAlertDialog.getWindow().setLayout((int)(SystemUtil.getInstance().getDM(SettingsActivity.this).widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            }
        });

        intentFloatingLyricResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        //此处是跳转的result回调方法
                        if(SystemUtil.getInstance().isCanDrawOverlays(getApplication())) {
                            if(null != mAlertDialog) {
                                mAlertDialog.dismiss();
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(MainActivity.intentLyricService);
                            } else {
                                startService(MainActivity.intentLyricService);
                            }
                            refreshFloatingLyric();
                        }
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void ThreadEvent(final ThreadEvent event) {
        switch (event.msgCode) {
            case ThreadEvent.THREAD_DOWNLOAD_APP_BY_SETTINGS:
                String[] permissions_download = { Manifest.permission.WRITE_EXTERNAL_STORAGE };
                //Android 13中 READ_EXTERNAL_STORAGE 已失效,则使用新的权限 READ_MEDIA_VIDEO
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissions_download = new String[] { Manifest.permission.READ_MEDIA_VIDEO };
                }

                if(PermissionUtil.getInstance().checkPermission(SettingsActivity.this, permissions_download)) {
                    ActivityCompat.requestPermissions(SettingsActivity.this, permissions_download, REQUEST_CODE_DOWNLOAD_APP_2);
                    return;
                }
                getViewModel().downloadUrl(event.str);
                break;
            case ThreadEvent.THREAD_SELECT_VIDEO_FILE:
                String[] permissions_video = { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE };
                //Android 13中 READ_EXTERNAL_STORAGE 已失效,则使用新的权限 READ_MEDIA_VIDEO
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissions_video = new String[] { Manifest.permission.READ_MEDIA_VIDEO };
                }

                if(PermissionUtil.getInstance().checkPermission(SettingsActivity.this, permissions_video)) {
                    ActivityCompat.requestPermissions(SettingsActivity.this, permissions_video, REQUEST_CODE_SELECT_VIDEO_FILE_2);
                    return;
                }

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("video/mp4");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intentActivityResultLauncher.launch(intent);
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEvent(ThreadEvent event) {
        switch (event.msgCode) {
            case ThreadEvent.VIEW_DOWNLOAD_APP_BY_SETTINGS_START:
                showLoadingApp();
                break;
            case ThreadEvent.VIEW_DOWNLOAD_APP_BY_SETTINGS_LOADING:
                if(null != downloadBinding) {
                    downloadBinding.tvValue.setText(""+event.i);
                    downloadBinding.pbProgress.setProgress(event.i);
                }
                break;
            case ThreadEvent.VIEW_DOWNLOAD_APP_BY_SETTINGS_SUCCESS:
                if(null != mAlertDialog) {
                    mAlertDialog.dismiss();
                }
                //是否完成下载app
                if(event.b) {
                    try {
                        Log.i(TAG,"file: " + event.file.toString());
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(),"com.banlap.llmusic.fileProvider", event.file);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");//设置类型
                        } else {
                            intent.setDataAndType(Uri.parse("file://"+event.file.toString()), "application/vnd.android.package-archive");//设置类型
                        }
                        startActivity(intent);
                    } catch (Exception e) { //修复部分版本（大于N）安装问题
                        Log.i(TAG,"安装出错: " + e.getMessage());
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Uri contentUri = Uri.fromFile(event.file);
                        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");//设置类型
                        startActivity(intent);
                    }
                } else {
                    event.file.delete();
                }
                break;
            case ThreadEvent.VIEW_DOWNLOAD_APP_BY_SETTINGS_ERROR:
                if(null != mAlertDialog) {
                    mAlertDialog.dismiss();
                }
                Toasty.error(this, "app下载失败，请重新下载", Toast.LENGTH_SHORT,true).show();
                break;
            case ThreadEvent.VIEW_SETTING_LAUNCH_VIDEO_SUCCESS:
                if(!TextUtils.isEmpty(event.str)) {
                    //SPUtil.setStrValue(getApplicationContext(), SPUtil.LaunchVideoPath, event.str);
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            AppData.saveRoomSettings(settings -> settings.launchVideoPath = event.str);
                        }
                    });
                    updateLaunchVideoSelectUI(false, "");
                }
                break;
            case ThreadEvent.VIEW_SETTING_LAUNCH_VIDEO_ERROR:
                Log.i(TAG, "设置启动动画失败");
                break;

            case ThreadEvent.VIEW_ROOM_GET_THEME_ID:
                if(event.str.equals(SettingsActivity.class.getSimpleName())) {
                    if(!TextUtils.isEmpty(event.str2)) {
                        try {
                            changeTheme(Integer.parseInt(event.str2));
                        } catch (Exception e) {
                            Log.e(TAG, "themeId转换失败,");
                        }
                    }
                }
                break;
        }
    }

    /** 设置启动动画 */
    private void showSettingVideo() {
        settingVideoBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_setting_video, null, false);

        settingVideoBinding.tvTitle.setText("设置启动动画");
        //设置是否开启启动动画
//        String isLaunchVideo = SPUtil.getStrValue(getApplicationContext(), SPUtil.CloseLaunchVideo);
        String isLaunchVideo = AppData.roomSettings.closeLaunchVideo;
        if(TextUtils.isEmpty(isLaunchVideo)) {
            settingVideoBinding.switchVideo.setChecked(true);
        } else {
            settingVideoBinding.switchVideo.setChecked(!"0".equals(isLaunchVideo));
        }
        settingVideoBinding.switchVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        AppData.saveRoomSettings(settings -> settings.closeLaunchVideo = settingVideoBinding.switchVideo.isChecked()? "1" : "0");
                    }
                });
//                SPUtil.setStrValue(getApplicationContext(), SPUtil.CloseLaunchVideo, settingVideoBinding.switchVideo.isChecked()? "1" : "0");
            }
        });

        //选择启动动画ui默认值
        updateLaunchVideoSelectUI(true, "1");

        //String launchVideoPath = SPUtil.getStrValue(getApplicationContext(), SPUtil.LaunchVideoPath);
        String launchVideoPath = AppData.roomSettings.launchVideoPath;
        if(!TextUtils.isEmpty(launchVideoPath)) {
            if(!mNormalLaunchVideoUrl.equals(launchVideoPath) && !mNormalLaunchVideoUrl2.equals(launchVideoPath)) {
                updateLaunchVideoSelectUI(false, "");
            }
            if(mNormalLaunchVideoUrl2.equals(launchVideoPath)) {
                updateLaunchVideoSelectUI(true, "2");
            }
        }

        //选择默认启动动画
        settingVideoBinding.llSelectVideoNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SPUtil.setStrValue(getApplicationContext(), SPUtil.LaunchVideoPath, mNormalLaunchVideoUrl);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        AppData.saveRoomSettings(settings -> settings.launchVideoPath = mNormalLaunchVideoUrl);
                    }
                });
                updateLaunchVideoSelectUI(true, "1");
            }
        });

        //选择默认启动动画2
        settingVideoBinding.llSelectVideoNormal2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SPUtil.setStrValue(getApplicationContext(), SPUtil.LaunchVideoPath, mNormalLaunchVideoUrl2);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        AppData.saveRoomSettings(settings -> settings.launchVideoPath = mNormalLaunchVideoUrl2);
                    }
                });
                updateLaunchVideoSelectUI(true, "2");
            }
        });

        //选择自定义启动动画
        settingVideoBinding.llSelectVideoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.THREAD_SELECT_VIDEO_FILE));
            }
        });

        settingVideoBinding.btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        mAlertDialog = new AlertDialog.Builder(this)
                .setView(settingVideoBinding.getRoot())
                .create();
        Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_3);
        mAlertDialog.show();
    }

    /**
     *  选择启动动画ui默认值
     *  @param isNormal true: 默认启动视频UI false: 自定义启动视频UI
     * */
    private void updateLaunchVideoSelectUI(boolean isNormal, String type) {
        if(settingVideoBinding != null) {
            if(isNormal) {
                settingVideoBinding.ivLaunchVideoNormalSelected.setVisibility(type.equals("1") ? View.VISIBLE : View.INVISIBLE);
                settingVideoBinding.ivLaunchVideoNormal2Selected.setVisibility(type.equals("1") ? View.INVISIBLE : View.VISIBLE);
                settingVideoBinding.ivLaunchVideoCustomSelected.setVisibility(View.INVISIBLE);
                settingVideoBinding.civLaunchVideoAdd.setVisibility(View.VISIBLE);
                settingVideoBinding.civLaunchVideoCustom.setVisibility(View.GONE);
                settingVideoBinding.tvLaunchVideoCustom.setText("");
            } else {
                settingVideoBinding.ivLaunchVideoNormalSelected.setVisibility(View.INVISIBLE);
                settingVideoBinding.ivLaunchVideoNormal2Selected.setVisibility(View.INVISIBLE);
                settingVideoBinding.ivLaunchVideoCustomSelected.setVisibility(View.VISIBLE);
                settingVideoBinding.civLaunchVideoAdd.setVisibility(View.GONE);
                settingVideoBinding.civLaunchVideoCustom.setVisibility(View.VISIBLE);
                settingVideoBinding.tvLaunchVideoCustom.setText("已设定视频");
            }
        }
    }


    /**设置界面模式 */
    private void showViewMode() {
        DialogSettingViewModeBinding viewModeBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_setting_view_mode, null, false);
        viewModeBinding.tvTitle.setText("设置界面模式");

        //String controllerScene = SPUtil.getStrValue(SettingsActivity.this, SPUtil.SaveControllerScene);
        String controllerScene = "";
        if(AppData.roomSettings != null && !TextUtils.isEmpty(AppData.roomSettings.saveControllerScene)) {
            controllerScene = AppData.roomSettings.saveControllerScene;
        }
        viewModeBinding.ivViewModeDefaultSelected.setVisibility(View.VISIBLE);
        viewModeBinding.ivViewModeFloatingSelected.setVisibility(View.INVISIBLE);
        viewModeBinding.ivViewModeSimpleSelected.setVisibility(View.INVISIBLE);

        if(!TextUtils.isEmpty(controllerScene)) {
            if(controllerScene.equals(SPUtil.SaveControllerSceneValue_DefaultScene)) {
                viewModeBinding.ivViewModeDefaultSelected.setVisibility(View.INVISIBLE);
                viewModeBinding.ivViewModeFloatingSelected.setVisibility(View.INVISIBLE);
                viewModeBinding.ivViewModeSimpleSelected.setVisibility(View.VISIBLE);
            } else if(controllerScene.equals(SPUtil.SaveControllerSceneValue_FloatingScene)) {
                viewModeBinding.ivViewModeDefaultSelected.setVisibility(View.INVISIBLE);
                viewModeBinding.ivViewModeFloatingSelected.setVisibility(View.VISIBLE);
                viewModeBinding.ivViewModeSimpleSelected.setVisibility(View.INVISIBLE);
            }
        }

        viewModeBinding.switchSetBg.setChecked(false);

//        String isBGScene = SPUtil.getStrValue(SettingsActivity.this, SPUtil.isBGScene);
        if(AppData.roomSettings != null) {
            if(AppData.roomSettings.isBGScene.equals("1")) {
                viewModeBinding.switchSetBg.setChecked(true);
            }
        }

        viewModeBinding.btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        viewModeBinding.llViewModeDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModeBinding.ivViewModeDefaultSelected.setVisibility(View.VISIBLE);
                viewModeBinding.ivViewModeFloatingSelected.setVisibility(View.INVISIBLE);
                viewModeBinding.ivViewModeSimpleSelected.setVisibility(View.INVISIBLE);
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_NEW_CONTROLLER_MODE));

            }
        });

        viewModeBinding.llViewModeFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModeBinding.ivViewModeDefaultSelected.setVisibility(View.INVISIBLE);
                viewModeBinding.ivViewModeFloatingSelected.setVisibility(View.VISIBLE);
                viewModeBinding.ivViewModeSimpleSelected.setVisibility(View.INVISIBLE);
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_CONTROLLER_MODE_FLOATING));
            }
        });

        viewModeBinding.llViewModeSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModeBinding.ivViewModeDefaultSelected.setVisibility(View.INVISIBLE);
                viewModeBinding.ivViewModeFloatingSelected.setVisibility(View.INVISIBLE);
                viewModeBinding.ivViewModeSimpleSelected.setVisibility(View.VISIBLE);
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_CONTROLLER_MODE));
            }
        });

        viewModeBinding.tvSetBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_INTO_SET_BG));
            }
        });
        viewModeBinding.switchSetBg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_BG_MODE));
//                SPUtil.setStrValue(getApplicationContext(), SPUtil.isBGScene, viewModeBinding.switchSetBg.isChecked()? "1" : "0");
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        AppData.saveRoomSettings(settings -> settings.isBGScene = viewModeBinding.switchSetBg.isChecked()? "1" : "0");
                    }
                });
            }
        });


        mAlertDialog = new AlertDialog.Builder(this)
                .setView(viewModeBinding.getRoot())
                .create();
        Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_3);
        mAlertDialog.show();


    }

    /** 显示弹窗更新App */
    private void showUpgradeApp() {
        DialogMessageBinding messageBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_message, null, false);

        messageBinding.tvTitle.setText(newVersionTitle);
        messageBinding.tvContent.setText(newVersionContent);
        messageBinding.btSelectIconCancel.setText("以后再说");
        messageBinding.btSelectIconCancel.setBackgroundResource(R.drawable.selector_button_selected4);
        messageBinding.btSelectIconCommit.setText("立即体验");
        messageBinding.btSelectIconCommit.setTextColor(getResources().getColor(R.color.white));
        ThemeHelper.getInstance().settingActivityUpgradeAppButton(LLActivityManager.getInstance().getTopActivity(), rThemeId, messageBinding.btSelectIconCommit);
        messageBinding.btSelectIconCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        messageBinding.btSelectIconCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!"".equals(newVersionUrl)) {
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.THREAD_DOWNLOAD_APP_BY_SETTINGS, newVersionUrl));
                }
            }
        });

        mAlertDialog = new AlertDialog.Builder(this)
                .setView(messageBinding.getRoot())
                .create();
        Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_3);
        mAlertDialog.show();
    }

    /** 清理缓存 */
    private void cleanCache() {
        DialogDefaultBinding defaultBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_default, null, false);

        defaultBinding.dialogSelectTitle.setText("是否清理缓存？");
        //取消
        defaultBinding.btSelectIconCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        //清理缓存
        defaultBinding.btSelectIconCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                String cache = CacheUtil.clearAllCacheAfter(v.getContext());
                getViewDataBinding().tvCacheValue.setText(cache);
                //清除当前播放列表所有歌曲
                //SPUtil.setListValue(getApplicationContext(), SPUtil.PlayListData, new ArrayList<>());
                //标记
                isRestartAfterClean = true;

                DialogDefaultBinding defaultBinding2 = DataBindingUtil.inflate(LayoutInflater.from(v.getContext()),
                        R.layout.dialog_default, null, false);
                defaultBinding2.dialogSelectTitle.setText("清理成功，请重新启动App");
                defaultBinding2.btSelectIconCancel.setVisibility(View.GONE);
                defaultBinding2.btSelectIconCommit.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();
                        System.exit(0);
                    }
                });

                mAlertDialog = new AlertDialog.Builder(v.getContext())
                        .setView(defaultBinding2.getRoot())
                        .setCancelable(false)
                        .create();
                mAlertDialog.setCanceledOnTouchOutside(false);
                Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_3);
                mAlertDialog.show();
                mAlertDialog.getWindow().setLayout((int)(SystemUtil.getInstance().getDM(SettingsActivity.this).widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);

            }
        });

        mAlertDialog = new AlertDialog.Builder(this)
                .setView(defaultBinding.getRoot())
                .create();
        Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_3);
        mAlertDialog.show();
        mAlertDialog.getWindow().setLayout((int)(SystemUtil.getInstance().getDM(this).widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    /**
     * 展示错误日志内容
     * */
    private void showErrorLog() {
        try {
            String path="";
            Uri logFileUri;
            //Android 10以上使用 getExternalFilesDir(null)获取路径
            path = LLActivityManager.getInstance().getTopActivity().getExternalFilesDir(null).getAbsolutePath() + "/LLMusicLog/crash.log";


            File file = new File(path);
            if(!file.exists()) {
                Toast.makeText(this, "当前没有错误日志", Toast.LENGTH_SHORT).show();
                return;
            }


            logFileUri= FileProvider.getUriForFile(this, "com.banlap.llmusic.fileProvider", file);


            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//设置标记
            intent.setAction(Intent.ACTION_VIEW);//动作，查看
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // 授予 URI 读取权限
            intent.setDataAndType(logFileUri, "text/plain");//设置类型
            this.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "e: " + e.getMessage());
        }

    }

    /** 关于 */
    private void showAboutApp() {
        DialogMessageBinding messageBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_message, null, false);

        messageBinding.tvTitle.setText("关于App");
        messageBinding.tvContent.setTextIsSelectable(true);
        messageBinding.tvContent.setText("设计者：banlap\nB站up：圆圈AB\nhttps://space.bilibili.com/18177261\nqq交流群：748504621");
        messageBinding.btSelectIconCancel.setVisibility(View.GONE);
        messageBinding.btSelectIconCommit.setText("确认");
        messageBinding.btSelectIconCommit.setTextColor(getResources().getColor(R.color.white));
        messageBinding.btSelectIconCommit.setBackgroundResource(R.drawable.selector_button_selected_default);
        messageBinding.btSelectIconCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        mAlertDialog = new AlertDialog.Builder(this)
                .setView(messageBinding.getRoot())
                .create();
        Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_3);
        mAlertDialog.show();

    }

    private void showPermission() {
        DialogPermissionBinding permissionBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_permission, null, false);

        String[] permissionStorage = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        permissionBinding.tvIsReadWrite.setText(PermissionUtil.getInstance().checkPermission(this, permissionStorage)? "未授权": "已授权");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String[] permissionAudio = {
                    Manifest.permission.READ_MEDIA_AUDIO
            };

            String[] permissionPicture = {
                    Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO
            };

            String[] permissionNotifications = {
                    Manifest.permission.POST_NOTIFICATIONS,
            };

            permissionBinding.tvIsMusicAudio.setText(PermissionUtil.getInstance().checkPermission(this, permissionAudio)? "未授权": "已授权");
            permissionBinding.tvIsPictureVideo.setText(PermissionUtil.getInstance().checkPermission(this, permissionPicture)? "未授权": "已授权");
            permissionBinding.tvIsNotifications.setText(PermissionUtil.getInstance().checkPermission(this, permissionNotifications)? "未授权": "已授权");
        }


        permissionBinding.btSelectIconCommit.setText("确认");
        permissionBinding.btSelectIconCommit.setTextColor(getResources().getColor(R.color.white));
        permissionBinding.btSelectIconCommit.setBackgroundResource(R.drawable.selector_button_selected_default);

        permissionBinding.btIntoPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtil.getInstance().intoSystemPermission(SettingsActivity.this);
            }
        });

        permissionBinding.btSelectIconCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        mAlertDialog = new AlertDialog.Builder(this)
                .setView(permissionBinding.getRoot())
                .create();
        Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_3);
        mAlertDialog.show();
    }

    /** 展示下载进度 */
    private void showLoadingApp() {
        if(null != mAlertDialog) {
            mAlertDialog.dismiss();
        }

        downloadBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_download, null, false);

        downloadBinding.pbProgress.setMax(100);

        ThemeHelper.getInstance().settingActivityProgressTheme(LLActivityManager.getInstance().getTopActivity(), rThemeId, downloadBinding.pbProgress);

        downloadBinding.btSelectIconCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getViewModel().changeDownloadApp(true);
            }
        });

        mAlertDialog = new AlertDialog.Builder(this)
                .setView(downloadBinding.getRoot())
                .create();

        mAlertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        mAlertDialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_3);
        mAlertDialog.show();

    }

    /** 刷新悬浮歌词内容 */
    private void refreshFloatingLyric() {
        new Handler().postDelayed(() -> runOnUiThread(() -> {
            //刷新悬浮歌词列表和ui
            if(MainActivity.binder != null) {
                if(MainActivity.musicLyricList != null && MainActivity.musicLyricList.size()>0) {
                    LyricService.setMusicLyrics(MainActivity.musicLyricList);
                }
                if(MainActivity.binder.isPlay()) {
                    LyricService.updateLyricUI(true);

                }
            }
        }), 500);
    }

    public class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.ll_theme_normal) {
                changeTheme(R.id.ll_theme_normal);
            } else if(v.getId() == R.id.ll_theme_blue) {
                changeTheme(R.id.ll_theme_blue);
            } else if(v.getId() == R.id.ll_theme_dark) {
                changeTheme(R.id.ll_theme_dark);
            } else if(v.getId() == R.id.ll_theme_white) {
                changeTheme(R.id.ll_theme_white);
            } else if(v.getId() == R.id.ll_theme_orange) {
                changeTheme(R.id.ll_theme_orange);
            } else if(v.getId() == R.id.ll_theme_light) {
                changeTheme(R.id.ll_theme_light);
            } else if(v.getId() == R.id.ll_theme_red) {
                changeTheme(R.id.ll_theme_red);
            } else if(v.getId() == R.id.ll_theme_stars) {
                changeTheme(R.id.ll_theme_stars);
            }
        }
    }



    /** 改变主题 */
    private void changeTheme(int rId) {
        rThemeId = rId;
        //SPUtil.setStrValue(getApplicationContext(), SPUtil.SaveThemeId, String.valueOf(rId));
        //EventBus.getDefault().post(new ThreadEvent(ThreadEvent.THREAD_ROOM_SAVE_THEME_ID, String.valueOf(rId)));
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                AppData.saveRoomSettings(roomSetting -> roomSetting.saveThemeId = String.valueOf(rThemeId));
            }
        });
        //主题变更
        ThemeHelper.getInstance().settingActivityTheme(this, rId, getViewDataBinding());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        DownloadReceiver.stopHandler();
    }

    @Override
    public void viewBack() { finish(); }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode) {
            case REQUEST_CODE_DOWNLOAD_APP_2:
                if(!PermissionUtil.getInstance().checkPermission(this, permissions)) {
                    if(!"".equals(newVersionUrl)){
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.THREAD_DOWNLOAD_APP_BY_SETTINGS, newVersionUrl));
                    }
                }
                break;
            case REQUEST_CODE_SELECT_VIDEO_FILE_2:
                if(!PermissionUtil.getInstance().checkPermission(this, permissions)) {
                    EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.THREAD_SELECT_VIDEO_FILE));
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /** 屏蔽返回键 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //物理返回键
            if(isRestartAfterClean) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, keyEvent);
    }
}
