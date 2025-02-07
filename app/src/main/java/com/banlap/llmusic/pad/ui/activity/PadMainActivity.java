package com.banlap.llmusic.pad.ui.activity;

import static android.view.View.GONE;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.media.session.MediaControllerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseActivity;
import com.banlap.llmusic.base.BaseApplication;
import com.banlap.llmusic.databinding.ActivityPadMainBinding;
import com.banlap.llmusic.databinding.DialogCharacterMenuBinding;
import com.banlap.llmusic.databinding.DialogDefaultBinding;
import com.banlap.llmusic.databinding.DialogDownloadBinding;
import com.banlap.llmusic.databinding.DialogMessageBinding;
import com.banlap.llmusic.databinding.DialogTimePickerBinding;
import com.banlap.llmusic.databinding.DialogTimeTasksBinding;
import com.banlap.llmusic.databinding.ItemPlayListBinding;
import com.banlap.llmusic.model.Music;
import com.banlap.llmusic.model.MusicLyric;
import com.banlap.llmusic.model.Version;
import com.banlap.llmusic.pad.ui.fragment.PadDetailMusicListFragment;
import com.banlap.llmusic.pad.ui.fragment.PadLoveLiveFragment;
import com.banlap.llmusic.pad.uivm.vm.PadMainVM;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.service.CharacterService;
import com.banlap.llmusic.service.MusicPlayService;
import com.banlap.llmusic.sql.MysqlHelper;
import com.banlap.llmusic.ui.ThemeHelper;
import com.banlap.llmusic.ui.activity.MainActivity;
import com.banlap.llmusic.ui.activity.SettingsActivity;
import com.banlap.llmusic.uivm.vm.MainVM;
import com.banlap.llmusic.utils.CacheUtil;
import com.banlap.llmusic.utils.CharacterHelper;
import com.banlap.llmusic.utils.CountDownHelper;
import com.banlap.llmusic.utils.LLActivityManager;
import com.banlap.llmusic.utils.MyAnimationUtil;
import com.banlap.llmusic.utils.NotificationHelper;
import com.banlap.llmusic.utils.PermissionUtil;
import com.banlap.llmusic.utils.PxUtil;
import com.banlap.llmusic.utils.SPUtil;
import com.banlap.llmusic.utils.SystemUtil;
import com.banlap.llmusic.utils.TimeUtil;
import com.banlap.llmusic.widget.LyricScrollView;
import com.banlap.llmusic.widget.SingleLyricScrollView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.danikula.videocache.HttpProxyCacheServer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import eightbitlab.com.blurview.RenderScriptBlur;
import es.dmoral.toasty.Toasty;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PadMainActivity extends BaseActivity<PadMainVM, ActivityPadMainBinding>  {

    private static final String TAG = PadMainActivity.class.getSimpleName();
    private final Activity activity = PadMainActivity.this;

    private final Context context = PadMainActivity.this;

    public static final int REQUEST_CODE_NEED_RUNNING_PERMISSION = 100; //检查进入APP需要的权限
    private static final int REQUEST_CODE_DOWNLOAD_APP_2 = 201;

    private PadMainFragmentStateAdapter padMainFragmentStateAdapter;
    private Fragment currentFragment;
    //碎片ID值
    public static int VIEW_PAD_FRAGMENT_MAIN = 0;  //LoveLive页面
    public static int VIEW_PAD_FRAGMENT_DETAIL = 1;  //LoveLive列表详细页面

    private AlertDialog mAlertDialog;                   //弹窗
    private ServiceConn conn;                           //用于绑定服务
    private MusicPlayService.MusicBinder binder;        //用于绑定服务
    private Intent intentService;                       //音乐服务
    private Intent intentCharacterService;              //角色服务
    public static int playMode = 0;                     //播放模式: 0顺序播放 1随机播放 2单曲循环
    private PopupWindow mPopupWindow;                   //弹窗

    public static List<Music> musicList;                      //按类型的所有歌曲
    public static List<Music> playList;                       //当前播放的列表
    private List<Version> versionList;                  //版本列表

    private List<MusicLyric> musicLyricList;            //当前播放歌曲的歌词列表

    private boolean isSelect = false;                   //查询一次数据
    private boolean isShowMusicList = false;          //判断是否显示当前播放列表
    private boolean isIntoMusicDetail = false;          //判断是否点进其中一个企划或团体
    private boolean isIntoMusicController = false;      //判断是否点击进入控制器
    private boolean isShowDetailMusicList = false;       //判断是否在音乐控制器里面显示当前播放列表
    public static boolean isPlay = false;               //判断是否播放音乐
    public static boolean isOnTouchSeekBar = false;      //是否按着控制条
    private boolean isExistNewVersion = false;            //是否存在新版本app
    private String mCharacterName;                      //当前角色
    private SingleLyricScrollView lyricNewScrollView;              //
    private LyricScrollView lyricNewScrollDetailView;              //

    private PlayMusicListAdapter playMusicListAdapter;  //播放列表适配器
    private ActivityResultLauncher<Intent> intentActivityResultLauncher;
    private DialogTimeTasksBinding dialogTimeTasksBinding;
    private DialogDownloadBinding downloadBinding;
    private String downloadAppUrl = "";                  //更新下载url
    private boolean isCountDown = false;                  //倒计时是否完成
    public boolean isFirstBluetoothControl = true;        //首次蓝牙控制标记

    private int selectCountDown;                          //选择的倒计时选项
    private int hour = 0, minute = 0, sec;

    private int deviceWidth = 0;  //设备此时宽度
    private int deviceHeight = 0; //设备此时高度
    private int deviceMaxHeight = 0; //获取最大高度值

    private RequestOptions requestOptions;

    //歌曲缓存
    private BaseApplication baseApplication;
    private HttpProxyCacheServer proxyCacheServer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pad_main;
    }

    public ActivityPadMainBinding getBinding() {
        return getViewDataBinding();
    }

    @Override
    protected void initData() {
        versionList = new ArrayList<>();
        musicList = new ArrayList<>();
        playList = new ArrayList<>();
        musicLyricList = new ArrayList<>();

        //当前播放的列表
        List<Music> spList = SPUtil.getListValue(this, SPUtil.PlayListData, Music.class);
        if(spList.size()>0){
            playList.addAll(spList);
            setPlayListDefault(playList);
        } else {
            SPUtil.setListValue(context, SPUtil.PlayListData, playList);
        }

        if(SPUtil.getStrValue(this, SPUtil.SavePlayMode) != null) {
            if (!(SPUtil.getStrValue(this, SPUtil.SavePlayMode).equals(""))) {
                playMode = Integer.parseInt(SPUtil.getStrValue(this, SPUtil.SavePlayMode));
            }
        }

        playMusicListAdapter = new PlayMusicListAdapter(this, playList);
        getViewDataBinding().rvPlayList.setLayoutManager(new LinearLayoutManager(this));
        getViewDataBinding().rvPlayList.setAdapter(playMusicListAdapter);

        getViewDataBinding().rvDetailNewPlayList.setLayoutManager(new LinearLayoutManager(this));
        getViewDataBinding().rvDetailNewPlayList.setAdapter(playMusicListAdapter);

        //长按移动列表其中一个item
        setItemTouchHelper(playMusicListAdapter);

        mCharacterName = CharacterHelper.CHARACTER_NAME_KEKE;

        requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.format(DecodeFormat.PREFER_RGB_565); //设置为这种格式去掉透明度通道，可以减少内存占有


        baseApplication = (BaseApplication) getApplication();
        proxyCacheServer = baseApplication.getProxy(this);

    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        initCheckPermission();
        initCheckNavigationBar();
        initMainView();
        initListener();
        //连接数据库
        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.PAD_CONNECT_MYSQL));
        startAllService();
        initFragment();
    }

    /** 统一检查权限 */
    private void initCheckPermission() {

        String[] permission = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };

        //Android 13时 检查 Manifest.permission.POST_NOTIFICATIONS、READ_MEDIA_VIDEO权限等
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = new String[]{
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO,
            };
        }

        if(PermissionUtil.getInstance().checkPermission(this, permission)) {
            ActivityCompat.requestPermissions(PadMainActivity.this, permission, REQUEST_CODE_NEED_RUNNING_PERMISSION);
        }

    }

    /** 开启所有相关服务 */
    private void startAllService() {
        conn = new ServiceConn();
        intentService = new Intent(this, MusicPlayService.class);           //创建音乐播放服务
        intentCharacterService = new Intent(this, CharacterService.class);  //创建角色服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMusicService(false, "LLMusic", "Singer", null);
        } else {
            startService(intentService);
        }
        bindService(intentService, conn, BIND_AUTO_CREATE);
    }

    /** 初始化音乐服务 */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMusicService(boolean isPlayMusic, String musicName, String musicSinger, Bitmap bitmap) {
        intentService.putExtra("IsPlayMusic", isPlayMusic);
        intentService.putExtra("MusicName", musicName);
        intentService.putExtra("MusicSinger", musicSinger);
        if(bitmap!=null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);//把bitmap100%高质量压缩 到 output对象里
            byte[] result = bos.toByteArray();
            intentService.putExtra("MusicBitmap", result);
        } else {
            intentService.putExtra("MusicBitmap", (byte[]) null);
        }
        startForegroundService(intentService);
    }

    /** 初始化角色服务 */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startCharacterService(String characterName) {
        intentCharacterService.putExtra("IsPlayMusic", isPlay);
        intentCharacterService.putExtra("CharacterName", characterName);
        startForegroundService(intentCharacterService);
    }

    /** 初始化通知栏消息 */
    @SuppressLint("RemoteViewLayout")
    private void initNotificationHelper(String musicName, String musicSinger, String imgUrl) {
        if(!imgUrl.equals("")) {
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.SHOW_IMAGE_URL, musicName, musicSinger, imgUrl));
        } else {
            NotificationHelper.getInstance().createRemoteViews(this, musicName, musicSinger, null, true);
            MusicPlayService.updateWidgetUI(context, false);
        }
    }

    /**
     * 判断是否存在底部栏，存在则调整高度
     * */
    private void initCheckNavigationBar() {
        SystemUtil.getInstance().hasNavigationBar(this, new SystemUtil.NavigationBarCallback() {
            @Override
            public void onResult(boolean isShow, int height) {
                if(isShow) { //存在底部导航栏则调整部分ui高度
                    ViewGroup.LayoutParams layoutParams = getViewDataBinding().viewBottomBar.getLayoutParams();
                    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                    layoutParams.height = height + 20;
                    getViewDataBinding().viewBottomBar.setLayoutParams(layoutParams);

                    ViewGroup.LayoutParams layoutParams2 = getViewDataBinding().viewBottomBar2.getLayoutParams();
                    layoutParams2.width = WindowManager.LayoutParams.MATCH_PARENT;
                    layoutParams2.height = height + 20;
                    getViewDataBinding().viewBottomBar2.setLayoutParams(layoutParams2);
                }
            }
        });

    }

    /** 设置播放列表长按移动item选项 */
    private void setItemTouchHelper(PlayMusicListAdapter playMusicListAdapter) {
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(playMusicListAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(getViewDataBinding().rvPlayList);

        ItemTouchHelper.Callback touchNewCallback = new ItemTouchHelperCallback(playMusicListAdapter);
        ItemTouchHelper touchNewHelper = new ItemTouchHelper(touchNewCallback);
        touchNewHelper.attachToRecyclerView(getViewDataBinding().rvDetailNewPlayList);
    }

    @SuppressLint("SetTextI18n")
    private void initMainView() {
        getViewDataBinding().clMain.setVisibility(View.VISIBLE);
        getViewDataBinding().clController.setVisibility(View.VISIBLE);
        getViewDataBinding().clController.setOnClickListener(new ButtonClickListener());
        initBlurView();

        getViewDataBinding().tvVersion.setText("版本 - V" + SystemUtil.getInstance().getAppVersionName(this));
        getViewDataBinding().pbLoadingMusic.setVisibility(View.INVISIBLE);
        getViewDataBinding().tvTittleName.setText("LoveLive");
        getViewDataBinding().tvNewListSize.setText("("+ playList.size() + ")");
        getViewDataBinding().tvDetailNewListSize.setText("("+ playList.size() + ")");
        lyricNewScrollView = getViewDataBinding().lvNewShowLyric;
        lyricNewScrollDetailView = getViewDataBinding().lvNewShowLyricDetail;

        //移走音乐控制器
        deviceWidth = SystemUtil.getInstance().getDM(activity).widthPixels;
        deviceHeight = SystemUtil.getInstance().getDM(activity).heightPixels;
        deviceMaxHeight = Math.max(deviceWidth, deviceHeight);

        //调整音乐控制器明细部分内容显示
        if(deviceWidth > deviceHeight) {
            getViewDataBinding().clControllerMainRight.setVisibility(View.VISIBLE);
            getViewDataBinding().lvNewShowLyric.setVisibility(View.GONE);
            getViewDataBinding().civMusicImg.getLayoutParams().width = PxUtil.getInstance().dp2px(200, context);
            getViewDataBinding().civMusicImg.getLayoutParams().height = PxUtil.getInstance().dp2px(200, context);
            getViewDataBinding().civMusicImg.requestLayout();
        } else {
            getViewDataBinding().clControllerMainRight.setVisibility(View.GONE);
            getViewDataBinding().lvNewShowLyric.setVisibility(View.VISIBLE);
            getViewDataBinding().civMusicImg.getLayoutParams().width = PxUtil.getInstance().dp2px(150, context);
            getViewDataBinding().civMusicImg.getLayoutParams().height = PxUtil.getInstance().dp2px(150, context);
            getViewDataBinding().civMusicImg.requestLayout();
        }

        getViewDataBinding().clCurrentMusicList.setVisibility(View.VISIBLE);
        ObjectAnimator newCurrentMusicList = MyAnimationUtil.objectAnimatorLeftOrRightNew(PadMainActivity.this, true, false, deviceMaxHeight, getViewDataBinding().clCurrentMusicList);
        newCurrentMusicList.start();

        ObjectAnimator animator = MyAnimationUtil.objectAnimatorUpOrDown(activity, true, deviceMaxHeight*2, getViewDataBinding().clController);
        animator.start();

        getViewDataBinding().clNewDetailCurrentMusicList.setVisibility(View.VISIBLE);
        ObjectAnimator newDetailCurrentMusicList = MyAnimationUtil.objectAnimatorUpOrDown(activity, true, deviceMaxHeight, getViewDataBinding().clNewDetailCurrentMusicList);
        newDetailCurrentMusicList.start();

    }

    /** 初始化磨砂透明 */
    private void initBlurView() {
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        getViewDataBinding().bvMain.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(5f)
                .setOverlayColor(getColor(R.color.light_fd_alpha_90))
                .setBlurAutoUpdate(true);
    }

    /** 初始化碎片 */
    private void initFragment() {
        Fragment fragment = new PadLoveLiveFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fcv_fragment, fragment);
        fragmentTransaction.commit();

        currentFragment = fragment;
    }

    /** 切换碎片 */
    private void changeFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fcv_fragment, fragment);
            fragmentTransaction.addToBackStack(null); // 添加到back stack以支持返回操作
            fragmentTransaction.commit();

            currentFragment = fragment;
        }
    }

    /** 初始化监听 */
    private void initListener() {
        getViewDataBinding().llVersionMain.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llMusicControllerBar.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().clCurrentMusicList.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llCharacter.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llTimeClose.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().flPlay.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().ivNewPanelPlay.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().ivNewPanelNext.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().ivNewPanelLast.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().flChangePlayMode.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().ivNewChangePlayMode.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llNewPlayMode.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llDetailNewPlayMode.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().flCurrentList.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().ivExit.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llNewDeleteAll.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llDetailNewDeleteAll.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llCleanCache.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llAbout.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().ivNewCurrentList.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().sbNewMusicBar.setOnSeekBarChangeListener(new MusicBarChangerListener());
        getViewDataBinding().sbNewMusicBar.setOnTouchListener(new ProgressBarTouchListener());
        getViewDataBinding().flClose.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().ivNewMore.setOnClickListener(new ButtonClickListener());

        intentActivityResultLauncher = registerForActivityResult(
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
                                startCharacterService(mCharacterName);
                            } else {
                                intentCharacterService.putExtra("CharacterName", mCharacterName);
                                startService(intentCharacterService);
                            }
                            getViewDataBinding().ivCharacterStatus.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    /** 屏幕大小方向改变时调整 */
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        Log.e(TAG, "onConfigurationChanged: " +
                ((config.orientation == Configuration.ORIENTATION_LANDSCAPE)? "横屏" : "竖屏") +
                " deviceWidth: " + deviceWidth + " deviceHeight: " + deviceHeight);
        deviceWidth = SystemUtil.getInstance().getDM(activity).heightPixels;
        deviceHeight = SystemUtil.getInstance().getDM(activity).widthPixels;
        deviceMaxHeight = Math.max(deviceWidth, deviceHeight);

        if(config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getViewDataBinding().clControllerMainRight.setVisibility(View.GONE);
            getViewDataBinding().lvNewShowLyric.setVisibility(View.VISIBLE);
            getViewDataBinding().civMusicImg.getLayoutParams().width = PxUtil.getInstance().dp2px(150, context);
            getViewDataBinding().civMusicImg.getLayoutParams().height = PxUtil.getInstance().dp2px(150, context);
            getViewDataBinding().civMusicImg.requestLayout();
        } else {
            getViewDataBinding().clControllerMainRight.setVisibility(View.VISIBLE);
            getViewDataBinding().lvNewShowLyric.setVisibility(View.GONE);
            getViewDataBinding().civMusicImg.getLayoutParams().width = PxUtil.getInstance().dp2px(200, context);
            getViewDataBinding().civMusicImg.getLayoutParams().height = PxUtil.getInstance().dp2px(200, context);
            getViewDataBinding().civMusicImg.requestLayout();
        }

    }


    public class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(isDoubleClick()) { return; }
            if(v.getId() == R.id.ll_music_controller_bar) {
                isIntoMusicController = true;
                ObjectAnimator animator = MyAnimationUtil.objectAnimatorUpOrDown(activity, false, deviceMaxHeight, getViewDataBinding().clController);
                animator.start();
            } else if(v.getId() == R.id.ll_version_main) {
                if(isExistNewVersion) {
                    showUpgradeApp();
                }
            } else if(v.getId() == R.id.fl_play) {
                padPlayButtonClick(v);
            } else if(v.getId() == R.id.iv_new_panel_play) {
                padPlayButtonClick(v);
            } else if(v.getId() == R.id.iv_new_panel_next) {
                lastOrNextMusic(true);
            } else if(v.getId() == R.id.iv_new_panel_last) {
                lastOrNextMusic(false);
            } else if(v.getId() == R.id.fl_change_play_mode) {
                playModeButtonClick(v);
            } else if(v.getId() == R.id.iv_new_change_play_mode) {
                playModeButtonClick(v);
            } else if(v.getId() == R.id.ll_new_play_mode) {
                playModeButtonClick(v);
            } else if(v.getId() == R.id.ll_detail_new_play_mode) {
                playModeButtonClick(v);
            } else if(v.getId() == R.id.ll_new_delete_all) {
                deletePlayListAll(v);
            } else if(v.getId() == R.id.ll_detail_new_delete_all) {
                deletePlayListAll(v);
            } else if(v.getId() == R.id.iv_exit) { //退出app
                exitApp();
            } else if(v.getId() == R.id.fl_current_list) {
                if(isShowMusicList) {
                    getViewDataBinding().rlDisableClick.setVisibility(View.GONE);
                    ObjectAnimator newCurrentMusicList = MyAnimationUtil.objectAnimatorLeftOrRight(PadMainActivity.this, false, false, getViewDataBinding().clCurrentMusicList);
                    newCurrentMusicList.start();
                } else {
                    getViewDataBinding().rlDisableClick.setVisibility(View.VISIBLE);
                    getViewDataBinding().rlDisableClick.setOnClickListener(new ButtonClickListener());
                    ObjectAnimator newCurrentMusicList = MyAnimationUtil.objectAnimatorLeftOrRight(PadMainActivity.this, false, true, getViewDataBinding().clCurrentMusicList);
                    newCurrentMusicList.start();
                }
                isShowMusicList = !isShowMusicList;
            } else if(v.getId() == R.id.rl_disable_click) {
                if(isShowMusicList) {
                    getViewDataBinding().rlDisableClick.setVisibility(View.GONE);
                    ObjectAnimator newCurrentMusicList = MyAnimationUtil.objectAnimatorLeftOrRight(PadMainActivity.this, false, false, getViewDataBinding().clCurrentMusicList);
                    newCurrentMusicList.start();
                    isShowMusicList = false;
                }
            } else if(v.getId() == R.id.ll_character) {
                showCharacterMenu(v);
            } else if(v.getId() == R.id.ll_time_close) {
                showTimeTasks();
            } else if(v.getId() == R.id.ll_clean_cache) {
                cleanCache();
            } else if(v.getId() == R.id.ll_about) {
                showAboutApp();
            } else if(v.getId() == R.id.fl_close) {
                isIntoMusicController = false;
                getViewDataBinding().clMain.setVisibility(View.VISIBLE);
                ObjectAnimator animator = MyAnimationUtil.objectAnimatorUpOrDown(activity, true, deviceMaxHeight*2, getViewDataBinding().clController);
                animator.start();
            } else if(v.getId() == R.id.iv_new_current_list) {
                if(!isShowDetailMusicList) {
                    getViewDataBinding().rlDetailDisableClick.setVisibility(View.VISIBLE);
                    getViewDataBinding().rlDetailDisableClick.setOnClickListener(new ButtonClickListener());
                    ObjectAnimator newDetailCurrentMusicList = MyAnimationUtil.objectAnimatorUpOrDown(PadMainActivity.this, false, deviceMaxHeight, getViewDataBinding().clNewDetailCurrentMusicList);
                    newDetailCurrentMusicList.start();
                } else {
                    getViewDataBinding().rlDetailDisableClick.setVisibility(View.GONE);
                    ObjectAnimator newDetailCurrentMusicList = MyAnimationUtil.objectAnimatorUpOrDown(PadMainActivity.this, true, deviceMaxHeight, getViewDataBinding().clNewDetailCurrentMusicList);
                    newDetailCurrentMusicList.start();
                }
                isShowDetailMusicList = !isShowDetailMusicList;
            } else if(v.getId() == R.id.rl_detail_disable_click) {
                if(isShowDetailMusicList) {
                    getViewDataBinding().rlDetailDisableClick.setVisibility(View.GONE);
                    ObjectAnimator newCurrentMusicList = MyAnimationUtil.objectAnimatorUpOrDown(PadMainActivity.this, true, deviceMaxHeight, getViewDataBinding().clNewDetailCurrentMusicList);
                    newCurrentMusicList.start();
                    isShowDetailMusicList = false;
                }
            } else if(v.getId() == R.id.iv_new_more) {

            }
        }
    }

    /** 显示弹窗更新App */
    private void showUpgradeApp() {
        if(versionList != null && versionList.size() >0) {
            DialogMessageBinding messageBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                    R.layout.dialog_message, null, false);
            messageBinding.tvTitle.setText(null != versionList.get(0).versionTitle? versionList.get(0).versionTitle : "");
            messageBinding.tvContent.setText(null != versionList.get(0).versionContent? versionList.get(0).versionContent : "");
            messageBinding.btSelectIconCancel.setText("以后再说");
            messageBinding.btSelectIconCancel.setBackgroundResource(R.drawable.selector_button_selected4);
            messageBinding.btSelectIconCommit.setText("立即体验");
            messageBinding.btSelectIconCommit.setTextColor(getResources().getColor(R.color.white));
            //ThemeHelper.getInstance().settingActivityUpgradeAppButton(LLActivityManager.getInstance().getTopActivity(), rThemeId, messageBinding.btSelectIconCommit);
            messageBinding.btSelectIconCommit.setBackgroundResource(R.drawable.selector_button_selected_default);

            messageBinding.btSelectIconCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlertDialog.dismiss();
                }
            });

            messageBinding.btSelectIconCommit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!TextUtils.isEmpty(versionList.get(0).versionUrl)) {
                        downloadAppUrl = versionList.get(0).versionUrl;
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP2, downloadAppUrl));
                    }
                }
            });

            mAlertDialog = new AlertDialog.Builder(this)
                    .setView(messageBinding.getRoot())
                    .create();
            Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_2);
            mAlertDialog.show();
        }

    }

    /** 展示下载进度 */
    private void showLoadingApp() {
        if(null != mAlertDialog) {
            mAlertDialog.dismiss();
        }

        downloadBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_download, null, false);

        downloadBinding.pbProgress.setMax(100);

        //ThemeHelper.getInstance().settingActivityProgressTheme(LLActivityManager.getInstance().getTopActivity(), rThemeId, downloadBinding.pbProgress);
        downloadBinding.pbProgress.setProgressDrawable(context.getResources().getDrawable(R.drawable.shape_bg_progress_bar_download_light_f8));

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

    /** 展示定时器弹窗 */
    private void showTimeTasks() {
        dialogTimeTasksBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.dialog_time_tasks, null, false);

        dialogTimeTasksBinding.tvTitleDetail.setVisibility(isCountDown? View.GONE : View.VISIBLE);
        dialogTimeTasksBinding.llTitleDetail.setVisibility(isCountDown? View.VISIBLE : View.GONE);
        dialogTimeTasksBinding.pbLoadingTask.setVisibility(isCountDown? View.VISIBLE : View.GONE);
        dialogTimeTasksBinding.switchTask.setChecked(false);

        String taskSwitch = SPUtil.getStrValue(context, SPUtil.TaskAfterMusicSwitch);
        if(!TextUtils.isEmpty(taskSwitch) && taskSwitch.equals("1")) {
            dialogTimeTasksBinding.switchTask.setChecked(true);
        }

        if(isCountDown) {
            dialogTimeTasksBinding.ivTasksClose.setVisibility(View.GONE);
            dialogTimeTasksBinding.ivTimeTasks1.setVisibility(selectCountDown == 10? View.VISIBLE : View.GONE);
            dialogTimeTasksBinding.ivTimeTasks2.setVisibility(selectCountDown == 20? View.VISIBLE : View.GONE);
            dialogTimeTasksBinding.ivTimeTasks3.setVisibility(selectCountDown == 30? View.VISIBLE : View.GONE);
            dialogTimeTasksBinding.ivTimeTasks4.setVisibility(selectCountDown == 60? View.VISIBLE : View.GONE);
            dialogTimeTasksBinding.ivTimeTasksCustom.setVisibility(selectCountDown == 0? View.VISIBLE : View.GONE);
        }
        dialogTimeTasksBinding.llTasksClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickTimeTasks(false, 0, dialogTimeTasksBinding);
            }
        });

        dialogTimeTasksBinding.llTimeTasks1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickTimeTasks(true, 10, dialogTimeTasksBinding);
            }
        });

        dialogTimeTasksBinding.llTimeTasks2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickTimeTasks(true, 20, dialogTimeTasksBinding);
            }
        });

        dialogTimeTasksBinding.llTimeTasks3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickTimeTasks(true, 30, dialogTimeTasksBinding);
            }
        });

        dialogTimeTasksBinding.llTimeTasks4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickTimeTasks(true, 60, dialogTimeTasksBinding);
            }
        });

        dialogTimeTasksBinding.llTimeTasksCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickTimeTasks(true, 0, dialogTimeTasksBinding);
            }
        });

        dialogTimeTasksBinding.switchTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SPUtil.setStrValue(getApplicationContext(), SPUtil.TaskAfterMusicSwitch, dialogTimeTasksBinding.switchTask.isChecked()? "1" : "0");
            }
        });

        mAlertDialog = new AlertDialog.Builder(context)
                .setView(dialogTimeTasksBinding.getRoot())
                .create();
        Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_3);
        mAlertDialog.show();
    }

    /**
     * 点击选择定时
     * */
    private void clickTimeTasks(boolean isStart, int time, DialogTimeTasksBinding binding) {
        if(!isStart) {
            cancelTimeTasks(binding);
        } else {
            if(time != 0) {
                sec = time * 60;
            } else {
                //自定义时间
                DialogTimePickerBinding dialogTimePickerBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                        R.layout.dialog_time_picker, null, false);

                dialogTimePickerBinding.tpSelect.setIs24HourView(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    dialogTimePickerBinding.tpSelect.setHour(0);
                    dialogTimePickerBinding.tpSelect.setMinute(1);
                }
                hour = 0;
                minute = 1;
                dialogTimePickerBinding.tpSelect.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker timePicker, int h, int m) {
                        hour = h;
                        minute = m;
                    }
                });
                dialogTimePickerBinding.btSelectIconCommit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(hour == 0 && minute == 0) {
                            Toasty.warning(context, "请设置一个正确的时间值", Toast.LENGTH_SHORT, true).show();
                            return;
                        }
                        mPopupWindow.dismiss();
                        sec = hour * 60 * 60 + minute * 60;
                        setTimeTasks(time, sec, binding);
                    }
                });
                dialogTimePickerBinding.btSelectIconCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                        cancelTimeTasks(binding);
                    }
                });

                if(mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
                mPopupWindow  = new PopupWindow(dialogTimePickerBinding.getRoot(),
                        WindowManager.LayoutParams.WRAP_CONTENT,  WindowManager.LayoutParams.WRAP_CONTENT, true);
                mPopupWindow.setTouchable(true);
                mPopupWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0,0);

                mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
//                        if(!isCountDown) {
//                            cancelTimeTasks(binding);
//                        }
                    }
                });
                return;
            }
            setTimeTasks(time, sec, binding);
        }
    }

    /**
     * 开始倒计时
     * */
    private void setTimeTasks(int time, int sec, DialogTimeTasksBinding binding) {
        binding.ivTasksClose.setVisibility(GONE);
        binding.ivTimeTasks1.setVisibility(time == 10? View.VISIBLE : GONE);
        binding.ivTimeTasks2.setVisibility(time == 20? View.VISIBLE : GONE);
        binding.ivTimeTasks3.setVisibility(time == 30? View.VISIBLE : GONE);
        binding.ivTimeTasks4.setVisibility(time == 60? View.VISIBLE : GONE);
        binding.ivTimeTasksCustom.setVisibility(time == 0? View.VISIBLE : GONE);
        selectCountDown = time;

        binding.tvTitleDetail.setVisibility(GONE);
        binding.llTitleDetail.setVisibility(View.VISIBLE);
        isCountDown = true;
        CountDownHelper.startCountTime(sec, new CountDownHelper.CountDownCallBack() {
            @Override
            public void showTime(int countDown, String time) {
                runOnUiThread(() -> {
                    binding.tvTitleDetailTime.setText(time);
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_COUNT_DOWN_REFRESH, time));
                });
            }

            @Override
            public void finish() {
                runOnUiThread(() -> {
                    isCountDown = false;
                    binding.tvTitleDetailTime.setText("00:00");
                    binding.tvTitleDetail.setVisibility(View.VISIBLE);
                    binding.llTitleDetail.setVisibility(GONE);
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_COUNT_DOWN_FINISH));
                });

            }
        });
    }

    /**
     * 取消倒计时
     * */
    private void cancelTimeTasks(DialogTimeTasksBinding binding) {
        binding.ivTasksClose.setVisibility(View.VISIBLE);
        binding.ivTimeTasks1.setVisibility(GONE);
        binding.ivTimeTasks2.setVisibility(GONE);
        binding.ivTimeTasks3.setVisibility(GONE);
        binding.ivTimeTasks4.setVisibility(GONE);
        binding.ivTimeTasksCustom.setVisibility(GONE);
        CountDownHelper.pauseImm();
        isCountDown = false;
        binding.tvTitleDetail.setVisibility(View.VISIBLE);
        binding.llTitleDetail.setVisibility(GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEvent(ThreadEvent event) {
        switch (event.msgCode) {
            case ThreadEvent.CONNECT_MYSQL_LOADING:
                getViewDataBinding().rlShowLoading.setVisibility(View.VISIBLE);
                break;
            case ThreadEvent.CONNECT_MYSQL_SUCCESS:
                Log.i("MYSQL", "mysql connect success");
                if (!isSelect) {
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_DATA_APP_VERSION));
                    //每日推荐
                    //PadMainVM.showRecommendData(getApplicationContext());
                    isSelect = true;
                }
                break;
            case ThreadEvent.CONNECT_MYSQL_ERROR:
                getViewDataBinding().rlShowLoading.setVisibility(View.GONE);
                Toasty.error(this, "网络连接失败", Toast.LENGTH_SHORT, true).show();
                break;
            case ThreadEvent.GET_APP_VERSION_SUCCESS:  //获取app版本更新
                isExistNewVersion = false;
                versionList.clear();
                versionList.addAll(event.tList);
                if(versionList.size()>0) {
                    //isExistNewVersion = true;
                    int versionCode = Integer.parseInt(versionList.get(0).getVersionCode());
                    if(SystemUtil.getInstance().getAppVersionCode(this) < versionCode) { //判断当前是否有新版本
                        isExistNewVersion = true;
                        String versionType = versionList.get(0).getVersionType();
                        if("1".equals(versionType)) {
                            getViewDataBinding().tvNewVersion.setVisibility(View.VISIBLE);
                        } else if ("2".equals(versionType)) {
                            getViewDataBinding().tvNewVersion.setVisibility(View.VISIBLE);
                            //showUpgradeApp();
                        } else {
                            isExistNewVersion = false;
                        }
                    }
                }
                break;
            case ThreadEvent.DOWNLOAD_APP_START2:
                showLoadingApp();
                break;
            case ThreadEvent.DOWNLOAD_APP_LOADING2:
                if(null != downloadBinding) {
                    downloadBinding.tvValue.setText(""+event.i);
                    downloadBinding.pbProgress.setProgress(event.i);
                }
                break;
            case ThreadEvent.DOWNLOAD_APP_SUCCESS2:
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
            case ThreadEvent.DOWNLOAD_APP_ERROR2:
                if(null != mAlertDialog) {
                    mAlertDialog.dismiss();
                }
                Toasty.error(this, "app下载失败，请重新下载", Toast.LENGTH_SHORT,true).show();
                break;
            case ThreadEvent.VIEW_PAD_CHANGE_FRAGMENT:
                if(event.i == VIEW_PAD_FRAGMENT_MAIN ) {
                    isIntoMusicDetail = false;
                    getViewDataBinding().tvTittleName.setText("LoveLive");
                    changeFragment(new PadLoveLiveFragment());
                } else if(event.i == VIEW_PAD_FRAGMENT_DETAIL) {
                    isIntoMusicDetail = true;
                    changeFragment(new PadDetailMusicListFragment());
                    if(event.str.equals(ThreadEvent.ALBUM_LIELLA)) {
                        getViewDataBinding().tvTittleName.setText("Liella!");
                        EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.PAD_GET_DATA_LIST_BY_LIELLA));
                    } else if(event.str.equals(ThreadEvent.ALBUM_FOUR_YUU)) {
                        getViewDataBinding().tvTittleName.setText("Liyuu");
                        EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.PAD_GET_DATA_LIST_BY_FOUR_YUU));
                    } else if(event.str.equals(ThreadEvent.ALBUM_SUNNY_PASSION)) {
                        getViewDataBinding().tvTittleName.setText("Sunny Passion");
                        EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.PAD_GET_DATA_LIST_BY_SUNNY_PASSION));
                    } else if(event.str.equals(ThreadEvent.ALBUM_NIJIGASAKI)) {
                        getViewDataBinding().tvTittleName.setText("虹ヶ咲学園スクールアイドル同好会");
                        EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.PAD_GET_DATA_LIST_BY_NIJIGASAKI));
                    } else if(event.str.equals(ThreadEvent.ALBUM_AQOURS)) {
                        getViewDataBinding().tvTittleName.setText("Aqours");
                        EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.PAD_GET_DATA_LIST_BY_AQOURS));
                    } else if(event.str.equals(ThreadEvent.ALBUM_US)) {
                        getViewDataBinding().tvTittleName.setText("μ's");
                        EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.PAD_GET_DATA_LIST_BY_US));
                    } else if(event.str.equals(ThreadEvent.ALBUM_HASUNOSORA)) {
                        getViewDataBinding().tvTittleName.setText("蓮ノ空女学院スクールアイドルクラブ");
                        EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.PAD_GET_DATA_LIST_BY_HASUNOSORA));
                    } else if(event.str.equals(ThreadEvent.ALBUM_SAINT_SNOW)) {
                        getViewDataBinding().tvTittleName.setText("Saint Snow");
                        EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.PAD_GET_DATA_LIST_BY_SAINT_SNOW));
                    } else if(event.str.equals(ThreadEvent.ALBUM_A_RISE)) {
                        getViewDataBinding().tvTittleName.setText("A-RISE");
                        EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.PAD_GET_DATA_LIST_BY_A_RISE));
                    }

                }
                break;

            case ThreadEvent.VIEW_PAD_CHANGE_LAST_FRAGMENT:
                isIntoMusicDetail = false;
                getViewDataBinding().tvTittleName.setText("LoveLive"); //ps:后续更改
                FragmentManager fm = this.getSupportFragmentManager();
                if(fm.getBackStackEntryCount()>0) {
                    fm.popBackStack();
                }
                break;
            case ThreadEvent.PAD_VIEW_PLAY_MUSIC:
                if(event.tList.size()>0) {
                    playMusic(event.tList, event.i);
                }

                break;
            case ThreadEvent.GET_MUSIC_LYRIC:
                getViewModel().showLyric(event.music, event.b);
                break;
            case ThreadEvent.VIEW_LYRIC:
                if(null != event.tList) {
                    //将歌词数据方法lyricScrollView显示
//                    lyricScrollView.setMusicLyrics(event.tList);
                    lyricNewScrollDetailView.setMusicLyrics(event.tList);
                    lyricNewScrollView.setMusicLyrics(event.tList);
                    musicLyricList.clear();
                    musicLyricList.addAll(event.tList);
                }
                if(binder !=null) {
                    binder.player(event.music, event.b, proxyCacheServer, musicLyricList);
                }
                break;
            case ThreadEvent.VIEW_SEEK_BAR_POS:
                if(!isOnTouchSeekBar) {
                    //lyricScrollView.setMusicPlayerPos(event.i);
                    lyricNewScrollDetailView.setMusicPlayerPos(event.i);
                    lyricNewScrollView.setMusicPlayerPos(event.i);
                    //getViewDataBinding().sbMusicBar.setProgress(event.i);
                    getViewDataBinding().sbNewMusicBar.setProgress(event.i);
                    //getViewDataBinding().hpvProgress.setCurrentCount(event.i);
                    //getViewDataBinding().pbNewProgress.setProgress(event.i);
                }
                break;
            case ThreadEvent.VIEW_SEEK_BAR_RESUME:
//                lyricScrollView.posLock(true);
                lyricNewScrollDetailView.posLock(true);
                lyricNewScrollView.posLock(true);
//                getViewDataBinding().sbMusicBar.setProgress(0);
                getViewDataBinding().sbNewMusicBar.setProgress(0);
                getViewDataBinding().pbLoadingMusic.setVisibility(View.VISIBLE);
//                getViewDataBinding().pbNewLoadingMusic.setVisibility(View.VISIBLE);
                MusicPlayService.updateWidgetUI(context, true);
                break;
            case ThreadEvent.VIEW_PAUSE:
                isPlay = !event.b;
//                lyricScrollView.posLock(event.b);
                lyricNewScrollDetailView.posLock(event.b);
                lyricNewScrollView.posLock(event.b);
                //角色服务存在时 对角色服务做处理
                if(SystemUtil.getInstance().isServiceWorked(this, CharacterService.class.getPackage().getName()
                        + "." + CharacterService.class.getSimpleName())) {
                    //根据播放或暂停 对角色状态变更
                    PadMainVM.stopHandler();
                    if(event.b) {
                        PadMainVM.initAnimatedCharacter(mCharacterName);
                    } else {
                        PadMainVM.animatedListenCharacter(mCharacterName);
                    }
                    //控制角色系统里面的播放按钮
                    CharacterService.isPlayMusic(!event.b);
                }

                //变更主题 pad版本目前先处理一个主题
                //ThemeHelper.getInstance().playButtonStatusTheme(rThemeId, getViewDataBinding(), event.b);
                getViewDataBinding().ivPlay.setBackgroundResource(event.b ? R.drawable.selector_play_black_selected : R.drawable.selector_pause_black_selected);
                getViewDataBinding().ivNewPanelPlay.setBackgroundResource(event.b ? R.drawable.selector_play_circle_black_33_selected : R.drawable.selector_pause_circle_black_33_selected);

                break;

            case ThreadEvent.VIEW_MUSIC_MSG:  //点击播放歌曲后处理
//                lyricScrollView.initView();
                lyricNewScrollView.initView();
                lyricNewScrollDetailView.initView();

//                String defaultSize = SPUtil.getStrValue(context, SPUtil.DefaultLyricSizeData);
//                if(!TextUtils.isEmpty(defaultSize)) {
//                    int defaultSizeInt = Integer.parseInt(defaultSize);
//                    lyricScrollView.setLyricSize(defaultSizeInt);
//                }
//
//                String singleSize = SPUtil.getStrValue(context, SPUtil.SingleLyricSizeData);
//                if(!TextUtils.isEmpty(singleSize)) {
//                    int singleSizeInt = Integer.parseInt(singleSize);
//                    lyricNewScrollView.setLyricSize(singleSizeInt);
//                }
//
//                String detailSize = SPUtil.getStrValue(context, SPUtil.DetailLyricSizeData);
//                if(!TextUtils.isEmpty(detailSize)) {
//                    int detailSizeInt = Integer.parseInt(detailSize);
//                    lyricNewScrollDetailView.setLyricSize(detailSizeInt);
//                }

//                getViewDataBinding().hpvProgress.setMaxCount(event.i);
//                getViewDataBinding().pbNewProgress.setMax(event.i);
//
                getViewDataBinding().pbLoadingMusic.setVisibility(View.INVISIBLE);
//                getViewDataBinding().pbNewLoadingMusic.setVisibility(View.GONE);
//                getViewDataBinding().sbMusicBar.setMax(event.i);
                getViewDataBinding().sbNewMusicBar.setMax(event.i);
//                getViewDataBinding().tvAllTime.setText(getViewModel().rebuildTime(event.i));
                getViewDataBinding().tvNewAllTime.setText(TimeUtil.rebuildTime(event.i));
                String musicName = event.music.musicName;
                getViewDataBinding().tvMusicName.setText(musicName);
                getViewDataBinding().tvNewMusicName.setText(musicName);
//                getViewDataBinding().tvNewPlayMusicName.setText(musicName);
                getViewDataBinding().tvSingerName.setText(event.music.musicSinger);
                getViewDataBinding().tvNewSingerName.setText(event.music.musicSinger);
//                getViewDataBinding().tvListSize.setText("(" + playList.size() + ")");
                getViewDataBinding().tvNewListSize.setText("(" + playList.size() + ")");
                getViewDataBinding().tvDetailNewListSize.setText("("+ playList.size() + ")");
                //currentMusicDetail = event.music;
                MusicPlayService.currentMusicImg = event.music.getMusicImg();
                MusicPlayService.currentMusicName = event.music.musicName;
                MusicPlayService.currentMusicSinger = event.music.musicSinger;

                //刷新播放列表的收藏歌曲显示ui
                //EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_FRESH_FAVORITE_MUSIC));

                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(event.music.isLocal?
                                (null != event.music.musicImgByte?
                                        BitmapFactory.decodeByteArray(event.music.musicImgByte, 0, event.music.musicImgByte.length) : R.mipmap.ic_llmp_new_2) : event.music.getMusicImg()
                        )
                        .transform(new RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.ALL))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(getViewDataBinding().ivMusicImg);

                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(event.music.isLocal?
                                (null != event.music.musicImgByte?
                                        BitmapFactory.decodeByteArray(event.music.musicImgByte, 0, event.music.musicImgByte.length) : R.mipmap.ic_llmp_new_2) : event.music.getMusicImg()
                        )
                        .transform(new RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.ALL))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(getViewDataBinding().civMusicImg);

                //变更主题
                //ThemeHelper.getInstance().padMusicBarMusicImgTheme(this, rThemeId, getViewDataBinding(), event.music);

//                if (objectAnimator != null) {
//                    objectAnimator.cancel();
//                }
//                objectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().ivMusicImg, "rotation", 0f, 360.0f);
//                objectAnimator.setDuration(15000);
//                objectAnimator.setInterpolator(new LinearInterpolator());//不停顿
//                objectAnimator.setRepeatCount(-1);//设置动画重复次数
//                objectAnimator.setRepeatMode(ValueAnimator.RESTART);//动画重复模式
//                objectAnimator.start();

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if(event.music.isLocal) {
                        if(null != event.music.musicImgByte) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(event.music.musicImgByte, 0, event.music.musicImgByte.length);
                            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.SHOW_IMAGE_URL, event.music.musicName, event.music.musicSinger, event.music.musicImg, bitmap, true));
                        } else {
                            MusicPlayService.currentMusicBitmap = null;
                            startMusicService(true, event.music.musicName, event.music.musicSinger, null);
                            MusicPlayService.updateWidgetUI(context, false);
                        }
                    } else {
                        if(!event.music.musicImg.isEmpty()) {
                            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.SHOW_IMAGE_URL, event.music.musicName, event.music.musicSinger, event.music.musicImg, null, false));
                        } else {
                            MusicPlayService.currentMusicImg = "";
                            startMusicService(true, event.music.musicName, event.music.musicSinger, null);
                            MusicPlayService.updateWidgetUI(context, false);
                        }
                    }
                }
                initNotificationHelper(event.music.musicName, event.music.musicSinger, event.music.musicImg);
                break;
            case ThreadEvent.SHOW_IMAGE_URL:  //设置状态栏显示对应图片
                if(event.b) {
                    getViewModel().showImageBitmap(event.str, event.str2, event.bitmap);
                } else {
                    getViewModel().showImageURL(event.str, event.str2, event.str3);
                }
                break;
            case ThreadEvent.VIEW_IMAGE_URL:
                MusicPlayService.currentMusicName = event.str;
                MusicPlayService.currentMusicSinger = event.str2;
                if(event.bitmap != null) {
                    MusicPlayService.currentMusicBitmap = event.bitmap;
                }

                NotificationHelper.getInstance().createRemoteViews(this, event.str, event.str2, (event.bitmap != null) ? event.bitmap : MusicPlayService.currentMusicBitmap, false);
                MusicPlayService.updateWidgetUI(context, false);
                break;
            case ThreadEvent.VIEW_ADD_MUSIC:
            case ThreadEvent.VIEW_DELETE_MUSIC:
                getViewDataBinding().tvNewListSize.setText("("+ playList.size() + ")");
                getViewDataBinding().tvDetailNewListSize.setText("("+ playList.size() + ")");
                break;
            case ThreadEvent.PLAY_MUSIC_BY_CHARACTER:
                if(binder!=null) {
                    binder.pause(this, MusicPlayService.currentMusicName, MusicPlayService.currentMusicSinger, MusicPlayService.currentMusicBitmap);
                }
                break;
            case ThreadEvent.MUSIC_IS_PAUSE:
                if(binder!=null) {
                    binder.pause(this, event.str, event.str2, event.bitmap);
                }
                break;
            case ThreadEvent.MUSIC_IS_NEXT:
                lastOrNextMusic(true);
                break;
            case ThreadEvent.MUSIC_IS_LAST:
                lastOrNextMusic(false);
                break;
            case ThreadEvent.PLAY_LIST_FIRST:
                if(playList.size()>0) {
                    binder.showLyric(playList.get(0), (playMode == 2));
                    playList.get(0).isPlaying = true;
                    playMusicListAdapter.notifyDataSetChanged();
                }
                break;
            case ThreadEvent.PLAY_FINISH_SUCCESS:
                String tsw = SPUtil.getStrValue(context, SPUtil.TaskAfterMusicSwitch);
                if(!TextUtils.isEmpty(tsw) && tsw.equals("1")) {
                    SPUtil.setStrValue(context, SPUtil.TaskAfterMusicSwitch, "0");
                    return;
                }
                if(playList.size()>0) {
                    //判断播放模式
                    switch (playMode) {
                        case 0: //顺序播放
                            Log.i("ABMediaPlay","0");
                            boolean isInto = false;
                            for (int i = 0; i < playList.size(); i++) {
                                if (playList.get(i).isPlaying) {
                                    if(i+1<playList.size()) {
                                        isInto = true;
                                        playList.get(i).isPlaying = false;
                                        binder.showLyric(playList.get(i+1), (playMode == 2));
                                        playList.get(i+1).isPlaying = true;
                                        playMusicListAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }

                            if(!isInto){
                                setPlayListDefault(playList);
                                binder.showLyric(playList.get(0), (playMode == 2));
                                playList.get(0).isPlaying = true;
                                playMusicListAdapter.notifyDataSetChanged();
                            }
                            //变更主题
                            //ThemeHelper.getInstance().playButtonTheme(rThemeId, getViewDataBinding());
                            getViewDataBinding().ivPlay.setBackgroundResource(R.drawable.selector_play_black_selected);
                            getViewDataBinding().ivNewPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_black_33_selected);
                            break;
                        case 1: //随机播放
                            Log.i("ABMediaPlay","1");
                            boolean isIntoRand = false;
                            for (int i = 0; i < playList.size(); i++) {
                                if(playList.get(i).isPlaying) {
                                    isIntoRand = true;
                                    playList.get(i).isPlaying = false;
                                    int rand = new Random().nextInt(playList.size());
                                    while(i == rand) {
                                        rand = new Random().nextInt(playList.size());
                                    }
                                    binder.showLyric(playList.get(rand), (playMode == 2));
                                    playList.get(rand).isPlaying = true;
                                    playMusicListAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }

                            if(!isIntoRand) {
                                int rand = new Random().nextInt(playList.size());
                                binder.showLyric(playList.get(rand), (playMode == 2));
                                playList.get(rand).isPlaying = true;
                                playMusicListAdapter.notifyDataSetChanged();
                            }
                            break;
                        case 2: //单曲循环
                            Log.i("ABMediaPlay","2");
                            break;
                    }
                } else {
                    //变更主题
                    //ThemeHelper.getInstance().playButtonTheme(rThemeId, getViewDataBinding());
                    getViewDataBinding().ivPlay.setBackgroundResource(R.drawable.selector_play_black_selected);
                    getViewDataBinding().ivNewPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_black_33_selected);
                }
                break;
            case ThreadEvent.PLAY_ERROR:
                Toasty.error(this, "播放失败", Toast.LENGTH_SHORT, true).show();

                getViewDataBinding().pbLoadingMusic.setVisibility(View.INVISIBLE);
                break;
            case ThreadEvent.PLAY_RECOMMEND_MUSIC:   //点击播放每日推荐的歌曲 并添加到播放列表
            case ThreadEvent.PLAY_LOCAL_MUSIC:   //点击播放本地歌曲 并添加到播放列表
                if(event.music != null) {
                    getViewDataBinding().pbLoadingMusic.setVisibility(View.VISIBLE);
                    List<Music> list = new ArrayList<>();
                    list.add(event.music);
                    playMusic(list, 0);
                }
                break;
            case ThreadEvent.VIEW_NORMAL_STATUS_CHARACTER:
                if(PadMainVM.CHARACTER_NAME_KEKE_INT == event.i) {
                    CharacterHelper.showNormalStatusCharacter(CharacterHelper.CHARACTER_NAME_KEKE);
                } else if(PadMainVM.CHARACTER_NAME_KANON_INT == event.i) {
                    CharacterHelper.showNormalStatusCharacter(CharacterHelper.CHARACTER_NAME_KANON);
                }
                break;
            case ThreadEvent.VIEW_MOVE_STATUS_CHARACTER:
                if(PadMainVM.CHARACTER_NAME_KEKE_INT == event.i) {
                    CharacterHelper.showMoveStatusCharacter(CharacterHelper.CHARACTER_NAME_KEKE);
                } else if(PadMainVM.CHARACTER_NAME_KANON_INT == event.i) {
                    CharacterHelper.showMoveStatusCharacter(CharacterHelper.CHARACTER_NAME_KANON);
                }
                break;
            case ThreadEvent.VIEW_LISTEN_STATUS_CHARACTER_LEFT:
                if(PadMainVM.CHARACTER_NAME_KEKE_INT == event.i) {
                    CharacterHelper.showListenStatusCharacter(CharacterHelper.CHARACTER_NAME_KEKE,true);
                } else if(PadMainVM.CHARACTER_NAME_KANON_INT == event.i) {
                    CharacterHelper.showListenStatusCharacter(CharacterHelper.CHARACTER_NAME_KANON,true);
                }
                break;
            case ThreadEvent.VIEW_LISTEN_STATUS_CHARACTER_RIGHT:
                if(PadMainVM.CHARACTER_NAME_KEKE_INT == event.i) {
                    CharacterHelper.showListenStatusCharacter(CharacterHelper.CHARACTER_NAME_KEKE, false);
                } else if(PadMainVM.CHARACTER_NAME_KANON_INT == event.i) {
                    CharacterHelper.showListenStatusCharacter(CharacterHelper.CHARACTER_NAME_KANON, false);
                }
                break;
            case ThreadEvent.VIEW_COUNT_DOWN_REFRESH:  //倒计时刷新
                if(dialogTimeTasksBinding != null) {
                    dialogTimeTasksBinding.tvTitleDetailTime.setText(event.str);
                    dialogTimeTasksBinding.pbLoadingTask.setVisibility(View.GONE);
                }
                break;
            case ThreadEvent.VIEW_COUNT_DOWN_FINISH:
                if(dialogTimeTasksBinding != null) {
                    isCountDown = false;
                    dialogTimeTasksBinding.tvTitleDetailTime.setText("00:00");
                    dialogTimeTasksBinding.pbLoadingTask.setVisibility(View.GONE);
                    dialogTimeTasksBinding.tvTitleDetail.setVisibility(View.VISIBLE);
                    dialogTimeTasksBinding.llTitleDetail.setVisibility(View.GONE);
                    dialogTimeTasksBinding.ivTasksClose.setVisibility(View.VISIBLE);
                    dialogTimeTasksBinding.ivTimeTasks1.setVisibility(View.GONE);
                    dialogTimeTasksBinding.ivTimeTasks2.setVisibility(View.GONE);
                    dialogTimeTasksBinding.ivTimeTasks3.setVisibility(View.GONE);
                    dialogTimeTasksBinding.ivTimeTasks4.setVisibility(View.GONE);
                    dialogTimeTasksBinding.ivTimeTasksCustom.setVisibility(View.GONE);
                }
                String taskSwitch = SPUtil.getStrValue(context, SPUtil.TaskAfterMusicSwitch);
                if(!TextUtils.isEmpty(taskSwitch) && taskSwitch.equals("1")) {
                    return;
                }
                if(binder!=null) {
                    binder.pauseImm(this, MusicPlayService.currentMusicName, MusicPlayService.currentMusicSinger, MusicPlayService.currentMusicBitmap);
                }
                break;
            case ThreadEvent.PAD_PLAY_ALL_MUSIC:  //点击播放所有歌曲
                allPlayMusic(event.musicList);
                break;
            case ThreadEvent.PAD_VIEW_ADD_MUSIC:  // 添加歌曲
                addMusic(event.tList, event.i);
                break;

            case ThreadEvent.PAD_VIEW_GET_MUSIC_METADATA:
                if(!TextUtils.isEmpty(event.str3)) {
                    MusicPlayService.currentMusicQuality = event.str3;
                    getViewDataBinding().tvQuality.setText(" " + event.str3 + " ");
                    getViewDataBinding().tvQuality.setTextColor(getColor(R.color.white));
                    if(event.str3.equals("SQ")) {
                        getViewDataBinding().rlQuality.setBackgroundResource(R.drawable.shape_button_orange_d3_6);
                        getViewDataBinding().tvQuality.setTextColor(getColor(R.color.orange_0b));
                    } else if(event.str3.equals("HQ")) {
                        getViewDataBinding().rlQuality.setBackgroundResource(R.drawable.shape_button_blue_f3_6);
                        getViewDataBinding().tvQuality.setTextColor(getColor(R.color.blue_3c));
                    }
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void ThreadEvent(final ThreadEvent event) {
        switch (event.msgCode) {
            case ThreadEvent.PAD_CONNECT_MYSQL:
                MysqlHelper.connectDB();
                break;
            case ThreadEvent.GET_DATA_APP_VERSION:
                EventBus.getDefault().post(new ThreadEvent<Version>(ThreadEvent.GET_APP_VERSION_SUCCESS, MysqlHelper.getInstance().findVersionSql(),""));
                break;
            case ThreadEvent.GET_DATA_RECOMMEND:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.GET_RECOMMEND_SUCCESS, MysqlHelper.getInstance().findMusicByRandomSql(3)));
                break;
            case ThreadEvent.DOWNLOAD_APP2:
                String[] permissions_download = { Manifest.permission.WRITE_EXTERNAL_STORAGE };
                //Android 13中 READ_EXTERNAL_STORAGE 已失效,则使用新的权限 READ_MEDIA_VIDEO
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissions_download = new String[] { Manifest.permission.READ_MEDIA_VIDEO };
                }

                if(PermissionUtil.getInstance().checkPermission(context, permissions_download)) {
                    ActivityCompat.requestPermissions(activity, permissions_download, REQUEST_CODE_DOWNLOAD_APP_2);
                    return;
                }
                getViewModel().downloadUrl(event.str);
                break;
            case ThreadEvent.PAD_GET_DATA_LIST_BY_LIELLA:
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAD_GET_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql(MysqlHelper.MUSIC_TYPE_LIELLA)));
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_COUNT_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_LIELLA)));
                break;
            case ThreadEvent.PAD_GET_DATA_LIST_BY_FOUR_YUU:
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAD_GET_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql(MysqlHelper.MUSIC_TYPE_LIYUU)));
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_COUNT_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_LIYUU)));
                break;
            case ThreadEvent.PAD_GET_DATA_LIST_BY_SUNNY_PASSION:
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAD_GET_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql(MysqlHelper.MUSIC_TYPE_SUNNYPASSION)));
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_COUNT_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_SUNNYPASSION)));
                break;
            case ThreadEvent.PAD_GET_DATA_LIST_BY_NIJIGASAKI:
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAD_GET_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql(MysqlHelper.MUSIC_TYPE_NIJIGASAKI)));
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_COUNT_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_NIJIGASAKI)));
                break;
            case ThreadEvent.PAD_GET_DATA_LIST_BY_AQOURS:
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAD_GET_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql(MysqlHelper.MUSIC_TYPE_AQOURS)));
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_COUNT_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_AQOURS)));
                break;
            case ThreadEvent.PAD_GET_DATA_LIST_BY_US:
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAD_GET_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql(MysqlHelper.MUSIC_TYPE_US)));
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_COUNT_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_US)));
                break;
            case ThreadEvent.PAD_GET_DATA_LIST_BY_HASUNOSORA:
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAD_GET_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql(MysqlHelper.MUSIC_TYPE_HASUNOSORA)));
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_COUNT_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_HASUNOSORA)));
                break;
            case ThreadEvent.PAD_GET_DATA_LIST_BY_SAINT_SNOW:
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAD_GET_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeAndMusicSingerSql(MysqlHelper.MUSIC_TYPE_AQOURS, MysqlHelper.MUSIC_TYPE_SAINT_SNOW)));
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_COUNT_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeAndMusicSingerCount(MysqlHelper.MUSIC_TYPE_AQOURS, MysqlHelper.MUSIC_TYPE_SAINT_SNOW)));
                break;
            case ThreadEvent.PAD_GET_DATA_LIST_BY_A_RISE:
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAD_GET_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeAndMusicSingerSql(MysqlHelper.MUSIC_TYPE_US, MysqlHelper.MUSIC_TYPE_A_RISE)));
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_COUNT_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeAndMusicSingerCount(MysqlHelper.MUSIC_TYPE_US, MysqlHelper.MUSIC_TYPE_A_RISE)));
                break;
            case ThreadEvent.SAVE_LOCAL_MUSIC_LIST:  //在子线程中保存列表数据
                List<Music> list = event.musicList;
                SPUtil.setListValue(context, SPUtil.PlayListData, list);
                break;
            case ThreadEvent.GET_MUSIC_METADATA:
                if(event.music != null) {
                    Map<String, String> map = MusicPlayService.getMediaMeta(event.music);
                    String bitrate = map.get("Bitrate");
                    String mime = map.get("Mime");
                    String quality = map.get("Quality");
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.PAD_VIEW_GET_MUSIC_METADATA, bitrate, mime, quality));
                }
                break;
        }
    }

    /** 展示桌面角色菜单 */
    private void showCharacterMenu(View v) {
        //展示角色菜单
        DialogCharacterMenuBinding characterMenuBinding = DataBindingUtil.inflate(LayoutInflater.from(v.getContext()),
                R.layout.dialog_character_menu, null, false);

        characterMenuBinding.llCharacterByKeke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCharacterAuth(CharacterHelper.CHARACTER_NAME_KEKE);
            }
        });

        characterMenuBinding.llCharacterByKanon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCharacterAuth(CharacterHelper.CHARACTER_NAME_KANON);
            }
        });

        PopupWindow characterPopupWindow  = new PopupWindow(characterMenuBinding.getRoot(),
                PxUtil.getInstance().dp2px(110, v.getContext()),  WindowManager.LayoutParams.WRAP_CONTENT, true);
        characterPopupWindow.setTouchable(true);

        //变更主题
        //ThemeHelper.getInstance().characterPopupWindowTheme(v.getContext(), rThemeId, characterPopupWindow, characterMenuBinding);
        characterPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_button_menu_normal));
        characterMenuBinding.tvCharacterByKeke.setTextColor(context.getResources().getColor(R.color.white));
        characterMenuBinding.tvCharacterByKanon.setTextColor(context.getResources().getColor(R.color.white));
        characterMenuBinding.vLine.setBackgroundColor(context.getResources().getColor(R.color.gray_c9));

        characterPopupWindow.showAsDropDown(v,  PxUtil.getInstance().dp2px(120, v.getContext()),  PxUtil.getInstance().dp2px(-55, v.getContext()));
    }

    /** 退出应用 */
    private void exitApp() {
        DialogDefaultBinding defaultBinding = DataBindingUtil.inflate(LayoutInflater.from(PadMainActivity.this),
                R.layout.dialog_default, null, false);

        defaultBinding.dialogSelectTitle.setText("是否退出应用");

        //取消
        defaultBinding.btSelectIconCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        //确认
        defaultBinding.btSelectIconCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });

        mAlertDialog = new AlertDialog.Builder(PadMainActivity.this)
                .setView(defaultBinding.getRoot())
                .create();
        Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_3);
        mAlertDialog.show();
    }

    /** 播放当前点击的歌曲 */
    private void playMusic(List<Music> list, int position) {
        if(playList.size()>0){

            Optional<Music> currentlyPlaying = playList.stream()
                    .filter(music -> music.isPlaying)
                    .findFirst();

            if(currentlyPlaying.isPresent()) {
                Music music = currentlyPlaying.get();
                music.isPlaying = false;
                binder.showLyric(list.get(position), (playMode == 2));
                int index = PadMainActivity.playList.indexOf(music);
                playList.add(index + 1, PadMainVM.setMusicMsg(list.get(position), true));
                playMusicListAdapter.notifyDataSetChanged();
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.SAVE_LOCAL_MUSIC_LIST, playList));
                return;
            }


            binder.showLyric(list.get(position), (playMode == 2));
            playList.add(playList.size(), PadMainVM.setMusicMsg(list.get(position), true));
        } else {
            binder.showLyric(list.get(position), (playMode == 2));
            playList.add(PadMainVM.setMusicMsg(list.get(position), true));
        }
        playMusicListAdapter.notifyDataSetChanged();
        EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.SAVE_LOCAL_MUSIC_LIST, playList));
        //SPUtil.setListValue(context, SPUtil.PlayListData, playList);
    }

    /** 点击播放全部歌曲 */
    @SuppressLint("SetTextI18n")
    public void allPlayMusic(List<Music> musicList) {
        if(musicList.size()!=0) {
            playList.clear();
            List<Music> list = musicList.stream()
                    .filter(music -> !music.getMusicType().equals(" "))
                    .collect(Collectors.toList());

            playList.addAll(list);
            playMusicListAdapter.notifyDataSetChanged();
            //保存当前列表数据
            SPUtil.setListValue(this, SPUtil.PlayListData, playList);
            //播放当前第一首音乐
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.PLAY_LIST_FIRST));
            getViewDataBinding().tvNewListSize.setText("("+ playList.size() + ")");
            getViewDataBinding().tvDetailNewListSize.setText("("+ playList.size() + ")");
        }
    }

    /** 点击展示授权悬浮窗显示角色系统 */
    public void showCharacterAuth(final String characterName) {
        //判断是否已经授权显示悬浮窗
        if(SystemUtil.getInstance().isCanDrawOverlays(this)) {
            if(SystemUtil.getInstance().isServiceWorked(this, CharacterService.class.getPackage().getName()
                    + "." + CharacterService.class.getSimpleName())) {

                stopService(intentCharacterService);
                PadMainVM.stopHandler();           //关闭角色并停止handler
                PadMainVM.stopTalkHandler();
                getViewDataBinding().ivCharacterStatus.setVisibility(View.GONE);

                if(!mCharacterName.equals(characterName)) {
                    showCharacterAuth(characterName);
                }
            } else {
                mCharacterName = characterName;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startCharacterService(mCharacterName);
                } else {
                    intentCharacterService.putExtra("CharacterName", mCharacterName);
                    startService(intentCharacterService);
                }
                getViewDataBinding().ivCharacterStatus.setVisibility(View.VISIBLE);
            }
        } else {
            DialogDefaultBinding defaultBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                    R.layout.dialog_default, null, false);

            defaultBinding.dialogSelectTitle.setText("开启悬浮窗权限以展示角色系统");

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
                    mCharacterName = characterName;
                    intentActivityResultLauncher.launch(intent);
                }
            });

            mAlertDialog = new AlertDialog.Builder(this)
                    .setView(defaultBinding.getRoot())
                    .create();
            Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_3);
            mAlertDialog.show();
            mAlertDialog.getWindow().setLayout((int)(SystemUtil.getInstance().getDM(this).widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
        }

    }

    /** 点击播放按钮 */
    public void padPlayButtonClick(View view) {
        binder.pause(this, MusicPlayService.currentMusicName, MusicPlayService.currentMusicSinger, MusicPlayService.currentMusicBitmap);
    }

    /** 添加当前点击的歌曲 */
    private void addMusic(List<Music> list, int position) {
        playList.add(MainVM.setMusicMsg(list.get(position), false));

        if(playList.size()==1) {
            playList.get(0).isPlaying = true;
            binder.showLyric(playList.get(0), (playMode == 2));
        } else {
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_ADD_MUSIC));
        }
        playMusicListAdapter.notifyDataSetChanged();
        EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.SAVE_LOCAL_MUSIC_LIST, playList));
        //EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_FRESH_FAVORITE_MUSIC));
    }

    /** 点击切换播放模式 */
    public void playModeButtonClick(View view) {
        playMode = playMode!=2 ? playMode+1 : 0;
        SPUtil.setStrValue(getApplicationContext(), SPUtil.SavePlayMode, String.valueOf(playMode));
        changePlayMode(playMode);
    }

    /** 指定改变播放模式 */
    public void changePlayMode(int changePlayMode) {
        switch (changePlayMode) {
            case 0: //顺序播放
                binder.setSingePlayMode(false);
                getViewDataBinding().tvNewPlayMode.setText("顺序播放");
                getViewDataBinding().tvDetailNewPlayMode.setText("顺序播放");
                //变更主题
                //ThemeHelper.getInstance().sequentialPlayButtonTheme(rThemeId, getViewDataBinding(), 0);
                getViewDataBinding().ivNewPlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                getViewDataBinding().ivDetailNewPlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                getViewDataBinding().ivChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                getViewDataBinding().ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                break;
            case 1: //随机播放
                binder.setSingePlayMode(false);
                getViewDataBinding().tvNewPlayMode.setText("随机播放");
                getViewDataBinding().tvDetailNewPlayMode.setText("随机播放");
                //变更主题
                //ThemeHelper.getInstance().sequentialPlayButtonTheme(rThemeId, getViewDataBinding(), 1);
                getViewDataBinding().ivNewPlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                getViewDataBinding().ivDetailNewPlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                getViewDataBinding().ivChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                getViewDataBinding().ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                break;
            case 2: //单曲循环
                binder.setSingePlayMode(true);
                getViewDataBinding().tvNewPlayMode.setText("单曲循环");
                getViewDataBinding().tvDetailNewPlayMode.setText("单曲循环");
                //变更主题
                //ThemeHelper.getInstance().sequentialPlayButtonTheme(rThemeId, getViewDataBinding(), 2);
                getViewDataBinding().ivNewPlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                getViewDataBinding().ivDetailNewPlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                getViewDataBinding().ivChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                getViewDataBinding().ivNewChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                break;
        }
    }

    /** 播放上一首或下一首歌曲 */
    public void lastOrNextMusic(final boolean isNext) {
        if (playList.size() > 0) {
            if (playList.size() == 1) {
                binder.showLyric(playList.get(0), (playMode == 2));
            } else {

                Optional<Music> currentMusicPlayList = playList.stream()
                        .filter(playMusic -> playMusic.isPlaying)
                        .findFirst();

                if (currentMusicPlayList.isPresent()) {
                    Music currentMusic = currentMusicPlayList.get();
                    int index = playList.indexOf(currentMusic);
                    currentMusic.isPlaying = false;

                    if (playMode == 1) {
                        int rand = new Random().nextInt(playList.size());
                        while (index == rand) {
                            rand = new Random().nextInt(playList.size());
                        }
                        binder.showLyric(playList.get(rand), false);
                        playList.get(rand).isPlaying = true;

                    } else if (playMode == 2) {
                        binder.showLyric(currentMusic, true);
                    } else {
                        if (isNext) {
                            int nextIndex = (index + 1) % playList.size();
                            binder.showLyric(playList.get(nextIndex), false);
                            playList.get(nextIndex).isPlaying = true;
                        } else {
                            int prevIndex = (index == 0) ? playList.size() - 1 : index - 1;
                            binder.showLyric(playList.get(prevIndex), false);
                            playList.get(prevIndex).isPlaying = true;
                        }
                    }

                    playMusicListAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    /** 点击删除当前歌单列表所有歌曲 */
    public void deletePlayListAll(View view) {
        DialogDefaultBinding defaultBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_default, null, false);

        //取消
        defaultBinding.btSelectIconCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        //删除
        defaultBinding.btSelectIconCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playList.clear();
                playMusicListAdapter.notifyDataSetChanged();
                SPUtil.setListValue(getApplicationContext(), SPUtil.PlayListData, playList);
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DELETE_MUSIC));
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog = new AlertDialog.Builder(this)
                .setView(defaultBinding.getRoot())
                .create();
        Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_3);
        mAlertDialog.show();
        mAlertDialog.getWindow().setLayout((int)(SystemUtil.getInstance().getDM(this).widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    /** 存储列表时默认所有歌单为未播放状态 */
    private void setPlayListDefault(List<Music> playList) {
        playList.forEach(music -> music.isPlaying = false );
    }

    /** 清理缓存 */
    @SuppressLint("SetTextI18n")
    private void cleanCache() {
        DialogDefaultBinding defaultBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_default, null, false);

        String cacheAllSize = CacheUtil.getTotalCacheSize(this);

        defaultBinding.dialogSelectTitle.setText("当前" + cacheAllSize + ", 是否清理缓存？");
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
                //getViewDataBinding().tvCacheValue.setText(cache);
            }
        });

        mAlertDialog = new AlertDialog.Builder(this)
                .setView(defaultBinding.getRoot())
                .create();
        Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_3);
        mAlertDialog.show();
        mAlertDialog.getWindow().setLayout((int)(SystemUtil.getInstance().getDM(this).widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);

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

    /** 将当前界面设置为Task中第一个Activity启动 */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }


    private static class PadMainFragmentStateAdapter extends FragmentStateAdapter {

        private List<Fragment> fragmentList;

        public PadMainFragmentStateAdapter(FragmentActivity fragmentActivity, List<Fragment> fragmentList) {
            super(fragmentActivity);
            this.fragmentList = fragmentList;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment = fragmentList.get(position);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return fragmentList.size();
        }

    }

    public static class PlayMusicListViewHolder extends RecyclerView.ViewHolder {
        public PlayMusicListViewHolder(@NonNull View itemView) { super(itemView); }
    }
    /** 播放列表Adapter */
    public class PlayMusicListAdapter extends RecyclerView.Adapter<PlayMusicListViewHolder>
            implements ItemTouchHelperAdapter {

        private Context context;
        private List<Music> list;

        public PlayMusicListAdapter(Context context, List<Music> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public PlayMusicListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemPlayListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                    R.layout.item_play_list, parent, false);
            return new PlayMusicListViewHolder(binding.getRoot());
        }

        @Override
        public void onBindViewHolder(@NonNull PlayMusicListViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            ItemPlayListBinding binding = DataBindingUtil.getBinding(holder.itemView);
            if(binding != null) {

                binding.tvOrderNum.setVisibility(list.get(position).isPlaying? GONE : View.VISIBLE);

                int num = position+1;
                binding.tvOrderNum.setText(num<10? "0"+num : ""+num);
                binding.tvMusicName.setText(list.get(position).musicName);
                binding.tvSingerName.setText(list.get(position).musicSinger);

                //展示各团Logo
                binding.ivShowLogo.setVisibility(list.get(position).isLocal? View.GONE : View.VISIBLE);
                binding.ivShowLogo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                if(MysqlHelper.MUSIC_TYPE_LIELLA.equals(list.get(position).musicType)) {
                    binding.ivShowLogo.setImageResource(R.mipmap.ic_album_liella_3);
                } else if (MysqlHelper.MUSIC_TYPE_LIYUU.equals(list.get(position).musicType)) {
                    binding.ivShowLogo.setImageResource(R.mipmap.ic_album_liyuu);
                } else if (MysqlHelper.MUSIC_TYPE_SUNNYPASSION.equals(list.get(position).musicType)) {
                    binding.ivShowLogo.setImageResource(R.mipmap.ic_album_sunny_passion);
                    binding.ivShowLogo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                } else if (MysqlHelper.MUSIC_TYPE_NIJIGASAKI.equals(list.get(position).musicType)) {
                    binding.ivShowLogo.setImageResource(R.mipmap.ic_album_nijigasaki_3);
                } else if (MysqlHelper.MUSIC_TYPE_AQOURS.equals(list.get(position).musicType)) {
                    binding.ivShowLogo.setImageResource(R.mipmap.ic_album_aqours_3);
                } else if (MysqlHelper.MUSIC_TYPE_US.equals(list.get(position).musicType)) {
                    binding.ivShowLogo.setImageResource(R.mipmap.ic_album_us_3);
                } else if (MysqlHelper.MUSIC_TYPE_HASUNOSORA.equals(list.get(position).musicType)) {
                    binding.ivShowLogo.setImageResource(R.mipmap.ic_album_hasu_2);
                }

                binding.llFavorite.setVisibility(list.get(position).musicFavorite == 1? View.VISIBLE : View.GONE);

                //变更主题
                //ThemeHelper.getInstance().playListTheme(context, rThemeId, binding, list.get(position).isPlaying);
                binding.tvOrderNum.setTextColor(list.get(position).isPlaying? context.getResources().getColor(R.color.light_f9) :  context.getResources().getColor(R.color.black));
                binding.tvMusicName.setTextColor(list.get(position).isPlaying?  context.getResources().getColor(R.color.light_f9) :  context.getResources().getColor(R.color.black));
                binding.tvSingerName.setTextColor(list.get(position).isPlaying?  context.getResources().getColor(R.color.light_f9) :  context.getResources().getColor(R.color.black));
                binding.ivDelete.setBackgroundResource(R.drawable.selector_delete_selected_2);
                binding.ivMusicRail.setVisibility(list.get(position).isPlaying? View.VISIBLE : GONE);


                //点击播放列表的歌曲
                binding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!list.get(position).isPlaying) {
                            setPlayListDefault(list);
                            binder.showLyric(list.get(position), (playMode == 2));
                            list.get(position).isPlaying = true;
                            notifyDataSetChanged();
                        }
                    }
                });
                //当前播放的歌曲列表不显示删除按钮
                binding.llDelete.setVisibility(list.get(position).isPlaying? View.INVISIBLE : View.VISIBLE);
                binding.llDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!list.get(position).isPlaying) {
                            list.remove(position);
                            playMusicListAdapter.notifyDataSetChanged();
                            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DELETE_MUSIC));
                            SPUtil.setListValue(context, SPUtil.PlayListData, list);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            Collections.swap(list, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemDismiss(int position) {

        }

    }

    private interface ItemTouchHelperAdapter {
        void onItemMove(int fromPosition, int toPosition);

        void onItemDismiss(int position);
    }

    /** item触摸移动处理 */
    private class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

        private final ItemTouchHelperAdapter adapter;

        public ItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            return makeMovementFlags(dragFlags, 0);

        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            adapter.onItemDismiss(viewHolder.getAdapterPosition());
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                if (viewHolder instanceof PlayMusicListViewHolder) {
                    PlayMusicListViewHolder itemViewHolder = (PlayMusicListViewHolder) viewHolder;
                    itemViewHolder.itemView.setBackgroundColor(getResources().getColor(R.color.alpha_30));
                }
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            if (viewHolder instanceof MainActivity.PlayMusicListViewHolder) {
                PlayMusicListViewHolder itemViewHolder = (PlayMusicListViewHolder) viewHolder;
                itemViewHolder.itemView.setBackgroundColor(0);
            }
        }
    }

    /** 音乐播放条调整 */
    private class MusicBarChangerListener implements SeekBar.OnSeekBarChangeListener {

        @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //拖动时改变显示时间
            //getViewDataBinding().tvStartTime.setText(getViewModel().rebuildTime(progress));
            getViewDataBinding().tvNewStartTime.setText(TimeUtil.rebuildTime(progress));
            //Log.i(TAG, "progress: " + progress);
            //lyricScrollView.setMusicPlayerPos(progress);
            lyricNewScrollDetailView.setMusicPlayerPos(progress);
            lyricNewScrollView.setMusicPlayerPos(progress);
            //getViewDataBinding().hpvProgress.setCurrentCount(progress);
            //getViewDataBinding().pbNewProgress.setProgress(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //lyricScrollView.setIsRefreshDraw(true);
            lyricNewScrollDetailView.setIsRefreshDraw(true);
            lyricNewScrollView.setIsRefreshDraw(true);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //跳到拖动位置播放
            binder.seekTo(seekBar);
            //lyricScrollView.setIsRefreshDraw(false);
            lyricNewScrollDetailView.setIsRefreshDraw(false);
            lyricNewScrollView.setIsRefreshDraw(false);
        }
    }


    /** 拖动进度条调整 （解决拖动时与进度条正常播放移动 显示错乱）*/
    private class ProgressBarTouchListener implements SeekBar.OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i(TAG, "Click: down ");
                    isOnTouchSeekBar = true;
                    binder.posLock(true);
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i(TAG, "Click: up ");
                    isOnTouchSeekBar = false;
                    binder.posLock(false);
                    break;
            }
            return false;
        }
    }


    /** 屏蔽返回键 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if(keyCode == KeyEvent.KEYCODE_BACK) { //物理返回键

            if(isShowDetailMusicList) { //是否点击播放控制器的当前列表
                getViewDataBinding().rlDetailDisableClick.setVisibility(View.GONE);
                ObjectAnimator newCurrentMusicList = MyAnimationUtil.objectAnimatorUpOrDown(PadMainActivity.this, true, deviceMaxHeight, getViewDataBinding().clNewDetailCurrentMusicList);
                newCurrentMusicList.start();
                isShowDetailMusicList = false;
                return true;
            }

            if(isIntoMusicController) { //是否点击播放控制器
                getViewDataBinding().clMain.setVisibility(View.VISIBLE);
                ObjectAnimator animator = MyAnimationUtil.objectAnimatorUpOrDown(activity, true, deviceMaxHeight*2, getViewDataBinding().clController);
                animator.start();
                isIntoMusicController = false;
                return true;
            }

            if(isShowMusicList) { //当前播放列表
                getViewDataBinding().rlDisableClick.setVisibility(View.GONE);
                ObjectAnimator newCurrentMusicList = MyAnimationUtil.objectAnimatorLeftOrRight(PadMainActivity.this, false, false, getViewDataBinding().clCurrentMusicList);
                newCurrentMusicList.start();
                isShowMusicList = false;
                return true;
            }

            if(isIntoMusicDetail) { //进入明细后返回主页
                isIntoMusicDetail = false;
                getViewDataBinding().tvTittleName.setText("LoveLive"); //ps:后续更改
                FragmentManager fm = this.getSupportFragmentManager();
                if(fm.getBackStackEntryCount()>0) {
                    fm.popBackStack();
                }
                return true;
            }


        } else if (KeyEvent.KEYCODE_MEDIA_PLAY == keyCode&& keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            if(isFirstBluetoothControl){
                isFirstBluetoothControl = false;
                binder.clearMedia();
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.PLAY_LIST_FIRST));
            } else {
                binder.pause(this, MusicPlayService.currentMusicName, MusicPlayService.currentMusicSinger, MusicPlayService.currentMusicBitmap);
            }
        } else if (KeyEvent.KEYCODE_MEDIA_PAUSE == keyCode && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            if(isFirstBluetoothControl){
                isFirstBluetoothControl = false;
                binder.clearMedia();
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.PLAY_LIST_FIRST));
            } else {
                binder.pause(this, MusicPlayService.currentMusicName, MusicPlayService.currentMusicSinger, MusicPlayService.currentMusicBitmap);
            }
        } else if(KeyEvent.KEYCODE_MEDIA_NEXT == keyCode && KeyEvent.ACTION_DOWN == keyEvent.getAction()) {
            lastOrNextMusic(true);
        } else if(KeyEvent.KEYCODE_MEDIA_PREVIOUS == keyCode && KeyEvent.ACTION_DOWN == keyEvent.getAction()) {
            lastOrNextMusic(false);
        }

        return super.onKeyDown(keyCode, keyEvent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /** 绑定服务需要ServiceConnection对象 */
    private class ServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (MusicPlayService.MusicBinder) service;
            MediaControllerCompat mediaControllerCompat = new MediaControllerCompat(PadMainActivity.this, binder.getService().getSessionToken());
            MediaControllerCompat.setMediaController(PadMainActivity.this, mediaControllerCompat);
            binder.setMediaController(mediaControllerCompat);
            changePlayMode(playMode);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) { }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode) {
            case REQUEST_CODE_DOWNLOAD_APP_2:
                if(!PermissionUtil.getInstance().checkPermission(this, permissions)) {
                    if(!"".equals(downloadAppUrl)){
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP2, downloadAppUrl));
                    }
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
