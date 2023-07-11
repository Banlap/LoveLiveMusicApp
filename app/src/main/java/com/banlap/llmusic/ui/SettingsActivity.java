package com.banlap.llmusic.ui;

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
import com.banlap.llmusic.uivm.LocalListFVM;
import com.banlap.llmusic.uivm.MainVM;
import com.banlap.llmusic.uivm.SettingsVM;
import com.banlap.llmusic.utils.BluetoothUtil;
import com.banlap.llmusic.utils.CacheUtil;
import com.banlap.llmusic.utils.FileUtil;
import com.banlap.llmusic.utils.SPUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

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

    @Override
    protected int getLayoutId() { return R.layout.activity_settings; }

    @Override
    protected void initData() {
        mNormalLaunchVideoUrl = "android.resource://" + getPackageName() + "/" + R.raw.welcomeliella;
        String strThemeId = SPUtil.getStrValue(getApplicationContext(), "SaveThemeId");
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
                                Log.e(TAG, "path: " + path);
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
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    int REQUEST_CODE_CONTACT = 101;
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    //验证是否许可权限
                    for (String str : permissions) {
                        if (checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                            //申请权限
                            requestPermissions(permissions, REQUEST_CODE_CONTACT);
                            return;
                        }
                    }
                }
                getViewModel().downloadUrl(event.str);
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
                    Log.e("LogByAB","file: " + event.file.toString());
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
                    SPUtil.setStrValue(getApplicationContext(), "LaunchVideoPath", event.str);
                    updateLaunchVideoSelectUI(false);
                }
                break;
            case ThreadEvent.VIEW_SETTING_LAUNCH_VIDEO_ERROR:
                Log.e(TAG, "设置启动动画失败");
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
        String isLaunchVideo = SPUtil.getStrValue(getApplicationContext(), "CloseLaunchVideo");
        if(TextUtils.isEmpty(isLaunchVideo)) {
            settingVideoBinding.switchVideo.setChecked(true);
        } else {
            settingVideoBinding.switchVideo.setChecked(!"0".equals(isLaunchVideo));
        }
        settingVideoBinding.switchVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtil.setStrValue(getApplicationContext(), "CloseLaunchVideo", settingVideoBinding.switchVideo.isChecked()? "1" : "0");
            }
        });

        //选择启动动画ui默认值
        updateLaunchVideoSelectUI(true);

        String launchVideoPath = SPUtil.getStrValue(getApplicationContext(), "LaunchVideoPath");
        if(!TextUtils.isEmpty(launchVideoPath)) {
            if(!mNormalLaunchVideoUrl.equals(launchVideoPath)) {
                updateLaunchVideoSelectUI(false);
            }
        }

        //选择默认启动动画
        settingVideoBinding.llSelectVideoNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtil.setStrValue(getApplicationContext(), "LaunchVideoPath", mNormalLaunchVideoUrl);
                updateLaunchVideoSelectUI(true);
            }
        });

        //选择自定义启动动画
        settingVideoBinding.llSelectVideoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("video/mp4");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intentActivityResultLauncher.launch(intent);
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

    private void showLoadingApp() {
        if(null != mAlertDialog) {
            mAlertDialog.dismiss();
        }

        downloadBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_download, null, false);

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
        SPUtil.setStrValue(getApplicationContext(), "SaveThemeId", String.valueOf(rId));
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
        for (int i=0; i<permissions.length; i++) {
            if(permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if(!"".equals(newVersionUrl)){
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP2, newVersionUrl));
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
