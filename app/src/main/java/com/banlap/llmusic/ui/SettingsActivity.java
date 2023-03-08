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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseActivity;
import com.banlap.llmusic.databinding.ActivitySettingsBinding;
import com.banlap.llmusic.databinding.DialogDeleteListAllBinding;
import com.banlap.llmusic.databinding.DialogDownloadBinding;
import com.banlap.llmusic.databinding.DialogMessageBinding;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.uivm.MainVM;
import com.banlap.llmusic.uivm.SettingsVM;
import com.banlap.llmusic.utils.BluetoothUtil;
import com.banlap.llmusic.utils.CacheUtil;
import com.banlap.llmusic.utils.SPUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

public class SettingsActivity extends BaseActivity<SettingsVM, ActivitySettingsBinding>
    implements SettingsVM.SettingsCallBack {

    private AlertDialog mAlertDialog;                   //弹窗
    private DialogDownloadBinding downloadBinding;
    private boolean isExistNewVersion = false;
    private String newVersionUrl = "";
    private String newVersionTitle = "";
    private String newVersionContent = "";

    @Override
    protected int getLayoutId() { return R.layout.activity_settings; }

    @Override
    protected void initData() {
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

        isExistNewVersion = getIntent().getBooleanExtra("IsExistNewVersion", false);
        newVersionUrl = getIntent().getStringExtra("NewVersionUrl");
        newVersionTitle = getIntent().getStringExtra("NewVersionTitle");
        newVersionContent = getIntent().getStringExtra("NewVersionContent");

        getViewDataBinding().llThemeNormal.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llThemeDark.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llThemeLight.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llThemeWhite.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llThemeOrange.setOnClickListener(new ButtonClickListener());

        getViewDataBinding().tvVersionValue.setText("V" + getAppVersionName(this));
        getViewDataBinding().tvNewVersion.setVisibility(isExistNewVersion? View.VISIBLE : View.GONE);
        getViewDataBinding().llVersionMain.setEnabled(isExistNewVersion);
        getViewDataBinding().llVersionMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpgradeApp();
            }
        });


        String cacheAllSize = CacheUtil.getTotalCacheSize(this);
        getViewDataBinding().tvCacheValue.setText(cacheAllSize);
        getViewDataBinding().llCleanCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanCache();
            }
        });

        getViewDataBinding().llAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAboutApp();
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
        if(rId == R.id.ll_theme_normal) {
            getViewDataBinding().clBg.setBackgroundResource(R.mipmap.ic_gradient_color5);
            getViewDataBinding().ivBack.setBackgroundResource(R.drawable.ic_arrow_back_light);
            getViewDataBinding().ivArrowInto.setBackgroundResource(R.drawable.ic_arrow_into_light);
            getViewDataBinding().tvSettings.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvTheme.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvThemeNormal.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvThemeDark.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvThemeWhite.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvThemeOrange.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvThemeLight.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().rlSettingsBar.setBackgroundResource(R.drawable.shape_button_white_alpha);
            getViewDataBinding().llThemeMain.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llThemeNormal.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llThemeWhite.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llVersionMain.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llCleanCache.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llAbout.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().tvVersion.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvVersionValue.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvCleanCache.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvCacheValue.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvAbout.setTextColor(getResources().getColor(R.color.light_ff));

        } else if(rId == R.id.ll_theme_dark) {
            getViewDataBinding().clBg.setBackgroundResource(R.mipmap.ic_gradient_color6);
            getViewDataBinding().ivBack.setBackgroundResource(R.drawable.ic_arrow_back);
            getViewDataBinding().ivArrowInto.setBackgroundResource(R.drawable.ic_arrow_into);
            getViewDataBinding().tvSettings.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvTheme.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvThemeNormal.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvThemeDark.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvThemeWhite.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvThemeOrange.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvThemeLight.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().rlSettingsBar.setBackgroundResource(R.drawable.shape_button_white_alpha);
            getViewDataBinding().llThemeMain.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llThemeNormal.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llThemeWhite.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llVersionMain.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llCleanCache.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llAbout.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().tvVersion.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvVersionValue.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvCleanCache.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvCacheValue.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvAbout.setTextColor(getResources().getColor(R.color.white));
        } else if(rId == R.id.ll_theme_white) {
            getViewDataBinding().clBg.setBackgroundResource(R.color.background_color_F2);
            getViewDataBinding().ivBack.setBackgroundResource(R.drawable.ic_arrow_back_purple);
            getViewDataBinding().ivArrowInto.setBackgroundResource(R.drawable.ic_arrow_into_purple);
            getViewDataBinding().tvTheme.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvSettings.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvThemeNormal.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvThemeDark.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvThemeWhite.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvThemeOrange.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvThemeLight.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().rlSettingsBar.setBackgroundResource(R.drawable.shape_button_black_alpha_2);
            getViewDataBinding().llThemeMain.setBackgroundResource(R.drawable.shape_button_black_alpha);
            getViewDataBinding().llThemeNormal.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llThemeWhite.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llVersionMain.setBackgroundResource(R.drawable.shape_button_black_alpha);
            getViewDataBinding().llCleanCache.setBackgroundResource(R.drawable.shape_button_black_alpha);
            getViewDataBinding().llAbout.setBackgroundResource(R.drawable.shape_button_black_alpha);
            getViewDataBinding().tvVersion.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvVersionValue.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvCleanCache.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvCacheValue.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvAbout.setTextColor(getResources().getColor(R.color.purple));
        } else if(rId == R.id.ll_theme_orange) {
            getViewDataBinding().clBg.setBackgroundResource(R.mipmap.ic_gradient_color7);
            getViewDataBinding().ivBack.setBackgroundResource(R.drawable.ic_arrow_back_orange);
            getViewDataBinding().ivArrowInto.setBackgroundResource(R.drawable.ic_arrow_into_orange);
            getViewDataBinding().tvSettings.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvTheme.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvThemeNormal.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvThemeDark.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvThemeWhite.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvThemeOrange.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvThemeLight.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().rlSettingsBar.setBackgroundResource(R.drawable.shape_button_white_alpha);
            getViewDataBinding().llThemeMain.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llThemeNormal.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llThemeWhite.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llVersionMain.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llCleanCache.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llAbout.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().tvVersion.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvVersionValue.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvCleanCache.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvCacheValue.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvAbout.setTextColor(getResources().getColor(R.color.orange_0b));
        } else if(rId == R.id.ll_theme_light) {
            getViewDataBinding().clBg.setBackgroundResource(R.mipmap.ic_gradient_color4);
            getViewDataBinding().ivBack.setBackgroundResource(R.drawable.ic_arrow_back_light);
            getViewDataBinding().ivArrowInto.setBackgroundResource(R.drawable.ic_arrow_into_light);
            getViewDataBinding().tvSettings.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvTheme.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvThemeNormal.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvThemeDark.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvThemeWhite.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvThemeOrange.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvThemeLight.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().rlSettingsBar.setBackgroundResource(R.drawable.shape_button_white_alpha);
            getViewDataBinding().llThemeMain.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llThemeNormal.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llThemeWhite.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llVersionMain.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llCleanCache.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llAbout.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().tvVersion.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvVersionValue.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvCleanCache.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvCacheValue.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvAbout.setTextColor(getResources().getColor(R.color.light_ff));
        }
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
