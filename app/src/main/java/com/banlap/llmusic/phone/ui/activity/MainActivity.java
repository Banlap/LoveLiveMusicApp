package com.banlap.llmusic.phone.ui.activity;

import static android.view.View.GONE;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseActivity;
import com.banlap.llmusic.base.BaseApplication;
import com.banlap.llmusic.databinding.ActivityMainBinding;
import com.banlap.llmusic.databinding.DialogAddMusicToLocalListBinding;
import com.banlap.llmusic.databinding.DialogChangeModeMenuBinding;
import com.banlap.llmusic.databinding.DialogCharacterMenuBinding;
import com.banlap.llmusic.databinding.DialogDefaultBinding;
import com.banlap.llmusic.databinding.DialogDownloadBinding;
import com.banlap.llmusic.databinding.DialogLocalFileBinding;
import com.banlap.llmusic.databinding.DialogMainMenuBinding;
import com.banlap.llmusic.databinding.DialogMessageBinding;
import com.banlap.llmusic.databinding.DialogMoreMenuBinding;
import com.banlap.llmusic.databinding.DialogSortMenuBinding;
import com.banlap.llmusic.databinding.DialogTimePickerBinding;
import com.banlap.llmusic.databinding.DialogTimeTasksBinding;
import com.banlap.llmusic.databinding.ItemAddMusicLocalListBinding;
import com.banlap.llmusic.databinding.ItemMusicListBinding;
import com.banlap.llmusic.databinding.ItemPlayListBinding;
import com.banlap.llmusic.fixed.AppMusic;
import com.banlap.llmusic.model.LocalPlayList;
import com.banlap.llmusic.model.Message;
import com.banlap.llmusic.model.Music;
import com.banlap.llmusic.model.MusicLyric;
import com.banlap.llmusic.model.Version;
import com.banlap.llmusic.phone.uivm.fvm.LocalListFVM;
import com.banlap.llmusic.phone.uivm.fvm.MainListFVM;
import com.banlap.llmusic.receiver.DownloadReceiver;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.service.CharacterService;
import com.banlap.llmusic.service.LyricService;
import com.banlap.llmusic.service.MusicPlayService;
import com.banlap.llmusic.sql.MysqlHelper;
import com.banlap.llmusic.phone.ui.ThemeHelper;
import com.banlap.llmusic.phone.ui.fragment.LocalListFragment;
import com.banlap.llmusic.phone.ui.fragment.MainListFragment;
import com.banlap.llmusic.phone.uivm.vm.MainVM;
import com.banlap.llmusic.sql.AppData;
import com.banlap.llmusic.sql.room.Converters;
import com.banlap.llmusic.sql.room.RoomCustomPlay;
import com.banlap.llmusic.sql.room.RoomFavoriteMusic;
import com.banlap.llmusic.sql.room.RoomPlayMusic;
import com.banlap.llmusic.sql.room.RoomRecommendMusic;
import com.banlap.llmusic.utils.AppExecutors;
import com.banlap.llmusic.utils.BitmapUtil;
import com.banlap.llmusic.utils.BluetoothUtil;
import com.banlap.llmusic.utils.CharacterHelper;
import com.banlap.llmusic.utils.CountDownHelper;
import com.banlap.llmusic.utils.DownloadHelper;
import com.banlap.llmusic.utils.FileUtil;
import com.banlap.llmusic.utils.MyAnimationUtil;
import com.banlap.llmusic.utils.NotificationHelper;
import com.banlap.llmusic.utils.PermissionUtil;
import com.banlap.llmusic.utils.PxUtil;
import com.banlap.llmusic.utils.SPUtil;
import com.banlap.llmusic.utils.SystemUtil;
import com.banlap.llmusic.utils.TimeUtil;
import com.banlap.llmusic.widget.LyricScrollView;
import com.banlap.llmusic.utils.RecyclerViewUtils;
import com.banlap.llmusic.widget.QuarterCircleProgressBar;
import com.banlap.llmusic.widget.SingleLyricScrollView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import es.dmoral.toasty.Toasty;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 主页界面
 * */
public class MainActivity extends BaseActivity<MainVM, ActivityMainBinding> implements MainVM.MainCallBack {
    private final String TAG = MainActivity.class.getSimpleName();
    private final Context context = MainActivity.this;
    private List<RoomPlayMusic> roomOnlineMusicList;       //按类型的线上歌曲
    private List<RoomPlayMusic> roomTempMusicList;         //临时音乐列表
    private List<RoomPlayMusic> roomPlayMusicList;         //当前播放的列表
    private List<RoomFavoriteMusic> roomFavoriteMusicList; //收藏夹歌单数据
    private List<RoomCustomPlay> roomCustomPlayList;       //自建歌单数据列表
    public static List<MusicLyric> musicLyricList;       //当前播放歌曲的歌词列表
    private List<Message> messageList;                  //消息列表
    private List<Version> versionList;                  //版本列
    private MusicListAdapter musicListAdapter;          //音乐列表适配器
    private PlayMusicListAdapter playMusicListAdapter;  //播放列表适配器
    private AddMusicCustomListAdapter addMusicCustomListAdapter; //点击添加到自建列表 适配器
    private ObjectAnimator objectAnimator;              //动画效果
    public static MusicPlayService.MusicBinder binder;        //用于绑定服务
    private ServiceConn conn;                           //用于绑定服务
    private Intent intentService;                       //音乐服务
    public static Intent intentCharacterService;              //角色服务
    public static Intent intentLyricService;            //歌词服务
    private boolean isSelect = false;                   //查询一次数据
    private boolean isClick = false;                    //判断是否点击按钮
    private boolean isNotMain = false;                  //判断是否在主界面
    private boolean isSearchMusic = false;              //判断是否处于搜索音乐状态
    private boolean isClickNextOrLastLoading = false;   //判断是否点击了上一首或下一首歌

    private boolean isShowMusicPanel = false;           //判断是否显示音乐面板
    private boolean isShowMusicList = false;            //判断是否显示音乐清单
    private boolean isShowControllerModePanel = false;  //判断是否显示新版音乐控制模式
    private boolean isShowNewPlayController = false;    //判断是否显示新版音乐控制栏
    private boolean isShowNewMusicList = false;         //判断是否显新版音乐清单
    private boolean isShowMoreMenu = false;             //判断是否显更多菜单

    public static boolean isPlay = false;               //判断是否播放音乐
    public static boolean isOnTouchSeekBar = false;      //是否按着控制条

    public static boolean isFavorite = false;           //当前歌曲是否已收藏
    private AlertDialog mAlertDialog;                   //弹窗
    private PopupWindow mPopupWindow;                   //弹窗
    private int musicListSize = 0;                      //获取总播放列表数
    public static int playMode = 0;                     //播放模式: 0顺序播放 1随机播放 2单曲循环
    private final int panelMoveAxis = 750;              //面板移动值
    private int heightPixels = 0 ;                      //设备高度
    private int rThemeId =0;                            //当前主题
    /** 角色视图 */
    private String mCharacterName;                      //当前角色
    private ActivityResultLauncher<Intent> intentSettingsLauncher; //设置页面回调
    private ActivityResultLauncher<Intent> intentCharacterLauncher; //角色服务回调
    private ActivityResultLauncher<Intent> intentTakePhotoLauncher;  //照片选择页面回调
    private DialogDownloadBinding downloadBinding;
    private DialogLocalFileBinding dialogLocalFileBinding;
    private DialogTimeTasksBinding dialogTimeTasksBinding;
    private int clickLocalPlayListId = 0;                 //点击自建歌单数据列表的ID号
    private boolean isClickLocalPlayList = false;         //是否点击了自建歌单数据列表
    private boolean isClickLocalOrFavorite = false;       //是否点击了本地歌曲或收藏列表
    private boolean isExistNewVersion = false;            //是否存在新版本app
    private boolean isFinishAnimator = true;              //是否执行标题栏文本显示动画
    private boolean isChangeScrollRange = false;          //解决重复高度时刷新appBar的标题文字动画
    private boolean isCountDown = false;                  //倒计时是否完成
    private int selectCountDown;                          //选择的倒计时选项
    private int hour = 0, minute = 0, sec;
    private ObjectAnimator textAnimator;                  //标题栏文本动画
    private ObjectAnimator controllerModeAnimator;        //音乐氛围模式动画
    private ObjectAnimator musicControllerAnimator;       //音乐控制器动画
    private RequestOptions requestOptions;
    private boolean isUpSortByTime = false;               //是否向上排序：时间
    private boolean isUpSortByName = false;               //是否向上排序：歌曲名称
    private boolean isUpSortBySinger = false;             //是否向上排序：歌手名称
    private int clickSortType = 0;                        //当前点击的排序类型
    private int defaultLyricSize = 25;                        //默认歌词字体大小
    private int singleLyricSize = 25;                        //滚动行歌词字体大小
    private int detailLyricSize = 25;                        //明细歌词字体大小
    private int navigationBarHeight = 0;
    private LyricScrollView lyricScrollView;              //
    private LyricScrollView lyricNewScrollDetailView;              //
    private SingleLyricScrollView lyricNewScrollView;              //
    public static MediaSessionCompat mSession;                        //用于获取按键事件
    public boolean isFirstBluetoothControl = true;        //首次蓝牙控制标记 (场景：进入app后直接蓝牙或车机控制播放按钮 或 正在播放的时候蓝牙或车机控制播放按钮)
    private MainFragmentStateAdapter  mainFragmentStateAdapter;     //主页与本地页面切换Adapter
    public static final int REQUEST_CODE_NEED_RUNNING_PERMISSION = 100; //检查进入APP需要的权限
    public static final int REQUEST_CODE_DOWNLOAD_APP = 101;            //检查下载app时需要的权限
    public static final int REQUEST_CODE_SCAN_LOCAL_FILE = 102;         //检查扫描文件所需要的权限
    public static final int REQUEST_CODE_SELECT_LOCAL_FILE = 103;       //检查选择文件所需要的权限
    public static final int REQUEST_CODE_SELECT_IMG_FILE = 104;       //检查选择图片所需要的权限
    public static final int REQUEST_CODE_CONTROLLER_MODE = 105;       //检查进入氛围模式时所需要的权限

    private final PublishSubject<String> textChangeSubject = PublishSubject.create();

//    public LLMusicDatabase llMusicDatabase;

    @Override
    protected int getLayoutId() { return R.layout.activity_main; }

    /** 初始化主页数据 */
    @SuppressLint("CheckResult")
    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        roomOnlineMusicList = new ArrayList<>();
        musicLyricList = new ArrayList<>();
        messageList = new ArrayList<>();
        versionList = new ArrayList<>();
        roomCustomPlayList = new ArrayList<>();
        roomPlayMusicList = new ArrayList<>();
        roomFavoriteMusicList = new ArrayList<>();

        roomPlayMusicList.addAll(AppData.roomPlayMusicList);
        roomFavoriteMusicList.addAll(AppData.roomFavoriteMusicList);

        //最爱歌曲缓存列表
        RoomFavoriteMusic roomFavoriteMusic = new RoomFavoriteMusic();
        roomFavoriteMusicList.add(roomFavoriteMusic);
        roomFavoriteMusicList.add(roomFavoriteMusic);


        //播放列表使用图标显示收藏的歌曲
        setMusicFavorite(roomFavoriteMusicList);

        if(AppData.roomSettings != null) {
            if(!TextUtils.isEmpty(AppData.roomSettings.savePlayMode)) {
                try {
                    playMode = Integer.parseInt(AppData.roomSettings.savePlayMode);
                } catch (Exception e) {
                    Log.e(TAG, "savePlayMode转换失败, 使用默认参数");
                }
            }
        }


//        String isBGScene = SPUtil.getStrValue(MainActivity.this, SPUtil.isBGScene);
//        if(TextUtils.isEmpty(isBGScene)) {
//            SPUtil.setStrValue(MainActivity.this, SPUtil.isBGScene, "0");
//        }


        requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        //requestOptions.override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL); //关键代码，加载原始大小
        requestOptions.format(DecodeFormat.PREFER_RGB_565); //设置为这种格式去掉透明度通道，可以减少内存占有

        mCharacterName = CharacterHelper.CHARACTER_NAME_KEKE;

        //
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void initView() {
        getViewDataBinding().setVm(getViewModel());
        getViewModel().setCallBack(this);
        //权限检查
        initCheckPermission();
        //检查是否存在底部栏
        initCheckNavigationBar();
        //初始化主页内容
        initMainView();
        //获取主题id
        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_ROOM_GET_THEME_ID, MainActivity.class.getSimpleName(), AppData.roomSettings != null ? AppData.roomSettings.saveThemeId : ""));
        //连接数据库
        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.THREAD_CONNECT_MYSQL));
        //各种监听
        initListener();
        //开启所有相关服务
        startAllService();
        //广播监听蓝牙连接状态
        BluetoothUtil.getInstance().registerBluetoothReceiver(this);
        //初始化碎片
        initFragment();
    }

    /** 是否自动播放音乐 */
    private void autoMusic() {
        String isAutoMusic = getIntent().getStringExtra("IsAutoMusic");
        if(!TextUtils.isEmpty(isAutoMusic) && isAutoMusic.equals("1")) {
            binder.pause(this, MusicPlayService.currentRoomPlayMusic.musicName, MusicPlayService.currentRoomPlayMusic.musicSinger, MusicPlayService.currentRoomPlayMusic.musicImgBitmap);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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
            ActivityCompat.requestPermissions(MainActivity.this, permission, REQUEST_CODE_NEED_RUNNING_PERMISSION);
        }

    }

    /** 初始化主页内容 */
    @SuppressLint("SetTextI18n")
    private void initMainView(){
        getViewDataBinding().rlPlayController.setVisibility(View.VISIBLE);
        getViewDataBinding().rlNewPlayController.setVisibility(View.VISIBLE);
        getViewDataBinding().clMain.setVisibility(View.VISIBLE);
        getViewDataBinding().clAlbumDetail.setVisibility(View.VISIBLE);
        getViewDataBinding().clControllerMode.setVisibility(View.VISIBLE);
        getViewDataBinding().clNewCurrentMusicList.setVisibility(View.VISIBLE);
        getViewDataBinding().rlMoreSetDialog.setVisibility(View.VISIBLE);

        getViewDataBinding().pbLoadingMusic.setVisibility(View.INVISIBLE);
        getViewDataBinding().pbNewLoadingMusic.setVisibility(GONE);
        getViewDataBinding().pbNewLoadingMusic2.setVisibility(GONE);
        getViewDataBinding().tvVersion.setVisibility(GONE);

        //动画：初始化将详细页面移走
        ObjectAnimator detailPanelDefault = MyAnimationUtil.objectAnimatorLeftOrRight(this, false, false, getViewDataBinding().clAlbumDetail);
        detailPanelDefault.start();

        String controllerScene = "";
        if(AppData.roomSettings != null && !TextUtils.isEmpty(AppData.roomSettings.saveControllerScene)) {
            controllerScene = AppData.roomSettings.saveControllerScene;
        } else {
            controllerScene = SPUtil.SaveControllerSceneValue_NewScene;
        }

        //动画：初始化将新版音乐控制器移走
        ObjectAnimator newPlayController = MyAnimationUtil.objectAnimatorUpOrDown(MainActivity.this, true, PxUtil.getInstance().dp2px(185, this), getViewDataBinding().rlNewPlayController);
        //动画：初始化将新版播放列表移走
        ObjectAnimator newCurrentMusicList = MyAnimationUtil.objectAnimatorUpOrDown(MainActivity.this, true, PxUtil.getInstance().dp2px(325, this), getViewDataBinding().clNewCurrentMusicList);
        //动画：初始化将更多菜单移走
        ObjectAnimator newMoreMenu = MyAnimationUtil.objectAnimatorUpOrDown(MainActivity.this, true, PxUtil.getInstance().dp2px(500, this), getViewDataBinding().rlMoreSetDialog);

        //配置简约模式下控制器高度
        ViewGroup.MarginLayoutParams marginLayoutParams1 = (ViewGroup.MarginLayoutParams) getViewDataBinding().clCurrentMusicPanel.getLayoutParams();
        marginLayoutParams1.setMargins(15, 0, 15, 15 + navigationBarHeight);
        getViewDataBinding().clCurrentMusicPanel.setLayoutParams(marginLayoutParams1);

        ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) getViewDataBinding().clCurrentMusicList.getLayoutParams();
        marginLayoutParams2.setMargins(15, 0, 15, 15 + navigationBarHeight);
        getViewDataBinding().clCurrentMusicList.setLayoutParams(marginLayoutParams2);

        //配置默认模式下控制器位置
        ViewGroup.MarginLayoutParams marginLayoutParams3 = (ViewGroup.MarginLayoutParams) getViewDataBinding().rlNewPlayController.getLayoutParams();
        marginLayoutParams3.setMargins(15, 0, 15, 15 + navigationBarHeight);
        getViewDataBinding().rlNewPlayController.setLayoutParams(marginLayoutParams3);

        if(!TextUtils.isEmpty(controllerScene)) {
            if(controllerScene.equals(SPUtil.SaveControllerSceneValue_NewScene)) {
                //隐藏默认音乐控制界面
                musicControllerAnimator = MyAnimationUtil.objectAnimatorUpOrDown(this, true, PxUtil.getInstance().dp2px(300, this), getViewDataBinding().rlPlayController);
                musicControllerAnimator.start();
                //有场景值时显示新版播放列表
                newPlayController = MyAnimationUtil.objectAnimatorUpOrDown(MainActivity.this, false, PxUtil.getInstance().dp2px(85, this), getViewDataBinding().rlNewPlayController);
                getViewDataBinding().clFloatingController.setVisibility(View.GONE);
            } else if(controllerScene.equals(SPUtil.SaveControllerSceneValue_FloatingScene)) {
                //隐藏默认音乐控制界面
                musicControllerAnimator = MyAnimationUtil.objectAnimatorUpOrDown(this, true, PxUtil.getInstance().dp2px(300, this), getViewDataBinding().rlPlayController);
                musicControllerAnimator.start();

                newPlayController = MyAnimationUtil.objectAnimatorUpOrDown(MainActivity.this, true, PxUtil.getInstance().dp2px(85, this), getViewDataBinding().rlNewPlayController);
                getViewDataBinding().clFloatingController.setVisibility(View.VISIBLE);
            }
        }

        newPlayController.start();
        newCurrentMusicList.start();
        newMoreMenu.start();

        if(AppData.roomSettings != null) {
            if(AppData.roomSettings.isBGScene.equals("1")) {
                getViewDataBinding().clBgMode.setVisibility(View.VISIBLE);
                getViewDataBinding().clBgMode.setOnClickListener(new ButtonClickListener());
            }
        }

        initDeviceMate();
        //获取设备高度
        DisplayMetrics dm = SystemUtil.getInstance().getDM(this);
        heightPixels = (dm != null)? PxUtil.getInstance().dp2px(dm.heightPixels, this) : 750;
        //
        controllerModeAnimator = MyAnimationUtil.objectAnimatorUpOrDown(this, true, heightPixels, getViewDataBinding().clControllerMode);
        controllerModeAnimator.start();

        textAnimator = MyAnimationUtil.objectAnimatorShowOrHide(MainActivity.this,  0, 0, getViewDataBinding().tvTitleBar);
        textAnimator.start();

        getViewDataBinding().llShowNormalBar.setVisibility(View.VISIBLE);
        getViewDataBinding().llShowSearchBar.setVisibility(GONE);

        musicListAdapter = new MusicListAdapter(this, roomOnlineMusicList);
        getViewDataBinding().rvMusicList.setLayoutManager(new LinearLayoutManager(this));
        getViewDataBinding().rvMusicList.setAdapter(musicListAdapter);

        //当滑动列表时停止加载图片资源，不滑动时继续加载图片资源
        RecyclerViewUtils.scrollSuspend(this, getViewDataBinding().rvMusicList);
        //加大RecyclerView 的缓存，用空间换时间，来提高滚动的流畅性
        RecyclerViewUtils.setViewCache(getViewDataBinding().rvMusicList);

        musicListAdapter.notifyDataSetChanged();

        playMusicListAdapter = new PlayMusicListAdapter(this, roomPlayMusicList);

        getViewDataBinding().rvPlayList.setLayoutManager(new LinearLayoutManager(this));
        getViewDataBinding().rvPlayList.setAdapter(playMusicListAdapter);

        //长按移动列表其中一个item
        setItemTouchHelper(playMusicListAdapter);
        //

        getViewDataBinding().rvNewPlayList.setLayoutManager(new LinearLayoutManager(this));
        getViewDataBinding().rvNewPlayList.setAdapter(playMusicListAdapter);
        playMusicListAdapter.notifyDataSetChanged();

        getViewDataBinding().tvListSize.setText("("+ roomPlayMusicList.size() + ")");
        getViewDataBinding().tvNewListSize.setText("("+ roomPlayMusicList.size() + ")");

        //歌词滚动
        lyricScrollView = getViewDataBinding().lvShowLyric;
        lyricNewScrollDetailView = getViewDataBinding().lvNewShowLyricDetail;
        lyricNewScrollView = getViewDataBinding().lvNewShowLyric;


    }

    /** 刷新底部控制栏高度 */
    private void updateController() {
        //配置简约模式下控制器高度
        ViewGroup.MarginLayoutParams marginLayoutParams1 = (ViewGroup.MarginLayoutParams) getViewDataBinding().clCurrentMusicPanel.getLayoutParams();
        marginLayoutParams1.setMargins(15, 0, 15, 15 + navigationBarHeight);
        getViewDataBinding().clCurrentMusicPanel.setLayoutParams(marginLayoutParams1);

        ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) getViewDataBinding().clCurrentMusicList.getLayoutParams();
        marginLayoutParams2.setMargins(15, 0, 15, 15 + navigationBarHeight);
        getViewDataBinding().clCurrentMusicList.setLayoutParams(marginLayoutParams2);

        //配置默认模式下控制器位置
        ViewGroup.MarginLayoutParams marginLayoutParams3 = (ViewGroup.MarginLayoutParams) getViewDataBinding().rlNewPlayController.getLayoutParams();
        marginLayoutParams3.setMargins(15, 0, 15, 15 + navigationBarHeight);
        getViewDataBinding().rlNewPlayController.setLayoutParams(marginLayoutParams3);
    }

    /** 设置播放列表长按移动item选项 */
    private void setItemTouchHelper(PlayMusicListAdapter playMusicListAdapter) {
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(playMusicListAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(getViewDataBinding().rvPlayList);

        ItemTouchHelper.Callback touchNewCallback = new ItemTouchHelperCallback(playMusicListAdapter);
        ItemTouchHelper touchNewHelper = new ItemTouchHelper(touchNewCallback);
        touchNewHelper.attachToRecyclerView(getViewDataBinding().rvNewPlayList);
    }

    /** 适配机型ui */
    private void initDeviceMate() {
        if(SystemUtil.getInstance().isSmallScaleDevice()) {
            // 设置宽度和高度
            int widthInDp = 120;  // 宽度（以dp为单位）
            int heightInDp = 120; // 高度（以dp为单位）

            // 将dp单位转换为像素
            int widthInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp, getResources().getDisplayMetrics());
            int heightInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics());

            // 创建LayoutParams并设置宽度和高度
            ViewGroup.LayoutParams layoutParams = getViewDataBinding().civMusicImg.getLayoutParams();
            layoutParams.width = widthInPx;
            layoutParams.height = heightInPx;
            getViewDataBinding().civMusicImg.setLayoutParams(layoutParams);
        }
    }

    /** 初始化碎片 */
    private void initFragment() {

        if(null == mainFragmentStateAdapter) {
            List<Fragment> fragmentList = new ArrayList<>();
            fragmentList.add(new MainListFragment());
            fragmentList.add(new LocalListFragment());
            mainFragmentStateAdapter = new MainFragmentStateAdapter(this, fragmentList);
        }

        getViewDataBinding().vp2Main.setOffscreenPageLimit(2);
        getViewDataBinding().vp2Main.setAdapter(mainFragmentStateAdapter);

        getViewDataBinding().vp2Main.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(0 == position) {
                    AnimatorSet animatorSet = MyAnimationUtil.animatorSetEnlarge(getViewDataBinding().tvDiscover, 1, (float) 1.3);
                    AnimatorSet animatorSet2 = MyAnimationUtil.animatorSetEnlarge(getViewDataBinding().tvLocal, (float) 1.3, 1);
                    animatorSet.start();
                    animatorSet2.start();

                    AnimatorSet animatorSetMove = MyAnimationUtil.animatorSetMove(getViewDataBinding().vLine, false);
                    animatorSetMove.start();

                } else if(1 == position) {
                    AnimatorSet animatorSet = MyAnimationUtil.animatorSetEnlarge(getViewDataBinding().tvDiscover,  (float) 1.3, 1);
                    AnimatorSet animatorSet2 = MyAnimationUtil.animatorSetEnlarge(getViewDataBinding().tvLocal, 1, (float) 1.3);
                    animatorSet.start();
                    animatorSet2.start();

                    AnimatorSet animatorSetMove = MyAnimationUtil.animatorSetMove(getViewDataBinding().vLine, true);
                    animatorSetMove.start();
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                //Log.i("PageScrolled", "position: " + position + " positionOffset: " + positionOffset + " positionOffsetPixels: " + positionOffsetPixels);
            }
        });


        View view = getViewDataBinding().vp2Main.getChildAt(0);
        if(view instanceof RecyclerView) {
            view.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        getViewDataBinding().tvDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getViewDataBinding().vp2Main.setCurrentItem(0);
            }
        });

        getViewDataBinding().tvLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getViewDataBinding().vp2Main.setCurrentItem(1);
            }
        });

    }

    /**
     * 判断是否存在底部栏，存在则调整高度
     * */
    private void initCheckNavigationBar() {
        SystemUtil.getInstance().hasNavigationBar(this, new SystemUtil.NavigationBarCallback() {
            @Override
            public void onResult(boolean isShow, int height) {
                if(isShow) { //存在底部导航栏则调整部分ui高度
                    navigationBarHeight = height;
                    Log.i(TAG, "navigationBarHeight: " + navigationBarHeight);
                    //更新UI高度
                    updateController();
                }
            }
        });
    }

    private Disposable textChangeDisposable;
    FlowableEmitter<String> mEmitter;

    /** 初始化所有功能监听 */
    @SuppressLint("NotifyDataSetChanged")
    private void initListener() {
        getViewDataBinding().sbMusicBar.setOnSeekBarChangeListener(new MusicBarChangerListener());
        getViewDataBinding().sbNewMusicBar.setOnSeekBarChangeListener(new MusicBarChangerListener());
        getViewDataBinding().sbMusicBar.setOnTouchListener(new ProgressBarTouchListener());
        getViewDataBinding().sbNewMusicBar.setOnTouchListener(new ProgressBarTouchListener());
        getViewDataBinding().rlPlayController.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().rlShowLoading.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().rlDisableClick.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().rlDisableClick3.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().rlDisableClick4.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llMoreSetMusicInfo.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llAllPlay.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llSearch.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llSort.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llCancel.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().clControllerMode.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().flCloseControllerMode.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().flNewChangeMode.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().rlNewPlayController.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().ivNewMyFavorite.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().lvNewShowLyric.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().lvNewShowLyricDetail.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().lvNewShowLyricDetail.setIsUseLyricDetailColor(true);

        //更多菜单
        getViewDataBinding().llSystemSet.setOnClickListener(new ButtonClickListener());

        //歌词大小设置监听
        getViewDataBinding().sbLyricSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int size = 25 + (i * 5);
                if(SystemUtil.getInstance().isSmallScaleDevice()) {
                    size = 15 + (i * 5);
                }
                getViewDataBinding().lvShowLyric.setLyricSize(size);
                defaultLyricSize = size;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                getViewDataBinding().lvShowLyric.setIsResetLyricSize(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getViewDataBinding().lvShowLyric.setIsResetLyricSize(false);
                SPUtil.setStrValue(MainActivity.this, SPUtil.DefaultLyricSizeData, "" + defaultLyricSize);
            }
        });
        getViewDataBinding().sbSingleLyricSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int size = 25 + (i * 5);
                if(SystemUtil.getInstance().isSmallScaleDevice()) {
                    size = 15 + (i * 5);
                }
                getViewDataBinding().lvNewShowLyric.setLyricSize(size);
                singleLyricSize = size;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                getViewDataBinding().lvNewShowLyric.setIsResetLyricSize(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getViewDataBinding().lvNewShowLyric.setIsResetLyricSize(false);
                SPUtil.setStrValue(MainActivity.this, SPUtil.SingleLyricSizeData, "" + singleLyricSize);
            }
        });
        getViewDataBinding().sbDetailLyricSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int size = 25 + (i * 5);
                if(SystemUtil.getInstance().isSmallScaleDevice()) {
                    size = 15 + (i * 5);
                }
                getViewDataBinding().lvNewShowLyricDetail.setLyricSize(size);
                detailLyricSize = size;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                getViewDataBinding().lvNewShowLyricDetail.setIsResetLyricSize(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getViewDataBinding().lvNewShowLyricDetail.setIsResetLyricSize(false);
                SPUtil.setStrValue(MainActivity.this, SPUtil.DetailLyricSizeData, "" + detailLyricSize);
            }
        });
        //
        getViewDataBinding().lvNewShowLyricDetail.setOnLyricClickListener(new LyricScrollView.LyricClickListener() {
            @Override
            public void onSingleTap() {
                getViewModel().toggleClickNewSingleLyricView(false);
            }
        });
        getViewDataBinding().llBack.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().ablAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //Toast.makeText(MainActivity.this, "vo: " + verticalOffset, Toast.LENGTH_SHORT).show();
                if(Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    if(isChangeScrollRange) {
                        textAnimator = MyAnimationUtil.objectAnimatorShowOrHide(MainActivity.this,  0, 1, getViewDataBinding().tvTitleBar);
                        textAnimator.start();
                        isFinishAnimator = false;
                        isChangeScrollRange = false;
                    }
                } else {
                    isChangeScrollRange = true;
                    if(!isFinishAnimator) {
                        textAnimator = MyAnimationUtil.objectAnimatorShowOrHide(MainActivity.this, 1, 0, getViewDataBinding().tvTitleBar);
                        isFinishAnimator = true;
                        textAnimator.start();
                    }
                }
            }
        });

       // initSearch();
        getViewDataBinding().etSearchMusic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChangeSubject.onNext(s.toString());
                //mEmitter.onNext(getViewDataBinding().etSearchMusic.getText().toString());
                String name = getViewDataBinding().etSearchMusic.getText().toString();
                if (!name.equals("")) {

                    List<RoomPlayMusic> nullData = roomTempMusicList.stream()
                            .filter(tempMusic -> tempMusic.musicType.equals(" "))
                            .collect(Collectors.toList());

                    List<RoomPlayMusic> searchList = roomTempMusicList.stream()
                            .filter(tempMusic -> tempMusic.musicName.toLowerCase().contains(name.toLowerCase()))
                            .collect(Collectors.toList());

                    if (searchList.size() > 0) {
                        roomOnlineMusicList.clear();
                        roomOnlineMusicList.addAll(searchList);
                        roomOnlineMusicList.addAll(nullData);
                        musicListAdapter.notifyDataSetChanged();
                        getViewDataBinding().tvNoSearchMusic.setVisibility(View.GONE);
                    } else {
                        roomOnlineMusicList.clear();
                        musicListAdapter.notifyDataSetChanged();
                        getViewDataBinding().tvNoSearchMusic.setVisibility(View.VISIBLE);
                    }
                } else {
                    roomOnlineMusicList.clear();
                    if(roomTempMusicList != null) {
                        roomOnlineMusicList.addAll(roomTempMusicList);
                    }
                    musicListAdapter.notifyDataSetChanged();
                    getViewDataBinding().tvNoSearchMusic.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        intentCharacterLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
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
                });

        intentTakePhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try {
                        Intent intent = result.getData();
                        Uri uri = intent.getData();

                        if(uri != null) {
                            try {
                                final String id = DocumentsContract.getDocumentId(uri); //获取文件id
                                if(id.contains(":")) {
                                    String[] parts = id.split(":");
                                    long mediaId = Long.parseLong(parts[1]);
                                    String mediaIdStr = String.valueOf(mediaId);
                                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            AppData.saveRoomSettings(settings -> settings.backgroundUri = mediaIdStr);
                                        }
                                    });
                                } else {
                                    Log.e(TAG, "intentTakePhotoLauncher 设置失败: ");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "intentTakePhotoLauncher error: " + e.getMessage());
                            }
                        }

                        Glide.with(getApplication())
                                .setDefaultRequestOptions(requestOptions)
                                .load(uri)
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                .into(getViewDataBinding().ivBg);
                        Toasty.success(context, "设置成功", Toast.LENGTH_SHORT, true).show();
                    } catch (Exception e) {
                        Log.i(TAG, "e: " + e.getMessage());
                    }
                });


        intentSettingsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    //设置页面返回时重新获取主题参数
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_ROOM_GET_THEME_ID, MainActivity.class.getSimpleName(), AppData.roomSettings != null ? AppData.roomSettings.saveThemeId : ""));
                }
        );

        getViewDataBinding().qcpProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getViewModel().toggleShowControllerNewProgress();
            }
        });

        getViewDataBinding().qcpProgress.setOnFloatingPlayerButtonClickListener(new QuarterCircleProgressBar.OnFloatingPlayerButtonClickListener() {
            @Override
            public void onFloatingPlayerButtonClickListener() {
                Log.i(TAG, "点击了悬浮播放按钮");
                if(isDoubleClick()) { return; }
                if (binder !=null) {
                    binder.pause(MainActivity.this, MusicPlayService.currentRoomPlayMusic.musicName, MusicPlayService.currentRoomPlayMusic.musicSinger, MusicPlayService.currentRoomPlayMusic.musicImgBitmap);
                }
            }
        });

        getViewDataBinding().qcpProgress.setOnFloatingSettingButtonClickListener(new QuarterCircleProgressBar.OnFloatingSettingButtonClickListener() {
            @Override
            public void onFloatingSettingButtonClickListener() {
                Log.i(TAG, "点击了悬浮设置按钮");
                if(isDoubleClick()) { return; }
                new Handler().postDelayed(() -> runOnUiThread(MainActivity.this::intoSettings), 200);
            }
        });

        //观察点击
        getViewModel().getMlIsShowControllerNewProgress().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isShow) {
                getViewDataBinding().qcpProgress.showProgress(isShow);
            }
        });

        getViewModel().getMlIsClickNewSingleLyricView().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isClick) {
                getViewDataBinding().llNewShowLyricDetail.setVisibility(isClick? View.VISIBLE : View.GONE);
                getViewDataBinding().llNewShowLyric.setVisibility(isClick? View.GONE : View.VISIBLE);
            }
        });

        MusicPlayService.getMlCurrentMusicDetail().observe(this, new Observer<Music>() {
            @Override
            public void onChanged(Music music) {

            }
        });
    }

    /** 开启所有相关服务 */
    private void startAllService() {
        conn = new ServiceConn();
        intentService = new Intent(this, MusicPlayService.class);           //创建音乐播放服务
        intentCharacterService = new Intent(this, CharacterService.class);  //创建角色服务
        intentLyricService = new Intent(this, LyricService.class);  //创歌词服务

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMusicService(false, "LLMusic", "Singer", null);
        } else {
            startService(intentService);
        }
        //initNotificationHelper("LLMusic", "Singer", "");
        bindService(intentService, conn, BIND_AUTO_CREATE);
        DownloadHelper.init(this);
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
    private void initNotificationHelper(String musicName, String musicSinger, String imgUrl, Bitmap musicImageBitmap) {
        if(!TextUtils.isEmpty(imgUrl)) {
            EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.THREAD_SHOW_IMAGE_URL, musicName, musicSinger, imgUrl, musicImageBitmap, false));
        } else {
            NotificationHelper.getInstance().createRemoteViews(this, musicName, musicSinger, musicImageBitmap, true);
            MusicPlayService.updateWidgetUI(MainActivity.this, false);
        }
    }

    /** 绑定服务需要ServiceConnection对象 */
    private class ServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (MusicPlayService.MusicBinder) service;
            MediaControllerCompat mediaControllerCompat = new MediaControllerCompat(MainActivity.this, binder.getService().getSessionToken());
            MediaControllerCompat.setMediaController(MainActivity.this, mediaControllerCompat);
            binder.setMediaController(mediaControllerCompat);
            changePlayMode(playMode);
            //是否直接自动播放音乐
            autoMusic();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) { }
    }

    @Override
    public void onResume(){
        super.onResume();
        //获取壁纸数据
        String strBackgroundId = AppData.roomSettings.backgroundUri;
        Log.i(TAG, "strBackgroundUri: " + strBackgroundId);
        if(!TextUtils.isEmpty(strBackgroundId)) {
            long mediaId = Long.parseLong(strBackgroundId);
            Uri contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    mediaId
            );
            Glide.with(getApplication())
                    .setDefaultRequestOptions(requestOptions)
                    .load(contentUri)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(getViewDataBinding().ivBg);
        }

        //判断是否在应用外关闭角色服务，是则同步UI状态
        if(SystemUtil.getInstance().isCanDrawOverlays(this)) {
            if(!SystemUtil.getInstance().isServiceWorked(this, CharacterService.class.getPackage().getName()
                    + "." + CharacterService.class.getSimpleName())) {
                getViewDataBinding().ivCharacterStatus.setVisibility(View.GONE);
            }
        }
    }


    @SuppressLint({"SetTextI18n", "RemoteViewLayout"})
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEvent(ThreadEvent event) {
        switch (event.msgCode) {
            case ThreadEvent.VIEW_CONNECT_MYSQL_LOADING:
                getViewDataBinding().rlShowLoading.setVisibility(View.VISIBLE);
                break;
            case ThreadEvent.VIEW_CONNECT_MYSQL_SUCCESS:
                Log.i("MYSQL", "mysql connect success");
                if (!isSelect) {
                    EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.THREAD_GET_DATA_APP_VERSION));
                    //每日推荐
                    MainVM.showRecommendData(getApplicationContext());
                    //刷新各个歌曲数目
                    //MainVM.showAllMusicTotal(getApplicationContext());
                    isSelect = true;
                }
                break;
            case ThreadEvent.VIEW_CONNECT_MYSQL_ERROR:
                getViewDataBinding().rlShowLoading.setVisibility(GONE);
                Toasty.error(this, "网络连接失败", Toast.LENGTH_SHORT, true).show();
                break;
            case ThreadEvent.VIEW_GET_ALBUM_LIST_SUCCESS:
                if(event.tList == null) {
                    return;
                }
                roomOnlineMusicList.clear();
                roomOnlineMusicList.addAll(event.tList);
                roomOnlineMusicList.addAll(AppMusic.getInstance().getNullMusicData());
                musicListAdapter.notifyDataSetChanged();
                sortList(0);
                clickSortType = 0;
                ObjectAnimator mainPanelChangeObjectAnimator = MyAnimationUtil.objectAnimatorLeftOrRight(MainActivity.this, true, false, getViewDataBinding().clMain);
                ObjectAnimator detailPanelChangeObjectAnimator = MyAnimationUtil.objectAnimatorLeftOrRight(MainActivity.this, false, true, getViewDataBinding().clAlbumDetail);
                mainPanelChangeObjectAnimator.start();
                detailPanelChangeObjectAnimator.start();
                getViewDataBinding().rlShowLoading.setVisibility(GONE);
                isNotMain = true;
                isClickLocalOrFavorite = false;
                getViewDataBinding().tvCount.setText("--");
                getViewDataBinding().tvMusicCount.setText("--");
                break;
            case ThreadEvent.VIEW_GET_ALBUM_DATA_SUCCESS:
                getViewDataBinding().rlShowLoading.setVisibility(View.VISIBLE);
                if(ThreadEvent.ALBUM_LIELLA.equals(event.str)) {
                    updateMusicDetailMessage("Liella!", "Liella!", "LoveLive!Superstar!!", getViewDataBinding().ivLogo, R.mipmap.ic_album_liella_3, 120, 80);
                } else if(ThreadEvent.ALBUM_FOUR_YUU.equals(event.str)) {
                    updateMusicDetailMessage("Liyuu", "Liyuu", "Liyuu", getViewDataBinding().ivLogo, R.mipmap.ic_album_liyuu, 120, 80);
                } else if(ThreadEvent.ALBUM_SUNNY_PASSION.equals(event.str)) {
                    updateMusicDetailMessage("サニーパッション", "サニーパッション", "SunnyPassion", getViewDataBinding().ivLogo, R.mipmap.ic_album_sunny_passion, 120, 80);
                } else if(ThreadEvent.ALBUM_NIJIGASAKI.equals(event.str)) {
                    updateMusicDetailMessage("虹ヶ咲学園スクールアイドル同好会", "虹ヶ咲学園スクールアイドル同好会", "Nijigasaki HighSchool IdolClub", getViewDataBinding().ivLogo, R.mipmap.ic_album_nijigasaki_3, 120, 80);
                } else if(ThreadEvent.ALBUM_AQOURS.equals(event.str)) {
                    updateMusicDetailMessage("Aqours", "Aqours", "LoveLive!Sunshine!!", getViewDataBinding().ivLogo, R.mipmap.ic_album_aqours_3, 120, 80);
                } else if(ThreadEvent.ALBUM_US.equals(event.str)) {
                    updateMusicDetailMessage("μ's", "μ's", "LoveLive!", getViewDataBinding().ivLogo, R.mipmap.ic_album_us_3, 120, 80);
                } else if(ThreadEvent.ALBUM_HASUNOSORA.equals(event.str)) {
                    updateMusicDetailMessage("蓮ノ空女学院スクールアイドルクラブ", "蓮ノ空女学院スクールアイドルクラブ", "Hasunosora Jogakuin School Idol Club", getViewDataBinding().ivLogo, R.mipmap.ic_album_hasu_2, 120, 80);
                } else if(ThreadEvent.ALBUM_SAINT_SNOW.equals(event.str)) {
                    updateMusicDetailMessage("セイントスノー", "セイントスノー", "Saint Snow", getViewDataBinding().ivLogo, R.mipmap.ic_album_saint_snow_2, 120, 80);
                } else if(ThreadEvent.ALBUM_A_RISE.equals(event.str)) {
                    updateMusicDetailMessage("アライズ", "アライズ", "A-RISE", getViewDataBinding().ivLogo, R.mipmap.ic_album_a_rise_2, 120, 50);
                } else if(ThreadEvent.ALBUM_OTHER.equals(event.str)) {
                    updateMusicDetailMessage("Other", "Other", "其他", getViewDataBinding().ivLogo, R.mipmap.ic_album_lovelive_2, 120, 50);
                } else if(ThreadEvent.ALBUM_BLUEBIRD.equals(event.str)) {
                    updateMusicDetailMessage("イキヅライブ!", "イキヅライブ!", "LoveLive! BLUEBIRD", getViewDataBinding().ivLogo, R.mipmap.ic_album_bluebird_2, 120, 50);
                }
                isClickLocalPlayList = false;
                break;
            case ThreadEvent.VIEW_GET_LOCAL_PLAY_LIST_SUCCESS:
                updateMusicDetailMessage(event.str, event.str, "", getViewDataBinding().ivLogo, R.drawable.ic_music_cover_4, 80, 80);
                if(event.byteArray != null && event.byteArray.length >0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(event.byteArray, 0, event.byteArray.length);
                    getViewDataBinding().ivLogo.setImageBitmap(bitmap);
                }
                getViewDataBinding().tvCount.setText("" + event.i);
                getViewDataBinding().tvMusicCount.setText("" + event.i);
                musicListSize = event.i;
                clickLocalPlayListId = event.i2;
                isClickLocalPlayList = true;
                break;
            case ThreadEvent.VIEW_GET_ALBUM_COUNT_SUCCESS:
                getViewDataBinding().tvCount.setText(""+event.i);
                getViewDataBinding().tvMusicCount.setText(""+event.i);
                musicListSize = event.i;
                if(ThreadEvent.ALBUM_LIELLA.equals(event.str)) {
                    SPUtil.setIntValue(context, SPUtil.MusicNewAllTotalByLiella, musicListSize);
                } else if(ThreadEvent.ALBUM_FOUR_YUU.equals(event.str)) {
                    SPUtil.setIntValue(context, SPUtil.MusicNewAllTotalByLiyuu, musicListSize);
                } else if(ThreadEvent.ALBUM_SUNNY_PASSION.equals(event.str)) {
                    SPUtil.setIntValue(context, SPUtil.MusicNewAllTotalBySunnyPassion, musicListSize);
                } else if(ThreadEvent.ALBUM_NIJIGASAKI.equals(event.str)) {
                    SPUtil.setIntValue(context, SPUtil.MusicNewAllTotalByNIJIGASAKI, musicListSize);
                } else if(ThreadEvent.ALBUM_AQOURS.equals(event.str)) {
                    SPUtil.setIntValue(context, SPUtil.MusicNewAllTotalByAqours, musicListSize);
                } else if(ThreadEvent.ALBUM_US.equals(event.str)) {
                    SPUtil.setIntValue(context, SPUtil.MusicNewAllTotalByUS, musicListSize);
                } else if(ThreadEvent.ALBUM_HASUNOSORA.equals(event.str)) {
                    SPUtil.setIntValue(context, SPUtil.MusicNewAllTotalByHASUNOSORA, musicListSize);
                } else if(ThreadEvent.ALBUM_SAINT_SNOW.equals(event.str)) {
                    SPUtil.setIntValue(context, SPUtil.MusicNewAllTotalBySAINTSNOW, musicListSize);
                } else if(ThreadEvent.ALBUM_A_RISE.equals(event.str)) {
                    SPUtil.setIntValue(context, SPUtil.MusicNewAllTotalByARISE, musicListSize);
                } else if(ThreadEvent.ALBUM_OTHER.equals(event.str)) {
                    SPUtil.setIntValue(context, SPUtil.MusicNewAllTotalByOther, musicListSize);
                }
                break;
            case ThreadEvent.THREAD_GET_APP_VERSION_SUCCESS:  //获取app版本更新
                isExistNewVersion = false;
                versionList.clear();
                versionList.addAll(event.tList);
                if(!versionList.isEmpty()) {
                    //isExistNewVersion = true;
                    int versionCode = Integer.parseInt(versionList.get(0).getVersionCode());
                    if(SystemUtil.getInstance().getAppVersionCode(this) < versionCode) { //判断当前是否有新版本
                        isExistNewVersion = true;
                        String versionType = versionList.get(0).getVersionType();
                        if("1".equals(versionType)) {
                            getViewDataBinding().tvVersion.setVisibility(View.VISIBLE);
                        } else if ("2".equals(versionType)) {
                            getViewDataBinding().tvVersion.setVisibility(View.VISIBLE);
                            showUpgradeApp();
                        } else {
                            isExistNewVersion = false;
                        }
                    }
                }
                break;
            case ThreadEvent.VIEW_GET_MESSAGE_SUCCESS:
                messageList.clear();
                messageList.addAll(event.tList);
                getViewDataBinding().tvMessageCount.setText(""+messageList.size());
                break;
            case ThreadEvent.VIEW_PLAY_FINISH_SUCCESS:
                String tsw = AppData.roomSettings.taskAfterMusicSwitch;
                if(!TextUtils.isEmpty(tsw) && tsw.equals("1")) {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            AppData.saveRoomSettings(settings -> settings.taskAfterMusicSwitch = "0");
                        }
                    });
                    return;
                }
                if(!roomPlayMusicList.isEmpty()) {
                    int currentIndex = MusicPlayService.currentMusicPlayIndex(roomPlayMusicList);
                    //判断播放模式
                    switch (playMode) {
                        case 0: //顺序播放
                            Log.i(TAG, "顺序播放");
                            int newIndex = (currentIndex != -1 && currentIndex +1 < roomPlayMusicList.size())? currentIndex + 1 : 0;
                            binder.showLyric(roomPlayMusicList.get(newIndex), (playMode == 2));

                            //变更主题
                            ThemeHelper.getInstance().playButtonTheme(rThemeId, getViewDataBinding());
                            if (objectAnimator != null) {
                                objectAnimator.pause();
                            }
                            break;
                        case 1: //随机播放
                            Log.i(TAG, "随机播放");
                            int rand = new Random().nextInt(roomPlayMusicList.size());
                            if(currentIndex != -1) {
                                while(currentIndex == rand) {
                                    rand = new Random().nextInt(roomPlayMusicList.size());
                                }
                            }
                            binder.showLyric(roomPlayMusicList.get(rand), (playMode == 2));
                            break;
                        case 2: //单曲循环
                            Log.i(TAG,"单曲循环");
                            break;
                    }
                } else {
                    //变更主题
                    ThemeHelper.getInstance().playButtonTheme(rThemeId, getViewDataBinding());
                    if (objectAnimator != null) {
                        objectAnimator.pause();
                    }
                }
                break;
            case ThreadEvent.VIEW_PLAY_ERROR:
                Toasty.error(this, "播放失败", Toast.LENGTH_SHORT, true).show();

                getViewDataBinding().pbLoadingMusic.setVisibility(View.INVISIBLE);
                getViewDataBinding().pbNewLoadingMusic.setVisibility(GONE);
                getViewDataBinding().pbNewLoadingMusic2.setVisibility(GONE);
                break;
            case ThreadEvent.VIEW_PLAY_MUSIC_BY_CHARACTER:
                if(binder!=null) {
                    binder.pause(this, MusicPlayService.currentRoomPlayMusic.musicName, MusicPlayService.currentRoomPlayMusic.musicSinger, MusicPlayService.currentRoomPlayMusic.musicImgBitmap);
                }
                break;
            case ThreadEvent.VIEW_PLAY_RECOMMEND_MUSIC:   //点击播放每日推荐的歌曲 并添加到播放列表
            case ThreadEvent.VIEW_PLAY_LOCAL_MUSIC:   //点击播放本地歌曲 并添加到播放列表
                if(event.roomPlayMusic != null) {
                    List<RoomPlayMusic> list = new ArrayList<>();
                    list.add(event.roomPlayMusic);
                    playMusic(list, 0);
                }
                break;
            case ThreadEvent.VIEW_ADD_LOCAL_MUSIC:   //点击添加本地歌曲到播放列表
                if(event.roomPlayMusic != null) {
                    List<RoomPlayMusic> list = new ArrayList<>();
                    list.add(event.roomPlayMusic);
                    addMusic(list, 0);
                }
                break;
            case ThreadEvent.VIEW_PLAY_FAVORITE_MUSIC:    //点击播放收藏歌曲 并添加到播放列表
                if(event.tList != null && !event.tList.isEmpty()) {
                    String favoriteMusicListStr = Converters.fromFavoriteMusicList(event.tList);
                    List<RoomPlayMusic> list = new ArrayList<>(Converters.toPlayMusicList(favoriteMusicListStr));
                    list.add((RoomPlayMusic) event.tList.get(event.i));
                    playMusic(list, 0);
                }
                break;
            case ThreadEvent.VIEW_ADD_FAVORITE_MUSIC:     //点击添加收藏歌曲到播放列表
                if(event.tList != null && !event.tList.isEmpty()) {
                    String favoriteMusicListStr = Converters.fromFavoriteMusicList(event.tList);
                    List<RoomPlayMusic> list = new ArrayList<>(Converters.toPlayMusicList(favoriteMusicListStr));
                    list.add((RoomPlayMusic) event.tList.get(event.i));
                    addMusic(list, 0);
                }
                break;
            case ThreadEvent.VIEW_MUSIC_IS_PAUSE:
                if(isDoubleClick()) { return; }
                if(binder!=null) {
                    binder.pause(this, event.str, event.str2, event.bitmap);
                }
                break;
            case ThreadEvent.VIEW_MUSIC_IS_NEXT:
                if(isDoubleClick()) { return; }
                lastOrNextMusic(true);
                break;
            case ThreadEvent.VIEW_MUSIC_IS_LAST:
                if(isDoubleClick()) { return; }
                lastOrNextMusic(false);
                break;
            case ThreadEvent.VIEW_PLAY_LIST_FIRST:
                if(!roomPlayMusicList.isEmpty()) {
                    binder.showLyric(roomPlayMusicList.get(0), (playMode == 2));
                }
                break;
            case ThreadEvent.VIEW_SEEK_BAR_POS:
                if(!isOnTouchSeekBar) {
                    lyricScrollView.setMusicPlayerPos(event.i);
                    lyricNewScrollDetailView.setMusicPlayerPos(event.i);
                    lyricNewScrollView.setMusicPlayerPos(event.i);
                    LyricService.setMusicPlayerPos(event.i);
                    getViewDataBinding().sbMusicBar.setProgress(event.i);
                    getViewDataBinding().sbNewMusicBar.setProgress(event.i);
                    getViewDataBinding().hpvProgress.setCurrentCount(event.i);
                    getViewDataBinding().pbNewProgress.setProgress(event.i);
                    getViewDataBinding().qcpProgress.setProgress(event.i);
                }
                //binder.getMediaController().getTransportControls().seekTo(event.i);
                //EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.UPDATE_NOTIFICATION_SEEK_BAR_POS));
                break;
            case ThreadEvent.VIEW_SEEK_BAR_RESUME:
                lyricScrollView.posLock(true);
                lyricNewScrollDetailView.posLock(true);
                lyricNewScrollView.posLock(true);
                getViewDataBinding().sbMusicBar.setProgress(0);
                getViewDataBinding().sbNewMusicBar.setProgress(0);
                getViewDataBinding().pbLoadingMusic.setVisibility(View.VISIBLE);
                getViewDataBinding().pbNewLoadingMusic.setVisibility(View.VISIBLE);
                getViewDataBinding().pbNewLoadingMusic2.setVisibility(View.VISIBLE);
                getViewDataBinding().qcpProgress.setPlayerIcon(0);
                MusicPlayService.updateWidgetUI(MainActivity.this, true);

                //是否开启浮动歌词服务
                if(SystemUtil.getInstance().isServiceWorked(this, LyricService.class.getPackage().getName()
                        + "." + LyricService.class.getSimpleName())) {
                    LyricService.updateLyricUI(false);
                }
                break;

            case ThreadEvent.VIEW_PAUSE:
                isPlay = !event.b;
                lyricScrollView.posLock(event.b);
                lyricNewScrollDetailView.posLock(event.b);
                lyricNewScrollView.posLock(event.b);

                //是否开启浮动歌词服务
                if(SystemUtil.getInstance().isServiceWorked(this, LyricService.class.getPackage().getName()
                        + "." + LyricService.class.getSimpleName())) {
                    LyricService.updateLyricUI(!event.b);
                }

                //角色服务存在时 对角色服务做处理
                if(SystemUtil.getInstance().isServiceWorked(this, CharacterService.class.getPackage().getName()
                        + "." + CharacterService.class.getSimpleName())) {
                    //根据播放或暂停 对角色状态变更
                    MainVM.stopHandler();
                    if(event.b) {
                        MainVM.initAnimatedCharacter(mCharacterName);
                    } else {
                        MainVM.animatedListenCharacter(mCharacterName);
                    }
                    //控制角色系统里面的播放按钮
                    CharacterService.isPlayMusic(!event.b);
                }

                //变更主题
                ThemeHelper.getInstance().playButtonStatusTheme(rThemeId, getViewDataBinding(), event.b);
                //音乐图片转圈
                if (objectAnimator != null) {
                    if (!event.b) {
                        objectAnimator.resume();
                    } else {
                        objectAnimator.pause();
                    }
                }
                break;
            case ThreadEvent.VIEW_ADD_MUSIC:
                getViewDataBinding().tvListSize.setText("("+ roomPlayMusicList.size() + ")");
                getViewDataBinding().tvNewListSize.setText("("+ roomPlayMusicList.size() + ")");
                break;
            case ThreadEvent.VIEW_DELETE_MUSIC:
                getViewDataBinding().tvListSize.setText("("+ roomPlayMusicList.size() + ")");
                getViewDataBinding().tvNewListSize.setText("("+ roomPlayMusicList.size() + ")");

                ThemeHelper.getInstance().playButtonStatusTheme( rThemeId, getViewDataBinding(), !binder.isPlay());
                //如果是删除全部按钮则 清除现在的播放信息及状态
                if(event.b) {
                    ThemeHelper.getInstance().resetDefaultStatusTheme(this, rThemeId, getViewDataBinding(), binder);

                    //重置所有状态
                    getViewDataBinding().tvMusicName.setText("MusicName");
                    getViewDataBinding().tvSingerName.setText("SingerName");
                    getViewDataBinding().tvNewMusicName.setText("MusicName");
                    getViewDataBinding().tvNewSingerName.setText("SingerName");
                    getViewDataBinding().tvNewPlayMusicName.setText("MusicName");
                    getViewDataBinding().tvStartTime.setText("00:00");
                    getViewDataBinding().tvAllTime.setText("00:00");
                    getViewDataBinding().tvNewStartTime.setText("00:00");
                    getViewDataBinding().tvNewAllTime.setText("00:00");
                    getViewDataBinding().pbLoadingMusic.setVisibility(View.INVISIBLE);
                    getViewDataBinding().pbNewLoadingMusic.setVisibility(GONE);
                    getViewDataBinding().pbNewLoadingMusic2.setVisibility(GONE);
                    setCurrentMusicFavorite(null, MusicPlayService.currentRoomPlayMusic.musicName);

                    //清空歌词
                    List<MusicLyric> list = new ArrayList<>();
                    lyricScrollView.setMusicLyrics(list);
                    lyricNewScrollDetailView.setMusicLyrics(list);
                    lyricNewScrollView.setMusicLyrics(list);
                    LyricService.setMusicLyrics(list);
                    musicLyricList.clear();

                    Glide.with(context)
                            .setDefaultRequestOptions(requestOptions)
                            .load(R.mipmap.ic_llmp_new_2)
                            .transform(new RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.ALL))
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(getViewDataBinding().civMusicImg);

                    if(binder!=null) {
                        binder.pauseImm(this, MusicPlayService.currentRoomPlayMusic.musicName, MusicPlayService.currentRoomPlayMusic.musicSinger, MusicPlayService.currentRoomPlayMusic.musicImgBitmap);
                        binder.clearMedia();
                        binder.resetMusicPlayer();
                        MusicPlayService.currentRoomPlayMusic = new RoomPlayMusic();
                    }
                    updateStopCharacterStatus();
                }
                break;
            case ThreadEvent.VIEW_MUSIC_MSG:
                MusicPlayService.currentRoomPlayMusic = event.roomPlayMusic;

                lyricScrollView.initView();
                lyricNewScrollView.initView();
                lyricNewScrollDetailView.initView();

                //是否开启浮动歌词服务
                if(SystemUtil.getInstance().isServiceWorked(this, LyricService.class.getPackage().getName()
                        + "." + LyricService.class.getSimpleName())) {
                    LyricService.updateLyricUI(true);
                }

                String defaultSize = SPUtil.getStrValue(MainActivity.this, SPUtil.DefaultLyricSizeData);
                if(!TextUtils.isEmpty(defaultSize)) {
                    int defaultSizeInt = Integer.parseInt(defaultSize);
                    lyricScrollView.setLyricSize(defaultSizeInt);
                }

                String singleSize = SPUtil.getStrValue(MainActivity.this, SPUtil.SingleLyricSizeData);
                if(!TextUtils.isEmpty(singleSize)) {
                    int singleSizeInt = Integer.parseInt(singleSize);
                    lyricNewScrollView.setLyricSize(singleSizeInt);
                }

                String detailSize = SPUtil.getStrValue(MainActivity.this, SPUtil.DetailLyricSizeData);
                if(!TextUtils.isEmpty(detailSize)) {
                    int detailSizeInt = Integer.parseInt(detailSize);
                    lyricNewScrollDetailView.setLyricSize(detailSizeInt);
                }

//                getViewDataBinding().pbLoadingMusic.setVisibility(View.INVISIBLE);
//                getViewDataBinding().pbNewLoadingMusic.setVisibility(GONE);
//                getViewDataBinding().pbNewLoadingMusic2.setVisibility(GONE);
                getViewDataBinding().hpvProgress.setMaxCount(0);
                getViewDataBinding().pbNewProgress.setMax(0);
                getViewDataBinding().qcpProgress.setMaxProgress(0);
                getViewDataBinding().sbMusicBar.setMax(0);
                getViewDataBinding().sbNewMusicBar.setMax(0);
                getViewDataBinding().tvAllTime.setText(TimeUtil.rebuildTime(0));
                getViewDataBinding().tvNewAllTime.setText(TimeUtil.rebuildTime(0));
                String musicName = MusicPlayService.currentRoomPlayMusic.musicName;
                getViewDataBinding().tvMusicName.setText(musicName);
                getViewDataBinding().tvNewMusicName.setText(musicName);
                getViewDataBinding().tvNewPlayMusicName.setText(musicName);
                getViewDataBinding().tvSingerName.setText(MusicPlayService.currentRoomPlayMusic.musicSinger);
                getViewDataBinding().tvNewSingerName.setText(MusicPlayService.currentRoomPlayMusic.musicSinger);
                getViewDataBinding().tvListSize.setText("(" + roomPlayMusicList.size() + ")");
                getViewDataBinding().tvNewListSize.setText("(" + roomPlayMusicList.size() + ")");

                //刷新播放列表的收藏歌曲显示ui
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_FRESH_FAVORITE_MUSIC));

                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(MusicPlayService.currentRoomPlayMusic.isLocal?
                                (null != MusicPlayService.currentRoomPlayMusic.musicImgByte?
                                        BitmapFactory.decodeByteArray(MusicPlayService.currentRoomPlayMusic.musicImgByte, 0, MusicPlayService.currentRoomPlayMusic.musicImgByte.length) : R.mipmap.ic_llmp_new_2) : MusicPlayService.currentRoomPlayMusic.musicImg
                        )
                        .transform(new RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.ALL))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(getViewDataBinding().civMusicImg);


                //变更主题
                ThemeHelper.getInstance().musicBarMusicImgTheme(this, rThemeId, getViewDataBinding(), MusicPlayService.currentRoomPlayMusic);

                if (objectAnimator != null) {
                    objectAnimator.cancel();
                }
                objectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().ivMusicImg, "rotation", 0f, 360.0f);
                objectAnimator.setDuration(15000);
                objectAnimator.setInterpolator(new LinearInterpolator());//不停顿
                objectAnimator.setRepeatCount(-1);//设置动画重复次数
                objectAnimator.setRepeatMode(ValueAnimator.RESTART);//动画重复模式
                objectAnimator.start();

                isClickNextOrLastLoading = false;
                //刷新当前播放列表状态
                playMusicListAdapter.notifyDataSetChanged();
                //playMusicListAdapter.updatePlayingState(event.i);
                break;
            case ThreadEvent.VIEW_MUSIC_MSG_UPDATE:
                getViewDataBinding().pbLoadingMusic.setVisibility(View.INVISIBLE);
                getViewDataBinding().pbNewLoadingMusic.setVisibility(GONE);
                getViewDataBinding().pbNewLoadingMusic2.setVisibility(GONE);
                getViewDataBinding().hpvProgress.setMaxCount(event.i);
                getViewDataBinding().pbNewProgress.setMax(event.i);
                getViewDataBinding().qcpProgress.setMaxProgress(event.i);
                getViewDataBinding().sbMusicBar.setMax(event.i);
                getViewDataBinding().sbNewMusicBar.setMax(event.i);
                getViewDataBinding().tvAllTime.setText(TimeUtil.rebuildTime(event.i));
                getViewDataBinding().tvNewAllTime.setText(TimeUtil.rebuildTime(event.i));

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if(MusicPlayService.currentRoomPlayMusic.isLocal) {
                        if(MusicPlayService.currentRoomPlayMusic.musicImgByte != null) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(MusicPlayService.currentRoomPlayMusic.musicImgByte, 0, MusicPlayService.currentRoomPlayMusic.musicImgByte.length);
                            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.THREAD_SHOW_IMAGE_URL, MusicPlayService.currentRoomPlayMusic.musicName, MusicPlayService.currentRoomPlayMusic.musicSinger, MusicPlayService.currentRoomPlayMusic.musicImg, bitmap, true));
                        } else {
                            MusicPlayService.currentRoomPlayMusic.musicImgBitmap = null;
                            startMusicService(true, MusicPlayService.currentRoomPlayMusic.musicName, MusicPlayService.currentRoomPlayMusic.musicSinger, null);
                            MusicPlayService.updateWidgetUI(MainActivity.this, false);
                        }
                    } else {
                        if(!MusicPlayService.currentRoomPlayMusic.musicImg.isEmpty()) {
                            //重复调用，在initNotificationHelper中有调用
                            //EventBus.getDefault().post(new ThreadEvent(ThreadEvent.SHOW_IMAGE_URL, event.music.musicName, event.music.musicSinger, event.music.musicImg, null, false));
                        } else {
                            MusicPlayService.currentRoomPlayMusic.musicImg = "";
                            startMusicService(true, MusicPlayService.currentRoomPlayMusic.musicName, event.music.musicSinger, MusicPlayService.currentRoomPlayMusic.musicImgByte != null? BitmapFactory.decodeByteArray(MusicPlayService.currentRoomPlayMusic.musicImgByte, 0, MusicPlayService.currentRoomPlayMusic.musicImgByte.length) : null);
                            MusicPlayService.updateWidgetUI(MainActivity.this, false);
                        }
                    }
                }
                initNotificationHelper(MusicPlayService.currentRoomPlayMusic.musicName, MusicPlayService.currentRoomPlayMusic.musicSinger, MusicPlayService.currentRoomPlayMusic.musicImg, MusicPlayService.currentRoomPlayMusic.musicImgByte != null? BitmapFactory.decodeByteArray(MusicPlayService.currentRoomPlayMusic.musicImgByte, 0, MusicPlayService.currentRoomPlayMusic.musicImgByte.length) : null);

                break;
            case ThreadEvent.VIEW_IMAGE_URL:
                //MusicPlayService.currentRoomPlayMusic.musicName = event.str;
                //MusicPlayService.currentRoomPlayMusic.musicSinger = event.str2;

                if(event.bitmap != null) {
                    MusicPlayService.currentRoomPlayMusic.musicImgBitmap = event.bitmap;
                    MusicPlayService.currentRoomPlayMusic.musicImgByte = BitmapUtil.getInstance().bitmapToByteArray(event.bitmap);
                    //设置
                    getViewDataBinding().qcpProgress.setIcon(event.bitmap);
                }

                if(binder !=null) {
                    binder.updateMetadata(MusicPlayService.currentRoomPlayMusic);
                }

                NotificationHelper.getInstance().createRemoteViews(this, event.str, event.str2, (event.bitmap != null) ? event.bitmap : MusicPlayService.currentRoomPlayMusic.musicImgBitmap, false);
                MusicPlayService.updateWidgetUI(MainActivity.this, false);

                break;
            case ThreadEvent.VIEW_LYRIC:
                if(null != event.tList) {
                    //将歌词数据方法lyricScrollView显示
                    lyricScrollView.setMusicLyrics(event.tList);
                    lyricNewScrollDetailView.setMusicLyrics(event.tList);
                    lyricNewScrollView.setMusicLyrics(event.tList);
                    LyricService.setMusicLyrics(event.tList);
                    musicLyricList.clear();
                    musicLyricList.addAll(event.tList);
                }

                if(binder !=null) {
                    binder.player(event.roomPlayMusic, event.b, musicLyricList);
                }
                break;
            case ThreadEvent.VIEW_DOWNLOAD_APP_BY_MAIN_START:
                showLoadingApp();
                break;
            case ThreadEvent.VIEW_DOWNLOAD_APP_BY_MAIN_LOADING:
                if(null != downloadBinding) {
                    downloadBinding.tvValue.setText(""+event.i);
                }
                break;
            case ThreadEvent.VIEW_DOWNLOAD_APP_BY_MAIN_SUCCESS:
                if(null != mAlertDialog) {
                    mAlertDialog.dismiss();
                }
                //是否完成下载app
                if(event.b) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.i(TAG,"file: " + event.file.toString());
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
            case ThreadEvent.VIEW_DOWNLOAD_APP_BY_MAIN_ERROR:
                if(null != mAlertDialog) {
                    mAlertDialog.dismiss();
                }
                Toasty.error(this, "app下载失败，请重新下载", Toast.LENGTH_SHORT, true).show();
                break;
            case ThreadEvent.VIEW_COUNT_DOWN_REFRESH:  //倒计时刷新
                if(dialogTimeTasksBinding != null) {
                    dialogTimeTasksBinding.tvTitleDetailTime.setText(event.str);
                    dialogTimeTasksBinding.pbLoadingTask.setVisibility(GONE);
                }
                break;
            case ThreadEvent.VIEW_COUNT_DOWN_FINISH:
                if(dialogTimeTasksBinding != null) {
                    isCountDown = false;
                    dialogTimeTasksBinding.tvTitleDetailTime.setText("00:00");
                    dialogTimeTasksBinding.pbLoadingTask.setVisibility(GONE);
                    dialogTimeTasksBinding.tvTitleDetail.setVisibility(View.VISIBLE);
                    dialogTimeTasksBinding.llTitleDetail.setVisibility(GONE);
                    dialogTimeTasksBinding.ivTasksClose.setVisibility(View.VISIBLE);
                    dialogTimeTasksBinding.ivTimeTasks1.setVisibility(GONE);
                    dialogTimeTasksBinding.ivTimeTasks2.setVisibility(GONE);
                    dialogTimeTasksBinding.ivTimeTasks3.setVisibility(GONE);
                    dialogTimeTasksBinding.ivTimeTasks4.setVisibility(GONE);
                    dialogTimeTasksBinding.ivTimeTasksCustom.setVisibility(GONE);
                }
                //String taskSwitch = SPUtil.getStrValue(context, SPUtil.TaskAfterMusicSwitch);
                String taskSwitch = AppData.roomSettings.taskAfterMusicSwitch;
                if(!TextUtils.isEmpty(taskSwitch) && taskSwitch.equals("1")) {
                    return;
                }
                if(binder!=null) {
                    binder.pauseImm(this, MusicPlayService.currentRoomPlayMusic.musicName, MusicPlayService.currentRoomPlayMusic.musicSinger, MusicPlayService.currentRoomPlayMusic.musicImgBitmap);
                }
                break;
            case ThreadEvent.VIEW_BLUETOOTH_DISCONNECT:
                if(binder!=null) {
                    binder.pauseImm(this, MusicPlayService.currentRoomPlayMusic.musicName, MusicPlayService.currentRoomPlayMusic.musicSinger, MusicPlayService.currentRoomPlayMusic.musicImgBitmap);
                }
                break;
            case ThreadEvent.VIEW_ACTION_MEDIA_BUTTON:
                if(null != event.kt) {
                    if(binder!=null) {
                        if(KeyEvent.KEYCODE_MEDIA_NEXT == event.kt.getKeyCode() && KeyEvent.ACTION_DOWN == event.kt.getKeyCode()) {
                            lastOrNextMusic(true);
                        } else if(KeyEvent.KEYCODE_MEDIA_PREVIOUS == event.kt.getKeyCode() && KeyEvent.ACTION_DOWN == event.kt.getKeyCode()) {
                            lastOrNextMusic(false);
                        } else if(KeyEvent.KEYCODE_MEDIA_PLAY == event.kt.getKeyCode() && KeyEvent.ACTION_DOWN == event.kt.getKeyCode()) {
                            binder.playImm(this, MusicPlayService.currentRoomPlayMusic.musicName, MusicPlayService.currentRoomPlayMusic.musicSinger, MusicPlayService.currentRoomPlayMusic.musicImgBitmap);
                        } else if(KeyEvent.KEYCODE_MEDIA_PAUSE == event.kt.getKeyCode() && KeyEvent.ACTION_DOWN == event.kt.getKeyCode()) {
                            binder.pauseImm(this, MusicPlayService.currentRoomPlayMusic.musicName, MusicPlayService.currentRoomPlayMusic.musicSinger, MusicPlayService.currentRoomPlayMusic.musicImgBitmap);
                        }
                    }
                }
                break;

            case ThreadEvent.VIEW_NORMAL_STATUS_CHARACTER:
                if(MainVM.CHARACTER_NAME_KEKE_INT == event.i) {
                    CharacterHelper.showNormalStatusCharacter(CharacterHelper.CHARACTER_NAME_KEKE);
                } else if(MainVM.CHARACTER_NAME_KANON_INT == event.i) {
                    CharacterHelper.showNormalStatusCharacter(CharacterHelper.CHARACTER_NAME_KANON);
                }
                break;
            case ThreadEvent.VIEW_MOVE_STATUS_CHARACTER:
                if(MainVM.CHARACTER_NAME_KEKE_INT == event.i) {
                    CharacterHelper.showMoveStatusCharacter(CharacterHelper.CHARACTER_NAME_KEKE);
                } else if(MainVM.CHARACTER_NAME_KANON_INT == event.i) {
                    CharacterHelper.showMoveStatusCharacter(CharacterHelper.CHARACTER_NAME_KANON);
                }
                break;
            case ThreadEvent.VIEW_LISTEN_STATUS_CHARACTER_LEFT:
                if(MainVM.CHARACTER_NAME_KEKE_INT == event.i) {
                    CharacterHelper.showListenStatusCharacter(CharacterHelper.CHARACTER_NAME_KEKE,true);
                } else if(MainVM.CHARACTER_NAME_KANON_INT == event.i) {
                    CharacterHelper.showListenStatusCharacter(CharacterHelper.CHARACTER_NAME_KANON,true);
                }
                break;
            case ThreadEvent.VIEW_LISTEN_STATUS_CHARACTER_RIGHT:
                if(MainVM.CHARACTER_NAME_KEKE_INT == event.i) {
                    CharacterHelper.showListenStatusCharacter(CharacterHelper.CHARACTER_NAME_KEKE, false);
                } else if(MainVM.CHARACTER_NAME_KANON_INT == event.i) {
                    CharacterHelper.showListenStatusCharacter(CharacterHelper.CHARACTER_NAME_KANON, false);
                }
                break;
            case ThreadEvent.VIEW_UPDATE_CHARACTER_STATUS:
                updateStopCharacterStatus();
                break;
            case ThreadEvent.VIEW_SCAN_LOCAL_FILE_SUCCESS:
                getViewDataBinding().rlShowLoading.setVisibility(GONE);
                if(null != dialogLocalFileBinding) {
                    dialogLocalFileBinding.prLocalMusicLoading.setVisibility(GONE);
                    dialogLocalFileBinding.llSelectFile.setClickable(true);
                    dialogLocalFileBinding.llScanFile.setClickable(true);
                }
                break;
            case ThreadEvent.VIEW_SELECT_LOCAL_FILE_SUCCESS:
                if(null != dialogLocalFileBinding) {
                    dialogLocalFileBinding.prLocalMusicLoading.setVisibility(GONE);
                    dialogLocalFileBinding.llSelectFile.setClickable(true);
                    dialogLocalFileBinding.llScanFile.setClickable(true);
                }
                break;

            case ThreadEvent.VIEW_SHOW_OR_HIDE_MASKING_BACKGROUND:
                getViewDataBinding().rlDisableClick2.setVisibility(event.b? View.VISIBLE : GONE);
                break;
            case ThreadEvent.VIEW_ADD_MUSIC_TO_LOCAL_PLAY_LIST_SUCCESS:
                Toasty.success(context, "添加成功", Toast.LENGTH_SHORT, true).show();
                break;
            case ThreadEvent.VIEW_DELETE_MUSIC_TO_LOCAL_PLAY_LIST_SUCCESS:
                if(musicListSize>0) {
                    --musicListSize;
                    getViewDataBinding().tvMusicCount.setText(""+ (musicListSize));
                    getViewDataBinding().tvCount.setText(""+ (musicListSize));
                }
                break;
            case ThreadEvent.VIEW_CLICK_LOCAL_OR_FAVORITE:
                isClickLocalOrFavorite = true;
                break;
            case ThreadEvent.VIEW_SHOW_VISUALIZER:
                //getViewDataBinding().ccvShowVisualizer.startVisualizer();
                break;
            case ThreadEvent.VIEW_SHOW_STOP_VISUALIZER:
                //getViewDataBinding().ccvShowVisualizer.posLock(event.b);
                break;
            case ThreadEvent.VIEW_CONTROLLER_MODE:

                isShowControllerModePanel = false;
                isShowNewPlayController = false;

                controllerModeAnimator = MyAnimationUtil.objectAnimatorUpOrDown(MainActivity.this, true, getViewDataBinding().clControllerMode.getHeight(), getViewDataBinding().clControllerMode);
                controllerModeAnimator.start();

                ObjectAnimator newPlayController1 = MyAnimationUtil.objectAnimatorUpOrDown(MainActivity.this, true, PxUtil.getInstance().dp2px(185, MainActivity.this), getViewDataBinding().rlNewPlayController);
                newPlayController1.start();

                //先升起再下降
                ObjectAnimator musicControllerAnimatorUp = MyAnimationUtil.objectAnimatorUpOrDown(MainActivity.this, false, getViewDataBinding().rlPlayController.getHeight(), getViewDataBinding().rlPlayController);
                musicControllerAnimatorUp.start();
                ObjectAnimator musicControllerAnimatorDown = MyAnimationUtil.objectAnimatorUpOrDown(this, true, getViewDataBinding().clCurrentAllPanel.getHeight(), getViewDataBinding().rlPlayController);
                musicControllerAnimatorDown.start();
                //重置状态
                isClick = false;
                isShowMusicPanel = false;
                isShowMusicList = false;

                getViewDataBinding().clFloatingController.setVisibility(View.GONE);

                //SPUtil.setStrValue(this, SPUtil.SaveControllerScene, SPUtil.SaveControllerSceneValue_DefaultScene);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        AppData.saveRoomSettings(settings -> settings.saveControllerScene = SPUtil.SaveControllerSceneValue_DefaultScene);
                    }
                });
                break;
            case ThreadEvent.VIEW_NEW_CONTROLLER_MODE:

                isShowControllerModePanel = false;
                isShowNewPlayController = true;

                controllerModeAnimator = MyAnimationUtil.objectAnimatorUpOrDown(MainActivity.this, true, getViewDataBinding().clControllerMode.getHeight(), getViewDataBinding().clControllerMode);
                controllerModeAnimator.start();

                ObjectAnimator newPlayController2 = MyAnimationUtil.objectAnimatorUpOrDown(MainActivity.this, false, PxUtil.getInstance().dp2px(85, MainActivity.this), getViewDataBinding().rlNewPlayController);
                newPlayController2.start();

                musicControllerAnimator = MyAnimationUtil.objectAnimatorUpOrDown(MainActivity.this, true, getViewDataBinding().rlPlayController.getHeight(), getViewDataBinding().rlPlayController);
                musicControllerAnimator.start();
//
                if(isClick) { //是否点击了旧版的播放器 按钮其中一个
                    getViewDataBinding().rlDisableClick.setVisibility(GONE);
                }

                getViewDataBinding().clFloatingController.setVisibility(View.GONE);

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        AppData.saveRoomSettings(settings -> settings.saveControllerScene = SPUtil.SaveControllerSceneValue_NewScene);
                    }
                });
                break;
            case ThreadEvent.VIEW_CONTROLLER_MODE_FLOATING:
                isShowControllerModePanel = false;
                isShowNewPlayController = false;

                controllerModeAnimator = MyAnimationUtil.objectAnimatorUpOrDown(MainActivity.this, true, getViewDataBinding().clControllerMode.getHeight(), getViewDataBinding().clControllerMode);
                controllerModeAnimator.start();

                ObjectAnimator newPlayController3 = MyAnimationUtil.objectAnimatorUpOrDown(MainActivity.this, true, PxUtil.getInstance().dp2px(85, MainActivity.this), getViewDataBinding().rlNewPlayController);
                newPlayController3.start();

                musicControllerAnimator = MyAnimationUtil.objectAnimatorUpOrDown(MainActivity.this, true, getViewDataBinding().rlPlayController.getHeight(), getViewDataBinding().rlPlayController);
                musicControllerAnimator.start();

                getViewDataBinding().clFloatingController.setVisibility(View.VISIBLE);

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        AppData.saveRoomSettings(settings -> settings.saveControllerScene = SPUtil.SaveControllerSceneValue_FloatingScene);
                    }
                });
                break;
            case ThreadEvent.VIEW_BG_MODE:
                if(View.VISIBLE == getViewDataBinding().clBgMode.getVisibility()) {
                    getViewDataBinding().clBgMode.setVisibility(View.GONE);
                } else {
                    getViewDataBinding().clBgMode.setVisibility(View.VISIBLE);
                    getViewDataBinding().clBgMode.setOnClickListener(new ButtonClickListener());
                }
                //getViewDataBinding().rlPlayController.setVisibility(View.VISIBLE);
                break;

            case ThreadEvent.VIEW_INTO_SET_BG:
                Intent intentPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentPhoto.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intentPhoto.addCategory(Intent.CATEGORY_OPENABLE);
                intentPhoto.setType("image/*");

                intentTakePhotoLauncher.launch(intentPhoto);
                break;
            case ThreadEvent.VIEW_FRESH_FAVORITE_MUSIC:
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        List<RoomFavoriteMusic> spTempList = BaseApplication.llMusicDatabase.favoriteMusicDao().getAllMusic();
                        runOnUiThread(()->{
                            if(!spTempList.isEmpty()) {
                                setMusicFavorite(spTempList);
                            }
                            //playMusicListAdapter.notifyDataSetChanged();
                            //刷新新播放界面是否收藏歌曲
                            setCurrentMusicFavorite(spTempList, MusicPlayService.currentRoomPlayMusic.musicName);
                        });
                    }
                });
                break;
            case ThreadEvent.VIEW_GET_MUSIC_METADATA:
                if(!TextUtils.isEmpty(event.str)){
                    MusicPlayService.currentRoomPlayMusic.musicBitrate = event.str;
                    getViewDataBinding().tvBitrateValue.setText(event.str + " kps");
                }
                if(!TextUtils.isEmpty(event.str2)) {
                    MusicPlayService.currentRoomPlayMusic.musicMime = event.str2;
                    getViewDataBinding().tvMimeValue.setText(event.str2);
                }

                if(!TextUtils.isEmpty(event.str3)) {
                    MusicPlayService.currentRoomPlayMusic.musicQuality = event.str3;
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

                if(MusicPlayService.mMediaSession != null) {
                    MediaMetadataCompat metadata =  MusicPlayService.mMediaSession.getController().getMetadata();
                    if(metadata != null) {
                        if(metadata.getString("IsLocal").equals("0")) {
                            MusicPlayService.getMusicFileSizeByHttp(metadata.getString("Path"));
                        } else {
                            MusicPlayService.currentRoomPlayMusic.musicFileSize = FileUtil.getInstance().getFileSizeByLocal(metadata.getString("Path"));
                            getViewDataBinding().tvFileSizeValue.setText(MusicPlayService.currentRoomPlayMusic.musicFileSize);
                        }
                    }
                }

                break;
            case ThreadEvent.VIEW_REFRESH_MUSIC_MEMORY_VALUE:
                if(!TextUtils.isEmpty(event.str)) {
                    MusicPlayService.currentRoomPlayMusic.musicFileSize = event.str;
                    getViewDataBinding().tvFileSizeValue.setText(event.str);
                }
                break;
            case ThreadEvent.VIEW_CLOSE_FLOATING_LYRIC: //关闭歌词
                if(SystemUtil.getInstance().isServiceWorked(MainActivity.this, LyricService.class.getPackage().getName()
                        + "." + LyricService.class.getSimpleName())) {
                    LyricService.updateLyricUI(false);
                    stopService(intentLyricService);
                }
                break;
            case ThreadEvent.VIEW_ROOM_GET_THEME_ID:
                if(event.str.equals(MainActivity.class.getSimpleName())) {
                    if(!TextUtils.isEmpty(event.str2)) {
                        try {
                            rThemeId = Integer.parseInt(event.str2);
                            changeTheme(rThemeId);
                            lyricScrollView.setThemeId(rThemeId);
                            lyricNewScrollDetailView.setThemeId(rThemeId);
                            lyricNewScrollView.setThemeId(rThemeId);
                        } catch (Exception e) {
                            Log.e(TAG, "rThemeId转换失败, ");
                        }
                    }
                }
                break;
        }
    }

    /** 更新专辑详细信息 */
    private void updateMusicDetailMessage(String titleName, String msgName1, String msgName2, ImageView imageView, int resId, int width, int height) {
        getViewDataBinding().ivLogo.setImageBitmap(null);
        getViewDataBinding().ivLogo.setBackgroundResource(resId);
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = PxUtil.getInstance().dp2px(width, context);
        layoutParams.height = PxUtil.getInstance().dp2px(height, context);
        getViewDataBinding().ivLogo.setLayoutParams(layoutParams);

        getViewDataBinding().tvTitleBar.setText(titleName);
        getViewDataBinding().tvListMsgName1.setText(msgName1);
        getViewDataBinding().tvListMsgName2.setText(msgName2);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void ThreadEvent(final ThreadEvent event) {
        switch (event.msgCode) {
            case ThreadEvent.THREAD_CONNECT_MYSQL:
                MysqlHelper.connectDB();
                break;
            case ThreadEvent.THREAD_DOWNLOAD_APP_BY_MAIN:
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    //验证是否许可权限
                    for (String str : permissions) {
                        if (checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                            //申请权限
                            requestPermissions(permissions, REQUEST_CODE_DOWNLOAD_APP);
                            return;
                        }
                    }
                }
                getViewModel().downloadUrl(event.str);
                break;
            case ThreadEvent.THREAD_GET_DATA_LIST:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_LIST_SUCCESS, MysqlHelper.getInstance().findMusicAll()));
                break;
            case ThreadEvent.GET_DATA_LIST_COUNT:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_COUNT_SUCCESS, MysqlHelper.getInstance().findMusicCount()));
                break;
            case ThreadEvent.THREAD_GET_DATA_APP_VERSION:
                EventBus.getDefault().post(new ThreadEvent<Version>(ThreadEvent.THREAD_GET_APP_VERSION_SUCCESS, MysqlHelper.getInstance().findVersionSql(),""));
                break;
            case ThreadEvent.THREAD_GET_DATA_RECOMMEND:
                List<RoomPlayMusic> playMusicList = new ArrayList<>(MysqlHelper.getInstance().findMusicByRandomSql(3));
                String data = Converters.fromPlayMusicList(playMusicList);
                List<RoomRecommendMusic> roomRecommendMusicList = new ArrayList<>(Converters.toRecommendMusicList(data));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_RECOMMEND_SUCCESS, roomRecommendMusicList));
                break;
            case ThreadEvent.THREAD_GET_TOTAL_LIELLA:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_TOTAL_LIELLA_SUCCESS, ThreadEvent.ALBUM_LIELLA, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_LIELLA)));
                break;
            case ThreadEvent.THREAD_GET_TOTAL_LIYUU:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_TOTAL_LIYUU_SUCCESS, ThreadEvent.ALBUM_FOUR_YUU, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_LIYUU)));
                break;
            case ThreadEvent.THREAD_GET_TOTAL_SUNNY_PASSION:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_TOTAL_SUNNY_PASSION_SUCCESS, ThreadEvent.ALBUM_SUNNY_PASSION, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_SUNNYPASSION)));
                break;
            case ThreadEvent.THREAD_GET_TOTAL_NIJIGASAKI:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_TOTAL_NIJIGASAKI_SUCCESS, ThreadEvent.ALBUM_NIJIGASAKI, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_NIJIGASAKI)));
                break;
            case ThreadEvent.THREAD_GET_TOTAL_AQOURS:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_TOTAL_AQOURS_SUCCESS, ThreadEvent.ALBUM_AQOURS, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_AQOURS)));
                break;
            case ThreadEvent.THREAD_GET_TOTAL_US:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_TOTAL_US_SUCCESS, ThreadEvent.ALBUM_US, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_US)));
                break;
            case ThreadEvent.THREAD_GET_TOTAL_HASUNOSORA:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_TOTAL_HASUNOSORA_SUCCESS, ThreadEvent.ALBUM_HASUNOSORA, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_HASUNOSORA)));
                break;
            case ThreadEvent.THREAD_GET_TOTAL_SAINT_SNOW:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_TOTAL_SAINT_SNOW_SUCCESS, ThreadEvent.ALBUM_SAINT_SNOW, MysqlHelper.getInstance().findMusicByMusicTypeAndMusicSingerCount(MysqlHelper.MUSIC_TYPE_AQOURS, MysqlHelper.MUSIC_TYPE_SAINT_SNOW)));
                break;
            case ThreadEvent.THREAD_GET_TOTAL_A_RISE:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_TOTAL_A_RISE_SUCCESS, ThreadEvent.ALBUM_A_RISE, MysqlHelper.getInstance().findMusicByMusicTypeAndMusicSingerCount(MysqlHelper.MUSIC_TYPE_US, MysqlHelper.MUSIC_TYPE_A_RISE)));
                break;
            case ThreadEvent.THREAD_GET_TOTAL_OTHER:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_TOTAL_OTHER_SUCCESS, ThreadEvent.ALBUM_OTHER, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_OTHER)));
                break;
            case ThreadEvent.GET_DATA_LIST_MESSAGE:
                EventBus.getDefault().post(new ThreadEvent<Message>(ThreadEvent.VIEW_GET_MESSAGE_SUCCESS, MysqlHelper.getInstance().findMessageSql(), ""));
                break;
            case ThreadEvent.THREAD_GET_DATA_LIST_BY_LIELLA:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_DATA_SUCCESS, ThreadEvent.ALBUM_LIELLA));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_LIST_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql(MysqlHelper.MUSIC_TYPE_LIELLA)));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_COUNT_SUCCESS, ThreadEvent.ALBUM_LIELLA, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_LIELLA)));
                break;
            case ThreadEvent.THREAD_GET_DATA_LIST_BY_FOUR_YUU:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_DATA_SUCCESS, ThreadEvent.ALBUM_FOUR_YUU));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_LIST_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql(MysqlHelper.MUSIC_TYPE_LIYUU)));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_COUNT_SUCCESS, ThreadEvent.ALBUM_FOUR_YUU, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_LIYUU)));
                break;
            case ThreadEvent.THREAD_GET_DATA_LIST_BY_SUNNY_PASSION:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_DATA_SUCCESS, ThreadEvent.ALBUM_SUNNY_PASSION));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_LIST_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql(MysqlHelper.MUSIC_TYPE_SUNNYPASSION)));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_COUNT_SUCCESS, ThreadEvent.ALBUM_SUNNY_PASSION, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_SUNNYPASSION)));
                break;
            case ThreadEvent.THREAD_GET_DATA_LIST_BY_NIJIGASAKI:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_DATA_SUCCESS, ThreadEvent.ALBUM_NIJIGASAKI));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_LIST_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql(MysqlHelper.MUSIC_TYPE_NIJIGASAKI)));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_COUNT_SUCCESS, ThreadEvent.ALBUM_NIJIGASAKI, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_NIJIGASAKI)));
                break;
            case ThreadEvent.THREAD_GET_DATA_LIST_BY_AQOURS:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_DATA_SUCCESS, ThreadEvent.ALBUM_AQOURS));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_LIST_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql(MysqlHelper.MUSIC_TYPE_AQOURS)));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_COUNT_SUCCESS, ThreadEvent.ALBUM_AQOURS, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_AQOURS)));
                break;
            case ThreadEvent.THREAD_GET_DATA_LIST_BY_US:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_DATA_SUCCESS, ThreadEvent.ALBUM_US));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_LIST_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql(MysqlHelper.MUSIC_TYPE_US)));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_COUNT_SUCCESS, ThreadEvent.ALBUM_US, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_US)));
                break;
            case ThreadEvent.THREAD_GET_DATA_LIST_BY_HASUNOSORA:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_DATA_SUCCESS, ThreadEvent.ALBUM_HASUNOSORA));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_LIST_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql(MysqlHelper.MUSIC_TYPE_HASUNOSORA)));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_COUNT_SUCCESS, ThreadEvent.ALBUM_HASUNOSORA, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_HASUNOSORA)));
                break;
            case ThreadEvent.THREAD_GET_DATA_LIST_BY_SAINT_SNOW:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_DATA_SUCCESS, ThreadEvent.ALBUM_SAINT_SNOW));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_LIST_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeAndMusicSingerSql(MysqlHelper.MUSIC_TYPE_AQOURS, MysqlHelper.MUSIC_TYPE_SAINT_SNOW)));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_COUNT_SUCCESS, ThreadEvent.ALBUM_SAINT_SNOW, MysqlHelper.getInstance().findMusicByMusicTypeAndMusicSingerCount(MysqlHelper.MUSIC_TYPE_AQOURS, MysqlHelper.MUSIC_TYPE_SAINT_SNOW)));
                break;
            case ThreadEvent.THREAD_GET_DATA_LIST_BY_A_RISE:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_DATA_SUCCESS, ThreadEvent.ALBUM_A_RISE));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_LIST_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeAndMusicSingerSql(MysqlHelper.MUSIC_TYPE_US, MysqlHelper.MUSIC_TYPE_A_RISE)));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_COUNT_SUCCESS, ThreadEvent.ALBUM_A_RISE, MysqlHelper.getInstance().findMusicByMusicTypeAndMusicSingerCount(MysqlHelper.MUSIC_TYPE_US, MysqlHelper.MUSIC_TYPE_A_RISE)));
                break;
            case ThreadEvent.THREAD_GET_DATA_LIST_BY_OTHER:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_DATA_SUCCESS, ThreadEvent.ALBUM_OTHER));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_LIST_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql(MysqlHelper.MUSIC_TYPE_OTHER)));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_COUNT_SUCCESS, ThreadEvent.ALBUM_OTHER, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_OTHER)));
                break;
            case ThreadEvent.THREAD_GET_DATA_LIST_BY_BLUEBIRD:
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_DATA_SUCCESS, ThreadEvent.ALBUM_BLUEBIRD));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_LIST_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql(MysqlHelper.MUSIC_TYPE_BLUEBIRD)));
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_COUNT_SUCCESS, ThreadEvent.ALBUM_OTHER, MysqlHelper.getInstance().findMusicByMusicTypeCount(MysqlHelper.MUSIC_TYPE_BLUEBIRD)));
                break;
            case ThreadEvent.THREAD_GET_DATA_LIST_BY_LOCAL_PLAY:
                if(event.t != null) {
                    RoomCustomPlay localPlayList = (RoomCustomPlay) event.t;
                    List<RoomPlayMusic> musicList1 = new ArrayList<>();
                    if(!TextUtils.isEmpty(localPlayList.musicListJson)) {
                        musicList1.addAll(Converters.toPlayMusicList(localPlayList.musicListJson));
                    }
                    EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_ALBUM_LIST_SUCCESS, musicList1));
                    EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_GET_LOCAL_PLAY_LIST_SUCCESS, localPlayList.playListName, localPlayList.playListCount, localPlayList.playListId, localPlayList.playListImgByte));
                }
                break;
            case ThreadEvent.THREAD_GET_MUSIC_LYRIC:
                getViewModel().showLyric(event.roomPlayMusic, event.b);
                break;
            case ThreadEvent.THREAD_SHOW_IMAGE_URL:  //设置状态栏显示对应图片
                if(event.b) {
                    getViewModel().showImageBitmap(event.str, event.str2, event.bitmap);
                } else {
                    getViewModel().showImageURL(event.str, event.str2, event.str3);
                }
                break;
            case ThreadEvent.THREAD_ADD_MUSIC_TO_CUSTOM_PLAY_LIST:   //添加在线音乐到自建歌单里面
                if(event.roomPlayMusic != null) {
//                    List<LocalPlayList> localPlayLists = SPUtil.getListValue(this, SPUtil.LocalPlayListData, LocalPlayList.class);
                    if(!AppData.roomCustomPlayList.isEmpty()) {
                        AppData.roomCustomPlayList.stream()
                                .filter(playList -> playList.playListId == event.i)
                                .findFirst()
                                .ifPresent(playList -> {
                                    List<RoomPlayMusic> musicList = Converters.toPlayMusicList(playList.musicListJson);
                                    //List<Music> musicList = playList.getMusicList();
                                    if (musicList != null) {
                                        musicList.add(event.roomPlayMusic);
                                        String musicListJson = Converters.fromPlayMusicList(musicList);
                                        playList.musicListJson = musicListJson != null? musicListJson : "";
                                        playList.playListCount = musicList.size();
                                        //SPUtil.setListValue(this, SPUtil.LocalPlayListData, localPlayLists);
                                        if(musicListJson != null) {
                                            AppData.saveRoomCustomPlayByMusicJson(event.i, customPlay -> customPlay.musicListJson = musicListJson );
                                        }
                                        EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_ADD_MUSIC_TO_LOCAL_PLAY_LIST));
                                        EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_ADD_MUSIC_TO_LOCAL_PLAY_LIST_SUCCESS));
                                    }
                                });
                    }
                }
                break;

            case ThreadEvent.THREAD_DELETE_MUSIC_IN_CUSTOM_PLAY_LIST:  //删除自建歌单里面到某一首歌曲
                if(event.tList != null) {
                    //List<LocalPlayList> localPlayLists = SPUtil.getListValue(this, SPUtil.LocalPlayListData, LocalPlayList.class);
                    if (!AppData.roomCustomPlayList.isEmpty()) {
                        AppData.roomCustomPlayList.stream()
                                .filter(playList -> playList.playListId == event.i)
                                .findFirst()
                                .ifPresent(playList -> {
                                    String musicListJson = Converters.fromPlayMusicList(event.tList);
                                    playList.musicListJson = !TextUtils.isEmpty(musicListJson) ? musicListJson : "";
                                    playList.playListCount = playList.playListCount -1;
                                    EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_DELETE_MUSIC_TO_LOCAL_PLAY_LIST));
                                    EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_DELETE_MUSIC_TO_LOCAL_PLAY_LIST_SUCCESS));
                                });
                    }
                }
                break;

            case ThreadEvent.THREAD_SAVE_MUSIC_DATA:  //在子线程中保存列表单个数据
                //List<Music> list = event.musicList;
                //SPUtil.setListValue(context, SPUtil.PlayListData, list);
                if(event.roomPlayMusic != null) {
                    RoomPlayMusic music = event.roomPlayMusic;
                    if(!event.b) {
                        music.id = MusicPlayService.createMusicId();
                    }
                    List<RoomPlayMusic> list = new ArrayList<>();
                    list.add(music);
                    AppData.saveRoomMusic(list);
                }
                break;
            case ThreadEvent.VIEW_SCREEN_LOCK:
//                if(binder != null && binder.isPlay()) {
//                    NotificationHelper.getInstance().createFullScreen(getApplicationContext());
//                } else {
//                    NotificationHelper.getInstance().cancelNotification(getApplicationContext(), NotificationHelper.LL_MUSIC_FULL_SCREEN);
//                }
                break;
            case ThreadEvent.THREAD_GET_MUSIC_METADATA:
                if(event.roomPlayMusic != null) {
                    Map<String, String> map = MusicPlayService.getMediaMeta(event.roomPlayMusic);
                    String bitrate = map.get("Bitrate");
                    String mime = map.get("Mime");
                    String quality = map.get("Quality");
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_GET_MUSIC_METADATA, bitrate, mime, quality));
                }
                break;
        }
    }

    /** 点击按钮事件 */
    public class ButtonClickListener implements View.OnClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            if(isDoubleClick()) { return; }
            if(v.getId() == R.id.rl_play_controller) {
                Log.i("ABMediaPlay", "clickController");
            } else if(v.getId() == R.id.rl_show_loading) {
                Log.i("ABMediaPlay", "showLoading");
            } else if (v.getId() == R.id.rl_disable_click) {
                Log.i("ABMediaPlay", "clickDisableView");
                hideAllMusicView();
            } else if (v.getId() == R.id.rl_disable_click_3) {
                showOrHideNewMusicPlayerList();
            } else if (v.getId() == R.id.rl_disable_click_4) {
                showNewMoreMenu();
            } else if (v.getId() == R.id.ll_all_play) {
                allPlayMusic();
            } else if (v.getId() == R.id.ll_search) {
                isSearchMusic = true;
                searchMusic();
            } else if (v.getId() == R.id.ll_sort) {
                showSortMenuDialog(v);
            } else if (v.getId() == R.id.ll_cancel) {
                isSearchMusic = false;
                searchCancel();
            } else if (v.getId() == R.id.ll_settings) {
                intoSettings();
            } else if(v.getId() == R.id.ll_back) {
                if(isSearchMusic) {
                    searchCancel();
                    isSearchMusic = false;
                }
                ObjectAnimator mainPanelChangeObjectAnimator = MyAnimationUtil.objectAnimatorLeftOrRight(MainActivity.this, true, true, getViewDataBinding().clMain);
                ObjectAnimator detailPanelChangeObjectAnimator = MyAnimationUtil.objectAnimatorLeftOrRight(MainActivity.this, false, false, getViewDataBinding().clAlbumDetail);
                mainPanelChangeObjectAnimator.start();
                detailPanelChangeObjectAnimator.start();
                isNotMain = false;
            } else if(v.getId() == R.id.fl_new_change_mode) {
                //intoSettings();
                //EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_NEW_CONTROLLER_MODE));
            } else if(v.getId() == R.id.fl_close_controller_mode) {  //关闭新版音乐播放页面
                showOrHideNewController(true);
            } else if(v.getId() == R.id.rl_new_play_controller) { //打开新版音乐播放页面
                showOrHideNewController(false);
            } else if(v.getId() == R.id.iv_new_my_favorite) {
                if(MusicPlayService.currentRoomPlayMusic != null) {
                    if(!isFavorite) {
                        EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_SAVE_FAVORITE_MUSIC, MusicPlayService.currentRoomPlayMusic));
                    } else {
                        EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_CANCEL_FAVORITE_MUSIC, MusicPlayService.currentRoomPlayMusic));
                    }
                }
            } else if(v.getId() == R.id.lv_new_show_lyric) {
                getViewModel().toggleClickNewSingleLyricView(true);
            } else if(v.getId() == R.id.lv_new_show_lyric_detail) {
                getViewModel().toggleClickNewSingleLyricView(false);
            } else if(v.getId() == R.id.ll_system_set) {
                if(isShowMoreMenu) {
                    showNewMoreMenu();
                }
                //跳转设置页面
                new Handler().postDelayed(() -> runOnUiThread(MainActivity.this::intoSettings), 200);
            }
        }
    }

    /** 显示弹窗更新App */
    private void showUpgradeApp(){

        DialogMessageBinding messageBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_message, null, false);

        messageBinding.tvTitle.setText(null != versionList.get(0).versionTitle? versionList.get(0).versionTitle : "");
        messageBinding.tvContent.setText(null != versionList.get(0).versionContent? versionList.get(0).versionContent : "");
        messageBinding.btSelectIconCancel.setText("以后再说");
        messageBinding.btSelectIconCommit.setText("立即体验");
        messageBinding.btSelectIconCommit.setTextColor(getResources().getColor(R.color.white));
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
                    EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.THREAD_DOWNLOAD_APP_BY_MAIN, versionList.get(0).versionUrl));
                }
            }
        });

        mAlertDialog = new AlertDialog.Builder(this)
                .setView(messageBinding.getRoot())
                .create();
        Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_2);
        mAlertDialog.show();
    }

    /** 显示弹窗正在下载App */
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

    /** 改变主题 */
    private void changeTheme(int rId) {
        //刷新ui
        ThemeHelper.getInstance().changeTheme(this, rId, getViewDataBinding(), binder);
        musicListAdapter.notifyDataSetChanged();
        playMusicListAdapter.notifyDataSetChanged();
        //处理其他页面
        getViewModel(MainListFVM.class).setThemeId(rThemeId);
        getViewModel(LocalListFVM.class).setThemeId(rThemeId);
    }

    /** 点击展示系统菜单 */
    public void showMainMenu(View view) {
        if(isDoubleClick()) { return; }
        DialogMainMenuBinding mainMenuBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_main_menu, null, false);

        PopupWindow menuPopupWindow  = new PopupWindow(mainMenuBinding.getRoot(),
                PxUtil.getInstance().dp2px(110, this),  WindowManager.LayoutParams.WRAP_CONTENT, true);
        menuPopupWindow.setTouchable(true);

        //变更主题
        ThemeHelper.getInstance().menuPopupWindowTheme(this, rThemeId, menuPopupWindow, mainMenuBinding);
        menuPopupWindow.setAnimationStyle(R.style.showPopupMenuAnimation);
        menuPopupWindow.showAsDropDown(view,  PxUtil.getInstance().dp2px(-60, this),  PxUtil.getInstance().dp2px(10, this));

        mainMenuBinding.llSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuPopupWindow.dismiss();
                intoSettings();
            }
        });
        //桌面角色
        mainMenuBinding.llCharacter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuPopupWindow.dismiss();
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
                ThemeHelper.getInstance().characterPopupWindowTheme(v.getContext(), rThemeId, characterPopupWindow, characterMenuBinding);

                characterPopupWindow.showAsDropDown(view,  PxUtil.getInstance().dp2px(-60, v.getContext()),  PxUtil.getInstance().dp2px(10, v.getContext()));
            }
        });
        //设置背景
        mainMenuBinding.llBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuPopupWindow.dismiss();
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_INTO_SET_BG));

            }
        });
        //导入歌曲
        mainMenuBinding.llLocalMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuPopupWindow.dismiss();
                if(null != dialogLocalFileBinding) {
                    dialogLocalFileBinding =null;
                }
                dialogLocalFileBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                        R.layout.dialog_local_file, null, false);

                dialogLocalFileBinding.llSelectFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogLocalFileBinding.llSelectFile.setClickable(false);
                        dialogLocalFileBinding.llScanFile.setClickable(false);
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_SCAN_LOCAL_FILE_BY_CHECK_PERMISSION,  "select"));
                        mAlertDialog.dismiss();
                    }
                });

                dialogLocalFileBinding.llScanFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAlertDialog.dismiss();
                        getViewDataBinding().rlShowLoading.setVisibility(View.VISIBLE);

                        dialogLocalFileBinding.prLocalMusicLoading.setVisibility(View.VISIBLE);
                        dialogLocalFileBinding.llSelectFile.setClickable(false);
                        dialogLocalFileBinding.llScanFile.setClickable(false);
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_SCAN_LOCAL_FILE_BY_CHECK_PERMISSION,  "scan"));
                    }
                });

                mAlertDialog = new AlertDialog.Builder(context)
                        .setView(dialogLocalFileBinding.getRoot())
                        .create();
                Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_3);
                mAlertDialog.show();

            }
        });
        //定时关闭
        mainMenuBinding.llTimeTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuPopupWindow.dismiss();
                dialogTimeTasksBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                        R.layout.dialog_time_tasks, null, false);

                dialogTimeTasksBinding.tvTitleDetail.setVisibility(isCountDown? View.GONE : View.VISIBLE);
                dialogTimeTasksBinding.llTitleDetail.setVisibility(isCountDown? View.VISIBLE : View.GONE);
                dialogTimeTasksBinding.pbLoadingTask.setVisibility(isCountDown? View.VISIBLE : View.GONE);
                dialogTimeTasksBinding.switchTask.setChecked(false);

                //String taskSwitch = SPUtil.getStrValue(context, SPUtil.TaskAfterMusicSwitch);
                String taskSwitch = AppData.roomSettings.taskAfterMusicSwitch;
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
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                AppData.saveRoomSettings(settings -> settings.taskAfterMusicSwitch = dialogTimeTasksBinding.switchTask.isChecked()? "1" : "0");
                            }
                        });
                        //SPUtil.setStrValue(getApplicationContext(), SPUtil.TaskAfterMusicSwitch, dialogTimeTasksBinding.switchTask.isChecked()? "1" : "0");
                    }
                });

                mAlertDialog = new AlertDialog.Builder(context)
                        .setView(dialogTimeTasksBinding.getRoot())
                        .create();
                Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_3);
                mAlertDialog.show();
            }
        });
        //下载管理
        mainMenuBinding.llDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuPopupWindow.dismiss();
                Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                startActivity(intent);
            }
        });

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
                    EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_COUNT_DOWN_FINISH));
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


    /** 点击展示授权悬浮窗显示角色系统 */
    public void showCharacterAuth(final String characterName) {
        //判断是否已经授权显示悬浮窗
        if(SystemUtil.getInstance().isCanDrawOverlays(this)) {
             if(SystemUtil.getInstance().isServiceWorked(this, CharacterService.class.getPackage().getName()
                        + "." + CharacterService.class.getSimpleName())) {

                 stopService(intentCharacterService);
                 updateStopCharacterStatus();

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
                    intentCharacterLauncher.launch(intent);
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

    /** 关闭角色状态并刷新ui */
    public void updateStopCharacterStatus() {
        MainVM.stopHandler();
        MainVM.stopTalkHandler();
        getViewDataBinding().ivCharacterStatus.setVisibility(View.GONE);
    }

    /** 点击当前音乐img */
    public void musicImgClick(View view) {
        showOrHideMusicPlayerPanel();
    }

    /** 更改模式菜单 */
    public void changeModeClick(View view) {
        if(isDoubleClick()) { return; }
        final DialogChangeModeMenuBinding changeModeMenuBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_change_mode_menu, null, false);

        PopupWindow changeModePopupWindow  = new PopupWindow(changeModeMenuBinding.getRoot(),
                PxUtil.getInstance().dp2px(120, this),  WindowManager.LayoutParams.WRAP_CONTENT, true);
        changeModePopupWindow.setTouchable(true);

        changeModeMenuBinding.llControllerMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeModePopupWindow.dismiss();

               /* String[] permissions = new String[]{ Manifest.permission.RECORD_AUDIO };
                *//*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissions = new String[]{ Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_MEDIA_AUDIO };
                }*//*
                if(PermissionUtil.getInstance().checkPermission(v.getContext(), permissions)) {
                    ActivityCompat.requestPermissions(MainActivity.this, permissions, REQUEST_CODE_CONTROLLER_MODE);
                    return;
                }*/
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_CONTROLLER_MODE));
            }
        });

        changeModeMenuBinding.llBgMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeModePopupWindow.dismiss();
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_BG_MODE));
            }
        });

        //变更主题
        ThemeHelper.getInstance().changeModePopupWindowTheme(this, rThemeId, changeModePopupWindow, changeModeMenuBinding);
        changeModePopupWindow.setAnimationStyle(R.style.showPopupChangeModeAnimation);
        changeModePopupWindow.showAsDropDown(view,  PxUtil.getInstance().dp2px(0, this),  PxUtil.getInstance().dp2px(-140, this));
    }

    /** 面板里面的更多菜单 */
    @SuppressLint("SetTextI18n")
    public void panelMoreMenu(View view) {
        if(isDoubleClick()) { return; }

//        if(MusicPlayService.mMediaSession != null) {
//            MediaMetadataCompat metadata =  MusicPlayService.mMediaSession.getController().getMetadata();
//            if(metadata != null) {
//                getViewDataBinding().tvArtistValue.setText(MusicPlayService.currentMusicSinger);
//                getViewDataBinding().tvSongTimeValue.setText(TimeUtil.rebuildTime(MusicPlayService.mAllPosition)+"");
//                getViewDataBinding().tvBitrateValue.setText(MusicPlayService.currentMusicBitrate + " kps");
//                getViewDataBinding().tvMimeValue.setText(MusicPlayService.currentMusicMime);
//                getViewDataBinding().tvFileSizeValue.setText(MusicPlayService.currentMusicFileSize);
//            }
//        }

        getViewDataBinding().tvArtistValue.setText(MusicPlayService.currentRoomPlayMusic.musicSinger);
        getViewDataBinding().tvSongTimeValue.setText(TimeUtil.rebuildTime(MusicPlayService.mAllPosition)+"");
        getViewDataBinding().tvBitrateValue.setText(MusicPlayService.currentRoomPlayMusic.musicBitrate + " kps");
        getViewDataBinding().tvMimeValue.setText(MusicPlayService.currentRoomPlayMusic.musicMime);
        getViewDataBinding().tvFileSizeValue.setText(MusicPlayService.currentRoomPlayMusic.musicFileSize);


        String defaultSize = SPUtil.getStrValue(MainActivity.this, SPUtil.DefaultLyricSizeData);
        if(!TextUtils.isEmpty(defaultSize)) {
            int defaultSizeInt = Integer.parseInt(defaultSize);
            if(SystemUtil.getInstance().isSmallScaleDevice()) {
                getViewDataBinding().sbLyricSize.setProgress((defaultSizeInt - 15)/5);
            } else {
                getViewDataBinding().sbLyricSize.setProgress((defaultSizeInt - 25)/5);
            }
        }

        getViewDataBinding().llDefaultLyricSizePanel.setVisibility(View.VISIBLE);
        getViewDataBinding().llNewLyricSizePanel.setVisibility(View.GONE);

        //根据当前显示的歌词页面展示对应设置
        //String controllerScene = SPUtil.getStrValue(MainActivity.this, SPUtil.SaveControllerScene);
        String controllerScene = "";
        if(AppData.roomSettings != null) {
            controllerScene = AppData.roomSettings.saveControllerScene;
        }
        if(!TextUtils.isEmpty(controllerScene)) {
            if(controllerScene.equals(SPUtil.SaveControllerSceneValue_NewScene)) {
                getViewDataBinding().llDefaultLyricSizePanel.setVisibility(View.GONE);
                getViewDataBinding().llNewLyricSizePanel.setVisibility(View.VISIBLE);

                String singleSize = SPUtil.getStrValue(MainActivity.this, SPUtil.SingleLyricSizeData);
                if(!TextUtils.isEmpty(singleSize)) {
                    int singleSizeInt = Integer.parseInt(singleSize);
                    if(SystemUtil.getInstance().isSmallScaleDevice()) {
                        getViewDataBinding().sbSingleLyricSize.setProgress((singleSizeInt - 15)/5);
                    } else {
                        getViewDataBinding().sbSingleLyricSize.setProgress((singleSizeInt - 25)/5);
                    }
                }

                String detailSize = SPUtil.getStrValue(MainActivity.this, SPUtil.DetailLyricSizeData);
                if(!TextUtils.isEmpty(detailSize)) {
                    int detailSizeInt = Integer.parseInt(detailSize);
                    if(SystemUtil.getInstance().isSmallScaleDevice()) {
                        getViewDataBinding().sbDetailLyricSize.setProgress((detailSizeInt - 15)/5);
                    } else {
                        getViewDataBinding().sbDetailLyricSize.setProgress((detailSizeInt - 25)/5);
                    }
                }
            }
        }

        showNewMoreMenu();

    }

    /** 点击播放按钮 */
    public void playButtonClick(View view)  {
        binder.pause(this, MusicPlayService.currentRoomPlayMusic.musicName, MusicPlayService.currentRoomPlayMusic.musicSinger, MusicPlayService.currentRoomPlayMusic.musicImgBitmap);
    }

    /** 点击切换播放模式按钮 */
    public void changePlayModeButtonClick(View view)  {
        playModeButtonClick(view);
    }

    /** 点击当前列表按钮 */
    public void currentListButtonClick(View view)  {
        showOrHideMusicPlayerList();
    }

    /** 点击当前列表按钮 新版 */
    public void newCurrentListButtonClick(View view) {
        showOrHideNewMusicPlayerList();
    }

    /** 播放上一首 */
    public void lastMusicButtonClick(View view) {
        lastOrNextMusic(false);
    }

    /** 播放下一首 */
    public void nextMusicButtonClick(View view) {
        lastOrNextMusic(true);
    }

    /** 播放上一首或下一首歌曲 */
    public void lastOrNextMusic(final boolean isNext) {
        if(isClickNextOrLastLoading) {
            return;
        }
        isClickNextOrLastLoading = true;
        if(!roomPlayMusicList.isEmpty()) {
            if(roomPlayMusicList.size() == 1) {
                binder.showLyric(roomPlayMusicList.get(0), (playMode == 2));
            } else {
                if (MusicPlayService.currentRoomPlayMusic == null || MusicPlayService.currentRoomPlayMusic.id == 0) {
                    binder.showLyric(roomPlayMusicList.get(0), (playMode == 2));
                    return;
                }
                // 直接查找当前歌曲在列表中的位置
                int oldIndex = MusicPlayService.currentMusicPlayIndex(roomPlayMusicList);
                if(oldIndex != -1) {
                    int newIndex = -1; //新播放歌曲索引
                    if(playMode ==1) {
                        int rand = new Random().nextInt(roomPlayMusicList.size());
                        while (oldIndex == rand) {
                            rand = new Random().nextInt(roomPlayMusicList.size());
                        }
                        newIndex = rand;
                    } else if(playMode == 2) {
                        newIndex = oldIndex;
                    } else {
                        if (isNext) {
                            newIndex  = (oldIndex + 1) % roomPlayMusicList.size();
                        } else {
                            newIndex  = (oldIndex == 0) ? roomPlayMusicList.size() - 1 : oldIndex - 1;
                        }
                    }

                    // 调用播放服务
                    binder.showLyric(roomPlayMusicList.get(newIndex), (playMode == 2));
                }
            }
        }
    }

    /** 显示当前播放列表 */
    public void showOrHideMusicPlayerList() {
        //Log.i("CLICK:", "isClick: " +isClick + " isShowMusicPanel: " + isShowMusicPanel + " isShowMusicList: " + isShowMusicList);
        //clCurrentAllPanel隐藏会首次没有高度，需给固定值
        int moveAxis = getViewDataBinding().clCurrentAllPanel.getHeight() == 0 ? panelMoveAxis : getViewDataBinding().clCurrentAllPanel.getHeight();

        if(isClick){
            if(isShowMusicList&&!isShowMusicPanel){
                ObjectAnimator allPanelObjectAnimator = MyAnimationUtil.objectAnimatorUpOrDown(this, true, moveAxis, getViewDataBinding().rlPlayController);
                allPanelObjectAnimator.start();

                getViewDataBinding().btPlay.setVisibility(View.VISIBLE);
                getViewDataBinding().btChangePlayMode.setVisibility(View.INVISIBLE);
                getViewDataBinding().rlDisableClick.setVisibility(isClick ? View.GONE : View.VISIBLE);
                isClick = !isClick;
            } else {
                isShowMusicPanel=!isShowMusicPanel;

                ObjectAnimator musicPanelObjectAnimator = MyAnimationUtil.objectAnimatorLeftOrRight(this, true, false, getViewDataBinding().clCurrentMusicPanel);
                ObjectAnimator musicListObjectAnimator = MyAnimationUtil.objectAnimatorLeftOrRight(this, false, true, getViewDataBinding().clCurrentMusicList);
                musicPanelObjectAnimator.start();
                musicListObjectAnimator.start();

                getViewDataBinding().clCurrentMusicPanel.setVisibility(View.VISIBLE);
                getViewDataBinding().clCurrentMusicList.setVisibility(View.VISIBLE);
                getViewDataBinding().btPlay.setVisibility(View.VISIBLE);
                getViewDataBinding().btChangePlayMode.setVisibility(View.INVISIBLE);
            }
        } else {
            ObjectAnimator allPanelObjectAnimator = MyAnimationUtil.objectAnimatorUpOrDown(this, false, moveAxis, getViewDataBinding().rlPlayController);
            allPanelObjectAnimator.start();
            ObjectAnimator musicListObjectAnimator = MyAnimationUtil.objectAnimatorInit(this, getViewDataBinding().clCurrentMusicList);
            musicListObjectAnimator.start();

            getViewDataBinding().clCurrentAllPanel.setVisibility(View.VISIBLE);
            getViewDataBinding().btPlay.setVisibility(View.VISIBLE);
            getViewDataBinding().btChangePlayMode.setVisibility(View.INVISIBLE);
            getViewDataBinding().clCurrentMusicList.setVisibility(isClick ? View.GONE : View.VISIBLE);
            getViewDataBinding().clCurrentMusicPanel.setVisibility(isClick ? View.VISIBLE: View.GONE);
            getViewDataBinding().rlDisableClick.setVisibility(isClick ? View.GONE : View.VISIBLE);
            isClick = !isClick;
        }
        isShowMusicList = !isShowMusicList;


    }

    /** 新版显示当前播放列表 */
    public void showOrHideNewMusicPlayerList() {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getViewDataBinding().clNewCurrentMusicList.getLayoutParams();
        marginLayoutParams.setMargins(15, 0, 15, 15 + navigationBarHeight);
        getViewDataBinding().clNewCurrentMusicList.setLayoutParams(marginLayoutParams);

        if(!isShowNewMusicList) {
            getViewDataBinding().rlDisableClick3.setVisibility(View.VISIBLE);
            ObjectAnimator newCurrentMusicList = MyAnimationUtil.objectAnimatorUpOrDown(MainActivity.this, false, PxUtil.getInstance().dp2px(500, this), getViewDataBinding().clNewCurrentMusicList);
            newCurrentMusicList.start();
        } else {
            getViewDataBinding().rlDisableClick3.setVisibility(View.GONE);
            ObjectAnimator newCurrentMusicList = MyAnimationUtil.objectAnimatorUpOrDown(MainActivity.this, true, PxUtil.getInstance().dp2px(500, this), getViewDataBinding().clNewCurrentMusicList);
            newCurrentMusicList.start();
        }
        isShowNewMusicList = !isShowNewMusicList;
    }

    /** 新版显示更多菜单 */
    public void showNewMoreMenu() {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getViewDataBinding().rlMoreSetDialog.getLayoutParams();
        marginLayoutParams.setMargins(15, 0, 15, 15 + navigationBarHeight);
        getViewDataBinding().rlMoreSetDialog.setLayoutParams(marginLayoutParams);

        ThemeHelper.getInstance().rlMoreSetDialogTheme(MainActivity.this, rThemeId, getViewDataBinding());
        if(!isShowMoreMenu) {
            getViewDataBinding().rlDisableClick4.setVisibility(View.VISIBLE);
            ObjectAnimator newMoreMenu = MyAnimationUtil.objectAnimatorUpOrDown(MainActivity.this, false, PxUtil.getInstance().dp2px(500, this), getViewDataBinding().rlMoreSetDialog);
            newMoreMenu.start();
        } else {
            getViewDataBinding().rlDisableClick4.setVisibility(View.GONE);
            ObjectAnimator newMoreMenu = MyAnimationUtil.objectAnimatorUpOrDown(MainActivity.this, true, PxUtil.getInstance().dp2px(500, this), getViewDataBinding().rlMoreSetDialog);
            newMoreMenu.start();
        }
        isShowMoreMenu = !isShowMoreMenu;
    }

    /** 显示当前播放面板 */
    public void showOrHideMusicPlayerPanel() {
        //Log.i("CLICK:", "isClick: " +isClick + " isShowMusicPanel: " + isShowMusicPanel + " isShowMusicList: " + isShowMusicList);
        //clCurrentAllPanel隐藏会首次没有高度，需给固定值
        int moveAxis = getViewDataBinding().clCurrentAllPanel.getHeight() == 0 ? panelMoveAxis : getViewDataBinding().clCurrentAllPanel.getHeight();

        if(isClick) {
            if(isShowMusicPanel&&!isShowMusicList) {
                ObjectAnimator allPanelObjectAnimator = MyAnimationUtil.objectAnimatorUpOrDown(this, true, moveAxis, getViewDataBinding().rlPlayController);
                allPanelObjectAnimator.start();

                getViewDataBinding().btPlay.setVisibility(View.INVISIBLE);
                getViewDataBinding().btChangePlayMode.setVisibility(View.VISIBLE);
                getViewDataBinding().rlDisableClick.setVisibility(isClick ? View.GONE : View.VISIBLE);
                isClick = !isClick;
            } else {
                isShowMusicList = !isShowMusicList;
                ObjectAnimator musicPanelObjectAnimator = MyAnimationUtil.objectAnimatorLeftOrRight(this, true, true, getViewDataBinding().clCurrentMusicPanel);
                ObjectAnimator musicListObjectAnimator = MyAnimationUtil.objectAnimatorLeftOrRight(this, false, false, getViewDataBinding().clCurrentMusicList);
                musicPanelObjectAnimator.start();
                musicListObjectAnimator.start();

                getViewDataBinding().clCurrentMusicPanel.setVisibility(View.VISIBLE);
                getViewDataBinding().clCurrentMusicList.setVisibility(View.VISIBLE);
                getViewDataBinding().btPlay.setVisibility(View.INVISIBLE);
                getViewDataBinding().btChangePlayMode.setVisibility(View.VISIBLE);
            }
        } else {
            ObjectAnimator allPanelObjectAnimator = MyAnimationUtil.objectAnimatorUpOrDown(this, false, moveAxis, getViewDataBinding().rlPlayController);
            allPanelObjectAnimator.start();
            ObjectAnimator musicPanelObjectAnimator = MyAnimationUtil.objectAnimatorInit(this, getViewDataBinding().clCurrentMusicPanel);
            musicPanelObjectAnimator.start();

            getViewDataBinding().clCurrentAllPanel.setVisibility(View.VISIBLE);
            getViewDataBinding().btPlay.setVisibility(View.INVISIBLE);
            getViewDataBinding().btChangePlayMode.setVisibility(View.VISIBLE);
            getViewDataBinding().clCurrentMusicList.setVisibility(isClick ? View.VISIBLE: View.GONE);
            getViewDataBinding().clCurrentMusicPanel.setVisibility(isClick ? View.GONE : View.VISIBLE);
            getViewDataBinding().rlDisableClick.setVisibility(isClick ? View.GONE : View.VISIBLE);
            isClick = !isClick;
        }
        isShowMusicPanel = !isShowMusicPanel;
    }

    /** 隐藏所有播放View */
    public void hideAllMusicView() {
        int moveAxis = getViewDataBinding().clCurrentAllPanel.getHeight();
        ObjectAnimator allPanelObjectAnimator = MyAnimationUtil.objectAnimatorUpOrDown(this, true, moveAxis, getViewDataBinding().rlPlayController);
        allPanelObjectAnimator.start();

        getViewDataBinding().btPlay.setVisibility(View.VISIBLE);
        getViewDataBinding().btChangePlayMode.setVisibility(View.INVISIBLE);
        getViewDataBinding().rlDisableClick.setVisibility(isClick ? View.GONE : View.VISIBLE);
        isClick = false;
        isShowMusicPanel = false;
        isShowMusicList = false;
    }

    /** 点击播放全部歌曲 */
    @SuppressLint("SetTextI18n")
    public void allPlayMusic() {
        if(musicListSize!=0) {
            roomPlayMusicList.clear();
            List<RoomPlayMusic> list = roomOnlineMusicList.stream()
                    .filter(music -> !music.musicType.equals(" "))
                    .collect(Collectors.toList());
            roomPlayMusicList.addAll(list);
            playMusicListAdapter.notifyDataSetChanged();
            //保存当前列表数据
            //SPUtil.setListValue(this, SPUtil.PlayListData, playList);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(roomPlayMusicList.isEmpty()) {
                            return;
                        }
                        AppData.deleteAllRoomMusic();
                        Thread.sleep(10);

                        for(RoomPlayMusic music: roomPlayMusicList) {
                            Thread.sleep(1);
                            music.id = MusicPlayService.createMusicId();
                        }
                        Thread.sleep(10);
                        AppData.saveRoomMusic(roomPlayMusicList);
                    } catch (Exception e) {
                        Log.e(TAG, "roomPlayMusicList all id error");
                    }
                }
            });
            getViewDataBinding().tvListSize.setText("("+ roomPlayMusicList.size() + ")");
            getViewDataBinding().tvNewListSize.setText("("+ roomPlayMusicList.size() + ")");
            //播放当前第一首音乐
            EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_PLAY_LIST_FIRST));
        }
    }

    /** 点击查找歌曲按钮 */
    public void searchMusic() {
        roomTempMusicList = new ArrayList<>();
        roomTempMusicList.addAll(roomOnlineMusicList);
        getViewDataBinding().llShowNormalBar.setVisibility(View.GONE);
        getViewDataBinding().llShowSearchBar.setVisibility(View.VISIBLE);
    }

    /** 点击排序按钮 */
    public void showSortMenuDialog(View view) {
        final DialogSortMenuBinding sortMenuBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_sort_menu, null, false);

        // 0:按时间   1:按名称   2:按歌手
        if(0 == clickSortType) {
            sortMenuBinding.ivSortByTimeType.setVisibility(View.VISIBLE);
            sortMenuBinding.ivSortByNameType.setVisibility(View.INVISIBLE);
            sortMenuBinding.ivSortBySingerType.setVisibility(View.INVISIBLE);
        } else if(1 == clickSortType) {
            sortMenuBinding.ivSortByTimeType.setVisibility(View.INVISIBLE);
            sortMenuBinding.ivSortByNameType.setVisibility(View.VISIBLE);
            sortMenuBinding.ivSortBySingerType.setVisibility(View.INVISIBLE);
        } else {
            sortMenuBinding.ivSortByTimeType.setVisibility(View.INVISIBLE);
            sortMenuBinding.ivSortByNameType.setVisibility(View.INVISIBLE);
            sortMenuBinding.ivSortBySingerType.setVisibility(View.VISIBLE);
        }

        //变更主题
        ThemeHelper.getInstance().sortMenuButtonTheme(rThemeId, sortMenuBinding, isUpSortByTime, isUpSortByName, isUpSortBySinger);

        /* 按时间排序 */
        sortMenuBinding.llSortByTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUpSortByTime = !isUpSortByTime;
                sortList(0);
                //变更主题
                ThemeHelper.getInstance().sortTypeButtonTheme(rThemeId, sortMenuBinding.ivSortByTimeType, isUpSortByTime);

                sortMenuBinding.ivSortByTimeType.setVisibility(View.VISIBLE);
                sortMenuBinding.ivSortByNameType.setVisibility(View.INVISIBLE);
                sortMenuBinding.ivSortBySingerType.setVisibility(View.INVISIBLE);
                clickSortType = 0;
            }
        });

        /* 按名称排序 */
        sortMenuBinding.llSortByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUpSortByName = !isUpSortByName;
                sortList(1);
                //变更主题
                ThemeHelper.getInstance().sortTypeButtonTheme(rThemeId, sortMenuBinding.ivSortByNameType, isUpSortByName);

                sortMenuBinding.ivSortByTimeType.setVisibility(View.INVISIBLE);
                sortMenuBinding.ivSortByNameType.setVisibility(View.VISIBLE);
                sortMenuBinding.ivSortBySingerType.setVisibility(View.INVISIBLE);
                clickSortType = 1;
            }
        });



        /* 按歌手名排序 */
        sortMenuBinding.llSortBySinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUpSortBySinger = !isUpSortBySinger;
                sortList(2);
                //变更主题
                ThemeHelper.getInstance().sortTypeButtonTheme(rThemeId, sortMenuBinding.ivSortBySingerType, isUpSortBySinger);

                sortMenuBinding.ivSortByTimeType.setVisibility(View.INVISIBLE);
                sortMenuBinding.ivSortByNameType.setVisibility(View.INVISIBLE);
                sortMenuBinding.ivSortBySingerType.setVisibility(View.VISIBLE);
                clickSortType = 2;
            }
        });

        PopupWindow sortMenuPopupWindow  = new PopupWindow(sortMenuBinding.getRoot(),
                PxUtil.getInstance().dp2px(150, this),  WindowManager.LayoutParams.WRAP_CONTENT, true);
        sortMenuPopupWindow.setTouchable(true);
        //变更主题
        ThemeHelper.getInstance().sortMenuTheme(this, rThemeId, sortMenuPopupWindow, sortMenuBinding);

        sortMenuPopupWindow.showAsDropDown(view,  PxUtil.getInstance().dp2px(-105, this),  PxUtil.getInstance().dp2px(10, this));
    }

    /**
     * 按类型排序歌曲
     * @param sortType 1：顺序 2：反序
     * */
    public void sortList(final int sortType) {
        //将空数据提取 排序后再放到列表最底部
        List<RoomPlayMusic> nullData = new ArrayList<>();
        for (RoomPlayMusic music : roomOnlineMusicList) {
            if(music.musicType.equals(" ")) {
                nullData.add(music);
            }
        }
        if(!nullData.isEmpty()) {
            for (RoomPlayMusic nullMusic : nullData) {
                roomOnlineMusicList.remove(nullMusic);
            }
        }
        Collections.sort(roomOnlineMusicList, new Comparator<RoomPlayMusic>() {
            @Override
            public int compare(RoomPlayMusic o1, RoomPlayMusic o2) {
                Collator collator = Collator.getInstance();
                if(1 == sortType) {
                    CollationKey key1 = collator
                            .getCollationKey(String.valueOf(((RoomPlayMusic) o1).musicName));
                    CollationKey key2 = collator
                            .getCollationKey(String.valueOf(((RoomPlayMusic) o2).musicName));
                    return isUpSortByName ? key1.compareTo(key2) : key2.compareTo(key1);
                } else if (2 == sortType) {
                    CollationKey key1 = collator
                            .getCollationKey(String.valueOf(((RoomPlayMusic) o1).musicSinger));
                    CollationKey key2 = collator
                            .getCollationKey(String.valueOf(((RoomPlayMusic) o2).musicSinger));
                    return isUpSortBySinger ? key1.compareTo(key2) : key2.compareTo(key1);
                } else {
                    CollationKey key1 = collator
                            .getCollationKey(String.valueOf(((RoomPlayMusic) o1).musicId));
                    CollationKey key2 = collator
                            .getCollationKey(String.valueOf(((RoomPlayMusic) o2).musicId));
                    return isUpSortByTime ? key1.compareTo(key2) : key2.compareTo(key1);
                }
            }
        });

        if(!nullData.isEmpty()) {
            roomOnlineMusicList.addAll(nullData);
        }
        musicListAdapter.notifyDataSetChanged();


    }

    /** 是否展示新的音乐控制器 */
    public void showOrHideNewController(boolean isShow) {
        controllerModeAnimator = MyAnimationUtil.objectAnimatorUpOrDown(MainActivity.this, isShow, getViewDataBinding().clControllerMode.getHeight(), getViewDataBinding().clControllerMode);
        controllerModeAnimator.start();

        ObjectAnimator newPlayController = MyAnimationUtil.objectAnimatorUpOrDown(MainActivity.this, !isShow, PxUtil.getInstance().dp2px(!isShow? 185: 85, MainActivity.this), getViewDataBinding().rlNewPlayController);
        newPlayController.start();
        isShowControllerModePanel = !isShow;
        isShowNewPlayController = isShow;
    }

    /** 点击取消搜索 */
    public void searchCancel() {
        roomOnlineMusicList.clear();
        if(roomTempMusicList != null) {
            roomOnlineMusicList.addAll(roomTempMusicList);
        }
        musicListAdapter.notifyDataSetChanged();
        getViewDataBinding().etSearchMusic.setText("");
        getViewDataBinding().llShowNormalBar.setVisibility(View.VISIBLE);
        getViewDataBinding().llShowSearchBar.setVisibility(View.GONE);
        hintKeyBoard();
    }

    /** 点击进入设置页面 */
    public void intoSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("IsExistNewVersion", isExistNewVersion);
        if(isExistNewVersion) {
            if (null != versionList && !versionList.isEmpty()) {
                intent.putExtra("NewVersionUrl", versionList.get(0).getVersionUrl());
                intent.putExtra("NewVersionTitle", versionList.get(0).getVersionTitle());
                intent.putExtra("NewVersionContent", versionList.get(0).getVersionContent());
            }
        }
        intentSettingsLauncher.launch(intent);
    }

    /** 指定改变播放模式 */
    public void changePlayMode(int changePlayMode) {
        switch (changePlayMode) {
            case 0: //顺序播放
                binder.setSingePlayMode(false);
                getViewDataBinding().tvPlayMode.setText("顺序播放");
                getViewDataBinding().tvNewPlayMode.setText("顺序播放");
                //变更主题
                ThemeHelper.getInstance().sequentialPlayButtonTheme(rThemeId, getViewDataBinding(), 0);
                break;
            case 1: //随机播放
                binder.setSingePlayMode(false);
                getViewDataBinding().tvPlayMode.setText("随机播放");
                getViewDataBinding().tvNewPlayMode.setText("随机播放");
                //变更主题
                ThemeHelper.getInstance().sequentialPlayButtonTheme(rThemeId, getViewDataBinding(), 1);
                break;
            case 2: //单曲循环
                binder.setSingePlayMode(true);
                getViewDataBinding().tvPlayMode.setText("单曲循环");
                getViewDataBinding().tvNewPlayMode.setText("单曲循环");
                //变更主题
                ThemeHelper.getInstance().sequentialPlayButtonTheme(rThemeId, getViewDataBinding(), 2);
                break;
        }
    }

    /** 点击切换播放模式 */
    public void playModeButtonClick(View view) {
        playMode = playMode!=2 ? playMode+1 : 0;
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                AppData.saveRoomSettings(roomSetting -> roomSetting.savePlayMode = String.valueOf(playMode));
            }
        });
        switch (playMode) {
            case 0: //顺序播放
                binder.setSingePlayMode(false);
                getViewDataBinding().tvPlayMode.setText("顺序播放");
                getViewDataBinding().tvNewPlayMode.setText("顺序播放");
                //变更主题
                ThemeHelper.getInstance().sequentialPlayButtonTheme(rThemeId, getViewDataBinding(), 0);
                break;
            case 1: //随机播放
                binder.setSingePlayMode(false);
                getViewDataBinding().tvPlayMode.setText("随机播放");
                getViewDataBinding().tvNewPlayMode.setText("随机播放");
                //变更主题
                ThemeHelper.getInstance().sequentialPlayButtonTheme(rThemeId, getViewDataBinding(), 1);
                break;
            case 2: //单曲循环
                binder.setSingePlayMode(true);
                getViewDataBinding().tvPlayMode.setText("单曲循环");
                getViewDataBinding().tvNewPlayMode.setText("单曲循环");
                //变更主题
                ThemeHelper.getInstance().sequentialPlayButtonTheme(rThemeId, getViewDataBinding(), 2);
                break;
        }

    }

    /** 点击删除当前歌单列表所有歌曲 */
    public void deletePlayListAll(View view) {
        if (roomPlayMusicList.isEmpty()) { //列表为空的时候不做弹窗
            return;
        }
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
                roomPlayMusicList.clear();
                playMusicListAdapter.notifyDataSetChanged();
                //SPUtil.setListValue(getApplicationContext(), SPUtil.PlayListData, playList);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        AppData.deleteAllRoomMusic();
                    }
                });
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_DELETE_MUSIC, true));
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

    /** 音乐播放条调整 */
    private class MusicBarChangerListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //拖动时改变显示时间
            getViewDataBinding().tvStartTime.setText(TimeUtil.rebuildTime(progress));
            getViewDataBinding().tvNewStartTime.setText(TimeUtil.rebuildTime(progress));
            //Log.i(TAG, "progress: " + progress);
            lyricScrollView.setMusicPlayerPos(progress);
            lyricNewScrollDetailView.setMusicPlayerPos(progress);
            lyricNewScrollView.setMusicPlayerPos(progress);
            LyricService.setMusicPlayerPos(progress);
            getViewDataBinding().hpvProgress.setCurrentCount(progress);
            getViewDataBinding().pbNewProgress.setProgress(progress);
            getViewDataBinding().qcpProgress.setProgress(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            lyricScrollView.setIsRefreshDraw(true);
            lyricNewScrollDetailView.setIsRefreshDraw(true);
            lyricNewScrollView.setIsRefreshDraw(true);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //跳到拖动位置播放
            binder.seekTo(seekBar);
            lyricScrollView.setIsRefreshDraw(false);
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

    /** 新版控制器2 触摸事件 */
    private class ControllerViewTouchListener implements View.OnTouchListener {
        private int lastX, lastY;
        private int startX, startY; // 记录按下时的初始位置
        private long mLastTime;
        private boolean isDragging = false; // 明确标记是否在拖动

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isDragging = false;
                    mLastTime = System.currentTimeMillis();
                    // 记录初始位置和当前触摸点
                    startX = lastX = (int) event.getRawX();
                    startY = lastY = (int) event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    // 计算偏移量
                    float dx = event.getRawX() - lastX;
                    float dy = event.getRawY() - lastY;

                    // 检查是否达到拖动阈值(如5像素)
                    if (!isDragging && (Math.abs(event.getRawX() - startX) > 5 ||
                            Math.abs(event.getRawY() - startY) > 5)) {
                        isDragging = true;
                    }

                    if (isDragging) {
                        // 更新View位置
                        v.setX(v.getX() + dx);
                        v.setY(v.getY() + dy);
                    }

                    // 重新记录坐标
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    break;

                case MotionEvent.ACTION_UP:
                    long mCurrentTime = System.currentTimeMillis();
                    if (mCurrentTime - mLastTime < 500 &&
                            Math.abs(event.getRawX() - startX) < 10 &&
                            Math.abs(event.getRawY() - startY) < 10) {
                        // 满足点击条件
                        return false; // 让点击事件继续传递
                    }
                    break;
            }
            return isDragging; // 如果是拖动则消费事件
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Stop - UnbindService");
        EventBus.getDefault().unregister(this);
        if(binder != null) {
            binder.pauseImm(this, MusicPlayService.currentRoomPlayMusic.musicName, MusicPlayService.currentRoomPlayMusic.musicSinger, MusicPlayService.currentRoomPlayMusic.musicImgBitmap);
            binder.clearMedia();
        }
        getViewDataBinding().ccvShowVisualizer.release();
        MusicPlayService.stopAppWidgetRunnable();
        MusicPlayService.stopMusicNotificationRunnable();
        MusicPlayService.clearMediaSession();
        updateStopCharacterStatus();
        DownloadReceiver.stopHandler();
        unbindService(conn);
        stopService(intentService);
        stopService(intentCharacterService);
        stopService(intentLyricService);
        //stopService(intentLockScreenService);
        BluetoothUtil.getInstance().unRegisterBluetoothReceiver(this);

    }

    /** 清除媒体会话参数 */
    public void clearMediaSession(){
        if (mSession != null) {
            mSession.setCallback(null);
            mSession.setActive(false);
            mSession.release();
            mSession = null;
        }
    }

    /** 屏蔽返回键 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if(keyCode == KeyEvent.KEYCODE_BACK) { //物理返回键

            if(isShowMoreMenu) {
                showNewMoreMenu();
                return true;
            }

            if(isShowNewMusicList) { //在新版音乐控制界面时当打开播放列表
                showOrHideNewMusicPlayerList();
                return true;
            }

            if(isShowControllerModePanel) { //当在新版音乐控制界面时关闭当前页面
                showOrHideNewController(true);
                return true;
            }

            if(!isShowNewPlayController) {
                if(isShowMusicPanel || isShowMusicList) {
                    hideAllMusicView();
                    return true;
                }
            }

            if(isNotMain) { //不在主界面下处理ui
                if(isSearchMusic) {
                    searchCancel();
                    isSearchMusic = false;
                }
                ObjectAnimator mainPanelChangeObjectAnimator = MyAnimationUtil.objectAnimatorLeftOrRight(MainActivity.this, true, true, getViewDataBinding().clMain);
                ObjectAnimator detailPanelChangeObjectAnimator = MyAnimationUtil.objectAnimatorLeftOrRight(MainActivity.this, false, false, getViewDataBinding().clAlbumDetail);
                mainPanelChangeObjectAnimator.start();
                detailPanelChangeObjectAnimator.start();
                isNotMain = false;
                return true;
            }
            if(isClickLocalOrFavorite) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_HIDE_LOCAL_OR_FAVORITE));
                isClickLocalOrFavorite = false;
                return true;
            }
        } else if (KeyEvent.KEYCODE_MEDIA_PLAY == keyCode&& keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            if(isFirstBluetoothControl){
                isFirstBluetoothControl = false;
                binder.clearMedia();
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_PLAY_LIST_FIRST));
            } else {
                binder.pause(this, MusicPlayService.currentRoomPlayMusic.musicName, MusicPlayService.currentRoomPlayMusic.musicSinger, MusicPlayService.currentRoomPlayMusic.musicImgBitmap);
            }
        } else if (KeyEvent.KEYCODE_MEDIA_PAUSE == keyCode && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            isFirstBluetoothControl = false;
            binder.pause(this, MusicPlayService.currentRoomPlayMusic.musicName, MusicPlayService.currentRoomPlayMusic.musicSinger, MusicPlayService.currentRoomPlayMusic.musicImgBitmap);
        } else if(KeyEvent.KEYCODE_MEDIA_NEXT == keyCode && KeyEvent.ACTION_DOWN == keyEvent.getAction()) {
            lastOrNextMusic(true);
        } else if(KeyEvent.KEYCODE_MEDIA_PREVIOUS == keyCode && KeyEvent.ACTION_DOWN == keyEvent.getAction()) {
            lastOrNextMusic(false);
        }

        return super.onKeyDown(keyCode, keyEvent);
    }

    /** 将当前界面设置为Task中第一个Activity启动 */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }




    public static class MusicListViewHolder extends RecyclerView.ViewHolder {
        public MusicListViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    /** 在线音乐列表Adapter */
    public class MusicListAdapter extends RecyclerView.Adapter<MusicListViewHolder> {

        private Context context;
        private List<RoomPlayMusic> list;
        private View mViewHeader;

        public static final int ITEM_TYPE_HEADER =0;
        public static final int ITEM_TYPE_CONTENT =1;

        public MusicListAdapter(Context context, List<RoomPlayMusic> list) {
            this.context = context;
            this.list = list;
        }

        public void setHeaderView(View viewHeader) {
            mViewHeader = viewHeader;
            notifyItemInserted(0);
        }


        @NonNull
        @Override
        public MusicListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(mViewHeader !=null && viewType == ITEM_TYPE_HEADER) {
                return new MusicListViewHolder(mViewHeader);
            }
            ItemMusicListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_music_list, parent, false);
            return new MusicListViewHolder(binding.getRoot());
        }

        @Override
        public void onBindViewHolder(@NonNull MusicListViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            final ItemMusicListBinding binding = DataBindingUtil.getBinding(holder.itemView);
            if (binding != null) {
                //变更主题
                ThemeHelper.getInstance().musicListAddButtonTheme(getApplication(), rThemeId, binding);


                binding.rlMusicAll.setVisibility(list.get(position).musicType.equals(" ") ? GONE : View.VISIBLE);
                Glide.with(context)
                        .load(list.get(position).musicImg)
                        .transform(new RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.ALL))
                        .apply(new RequestOptions().format(DecodeFormat.PREFER_RGB_565).diskCacheStrategy(DiskCacheStrategy.ALL))
                        .thumbnail(0.5f)
                        .into(binding.ivMusicImg);


                binding.tvMusicName.setText(list.get(position).musicName);
                binding.tvSingerName.setText(list.get(position).musicSinger);
                //点击播放歌曲
                binding.rlMusicAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playMusic(list, position);
                    }
                });

                //点击添加歌曲到列表
                binding.llAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //变更主题
                        ThemeHelper.getInstance().musicListAddButtonAnimatorTheme(rThemeId, binding);
                        addMusic(list, position);
                    }
                });

                binding.llMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMoreMenu(v, list, position);
                    }
                });
            }

        }

        @Override
        public int getItemViewType(int position){
            if(mViewHeader == null) return ITEM_TYPE_CONTENT;
            if(position == 0) return ITEM_TYPE_HEADER;
            return ITEM_TYPE_CONTENT;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

    }

    /** 播放当前点击的歌曲 */
    private void playMusic(List<RoomPlayMusic> list, int position) {
        RoomPlayMusic music = list.get(position);
        if(!roomPlayMusicList.isEmpty()){
            int currentIndex = MusicPlayService.currentMusicPlayIndex(roomPlayMusicList);
            if(currentIndex != -1) {
                RoomPlayMusic currentPlayMusic = list.get(currentIndex);
                if(roomPlayMusicList.size() > currentIndex + 1) {
                    long currentMusicId = currentPlayMusic.id;
                    long nextMusicId = roomPlayMusicList.get(currentIndex + 1).id;
                    music.id = (currentMusicId + nextMusicId) / 2;
                } else {
                    music.id = MusicPlayService.createMusicId();
                }
                binder.showLyric(music, (playMode == 2));
                roomPlayMusicList.add(currentIndex + 1, music);
                //playMusicListAdapter.updatePlayingState(currentIndex + 1);
            } else {
                binder.showLyric(list.get(position), (playMode == 2));
                roomPlayMusicList.add(roomPlayMusicList.size(), list.get(position));
            }
        } else {
            binder.showLyric(list.get(position), (playMode == 2));
            roomPlayMusicList.add(list.get(position));
        }
        EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.THREAD_SAVE_MUSIC_DATA, music, false));
    }

    /** 添加当前点击的歌曲 */
    private void addMusic(List<RoomPlayMusic> list, int position) {
        roomPlayMusicList.add(list.get(position));
        if(roomPlayMusicList.size()==1) {
            binder.showLyric(roomPlayMusicList.get(0), (playMode == 2));
        } else {
            EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_ADD_MUSIC));
        }
        playMusicListAdapter.notifyDataSetChanged();
        EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.THREAD_SAVE_MUSIC_DATA, list.get(position), false));
        EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_FRESH_FAVORITE_MUSIC));
        //SPUtil.setListValue(context, SPUtil.PlayListData, playList);
    }

    /** 在线歌曲item点击展示更多菜单 */
    private void showMoreMenu(View v, List<RoomPlayMusic> list, int position) {
        getViewDataBinding().rlDisableClick2.setVisibility(View.VISIBLE);
        DialogMoreMenuBinding dialogMoreMenuBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_more_menu, null, false);

        dialogMoreMenuBinding.llList.setVisibility(isClickLocalPlayList? View.GONE : View.VISIBLE);
        dialogMoreMenuBinding.llList2.setVisibility(isClickLocalPlayList? View.VISIBLE : View.GONE);

        if(mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
        mPopupWindow  = new PopupWindow(dialogMoreMenuBinding.getRoot(),
                PxUtil.getInstance().dp2px(200, v.getContext()),  WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.showAtLocation(v, Gravity.CENTER, 0,0);

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                getViewDataBinding().rlDisableClick2.setVisibility(View.GONE);
            }
        });

        dialogMoreMenuBinding.tvMusicName.setText(list.get(position).musicName);
        dialogMoreMenuBinding.tvMusicSinger.setText(list.get(position).musicSinger);

        dialogMoreMenuBinding.llAddFavorite.setOnClickListener(new View.OnClickListener() {  //在线歌曲添加到收藏列表
            @Override
            public void onClick(View v) {
                if(mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_SAVE_FAVORITE_MUSIC, list.get(position)));

            }
        });

        dialogMoreMenuBinding.llAddLocalPlayList.setOnClickListener(new View.OnClickListener() {  //在线歌曲添加到自建列表
            @Override
            public void onClick(View v) {
                if(mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }

                //展示自建列表
                getViewDataBinding().rlDisableClick2.setVisibility(View.VISIBLE);
                DialogAddMusicToLocalListBinding dialogAddMusicToLocalListBinding = DataBindingUtil.inflate(LayoutInflater.from(v.getContext()), R.layout.dialog_add_music_to_local_list, null, false);

                addMusicCustomListAdapter = new AddMusicCustomListAdapter(v.getContext(), roomCustomPlayList, list.get(position));
                dialogAddMusicToLocalListBinding.rvLocalMusicList.setLayoutManager(new LinearLayoutManager(v.getContext()));
                dialogAddMusicToLocalListBinding.rvLocalMusicList.setAdapter(addMusicCustomListAdapter);
                roomCustomPlayList.clear();
//                List<RoomCustomPlay> list = SPUtil.getListValue(v.getContext(), SPUtil.LocalPlayListData, LocalPlayList.class);
                if(!AppData.roomCustomPlayList.isEmpty()) {
                    roomCustomPlayList.addAll(AppData.roomCustomPlayList);
                }
                addMusicCustomListAdapter.notifyDataSetChanged();


                mPopupWindow  = new PopupWindow(dialogAddMusicToLocalListBinding.getRoot(),
                        PxUtil.getInstance().dp2px(300, v.getContext()),  WindowManager.LayoutParams.WRAP_CONTENT, true);
                mPopupWindow.setTouchable(true);
                mPopupWindow.showAtLocation(v, Gravity.CENTER, 0,0);

                mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        getViewDataBinding().rlDisableClick2.setVisibility(View.GONE);
                    }
                });

            }
        });

        dialogMoreMenuBinding.llDeleteMusic.setOnClickListener(new View.OnClickListener() { //自建歌曲列表中删除该歌曲
            @Override
            public void onClick(View v) {
                if(mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }

                list.remove(position);
                musicListAdapter.notifyItemRemoved(position);
                musicListAdapter.notifyItemRangeChanged(position, roomOnlineMusicList.size());

                List<RoomPlayMusic> list1 = new ArrayList<>(list);
                list1.removeIf(roomPlayMusic -> roomPlayMusic.musicName.equals(" "));
                if(clickLocalPlayListId != 0) {
                    EventBus.getDefault().post(new ThreadEvent<RoomPlayMusic>(ThreadEvent.THREAD_DELETE_MUSIC_IN_CUSTOM_PLAY_LIST, list1, clickLocalPlayListId));
                }
            }
        });

        dialogMoreMenuBinding.llDownloadMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }

                String fileName = list.get(position).musicName + " - " + list.get(position).musicSinger;
                String url = list.get(position).musicURL;
                getViewModel().downloadMusic(fileName, url);
                Toasty.success(context, "已添加到下载列表", Toast.LENGTH_SHORT, true).show();
            }
        });

    }

    /** 设置播放列表使用图标显示收藏的歌 */
    private void setMusicFavorite(List<RoomFavoriteMusic> favoriteList) {
        roomPlayMusicList.forEach(playMusic ->{
           boolean isFavorite = favoriteList.stream().anyMatch(favoriteMusic ->
                   !TextUtils.isEmpty(playMusic.musicName) && !TextUtils.isEmpty(favoriteMusic.musicName)
                           && playMusic.musicName.equals(favoriteMusic.musicName));
           playMusic.musicFavorite = isFavorite? 1 : 0;
        });
    }

    /** 设置收藏按钮ui*/
    private void setCurrentMusicFavorite(List<RoomFavoriteMusic> favoriteMusicList, String currentMusic) {
        isFavorite = false;

        if(favoriteMusicList != null && !favoriteMusicList.isEmpty()) {
            isFavorite = favoriteMusicList.stream().anyMatch(favoriteMusic ->
                    !TextUtils.isEmpty(favoriteMusic.musicName) && favoriteMusic.musicName.equals(currentMusic));
        }
        //变更主题
        ThemeHelper.getInstance().musicBarMusicFavoriteTheme(this, rThemeId, getViewDataBinding(), isFavorite);
    }

    public static class PlayMusicListViewHolder extends RecyclerView.ViewHolder {
        public PlayMusicListViewHolder(@NonNull View itemView) { super(itemView); }
    }
    /** 播放列表Adapter */
    public class PlayMusicListAdapter extends RecyclerView.Adapter<PlayMusicListViewHolder>
            implements ItemTouchHelperAdapter {

        private Context context;
        private List<RoomPlayMusic> roomList;
        private int currentPlayingIndex = -1;
        private final Object indexLock = new Object(); // 同步锁

        public PlayMusicListAdapter(Context context, List<RoomPlayMusic> list) {
            this.context = context;
            this.roomList = list;
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
                // 设置默认值（防残留）
                binding.tvMusicName.setText("");
                binding.tvSingerName.setText("");

                binding.tvOrderNum.setVisibility(MusicPlayService.currentMusicIsPlay(position, roomList)? View.GONE : View.VISIBLE);

                int num = position+1;
                binding.tvOrderNum.setText(num<10? "0"+num : ""+num);
                binding.tvMusicName.setText(roomList.get(position).musicName);
                binding.tvSingerName.setText(roomList.get(position).musicSinger);

                //展示各团Logo
                binding.ivShowLogo.setVisibility(roomList.get(position).isLocal? View.GONE : View.VISIBLE);
                binding.ivShowLogo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                if(MysqlHelper.MUSIC_TYPE_LIELLA.equals(roomList.get(position).musicType)) {
                    binding.ivShowLogo.setImageResource(R.mipmap.ic_album_liella_3);
                } else if (MysqlHelper.MUSIC_TYPE_LIYUU.equals(roomList.get(position).musicType)) {
                    binding.ivShowLogo.setImageResource(R.mipmap.ic_album_liyuu);
                } else if (MysqlHelper.MUSIC_TYPE_SUNNYPASSION.equals(roomList.get(position).musicType)) {
                    binding.ivShowLogo.setImageResource(R.mipmap.ic_album_sunny_passion);
                    binding.ivShowLogo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                } else if (MysqlHelper.MUSIC_TYPE_NIJIGASAKI.equals(roomList.get(position).musicType)) {
                    binding.ivShowLogo.setImageResource(R.mipmap.ic_album_nijigasaki_3);
                } else if (MysqlHelper.MUSIC_TYPE_AQOURS.equals(roomList.get(position).musicType)) {
                    binding.ivShowLogo.setImageResource(R.mipmap.ic_album_aqours_3);
                } else if (MysqlHelper.MUSIC_TYPE_US.equals(roomList.get(position).musicType)) {
                    binding.ivShowLogo.setImageResource(R.mipmap.ic_album_us_3);
                } else if (MysqlHelper.MUSIC_TYPE_HASUNOSORA.equals(roomList.get(position).musicType)) {
                    binding.ivShowLogo.setImageResource(R.mipmap.ic_album_hasu_2);
                } else if (MysqlHelper.MUSIC_TYPE_BLUEBIRD.equals(roomList.get(position).musicType)) {
                    binding.ivShowLogo.setImageResource(R.mipmap.ic_album_bluebird_2);
                }

                binding.llFavorite.setVisibility(roomList.get(position).musicFavorite == 1? View.VISIBLE : View.GONE);

                //变更主题
                ThemeHelper.getInstance().playListTheme(context, rThemeId, binding, MusicPlayService.currentMusicIsPlay(position, roomList));

                //点击播放列表的歌曲
                binding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!MusicPlayService.currentMusicIsPlay(position, roomList)) {
                            binder.showLyric(roomList.get(position), (playMode == 2));
                        }
                    }
                });
                //当前播放的歌曲列表不显示删除按钮
                binding.llDelete.setVisibility(MusicPlayService.currentMusicIsPlay(position, roomList)? View.INVISIBLE : View.VISIBLE);
                binding.llDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!MusicPlayService.currentMusicIsPlay(position, roomList)) {
                            RoomPlayMusic roomPlayMusic = roomList.get(position);
                            roomList.remove(position);
                            playMusicListAdapter.notifyItemRemoved(position);
                            playMusicListAdapter.notifyItemRangeChanged(position, roomList.size() - position);
                            //new Thread(() -> SPUtil.setListValue(context, SPUtil.PlayListData, roomList)).start();
                            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    AppData.deleteRoomMusic(roomPlayMusic);
                                }
                            });
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_DELETE_MUSIC, false));
                            }, 200);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return roomList.size();
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            Collections.swap(roomList, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemDismiss(int position) {

        }

        // 更新当前播放状态（只刷新两个item）
        public void updatePlayingState(int newPosition) {
            synchronized (indexLock) {
                int oldPosition = currentPlayingIndex;
                currentPlayingIndex = newPosition;

                // 局部刷新
                if (oldPosition >= 0 && oldPosition < getItemCount()) {
                    notifyItemChanged(oldPosition, "UPDATE_PLAYING_STATE");
                }
                if (currentPlayingIndex >= 0 && currentPlayingIndex < getItemCount()) {
                    notifyItemChanged(currentPlayingIndex, "UPDATE_PLAYING_STATE");
                }
            }
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
            if (viewHolder instanceof PlayMusicListViewHolder) {
                PlayMusicListViewHolder itemViewHolder = (PlayMusicListViewHolder) viewHolder;
                itemViewHolder.itemView.setBackgroundColor(0);
            }
        }
    }

    public static class AddMusicLocalListViewHolder extends RecyclerView.ViewHolder {
        public AddMusicLocalListViewHolder(@NonNull View itemView) { super(itemView); }
    }
    /** 点击添加到自建列表 适配器*/
    public class AddMusicCustomListAdapter extends RecyclerView.Adapter<AddMusicLocalListViewHolder>{

        private final Context context;
        private final List<RoomCustomPlay> list;
        private final RoomPlayMusic music;

        public AddMusicCustomListAdapter(Context context, List<RoomCustomPlay> list, RoomPlayMusic music) {
            this.context = context;
            this.list = list;
            this.music = music;
        }

        @NonNull
        @Override
        public AddMusicLocalListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemAddMusicLocalListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                    R.layout.item_add_music_local_list, parent, false);
            return new AddMusicLocalListViewHolder(binding.getRoot());
        }

        @Override
        public void onBindViewHolder(@NonNull AddMusicLocalListViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ItemAddMusicLocalListBinding binding = DataBindingUtil.getBinding(holder.itemView);
            if(binding != null) {
                if(TextUtils.isEmpty(list.get(position).playListName)) {
                    binding.llMusicAll.setVisibility(View.GONE);
                } else {
                    binding.llMusicAll.setVisibility(View.VISIBLE);
                    binding.tvMusicListName.setText(list.get(position).playListName);
                    if(list.get(position).playListImgByte != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(list.get(position).playListImgByte, 0, list.get(position).playListImgByte.length);
                        binding.civImage.setImageBitmap(bitmap);
                    } else {
                        binding.civImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_music_cover_4));
                    }
                    //点击添加到该自建歌单
                    binding.llMusicAll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mPopupWindow != null) {
                                mPopupWindow.dismiss();
                            }
                            EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.THREAD_ADD_MUSIC_TO_CUSTOM_PLAY_LIST, music, list.get(position).playListId));
                        }
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    /** 关闭软键盘 */
    public void hintKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /** 根据权限对应实现功能 */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode) {
            case REQUEST_CODE_DOWNLOAD_APP:
                resultPost(permissions, ThreadEvent.THREAD_DOWNLOAD_APP_BY_MAIN, versionList.get(0).versionUrl);
                break;
            case REQUEST_CODE_SCAN_LOCAL_FILE:
                resultPost(permissions, ThreadEvent.VIEW_SCAN_LOCAL_FILE_BY_CHECK_PERMISSION, "scan");
                break;
            case REQUEST_CODE_SELECT_LOCAL_FILE:
                resultPost(permissions, ThreadEvent.VIEW_SCAN_LOCAL_FILE_BY_CHECK_PERMISSION, "select");
                break;
            case REQUEST_CODE_SELECT_IMG_FILE:
                resultPost(permissions, ThreadEvent.VIEW_SELECT_IMG_FILE_SUCCESS, null);
                break;
            case REQUEST_CODE_CONTROLLER_MODE:
                resultPost(permissions, ThreadEvent.VIEW_CONTROLLER_MODE, null);
                break;
            case REQUEST_CODE_NEED_RUNNING_PERMISSION:
                if(PermissionUtil.getInstance().checkPermission(this, permissions)) {
                    //System.exit(0);
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 处理权限并调用EventBus事件
     * */
    private void resultPost(String[] permissions, int EVENT_NAME, String value) {
        if(!PermissionUtil.getInstance().checkPermission(this, permissions)) {
            if(!TextUtils.isEmpty(value)) {
                EventBus.getDefault().post(new ThreadEvent<>(EVENT_NAME, value));
            } else {
                EventBus.getDefault().post(new ThreadEvent<>(EVENT_NAME));
            }
        }
    }

    /** 在线与本地页面碎片切换 */
    private static class MainFragmentStateAdapter extends FragmentStateAdapter {

        private final List<Fragment> fragmentList;

        public MainFragmentStateAdapter(FragmentActivity fragmentActivity, List<Fragment> fragmentList) {
            super(fragmentActivity);
            this.fragmentList = fragmentList;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentList.size();
        }

    }

}