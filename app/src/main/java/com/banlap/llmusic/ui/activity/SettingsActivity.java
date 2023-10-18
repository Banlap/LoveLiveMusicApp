package com.banlap.llmusic.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
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
import com.banlap.llmusic.databinding.ActivitySettingsBinding;
import com.banlap.llmusic.databinding.DialogDeleteListAllBinding;
import com.banlap.llmusic.databinding.DialogDownloadBinding;
import com.banlap.llmusic.databinding.DialogMessageBinding;
import com.banlap.llmusic.databinding.DialogSettingVideoBinding;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.ui.ThemeHelper;
import com.banlap.llmusic.uivm.vm.SettingsVM;
import com.banlap.llmusic.utils.CacheUtil;
import com.banlap.llmusic.utils.FileUtil;
import com.banlap.llmusic.utils.LLActivityManager;
import com.banlap.llmusic.utils.PermissionUtil;
import com.banlap.llmusic.utils.SPUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Objects;

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
    private String mNormalLaunchVideoUrl; //默认启动动画UI

    private static final int REQUEST_CODE_DOWNLOAD_APP_2 = 201;
    private static final int REQUEST_CODE_SELECT_VIDEO_FILE_2 = 202;

    @Override
    protected int getLayoutId() { return R.layout.activity_settings; }


    @Override
    protected void initWindow() {
        super.initWindow();
    }

    @Override
    protected void initData() {
        mNormalLaunchVideoUrl = "android.resource://" + getPackageName() + "/" + R.raw.welcomeliella;
        String strThemeId = SPUtil.getStrValue(getApplicationContext(), SPUtil.SaveThemeId);
        if(strThemeId!=null) {
            if(!strThemeId.equals("")) {
                int rId = Integer.parseInt(strThemeId);
                changeTheme(rId);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
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

        getViewDataBinding().llSettingWelcomeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingVideo();
            }
        });

        getViewDataBinding().tvVersionValue.setText("V" + getAppVersionName(this));
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
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void ThreadEvent(final ThreadEvent event) {
        switch (event.msgCode) {
            case ThreadEvent.DOWNLOAD_APP2:
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
            case ThreadEvent.SELECT_VIDEO_FILE:
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
            case ThreadEvent.DOWNLOAD_APP_START2:
                showLoadingApp();
                break;
            case ThreadEvent.DOWNLOAD_APP_LOADING2:
                if(null != downloadBinding) {
                    downloadBinding.tvValue.setText(""+event.i);
                    downloadBinding.hpvProgress.setCurrentCount(event.i);
                }
                break;
            case ThreadEvent.DOWNLOAD_APP_SUCCESS2:
                if(null != mAlertDialog) {
                    mAlertDialog.dismiss();
                }
                //是否完成下载app
                if(event.b) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.i("ABMusicPlayer","file: " + event.file.toString());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri contentUri = FileProvider.getUriForFile(getApplicationContext(),"com.banlap.llmusic.fileProvider", event.file);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");//设置类型
                    } else {
                        intent.setDataAndType(Uri.parse("file://"+event.file.toString()), "application/vnd.android.package-archive");//设置类型
                    }
                    startActivity(intent);
                } else {
                    event.file.delete();
                }
                break;
            case ThreadEvent.DOWNLOAD_APP_ERROR2:
                if(null != mAlertDialog) {
                    mAlertDialog.dismiss();
                }
                Toast.makeText(this, "app下载失败，请重新下载", Toast.LENGTH_SHORT).show();
                break;
            case ThreadEvent.VIEW_SETTING_LAUNCH_VIDEO_SUCCESS:
                if(!TextUtils.isEmpty(event.str)) {
                    SPUtil.setStrValue(getApplicationContext(), SPUtil.LaunchVideoPath, event.str);
                    updateLaunchVideoSelectUI(false);
                }
                break;
            case ThreadEvent.VIEW_SETTING_LAUNCH_VIDEO_ERROR:
                Log.i(TAG, "设置启动动画失败");
                break;
        }
    }

    /** 获取版本名称 */
    public static String getAppVersionName(Context context) {
        String versionName=null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /** 设置启动动画 */
    private void showSettingVideo() {
        settingVideoBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_setting_video, null, false);

        settingVideoBinding.tvTitle.setText("设置启动动画");
        //设置是否开启启动动画
        String isLaunchVideo = SPUtil.getStrValue(getApplicationContext(), SPUtil.CloseLaunchVideo);
        if(TextUtils.isEmpty(isLaunchVideo)) {
            settingVideoBinding.switchVideo.setChecked(true);
        } else {
            settingVideoBinding.switchVideo.setChecked(!"0".equals(isLaunchVideo));
        }
        settingVideoBinding.switchVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtil.setStrValue(getApplicationContext(), SPUtil.CloseLaunchVideo, settingVideoBinding.switchVideo.isChecked()? "1" : "0");
            }
        });

        //选择启动动画ui默认值
        updateLaunchVideoSelectUI(true);

        String launchVideoPath = SPUtil.getStrValue(getApplicationContext(), SPUtil.LaunchVideoPath);
        if(!TextUtils.isEmpty(launchVideoPath)) {
            if(!mNormalLaunchVideoUrl.equals(launchVideoPath)) {
                updateLaunchVideoSelectUI(false);
            }
        }

        //选择默认启动动画
        settingVideoBinding.llSelectVideoNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtil.setStrValue(getApplicationContext(), SPUtil.LaunchVideoPath, mNormalLaunchVideoUrl);
                updateLaunchVideoSelectUI(true);
            }
        });

        //选择自定义启动动画
        settingVideoBinding.llSelectVideoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.SELECT_VIDEO_FILE));
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
        Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_2);
        mAlertDialog.show();
    }

    /**
     *  选择启动动画ui默认值
     *  @param isNormal true: 默认启动视频UI false: 自定义启动视频UI
     * */
    private void updateLaunchVideoSelectUI(boolean isNormal) {
        if(settingVideoBinding != null) {
            settingVideoBinding.ivLaunchVideoNormalSelected.setVisibility(isNormal ? View.VISIBLE : View.INVISIBLE);
            settingVideoBinding.ivLaunchVideoCustomSelected.setVisibility(isNormal ? View.INVISIBLE : View.VISIBLE);
            settingVideoBinding.civLaunchVideoAdd.setVisibility(isNormal ? View.VISIBLE : View.GONE);
            settingVideoBinding.civLaunchVideoCustom.setVisibility(isNormal ? View.GONE : View.VISIBLE);
            settingVideoBinding.tvLaunchVideoCustom.setText(isNormal ? "" : "已设定视频");
        }
    }

    /** 显示弹窗更新App */
    private void showUpgradeApp() {
        DialogMessageBinding messageBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_message, null, false);

        messageBinding.tvTitle.setText(newVersionTitle);
        messageBinding.tvContent.setText(newVersionContent);
        messageBinding.btSelectIconCancel.setText("以后再说");
        messageBinding.btSelectIconCommit.setText("立即体验");
        messageBinding.btSelectIconCommit.setTextColor(getResources().getColor(R.color.white));
        messageBinding.btSelectIconCommit.setBackgroundResource(R.drawable.selector_button_selected3);

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
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP2, newVersionUrl));
                }
            }
        });

        mAlertDialog = new AlertDialog.Builder(this)
                .setView(messageBinding.getRoot())
                .create();
        Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_2);
        mAlertDialog.show();
    }

    /** 清理缓存 */
    private void cleanCache() {
        DialogDeleteListAllBinding deleteListAllBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_delete_list_all, null, false);

        deleteListAllBinding.dialogSelectTitle.setText("是否清理缓存？");
        //取消
        deleteListAllBinding.btSelectIconCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        //清理缓存
        deleteListAllBinding.btSelectIconCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                String cache = CacheUtil.clearAllCacheAfter(v.getContext());
                getViewDataBinding().tvCacheValue.setText(cache);
            }
        });

        mAlertDialog = new AlertDialog.Builder(this)
                .setView(deleteListAllBinding.getRoot())
                .create();
        Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_2);
        mAlertDialog.show();
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
        messageBinding.btSelectIconCommit.setBackgroundResource(R.drawable.selector_button_selected3);
        messageBinding.btSelectIconCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        mAlertDialog = new AlertDialog.Builder(this)
                .setView(messageBinding.getRoot())
                .create();
        Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_2);
        mAlertDialog.show();

    }

    /** 展示下载进度 */
    private void showLoadingApp() {
        if(null != mAlertDialog) {
            mAlertDialog.dismiss();
        }

        downloadBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_download, null, false);

        downloadBinding.hpvProgress.setMaxCount(100);
        String strThemeId = SPUtil.getStrValue(getApplicationContext(), SPUtil.SaveThemeId);
        if(strThemeId!=null) {
            if(!strThemeId.equals("")) {
                int rId = Integer.parseInt(strThemeId);
                ThemeHelper.getInstance().settingActivityProgressTheme(LLActivityManager.getInstance().getTopActivity(), rId, downloadBinding.hpvProgress);
            }
        }

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
        Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_2);
        mAlertDialog.show();

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
            }
        }
    }



    /** 改变主题 */
    private void changeTheme(int rId) {
        SPUtil.setStrValue(getApplicationContext(), SPUtil.SaveThemeId, String.valueOf(rId));
        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_CHANGE_THEME));
        //主题变更
        ThemeHelper.getInstance().settingActivityTheme(this, rId, getViewDataBinding());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void viewBack() { finish(); }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode) {
            case REQUEST_CODE_DOWNLOAD_APP_2:
                if(!PermissionUtil.getInstance().checkPermission(this, permissions)) {
                    if(!"".equals(newVersionUrl)){
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP2, newVersionUrl));
                    }
                }
                break;
            case REQUEST_CODE_SELECT_VIDEO_FILE_2:
                if(!PermissionUtil.getInstance().checkPermission(this, permissions)) {
                    EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.SELECT_VIDEO_FILE));
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
