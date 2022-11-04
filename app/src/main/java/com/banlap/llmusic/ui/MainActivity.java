package com.banlap.llmusic.ui;

import static android.view.View.GONE;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseActivity;
import com.banlap.llmusic.databinding.ActivityMainBinding;
import com.banlap.llmusic.databinding.DialogCharacterMenuBinding;
import com.banlap.llmusic.databinding.DialogDeleteListAllBinding;
import com.banlap.llmusic.databinding.DialogDownloadBinding;
import com.banlap.llmusic.databinding.DialogMessageBinding;
import com.banlap.llmusic.databinding.DialogSortMenuBinding;
import com.banlap.llmusic.databinding.ItemLyricListBinding;
import com.banlap.llmusic.databinding.ItemMusicListBinding;
import com.banlap.llmusic.databinding.ItemPlayListBinding;
import com.banlap.llmusic.fixed.LiellaMusic;
import com.banlap.llmusic.model.Message;
import com.banlap.llmusic.model.Music;
import com.banlap.llmusic.model.MusicLyric;
import com.banlap.llmusic.model.Version;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.service.CharacterService;
import com.banlap.llmusic.service.MusicPlayService;
import com.banlap.llmusic.sql.MysqlHelper;
import com.banlap.llmusic.uivm.MainVM;
import com.banlap.llmusic.utils.BluetoothUtil;
import com.banlap.llmusic.utils.CharacterHelper;
import com.banlap.llmusic.utils.MyAnimationUtil;
import com.banlap.llmusic.utils.NotificationHelper;
import com.banlap.llmusic.utils.PxUtil;
import com.banlap.llmusic.utils.SPUtil;
import com.banlap.llmusic.widget.CenterLayoutManager;
import com.banlap.llmusic.widget.LyricScrollView;
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
import java.util.Objects;
import java.util.Random;

import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MainActivity extends BaseActivity<MainVM, ActivityMainBinding>
        implements MainVM.MainCallBack {

    private List<Music> musicList;                      //按类型的所有歌曲
    private List<Music> tempMusicList;                  //临时音乐列表
    private List<Music> playList;                       //当前播放的列表
    private List<MusicLyric> musicLyricList;            //当前播放歌曲的歌词列表
    private List<Message> messageList;                  //消息列表
    private List<Version> versionList;                  //版本列表
    private MusicListAdapter musicListAdapter;          //音乐列表适配器
    private PlayMusicListAdapter playMusicListAdapter;  //播放列表适配器
    private ScrollLyricAdapter scrollLyricAdapter;      //滚动歌词适配器
    private ObjectAnimator objectAnimator;              //动画效果
    private MusicPlayService.MusicBinder binder;        //用于绑定服务
    private ServiceConn conn;                           //用于绑定服务
    private Intent intentService;                       //音乐服务
    private Intent intentCharacterService;              //角色服务
    private boolean isPlay = false;                     //判断是否播放音乐
    private boolean isSelect = false;                   //查询一次数据
    private boolean isClick = false;                    //判断是否点击按钮
    private boolean isNotMain = false;                  //判断是否在主界面
    private boolean isShowMusicPanel = false;           //判断是否显示音乐面板
    private boolean isShowMusicList = false;            //判断是否显示音乐清单
    private String currentMusicName ="";                //当前歌曲的歌名
    private String currentMusicSinger ="";              //当前歌曲的歌手
    private String currentMusicImg ="";                 //当前歌曲的图片
    private Bitmap currentBitmap = null;                //当前歌曲Bitmap图
    private String currentMusicLyric="";                //当前歌词（没有时间）
    private String currentMusicLyricWithTime="";        //当前歌词
    private AlertDialog mAlertDialog;                   //弹窗
    private int musicListSize = 0;                      //获取总播放列表数
    private int playMode = 0;                           //播放模式: 0顺序播放 1随机播放 2单曲循环
    private int currentAllTime = 0;                     //当前歌曲总时间
    private int cLyricScrollHeight=0;                   //当前歌词滚动高度
    private int clineHeight=0;                          //当前歌词行高
    private int lyricViewHeight=0;                      //歌词View高度
    private int[] cLyricIntArray = new int[MusicPlayService.ARRAY_LENGTH];
    private final int panelMoveAxis=750;                 //面板移动值
    private int rThemeId =0;                             //当前主题
    /** 角色视图 */
    private String mCharacterName;                       //当前角色
    private float sx;                                    //获取当前点击时X坐标
    private float sy;                                    //获取当前点击时Y坐标
    private boolean isMove = false;                      //角色功能：是否在触摸移动
    private int mStartX, mStartY;                        //触摸时记录开始时的坐标 用于判断按下事件跟结束事件
    private long mLastTime;                              //触摸时记录开始时的时间 用于判断按下事件跟结束事件
    private ActivityResultLauncher<Intent> intentActivityResultLauncher;
    private DialogDownloadBinding downloadBinding;
    private boolean isExistNewVersion = false;            //是否存在新版本app
    private boolean isFinishAnimator = true;              //是否执行标题栏文本显示动画
    private boolean isChangeScrollRange = false;          //解决重复高度时刷新appBar的标题文字动画
    private ObjectAnimator textAnimator;                  //标题栏文本动画
    private RequestOptions requestOptions;
    private boolean isUpSortByTime = false;
    private boolean isUpSortByName = false;
    private boolean isUpSortBySinger = false;
    private int clickSortType = 0;                        //当前点击的排序类型
    private LyricScrollView lyricScrollView;              //
    @Override
    protected int getLayoutId() { return R.layout.activity_main; }

    @SuppressLint("CheckResult")
    @Override
    protected void initData() {
        //musicList = LiellaMusic.getInstance().getMusicData();
        musicList = new ArrayList<>();
        playList = new ArrayList<>();
        musicLyricList = new ArrayList<>();
        messageList = new ArrayList<>();
        versionList = new ArrayList<>();
        //本地缓存列表
        List<Music> spList = SPUtil.getListValue(this, "PlayListData", Music.class);
        if(spList.size()>0){
            playList.addAll(spList);
            setPlayListDefault(playList);
        }

        if(SPUtil.getStrValue(this, "SavePlayMode")!=null) {
            if (!(SPUtil.getStrValue(this, "SavePlayMode").equals(""))) {
                playMode = Integer.parseInt(SPUtil.getStrValue(this, "SavePlayMode"));
            }
        }

        requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        //requestOptions.override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL); //关键代码，加载原始大小
        requestOptions.format(DecodeFormat.PREFER_RGB_565); //设置为这种格式去掉透明度通道，可以减少内存占有

        mCharacterName = CharacterHelper.CHARACTER_NAME_KEKE;
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        getViewDataBinding().setVm(getViewModel());
        getViewModel().setCallBack(this);

        getViewDataBinding().clMain.setVisibility(View.VISIBLE);
        getViewDataBinding().clAlbumDetail.setVisibility(View.VISIBLE);
        getViewDataBinding().pbLoadingMusic.setVisibility(View.INVISIBLE);
        getViewDataBinding().tvVersion.setVisibility(View.GONE);

        /** 动画：初始化将详细页面移走 */
        ObjectAnimator detailPanelDefault = MyAnimationUtil.objectAnimatorLeftOrRight(this, false, false, getViewDataBinding().clAlbumDetail);
        detailPanelDefault.start();

        textAnimator = MyAnimationUtil.objectAnimatorShowOrHide(MainActivity.this,  0, 0, getViewDataBinding().tvTitleBar);
        textAnimator.start();

        getViewDataBinding().llShowNormalBar.setVisibility(View.VISIBLE);
        getViewDataBinding().llShowSearchBar.setVisibility(View.GONE);

        musicListAdapter = new MusicListAdapter(this, musicList);
        getViewDataBinding().rvMusicList.setLayoutManager(new LinearLayoutManager(this));
        //setMusicListHeader(getViewDataBinding().rvMusicList);
        getViewDataBinding().rvMusicList.setAdapter(musicListAdapter);
        musicListAdapter.notifyDataSetChanged();

        playMusicListAdapter = new PlayMusicListAdapter(this, playList);
        getViewDataBinding().rvPlayList.setLayoutManager(new LinearLayoutManager(this));
        getViewDataBinding().rvPlayList.setAdapter(playMusicListAdapter);
        playMusicListAdapter.notifyDataSetChanged();

        scrollLyricAdapter = new ScrollLyricAdapter(this, musicLyricList);
        getViewDataBinding().rvMusicLyricList.setLayoutManager(new CenterLayoutManager(this));
        getViewDataBinding().rvMusicLyricList.setAdapter(scrollLyricAdapter);
        scrollLyricAdapter.notifyDataSetChanged();

        Glide.with(getApplication())
                .setDefaultRequestOptions(requestOptions)
                .load(getResources().getIdentifier("ic_music_3", "mipmap", getPackageName()))
                .transform(new RoundedCornersTransformation(30, 0, RoundedCornersTransformation.CornerType.ALL))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(getViewDataBinding().ivMusicImg);

        getViewDataBinding().tvListSize.setText("("+ playList.size() + ")");
        //连接数据库
        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.CONNECT_MYSQL));
        //各种监听
        initListener();
        //initCharacter();
        //开启所有相关服务
        startAllService();
        //广播监听蓝牙连接状态
        BluetoothUtil.getInstance().registerBluetoothReceiver(this);

        lyricScrollView = getViewDataBinding().lvShowLyric;

        //getViewDataBinding().lvgShowLyric.addView(lyricScrollView);
    }

    /** 初始化角色显示 (已使用服务形式显示) */
    private void initCharacter() {
        CharacterHelper.initCharacter(getViewDataBinding().ivCharacter);
        getViewDataBinding().clCharacter.setVisibility(View.GONE);
        getViewDataBinding().llCharacterTalk.setVisibility(View.GONE);
        getViewDataBinding().llSayHello.setVisibility(View.GONE);
        getViewDataBinding().llSayGood.setVisibility(View.GONE);
        getViewDataBinding().llSayHello.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llSayGood.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().ivCharacter.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().ivCharacter.setOnTouchListener(new ViewTouchListener());
    }

    /** 初始化所有功能监听 */
    private void initListener() {
        getViewDataBinding().sbMusicBar.setOnSeekBarChangeListener(new MusicBarChangerListener());
        getViewDataBinding().sbMusicBar.setOnTouchListener(new ProgressBarTouchListener());
        getViewDataBinding().rlPlayController.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().rlShowLoading.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().rlDisableClick.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llAllPlay.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llSearch.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llSort.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llCancel.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llSettings.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList1.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList2.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList3.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList4.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList5.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList6.setOnClickListener(new ButtonClickListener());
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


        getViewDataBinding().etSearchMusic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = getViewDataBinding().etSearchMusic.getText().toString();
                if (!name.equals("")) {
                    List<Music> nullData = new ArrayList<>();
                    for (Music music : tempMusicList) {
                        if (music.getMusicType().equals(" ")) {
                            nullData.add(music);
                        }
                    }
                    List<Music> searchList = new ArrayList<>();
                    int size = tempMusicList.size();
                    for (int i = 0; i < size; i++) {
                        if (tempMusicList.get(i).musicName.contains(name)) {
                            searchList.add(tempMusicList.get(i));
                        }
                    }
                    if (searchList.size() > 0) {
                        musicList.clear();
                        musicList.addAll(searchList);
                        musicList.addAll(nullData);
                        musicListAdapter.notifyDataSetChanged();
                    }
                } else {
                    musicList.clear();
                    if(tempMusicList!=null) {
                        musicList.addAll(tempMusicList);
                    }
                    musicListAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        intentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        //此处是跳转的result回调方法
                        if(isCanDrawOverlays()) {
                            if(null != mAlertDialog) {
                                mAlertDialog.dismiss();
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startCharacterService(mCharacterName);
                            } else {
                                intentCharacterService.putExtra("CharacterName", mCharacterName);
                                startService(intentCharacterService);
                            }
                        }
                    }
                });
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
            initNotificationHelper("LLMusic", "Singer", "");
        }
        bindService(intentService, conn, BIND_AUTO_CREATE);
    }

    /** 初始化音乐服务*/
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


    /** 是否已经开启弹窗权限*/
    private boolean isCanDrawOverlays() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                return false;
            }
        }
        return true;
    }

    /** 初始化角色服务*/
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startCharacterService(String characterName) {
        intentCharacterService.putExtra("IsPlayMusic", isPlay);
        intentCharacterService.putExtra("CharacterName", characterName);
        startForegroundService(intentCharacterService);
    }

    /**
     * 判断服务是否开启
     *
     * @param mContext 上下文
     * @param className 服务class名
     * @return true:开启 false:未开启
     */
    public static boolean isWorked(Context mContext, String className) {
        ActivityManager myManager = (ActivityManager) mContext
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(className)) {
                return true;
            }
        }
        return false;
    }

    /** 初始化通知栏消息 */
    @SuppressLint("RemoteViewLayout")
    private void initNotificationHelper(String musicName, String musicSinger, String imgUrl) {
        if(!imgUrl.equals("")) {
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.SHOW_IMAGE_URL, musicName, musicSinger, imgUrl));
        } else {
            NotificationHelper.getInstance().createRemoteViews(this, musicName, musicSinger, null, true);
        }
    }

    /** 绑定服务需要ServiceConnection对象 */
    private class ServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (MusicPlayService.MusicBinder) service;
            changePlayMode(playMode);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) { }
    }

    @Override
    public void onResume(){
        super.onResume();
        String strThemeId = SPUtil.getStrValue(getApplicationContext(), "SaveThemeId");
        if(strThemeId!=null) {
            if(!strThemeId.equals("")) {
                rThemeId = Integer.parseInt(strThemeId);
                changeTheme(rThemeId);
                lyricScrollView.setThemeId(rThemeId);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            //TopFloatView.start(this);
        }
    }
    @SuppressLint({"SetTextI18n", "RemoteViewLayout"})
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEvent(ThreadEvent event) {
        switch (event.msgCode) {
            case ThreadEvent.CONNECT_MYSQL_LOADING:
                getViewDataBinding().rlShowLoading.setVisibility(View.VISIBLE);
                break;
            case ThreadEvent.CONNECT_MYSQL_SUCCESS:
                Log.e("MYSQL", "mysql connect success");
                if (!isSelect) {
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_DATA_APP_VERSION));
                    //EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_DATA_LIST_MESSAGE));
                    //EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_DATA_LIST_COUNT));
                    //EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_DATA_LIST));
                    isSelect = true;
                }
                break;
            case ThreadEvent.CONNECT_MYSQL_ERROR:
                getViewDataBinding().rlShowLoading.setVisibility(View.GONE);
                Toast.makeText(this, "connect error", Toast.LENGTH_SHORT).show();
                break;
            case ThreadEvent.GET_SUCCESS:
                musicList.clear();
                musicList.addAll(event.musicList);
                musicList.addAll(LiellaMusic.getInstance().getNullMusicData());
                musicListAdapter.notifyDataSetChanged();
                sortList(0);
                clickSortType = 0;
                ObjectAnimator mainPanelChangeObjectAnimator = MyAnimationUtil.objectAnimatorLeftOrRight(MainActivity.this, true, false, getViewDataBinding().clMain);
                ObjectAnimator detailPanelChangeObjectAnimator = MyAnimationUtil.objectAnimatorLeftOrRight(MainActivity.this, false, true, getViewDataBinding().clAlbumDetail);
                mainPanelChangeObjectAnimator.start();
                detailPanelChangeObjectAnimator.start();
                getViewDataBinding().rlShowLoading.setVisibility(View.GONE);
                isNotMain = true;
                break;
            case ThreadEvent.GET_COUNT_SUCCESS:
                getViewDataBinding().tvCount.setText(""+event.i);
                getViewDataBinding().tvMusicCount.setText(""+event.i);
                musicListSize = event.i;
                break;
            case ThreadEvent.GET_APP_VERSION_SUCCESS:
                isExistNewVersion = false;
                versionList.clear();
                versionList.addAll(event.tList);
                if(versionList.size()>0) {
                    int versionCode = Integer.parseInt(versionList.get(0).getVersionCode());
                    if(getAppVersionCode(this) < versionCode) {
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
            case ThreadEvent.GET_MESSAGE_SUCCESS:
                messageList.clear();
                messageList.addAll(event.tList);
                getViewDataBinding().tvMessageCount.setText(""+messageList.size());
                break;
            case ThreadEvent.GET_CURRENT_TIME:
                if(currentAllTime!=0 && currentAllTime > event.i) {
                    getViewDataBinding().tvStartTime.setText(event.str2);
                }
                break;
            case ThreadEvent.PLAY_FINISH_SUCCESS:
                if(playList.size()>0) {
                    //判断播放模式
                    switch (playMode) {
                        case 0: //顺序播放
                            Log.e("ABMediaPlay","0");
                            boolean isInto = false;
                            for (int i = 0; i < playList.size(); i++) {
                                if (playList.get(i).isPlaying) {
                                    if(i+1<playList.size()) {
                                        isInto = true;
                                        playList.get(i).isPlaying = false;
                                        //getViewModel().player(playList.get(i+1), false);
                                        //binder.player(playList.get(i+1), (playMode == 2));
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
                            if(rThemeId!=0) {
                                if(rThemeId == R.id.ll_theme_normal) {
                                    getViewDataBinding().btPlay.setBackgroundResource(R.drawable.selector_play_black_selected);
                                } else if(rThemeId == R.id.ll_theme_dark) {
                                    getViewDataBinding().btPlay.setBackgroundResource(R.drawable.ic_play_2_white);
                                } else if(rThemeId == R.id.ll_theme_white) {
                                    getViewDataBinding().btPlay.setBackgroundResource(R.drawable.selector_play_purple_selected);
                                } else if(rThemeId == R.id.ll_theme_orange) {
                                    getViewDataBinding().btPlay.setBackgroundResource(R.drawable.selector_play_orange_selected);
                                } else if(rThemeId == R.id.ll_theme_light) {
                                    getViewDataBinding().btPlay.setBackgroundResource(R.drawable.selector_play_light_selected);
                                }

                            }

                            if (objectAnimator != null) {
                                objectAnimator.pause();
                            }
                            break;
                        case 1: //随机播放
                            Log.e("ABMediaPlay","1");
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
                            Log.e("ABMediaPlay","2");
                            break;
                    }
                } else {
                    if(rThemeId!=0) {
                        if(rThemeId == R.id.ll_theme_normal) {
                            getViewDataBinding().btPlay.setBackgroundResource(R.drawable.selector_play_black_selected);
                        } else if(rThemeId == R.id.ll_theme_dark) {
                            getViewDataBinding().btPlay.setBackgroundResource(R.drawable.ic_play_2_white);
                        } else if(rThemeId == R.id.ll_theme_white) {
                            getViewDataBinding().btPlay.setBackgroundResource(R.drawable.selector_play_purple_selected);
                        } else if(rThemeId == R.id.ll_theme_orange) {
                            getViewDataBinding().btPlay.setBackgroundResource(R.drawable.selector_play_orange_selected);
                        } else if(rThemeId == R.id.ll_theme_light) {
                            getViewDataBinding().btPlay.setBackgroundResource(R.drawable.selector_play_light_selected);
                        }
                    }
                    if (objectAnimator != null) {
                        objectAnimator.pause();
                    }
                }
                break;
            case ThreadEvent.PLAY_ERROR:
                Toast.makeText(this, "播放失败", Toast.LENGTH_SHORT).show();
                getViewDataBinding().pbLoadingMusic.setVisibility(View.INVISIBLE);
                break;
            case ThreadEvent.PLAY_MUSIC:
                if(binder!=null) {
                    binder.pause(this, currentMusicName, currentMusicSinger, currentBitmap);
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

            case ThreadEvent.VIEW_SEEK_BAR_POS:
                lyricScrollView.setMusicPlayerPos(event.i);
                getViewDataBinding().sbMusicBar.setProgress(event.i);
                getViewDataBinding().hpvProgress.setCurrentCount(event.i);

                break;
            case ThreadEvent.VIEW_SEEK_BAR_RESUME:
                lyricScrollView.posLock(true);
                getViewDataBinding().sbMusicBar.setProgress(0);
                getViewDataBinding().pbLoadingMusic.setVisibility(View.VISIBLE);
                break;

            case ThreadEvent.VIEW_PAUSE:
                isPlay = !event.b;
                lyricScrollView.posLock(event.b);
                //角色服务存在时 对角色服务做处理
                if(isWorked(this, CharacterService.class.getPackage().getName()
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

                if(rThemeId!=0) {
                    Log.e("LogByAB","rThemeId: " + rThemeId);
                    if(rThemeId == R.id.ll_theme_normal) {
                        getViewDataBinding().btPlay.setBackgroundResource(event.b ? R.drawable.selector_play_black_selected : R.drawable.selector_pause_black_selected);
                        getViewDataBinding().ivPanelPlay.setBackgroundResource(event.b ? R.drawable.selector_play_circle_black_selected : R.drawable.selector_pause_circle_black_selected);
                    } else if(rThemeId == R.id.ll_theme_dark) {
                        getViewDataBinding().btPlay.setBackgroundResource(event.b ? R.drawable.ic_play_2_white : R.drawable.ic_pause_2_white);
                        getViewDataBinding().ivPanelPlay.setBackgroundResource(event.b ? R.drawable.ic_play_circle_white : R.drawable.ic_pause_circle_white);
                    } else if(rThemeId == R.id.ll_theme_white) {
                        getViewDataBinding().btPlay.setBackgroundResource(event.b ? R.drawable.selector_play_purple_selected : R.drawable.selector_pause_purple_selected);
                        getViewDataBinding().ivPanelPlay.setBackgroundResource(event.b ? R.drawable.selector_play_circle_purple_selected : R.drawable.selector_pause_circle_purple_selected);
                    } else if(rThemeId == R.id.ll_theme_orange) {
                        getViewDataBinding().btPlay.setBackgroundResource(event.b ? R.drawable.selector_play_orange_selected : R.drawable.selector_pause_orange_selected);
                        getViewDataBinding().ivPanelPlay.setBackgroundResource(event.b ? R.drawable.selector_play_circle_orange_selected : R.drawable.selector_pause_circle_orange_selected);
                    } else if(rThemeId == R.id.ll_theme_light) {
                        getViewDataBinding().btPlay.setBackgroundResource(event.b ? R.drawable.selector_play_light_selected : R.drawable.selector_pause_light_selected);
                        getViewDataBinding().ivPanelPlay.setBackgroundResource(event.b ? R.drawable.selector_play_circle_light_selected : R.drawable.selector_pause_circle_light_selected);
                    } else {
                        getViewDataBinding().btPlay.setBackgroundResource(event.b ? R.drawable.selector_play_black_selected : R.drawable.selector_pause_black_selected);
                        getViewDataBinding().ivPanelPlay.setBackgroundResource(event.b ? R.drawable.selector_play_circle_black_selected : R.drawable.selector_pause_circle_black_selected);
                    }
                } else {
                    getViewDataBinding().btPlay.setBackgroundResource(event.b ? R.drawable.selector_play_black_selected : R.drawable.selector_pause_black_selected);
                    getViewDataBinding().ivPanelPlay.setBackgroundResource(event.b ? R.drawable.selector_play_circle_black_selected : R.drawable.selector_pause_circle_black_selected);
                }
                if (objectAnimator != null) {
                    if (!event.b) {
                        objectAnimator.resume();
                    } else {
                        objectAnimator.pause();
                    }
                }
                break;
            case ThreadEvent.VIEW_ADD_MUSIC:
            case ThreadEvent.VIEW_DELETE_MUSIC:
                getViewDataBinding().tvListSize.setText("("+ playList.size() + ")");
                break;
            case ThreadEvent.VIEW_MUSIC_MSG:
                lyricScrollView.initView();

                getViewDataBinding().hpvProgress.setMaxCount(event.i);

                getViewDataBinding().pbLoadingMusic.setVisibility(View.INVISIBLE);
                getViewDataBinding().sbMusicBar.setMax(event.i);
                getViewDataBinding().tvAllTime.setText(getViewModel().rebuildTime(event.i));
                String musicMsg = event.music.musicName;
                getViewDataBinding().tvMusicName.setText(musicMsg);
                getViewDataBinding().tvSingerName.setText(event.music.musicSinger);
                getViewDataBinding().tvListSize.setText("("+ playList.size() + ")");
                currentMusicImg = event.music.getMusicImg();

                if(rThemeId!=0) {
                    if(rThemeId == R.id.ll_theme_normal) {
                        Glide.with(getApplication())
                                .setDefaultRequestOptions(requestOptions)
                                .load(event.music.getMusicImg())
                                .transform(new CropCircleWithBorderTransformation(5, getResources().getColor(R.color.light_ea)))
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                .into(getViewDataBinding().ivMusicImg);
                    } else if(rThemeId == R.id.ll_theme_dark) {
                        Glide.with(getApplication())
                                .setDefaultRequestOptions(requestOptions)
                                .load(event.music.getMusicImg())
                                .transform(new CropCircleWithBorderTransformation(5, getResources().getColor(R.color.white)))
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                .into(getViewDataBinding().ivMusicImg);
                    } else if(rThemeId == R.id.ll_theme_white) {
                        Glide.with(getApplication())
                                .setDefaultRequestOptions(requestOptions)
                                .load(event.music.getMusicImg())
                                .transform(new CropCircleWithBorderTransformation(5, getResources().getColor(R.color.purple_light)))
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                .into(getViewDataBinding().ivMusicImg);
                    } else if(rThemeId == R.id.ll_theme_orange) {
                        Glide.with(getApplication())
                                .setDefaultRequestOptions(requestOptions)
                                .load(event.music.getMusicImg())
                                .transform(new CropCircleWithBorderTransformation(5, getResources().getColor(R.color.orange_0b)))
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                .into(getViewDataBinding().ivMusicImg);
                    } else if(rThemeId == R.id.ll_theme_light) {
                        Glide.with(getApplication())
                                .setDefaultRequestOptions(requestOptions)
                                .load(event.music.getMusicImg())
                                .transform(new CropCircleWithBorderTransformation(5, getResources().getColor(R.color.light_b5)))
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                .into(getViewDataBinding().ivMusicImg);
                    } else {
                        Glide.with(getApplication())
                                .setDefaultRequestOptions(requestOptions)
                                .load(event.music.getMusicImg())
                                .transform(new CropCircleWithBorderTransformation(5, getResources().getColor(R.color.light_ea)))
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                .into(getViewDataBinding().ivMusicImg);
                    }
                } else {
                    Glide.with(getApplication())
                            .load(event.music.getMusicImg())
                            .transform(new CropCircleWithBorderTransformation(5, getResources().getColor(R.color.light_ea)))
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(getViewDataBinding().ivMusicImg);
                }

                if (objectAnimator != null) {
                    objectAnimator.cancel();
                }
                objectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().ivMusicImg, "rotation", 0f, 360.0f);
                objectAnimator.setDuration(15000);
                objectAnimator.setInterpolator(new LinearInterpolator());//不停顿
                objectAnimator.setRepeatCount(-1);//设置动画重复次数
                objectAnimator.setRepeatMode(ValueAnimator.RESTART);//动画重复模式
                objectAnimator.start();

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if(!event.music.musicImg.equals("")) {
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.SHOW_IMAGE_URL, event.music.musicName, event.music.musicSinger, event.music.musicImg));
                    } else {
                        startMusicService(true,event.music.musicName, event.music.musicSinger, null);
                    }
                } else {
                    initNotificationHelper(event.music.musicName, event.music.musicSinger, event.music.musicImg);
                }
                break;
            case ThreadEvent.VIEW_IMAGE_URL:
                currentMusicName = event.str;
                currentMusicSinger = event.str2;
                currentBitmap = event.bitmap;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startMusicService(true, event.str, event.str2, event.bitmap);
                } else {
                    NotificationHelper.getInstance().createRemoteViews(this, event.str, event.str2, event.bitmap, false);
                }
                break;
            case ThreadEvent.VIEW_LYRIC:
                lyricScrollView.setMusicLyrics(event.tList);
                //getViewDataBinding().lvShowLyric.setThemeId(rThemeId);
                //是否显示歌词
                ScrollView.LayoutParams lp = new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if(!event.str2.equals("")) {
                    getViewDataBinding().svMusicLyric.smoothScrollTo(0,0);
                    lp.gravity = Gravity.TOP;
                    getViewDataBinding().llShowLyric.setLayoutParams(lp);
                    getViewDataBinding().tvMusicLyric.setVisibility(View.VISIBLE);
                    getViewDataBinding().tvMusicNoLyric.setVisibility(View.GONE);
                    getViewDataBinding().tvMusicLyric.setText(event.str2);
                    //主题切换代码
                    if(rThemeId!=0) {
                        if(rThemeId == R.id.ll_theme_normal) {
                            getViewDataBinding().tvMusicLyric.setTextColor(getResources().getColor(R.color.black));
                        } else if(rThemeId == R.id.ll_theme_dark) {
                            getViewDataBinding().tvMusicLyric.setTextColor(getResources().getColor(R.color.white));
                        } else if (rThemeId == R.id.ll_theme_white) {
                            getViewDataBinding().tvMusicLyric.setTextColor(getResources().getColor(R.color.gray_purple_ac));
                        } else if(rThemeId == R.id.ll_theme_orange) {
                            getViewDataBinding().tvMusicLyric.setTextColor(getResources().getColor(R.color.orange_alpha_70));
                        } else if(rThemeId == R.id.ll_theme_light) {
                            getViewDataBinding().tvMusicLyric.setTextColor(getResources().getColor(R.color.light_b5));
                        } else {
                            getViewDataBinding().tvMusicLyric.setTextColor(getResources().getColor(R.color.black));
                        }
                    }
                } else {
                    lp.gravity = Gravity.CENTER;
                    getViewDataBinding().llShowLyric.setLayoutParams(lp);
                    getViewDataBinding().tvMusicLyric.setVisibility(View.GONE);
                    getViewDataBinding().tvMusicNoLyric.setVisibility(View.VISIBLE);
                    //主题切换代码
                    if(rThemeId!=0) {
                        if(rThemeId == R.id.ll_theme_normal) {
                            getViewDataBinding().tvMusicNoLyric.setTextColor(getResources().getColor(R.color.black));
                        } else if(rThemeId == R.id.ll_theme_dark) {
                            getViewDataBinding().tvMusicNoLyric.setTextColor(getResources().getColor(R.color.white));
                        } else if (rThemeId == R.id.ll_theme_white) {
                            getViewDataBinding().tvMusicNoLyric.setTextColor(getResources().getColor(R.color.gray_purple_ac));
                        } else if(rThemeId == R.id.ll_theme_orange) {
                            getViewDataBinding().tvMusicNoLyric.setTextColor(getResources().getColor(R.color.orange_alpha_70));
                        } else if(rThemeId == R.id.ll_theme_light) {
                            getViewDataBinding().tvMusicNoLyric.setTextColor(getResources().getColor(R.color.light_b5));
                        } else {
                            getViewDataBinding().tvMusicNoLyric.setTextColor(getResources().getColor(R.color.black));
                        }
                    }
                }
                //歌词
                currentMusicLyric = event.str2;
                currentMusicLyricWithTime = event.str;
                //初始化当前歌词行数组（用于显示和变色）
                cLyricIntArray = new int[MusicPlayService.ARRAY_LENGTH];
                //歌词总行数
                int lyricSize = getViewDataBinding().tvMusicLyric.getText().toString().split("\n").length;

                int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
                int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
                getViewDataBinding().tvMusicLyric.measure(w, h);
                Log.e("ABMediaPlay","lineHeight" + getViewDataBinding().tvMusicLyric.getLineHeight());
                Log.e("ABMediaPlay", "height: " + getViewDataBinding().tvMusicLyric.getMeasuredHeight() + " line: " + getViewDataBinding().tvMusicLyric.getText().toString().split("\n").length);
                lyricViewHeight = getViewDataBinding().tvMusicLyric.getMeasuredHeight();
                clineHeight = lyricViewHeight/lyricSize;
                Log.e("ABMediaPlay", "mathLineheight: " + clineHeight);

               /* List<MusicLyric> currentMusicLyricList = new ArrayList<>();
                int lyricId =0;
                String[] str = currentMusicLyric.split("\n\n\n");
                for (int x=0; x<str.length; x++) {
                    if (!str[x].equals("")) {
                        lyricId++;
                        MusicLyric musicLyric = new MusicLyric();
                        musicLyric.setLyricId(lyricId);
                        musicLyric.setLyricContext(str[x]);
                        currentMusicLyricList.add(musicLyric);
                    }
                }
                currentLyricPos=0;
                musicLyricList.clear();
                musicLyricList.addAll(currentMusicLyricList);
                scrollLyricAdapter.notifyDataSetChanged();*/
                if(null != event.tList) {
                    musicLyricList.clear();
                    musicLyricList.addAll(event.tList);
                    scrollLyricAdapter.notifyDataSetChanged();
                }

                if(binder !=null) {
                    binder.player(event.music, event.b, event.str, musicLyricList);
                }
                break;
            case ThreadEvent.VIEW_SPANNABLE_LYRIC:
                getViewDataBinding().tvMusicLyric.setText(event.ssb);
                break;
            case ThreadEvent.DOWNLOAD_APP_START:
                showLoadingApp();
                break;
            case ThreadEvent.DOWNLOAD_APP_LOADING:
                if(null != downloadBinding) {
                    downloadBinding.tvValue.setText(""+event.i);
                }
                break;
            case ThreadEvent.DOWNLOAD_APP_SUCCESS:
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
            case ThreadEvent.DOWNLOAD_APP_ERROR:
                if(null != mAlertDialog) {
                    mAlertDialog.dismiss();
                }
                Toast.makeText(this, "app下载失败，请重新下载", Toast.LENGTH_SHORT).show();
                break;
            case ThreadEvent.BLUETOOTH_DISCONNECT:
                if(binder!=null) {
                    binder.pauseImm(this, currentMusicName, currentMusicSinger, currentBitmap);
                }
                break;
            case ThreadEvent.VIEW_SHOW_CHARACTER_TALK_CONTENT:
                getViewDataBinding().tvCharacterTalk.setText(event.str);
                break;
            case ThreadEvent.VIEW_HIDE_CHARACTER_TALK:
                getViewDataBinding().llCharacterTalk.setVisibility(View.GONE);
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
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void ThreadEvent(final ThreadEvent event) {
        switch (event.msgCode) {
            case ThreadEvent.CONNECT_MYSQL:
                MysqlHelper.connectDB();
                break;
            case ThreadEvent.DOWNLOAD_APP:
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
                    getViewModel().downloadUrl(event.str);
                } else {
                    getViewModel().downloadUrl(event.str);
                }
                break;
            case ThreadEvent.GET_DATA_LIST:
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_SUCCESS, MysqlHelper.getInstance().findMusicSql()));
                break;
            case ThreadEvent.GET_DATA_LIST_COUNT:
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_COUNT_SUCCESS, MysqlHelper.getInstance().findMusicCount()));
                break;
            case ThreadEvent.GET_DATA_APP_VERSION:
                EventBus.getDefault().post(new ThreadEvent<Version>(ThreadEvent.GET_APP_VERSION_SUCCESS, MysqlHelper.getInstance().findVersionSql(),""));
                break;
            case ThreadEvent.GET_DATA_LIST_MESSAGE:
                EventBus.getDefault().post(new ThreadEvent<Message>(ThreadEvent.GET_MESSAGE_SUCCESS, MysqlHelper.getInstance().findMessageSql(), ""));
                break;
            case ThreadEvent.GET_DATA_LIST_BY_LIELLA:
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql("Liella")));
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_COUNT_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeCount("Liella")));
                break;
            case ThreadEvent.GET_DATA_LIST_BY_FOUR_YUU:
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql("Fo(u)rYuU")));
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_COUNT_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeCount("Fo(u)rYuU")));
                break;
            case ThreadEvent.GET_DATA_LIST_BY_SUNNY_PASSION:
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql("SunnyPassion")));
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_COUNT_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeCount("SunnyPassion")));
                break;
            case ThreadEvent.GET_DATA_LIST_BY_NIJIGASAKI:
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql("Nijigasaki")));
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_COUNT_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeCount("Nijigasaki")));
                break;
            case ThreadEvent.GET_DATA_LIST_BY_AQOURS:
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql("Aqours")));
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_COUNT_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeCount("Aqours")));
                break;
            case ThreadEvent.GET_DATA_LIST_BY_US:
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeSql("μs")));
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_COUNT_SUCCESS, MysqlHelper.getInstance().findMusicByMusicTypeCount("μs")));
                break;
            case ThreadEvent.GET_MUSIC_LYRIC:
                cLyricScrollHeight=0;
                getViewModel().showLyric(event.music, event.b);
                break;
            case ThreadEvent.SCROLL_LYRIC:
                /*getViewDataBinding().clCurrentMusicPanel.post(new Runnable() {
                    @Override
                    public void run() {
                        getViewDataBinding().rvMusicLyricList.smoothScrollToPosition(event.i);
                    }
                });*/

               /* getViewDataBinding().svMusicLyric.post(new Runnable() {
                    @Override
                    public void run() {
                        *//*if(musicLyricList.size()>0) {
                            if(currentLyricPos < musicLyricList.size());{
                                currentLyricPos++;
                                getViewDataBinding().rvMusicLyricList.smoothScrollToPosition(currentLyricPos);
                            }
                        }*//*
                        if(event.intArray[0] !=0) {
                            if (cLyricScrollHeight < lyricViewHeight) {
                                //Log.e("ABMediaPlay", "clineHeight: " + clineHeight + " cLyricIntArray: "+ cLyricIntArray[0] + " " + cLyricIntArray[1] + " " + cLyricIntArray[2]);
                                getViewDataBinding().svMusicLyric.smoothScrollTo(0, clineHeight*cLyricIntArray[0]);
                                //getViewDataBinding().svMusicLyric.smoothScrollBy(0, clineHeight*4);
                                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.REMOVE_TIME_BY_LYRIC, event.intArray, event.strArray));
                                cLyricIntArray = event.intArray;
                            }
                        }

                    }
                });*/
                break;
            case ThreadEvent.SHOW_IMAGE_URL:  //设置状态栏显示对应图片
                getViewModel().showImageURL(event.str, event.str2, event.str3);
                break;

            case ThreadEvent.REMOVE_TIME_BY_LYRIC:
                if(!currentMusicLyricWithTime.equals("")) {
                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    int intArraySize = event.intArray.length;
                    int strArraySize = event.strArray.length;

                    //处理全歌词文本去除时间点 （除当前播放时间点所显示的歌词）
                    String[] str = currentMusicLyricWithTime.split("\n");
                    for (int x=0; x<str.length; x++) {
                        boolean isExist = false;
                        for(int y=0; y<strArraySize; y++) {
                            if(!event.strArray[y].equals("")) {
                                if(str[x].contains(event.strArray[y]))  {
                                    isExist=true;
                                    break;
                                }
                            }
                        }
                        builder.append(!isExist? str[x].substring(str[x].indexOf("]")+1) + "\n" : str[x] + "\n");
                    }

                    //指定行变色
                    String handleLyric = builder.toString();
                    for (int i = 0; i < intArraySize; i++) {
                        if (event.intArray[i] != 0 && !event.strArray[i].equals("")) {
                            int startLyric = handleLyric.indexOf(event.strArray[i]);
                            //int endLyric = startLyric + event.strArray[i].length();
                            int endLyric = startLyric + event.strArray[i].substring(0, event.strArray[i].indexOf("]")+1).length();
                            builder.replace(startLyric, endLyric, "");

                            int endLyricAfter = startLyric + event.strArray[i].substring(event.strArray[i].indexOf("]")+1).length();
                            //主题切换代码
                            if(rThemeId!=0) {
                                if(rThemeId == R.id.ll_theme_normal) {
                                    builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.light_ea)), startLyric, endLyricAfter, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                } else if(rThemeId == R.id.ll_theme_dark) {
                                    builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), startLyric, endLyricAfter, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                } else if (rThemeId == R.id.ll_theme_white) {
                                    builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.purple_light)), startLyric, endLyricAfter, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                } else if(rThemeId == R.id.ll_theme_orange) {
                                    builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange_f4)), startLyric, endLyricAfter, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                } else if(rThemeId == R.id.ll_theme_light) {
                                    builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.light_8a)), startLyric, endLyricAfter, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                } else {
                                    builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.light_ea)), startLyric, endLyricAfter, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                }
                            } else {
                                builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.light_ea)), startLyric, endLyricAfter, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            }
                            //Log.e("ABMusicPlayer",  " start: " + startLyric + " end: " + endLyric + " endAfter: " + endLyricAfter);
                            handleLyric = builder.toString();
                        }
                    }
                    //Log.e("ABMusicPlayer",  " int: " + Arrays.toString(event.intArray) + " str: " + Arrays.toString(event.strArray) + " currentLyric: " + event.strArray[0]);
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_SPANNABLE_LYRIC, builder));

                }
                break;
        }
    }

    /** 点击按钮事件 */
    public class ButtonClickListener implements View.OnClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.rl_play_controller) {
                Log.e("ABMediaPlay", "clickController");
            } else if(v.getId() == R.id.rl_show_loading) {
                Log.e("ABMediaPlay", "showLoading");
            } else if (v.getId() == R.id.rl_disable_click) {
                Log.e("ABMediaPlay", "clickDisableView");
                hideAllMusicView();
            } else if (v.getId() == R.id.ll_all_play) {
                allPlayMusic();
            } else if (v.getId() == R.id.ll_search) {
                searchMusic();
            } else if (v.getId() == R.id.ll_sort) {
                showSortMenuDialog(v);
            } else if (v.getId() == R.id.ll_cancel) {
                searchCancel();
            } else if (v.getId() == R.id.ll_settings) {
                intoSettings();
            } else if (v.getId() == R.id.ll_list_1) {
                getViewDataBinding().rlShowLoading.setVisibility(View.VISIBLE);
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_DATA_LIST_BY_LIELLA));
                getViewDataBinding().ivLogo.setBackgroundResource(R.mipmap.ic_album_liella_3);
                getViewDataBinding().tvTitleBar.setText("Liella!");
                getViewDataBinding().tvListMsgName1.setText("Liella!");
                getViewDataBinding().tvListMsgName2.setText("LoveLive!Superstar!!");

            } else if (v.getId() == R.id.ll_list_2) {
                getViewDataBinding().rlShowLoading.setVisibility(View.VISIBLE);
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_DATA_LIST_BY_FOUR_YUU));
                getViewDataBinding().ivLogo.setBackgroundResource(R.mipmap.ic_album_liyuu);
                getViewDataBinding().tvTitleBar.setText("Liyuu");
                getViewDataBinding().tvListMsgName1.setText("Liyuu");
                getViewDataBinding().tvListMsgName2.setText("Liyuu");

            } else if (v.getId() == R.id.ll_list_3) {
                getViewDataBinding().rlShowLoading.setVisibility(View.VISIBLE);
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_DATA_LIST_BY_SUNNY_PASSION));
                getViewDataBinding().ivLogo.setBackgroundResource(R.mipmap.ic_album_sunny_passion);
                getViewDataBinding().tvTitleBar.setText("SunnyPassion");
                getViewDataBinding().tvListMsgName1.setText("SunnyPassion");
                getViewDataBinding().tvListMsgName2.setText("サニーパッション");

            } else if (v.getId() == R.id.ll_list_4) {
                getViewDataBinding().rlShowLoading.setVisibility(View.VISIBLE);
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_DATA_LIST_BY_NIJIGASAKI));
                getViewDataBinding().ivLogo.setBackgroundResource(R.mipmap.ic_album_nijigasaki_3);
                getViewDataBinding().tvTitleBar.setText("虹ヶ咲学園スクールアイドル同好会");
                getViewDataBinding().tvListMsgName1.setText("Nijigasaki HighSchool IdolClub");
                getViewDataBinding().tvListMsgName2.setText("虹ヶ咲学園スクールアイドル同好会");

            } else if (v.getId() == R.id.ll_list_5) {
                getViewDataBinding().rlShowLoading.setVisibility(View.VISIBLE);
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_DATA_LIST_BY_AQOURS));
                getViewDataBinding().ivLogo.setBackgroundResource(R.mipmap.ic_album_aqours_3);
                getViewDataBinding().tvTitleBar.setText("Aqours");
                getViewDataBinding().tvListMsgName1.setText("Aqours");
                getViewDataBinding().tvListMsgName2.setText("LoveLive!Sunshine!!");

            } else if (v.getId() == R.id.ll_list_6) {
                getViewDataBinding().rlShowLoading.setVisibility(View.VISIBLE);
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_DATA_LIST_BY_US));
                getViewDataBinding().ivLogo.setBackgroundResource(R.mipmap.ic_album_us_3);
                getViewDataBinding().tvTitleBar.setText("μ's");
                getViewDataBinding().tvListMsgName1.setText("μ's");
                getViewDataBinding().tvListMsgName2.setText("国立音ノ木坂学院");

            } else if(v.getId() == R.id.ll_back) {
                searchCancel();
                ObjectAnimator mainPanelChangeObjectAnimator = MyAnimationUtil.objectAnimatorLeftOrRight(MainActivity.this, true, true, getViewDataBinding().clMain);
                ObjectAnimator detailPanelChangeObjectAnimator = MyAnimationUtil.objectAnimatorLeftOrRight(MainActivity.this, false, false, getViewDataBinding().clAlbumDetail);
                mainPanelChangeObjectAnimator.start();
                detailPanelChangeObjectAnimator.start();
                isNotMain = false;

            } else if(v.getId() == R.id.iv_character) {
                if(getViewDataBinding().llSayHello.getVisibility() == View.GONE &&
                        getViewDataBinding().llSayGood.getVisibility() == View.GONE) {
                    getViewDataBinding().llSayHello.setVisibility(View.VISIBLE);
                    getViewDataBinding().llSayGood.setVisibility(View.VISIBLE);
                } else {
                    getViewDataBinding().llSayHello.setVisibility(View.GONE);
                    getViewDataBinding().llSayGood.setVisibility(View.GONE);
                }
            } else if(v.getId() == R.id.ll_say_hello) {
                if(getViewDataBinding().llCharacterTalk.getVisibility() == View.GONE) {
                    getViewDataBinding().llCharacterTalk.setVisibility(View.VISIBLE);
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_SHOW_CHARACTER_TALK_CONTENT, CharacterHelper.sayHelloContent(mCharacterName)));
                    getViewModel().showCharacterContent();
                }
            } else if(v.getId() == R.id.ll_say_good) {
                if(getViewDataBinding().llCharacterTalk.getVisibility() == View.GONE) {
                    getViewDataBinding().llCharacterTalk.setVisibility(View.VISIBLE);
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_SHOW_CHARACTER_TALK_CONTENT, CharacterHelper.sayGoodContent(mCharacterName)));
                    getViewModel().showCharacterContent();
                }
            }
        }
    }

    /** 点击触摸事件 */
    public class ViewTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (R.id.iv_character == v.getId()) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mStartX = (int) event.getRawX();
                        mStartY = (int) event.getRawY();
                        sx = event.getRawX();
                        sy = event.getRawY();
                        mLastTime = System.currentTimeMillis();
                        isMove = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isMove = true;
                        int containerWidth = getViewDataBinding().rlCharacterView.getWidth();
                        int containerHeight = getViewDataBinding().rlCharacterView.getHeight();

                        float distanceX = sx - event.getRawX();
                        float distanceY = sy - event.getRawY();

                        float x = getViewDataBinding().clCharacter.getX() - distanceX;
                        float y = getViewDataBinding().clCharacter.getY() - distanceY;
                        // 不能移出屏幕
                        if (y < 0) {
                            y = 0;
                        } else if (y > containerHeight - getViewDataBinding().clCharacter.getHeight()) {
                            y = containerHeight - getViewDataBinding().clCharacter.getHeight();
                        }
                        if (x < 0) {
                            x = 0;
                        } else if (x > containerWidth - getViewDataBinding().clCharacter.getWidth()) {
                            x = containerWidth - getViewDataBinding().clCharacter.getWidth();
                        }
                        // 属性动画移动
                        CharacterHelper.moveCharacterView(getViewDataBinding().clCharacter, x, y);

                        sx = event.getRawX();
                        sy = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        long mCurrentTime = System.currentTimeMillis();
                        int mStopX = (int) event.getRawX();
                        int mStopY = (int) event.getRawY();
                        //判断时间
                        if (mCurrentTime - mLastTime < 500) {
                            //判断移动距离
                            if (Math.abs(mStartX - mStopX) >= 10 || Math.abs(mStartY - mStopY) >= 10) {
                                isMove = true;
                            } else {
                                isMove = false;
                            }
                        } else {
                            isMove = true;
                        }
                        break;
                }
                return isMove;
            } else {
                return true;
            }
        }
    }

    /** 获取版本code */
    public int getAppVersionCode(Context context) {
        int versionCode=0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
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
                if(null != versionList.get(0).versionUrl && !"".equals(versionList.get(0).versionUrl)) {
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP, versionList.get(0).versionUrl));
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
        if(rId == R.id.ll_theme_normal) {
            getViewDataBinding().rlPlayControllerIn.setBackgroundResource(R.drawable.shape_button_white_3);
            getViewDataBinding().clCurrentMusicPanel.setBackgroundResource(R.drawable.shape_button_white_3);
            getViewDataBinding().clCurrentMusicList.setBackgroundResource(R.drawable.shape_button_white_3);
            getViewDataBinding().tvDiscover.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvTitleBar.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvLiellaMusic.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvFourYuu.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvSunnyPassion.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvNijigasaki.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvListMsgName1.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvPlayAll.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvCancel.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvListMsgName2.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvSingle.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvCount.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvMusicCount.setTextColor(getResources().getColor(R.color.light_ff));

            getViewDataBinding().tvMusicName.setTextColor(getResources().getColor(R.color.black));
            getViewDataBinding().tvSingerName.setTextColor(getResources().getColor(R.color.black));
            getViewDataBinding().tvPlayMode.setTextColor(getResources().getColor(R.color.black));
            getViewDataBinding().tvListSize.setTextColor(getResources().getColor(R.color.black));
            getViewDataBinding().tvStartTime.setTextColor(getResources().getColor(R.color.black));
            getViewDataBinding().tvAllTime.setTextColor(getResources().getColor(R.color.black));
            getViewDataBinding().tvMusicLyric.setTextColor(getResources().getColor(R.color.black));
            getViewDataBinding().tvMusicNoLyric.setTextColor(getResources().getColor(R.color.black));

            getViewDataBinding().clBg.setBackgroundResource(R.mipmap.ic_gradient_color5);
            //getViewDataBinding().rlPlayController.setBackgroundResource(R.drawable.shape_button_alpha_50);
            getViewDataBinding().btCurrentList.setBackgroundResource(R.drawable.selector_music_list_2_selected);

            getViewDataBinding().llAllPlay.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llSearch.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llSort.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llSettings.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llSearchBar.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llCancel.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llBack.setBackgroundResource(R.drawable.selector_normal_selected);

            getViewDataBinding().ivAllPlay.setBackgroundResource(R.drawable.ic_play_mini_light);
            getViewDataBinding().ivSearch.setBackgroundResource(R.drawable.ic_search_light);
            getViewDataBinding().ivSort.setBackgroundResource(R.drawable.ic_sort_light);
            getViewDataBinding().ivSettings.setBackgroundResource(R.drawable.ic_settings_light);
            getViewDataBinding().ivDeleteAll.setBackgroundResource(R.drawable.ic_delete_black);
            getViewDataBinding().ivSearchMusic.setBackgroundResource(R.drawable.ic_search_light);
            getViewDataBinding().ivBack.setBackgroundResource(R.drawable.ic_arrow_back_light);
            getViewDataBinding().ivPanelLast.setBackgroundResource(R.drawable.selector_last_2_selected);
            getViewDataBinding().ivPanelNext.setBackgroundResource(R.drawable.selector_next_2_selected);

            getViewDataBinding().etSearchMusic.setHintTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().etSearchMusic.setTextColor(getResources().getColor(R.color.light_ff));

            if(playMode == 0) {
                getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
            } else if (playMode == 1) {
                getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
            } else if (playMode == 2) {
                getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
            }

            if(binder!=null) {
                if (binder.isPlay()) {
                    getViewDataBinding().btPlay.setBackgroundResource(R.drawable.selector_pause_black_selected);
                    getViewDataBinding().ivPanelPlay.setBackgroundResource(R.drawable.selector_pause_circle_black_selected);
                } else {
                    getViewDataBinding().btPlay.setBackgroundResource(R.drawable.selector_play_black_selected);
                    getViewDataBinding().ivPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_black_selected);
                }
            } else {
                getViewDataBinding().btPlay.setBackgroundResource(R.drawable.selector_play_black_selected);
                getViewDataBinding().ivPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_black_selected);
            }
            if(currentMusicImg!=null) {
                if (!currentMusicImg.equals("")) {
                    Glide.with(getApplication())
                            .setDefaultRequestOptions(requestOptions)
                            .load(currentMusicImg)
                            .transform(new CropCircleWithBorderTransformation(5, getResources().getColor(R.color.light_ea)))
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(getViewDataBinding().ivMusicImg);
                }
            }
            //解决seekBar滚动条变形问题
            Rect r = getViewDataBinding().sbMusicBar.getProgressDrawable().getBounds();
            getViewDataBinding().sbMusicBar.setThumb(getResources().getDrawable(R.drawable.shape_seek_bar_thumb));
            getViewDataBinding().sbMusicBar.setProgressDrawable(getResources().getDrawable(R.drawable.layer_seek_bar));
            getViewDataBinding().sbMusicBar.getProgressDrawable().setBounds(r);
            //getViewDataBinding().sbMusicBar.setProgressTintMode(PorterDuff.Mode.SRC_ATOP);
            //loading加载颜色
            getViewDataBinding().pbLoadingMusic.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.light_ea), PorterDuff.Mode.SRC_IN);
            getViewDataBinding().prLoading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.light_ea), PorterDuff.Mode.SRC_IN);
            getViewDataBinding().hpvProgress.setLinearGradient(R.color.light_ea);
        } else if(rId == R.id.ll_theme_dark) {
            getViewDataBinding().rlPlayControllerIn.setBackgroundResource(R.drawable.shape_button_black_2);
            getViewDataBinding().clCurrentMusicPanel.setBackgroundResource(R.drawable.shape_button_black_2);
            getViewDataBinding().clCurrentMusicList.setBackgroundResource(R.drawable.shape_button_black_2);
            getViewDataBinding().pbLoadingMusic.setProgressDrawable(getResources().getDrawable(R.color.gray_36));
            getViewDataBinding().tvDiscover.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvTitleBar.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvLiellaMusic.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvFourYuu.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvSunnyPassion.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvNijigasaki.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvListMsgName1.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvPlayAll.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvCancel.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvMusicCount.setTextColor(getResources().getColor(R.color.white));

            getViewDataBinding().tvListMsgName2.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvSingle.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvCount.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvMusicName.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvSingerName.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvPlayMode.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvListSize.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvStartTime.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvAllTime.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvMusicLyric.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvMusicNoLyric.setTextColor(getResources().getColor(R.color.white));

            getViewDataBinding().clBg.setBackgroundResource(R.mipmap.ic_gradient_color6);
            //getViewDataBinding().rlPlayController.setBackgroundResource(R.drawable.shape_button_orange_alpha_50);
            getViewDataBinding().btCurrentList.setBackgroundResource(R.drawable.ic_music_list);

            getViewDataBinding().llAllPlay.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llSearch.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llSort.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llSettings.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llSearchBar.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llCancel.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llBack.setBackgroundResource(R.drawable.selector_normal_selected);

            getViewDataBinding().ivAllPlay.setBackgroundResource(R.drawable.ic_play_mini_white);
            getViewDataBinding().ivSearch.setBackgroundResource(R.drawable.ic_search);
            getViewDataBinding().ivSort.setBackgroundResource(R.drawable.ic_sort);
            getViewDataBinding().ivSettings.setBackgroundResource(R.drawable.ic_settings);
            getViewDataBinding().ivDeleteAll.setBackgroundResource(R.drawable.ic_delete);
            getViewDataBinding().ivSearchMusic.setBackgroundResource(R.drawable.ic_search);
            getViewDataBinding().ivBack.setBackgroundResource(R.drawable.ic_arrow_back);
            getViewDataBinding().ivPanelLast.setBackgroundResource(R.drawable.ic_last);
            getViewDataBinding().ivPanelNext.setBackgroundResource(R.drawable.ic_next);

            getViewDataBinding().etSearchMusic.setHintTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().etSearchMusic.setTextColor(getResources().getColor(R.color.white));

            if(playMode == 0) {
                getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_order_play);
                getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play);
            } else if (playMode == 1) {
                getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_random_play);
                getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play);
            } else if (playMode == 2) {
                getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_single_play);
                getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play);
            }

            if(binder!=null) {
                if (binder.isPlay()) {
                    getViewDataBinding().btPlay.setBackgroundResource(R.drawable.ic_pause_2_white);
                    getViewDataBinding().ivPanelPlay.setBackgroundResource(R.drawable.ic_pause_circle_white);
                } else {
                    getViewDataBinding().btPlay.setBackgroundResource(R.drawable.ic_play_2_white);
                    getViewDataBinding().ivPanelPlay.setBackgroundResource(R.drawable.ic_play_circle_white);
                }
            } else {
                getViewDataBinding().btPlay.setBackgroundResource(R.drawable.ic_play_2_white);
                getViewDataBinding().ivPanelPlay.setBackgroundResource(R.drawable.ic_play_circle_white);
            }
            if(currentMusicImg!=null) {
                if(!currentMusicImg.equals("")) {
                    Glide.with(getApplication())
                            .setDefaultRequestOptions(requestOptions)
                            .load(currentMusicImg)
                            .transform(new CropCircleWithBorderTransformation(5, getResources().getColor(R.color.white)))
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(getViewDataBinding().ivMusicImg);
                }
            }
            //解决seekBar滚动条变形问题
            Rect r = getViewDataBinding().sbMusicBar.getProgressDrawable().getBounds();
            getViewDataBinding().sbMusicBar.setThumb(getResources().getDrawable(R.drawable.shape_seek_bar_thumb));
            getViewDataBinding().sbMusicBar.setProgressDrawable(getResources().getDrawable(R.drawable.layer_seek_bar_dark));
            getViewDataBinding().sbMusicBar.getProgressDrawable().setBounds(r);
            //getViewDataBinding().sbMusicBar.setProgressTintMode(PorterDuff.Mode.SRC_ATOP);
            //loading加载颜色
            getViewDataBinding().pbLoadingMusic.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.gray_36), PorterDuff.Mode.SRC_IN);
            getViewDataBinding().prLoading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.gray_36), PorterDuff.Mode.SRC_IN);
            getViewDataBinding().hpvProgress.setLinearGradient(R.color.white);
        } else if(rId == R.id.ll_theme_white) {
            getViewDataBinding().rlPlayControllerIn.setBackgroundResource(R.drawable.shape_button_white_3);
            getViewDataBinding().clCurrentMusicPanel.setBackgroundResource(R.drawable.shape_button_white_3);
            getViewDataBinding().clCurrentMusicList.setBackgroundResource(R.drawable.shape_button_white_3);
            getViewDataBinding().tvDiscover.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvTitleBar.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvLiellaMusic.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvFourYuu.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvSunnyPassion.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvNijigasaki.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvListMsgName1.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvMusicName.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvPlayAll.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvPlayMode.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvListSize.setTextColor(getResources().getColor(R.color.purple));
            getViewDataBinding().tvCancel.setTextColor(getResources().getColor(R.color.purple));

            getViewDataBinding().tvListMsgName2.setTextColor(getResources().getColor(R.color.gray_purple_ac));
            getViewDataBinding().tvSingle.setTextColor(getResources().getColor(R.color.gray_purple_ac));
            getViewDataBinding().tvCount.setTextColor(getResources().getColor(R.color.gray_purple_ac));
            getViewDataBinding().tvSingerName.setTextColor(getResources().getColor(R.color.gray_purple_ac));
            getViewDataBinding().tvStartTime.setTextColor(getResources().getColor(R.color.gray_purple_ac));
            getViewDataBinding().tvAllTime.setTextColor(getResources().getColor(R.color.gray_purple_ac));
            getViewDataBinding().tvMusicCount.setTextColor(getResources().getColor(R.color.gray_purple_ac));
            getViewDataBinding().tvMusicLyric.setTextColor(getResources().getColor(R.color.gray_purple_ac));
            getViewDataBinding().tvMusicNoLyric.setTextColor(getResources().getColor(R.color.gray_purple_ac));

            getViewDataBinding().clBg.setBackgroundResource(R.color.background_color_F2);
            //getViewDataBinding().rlPlayController.setBackgroundResource(R.drawable.shape_button_white_alpha_50);
            getViewDataBinding().btCurrentList.setBackgroundResource(R.drawable.ic_music_list_purple);

            getViewDataBinding().llAllPlay.setBackgroundResource(R.drawable.selector_white_theme_selected);
            getViewDataBinding().llSearch.setBackgroundResource(R.drawable.selector_white_theme_selected);
            getViewDataBinding().llSort.setBackgroundResource(R.drawable.selector_white_theme_selected);
            getViewDataBinding().llSettings.setBackgroundResource(R.drawable.selector_white_theme_selected);
            getViewDataBinding().llSearchBar.setBackgroundResource(R.drawable.selector_white_theme_selected);
            getViewDataBinding().llCancel.setBackgroundResource(R.drawable.selector_white_theme_selected);
            getViewDataBinding().llBack.setBackgroundResource(R.drawable.selector_white_theme_selected);


            getViewDataBinding().ivAllPlay.setBackgroundResource(R.drawable.ic_play_mini_purple);
            getViewDataBinding().ivSearch.setBackgroundResource(R.drawable.ic_search_purple);
            getViewDataBinding().ivSort.setBackgroundResource(R.drawable.ic_sort_purple);
            getViewDataBinding().ivSettings.setBackgroundResource(R.drawable.ic_settings_purple);
            getViewDataBinding().ivDeleteAll.setBackgroundResource(R.drawable.ic_delete_purple);
            getViewDataBinding().ivSearchMusic.setBackgroundResource(R.drawable.ic_search_purple);
            getViewDataBinding().ivBack.setBackgroundResource(R.drawable.ic_arrow_back_purple);
            getViewDataBinding().ivPanelLast.setBackgroundResource(R.drawable.ic_last_purple);
            getViewDataBinding().ivPanelNext.setBackgroundResource(R.drawable.ic_next_purple);

            getViewDataBinding().etSearchMusic.setHintTextColor(getResources().getColor(R.color.gray_purple_ac));
            getViewDataBinding().etSearchMusic.setTextColor(getResources().getColor(R.color.purple));


            if(playMode == 0) {
                getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_purple);
                getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_purple);
            } else if (playMode == 1) {
                getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_purple);
                getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_purple);
            } else if (playMode == 2) {
                getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_purple);
                getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_purple);
            }

            if(binder!=null) {
                if (binder.isPlay()) {
                    getViewDataBinding().btPlay.setBackgroundResource(R.drawable.selector_pause_purple_selected);
                    getViewDataBinding().ivPanelPlay.setBackgroundResource(R.drawable.selector_pause_circle_purple_selected);
                } else {
                    getViewDataBinding().btPlay.setBackgroundResource(R.drawable.selector_play_purple_selected);
                    getViewDataBinding().ivPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_purple_selected);
                }
            } else {
                getViewDataBinding().btPlay.setBackgroundResource(R.drawable.selector_play_purple_selected);
                getViewDataBinding().ivPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_purple_selected);
            }
            if(currentMusicImg!=null) {
                if (!currentMusicImg.equals("")) {
                    Glide.with(getApplication())
                            .setDefaultRequestOptions(requestOptions)
                            .load(currentMusicImg)
                            .transform(new CropCircleWithBorderTransformation(5, getResources().getColor(R.color.purple_light)))
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(getViewDataBinding().ivMusicImg);
                }
            }
            //解决seekBar滚动条变形问题
            Rect r = getViewDataBinding().sbMusicBar.getProgressDrawable().getBounds();
            getViewDataBinding().sbMusicBar.setThumb(getResources().getDrawable(R.drawable.shape_seek_bar_thumb));
            getViewDataBinding().sbMusicBar.setProgressDrawable(getResources().getDrawable(R.drawable.layer_seek_bar_purple));
            getViewDataBinding().sbMusicBar.getProgressDrawable().setBounds(r);
            //getViewDataBinding().sbMusicBar.setProgressTintMode(PorterDuff.Mode.SRC_ATOP);
            //loading加载颜色
            getViewDataBinding().pbLoadingMusic.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.purple_light), PorterDuff.Mode.SRC_IN);
            getViewDataBinding().prLoading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.purple_light), PorterDuff.Mode.SRC_IN);
            getViewDataBinding().hpvProgress.setLinearGradient(R.color.purple_light);
        } else if(rId == R.id.ll_theme_orange) {
            getViewDataBinding().rlPlayControllerIn.setBackgroundResource(R.drawable.shape_button_white_3);
            getViewDataBinding().clCurrentMusicPanel.setBackgroundResource(R.drawable.shape_button_white_3);
            getViewDataBinding().clCurrentMusicList.setBackgroundResource(R.drawable.shape_button_white_3);
            getViewDataBinding().pbLoadingMusic.setProgressDrawable(getResources().getDrawable(R.color.orange_0b));
            getViewDataBinding().tvDiscover.setTextColor(getResources().getColor(R.color.orange_f4));
            getViewDataBinding().tvTitleBar.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvLiellaMusic.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvFourYuu.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvSunnyPassion.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvNijigasaki.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvListMsgName1.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvPlayAll.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvCancel.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvMusicCount.setTextColor(getResources().getColor(R.color.orange_0b));

            getViewDataBinding().tvListMsgName2.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvSingle.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvCount.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvMusicName.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvSingerName.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvPlayMode.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvListSize.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvStartTime.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvAllTime.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvMusicLyric.setTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().tvMusicNoLyric.setTextColor(getResources().getColor(R.color.orange_0b));

            getViewDataBinding().clBg.setBackgroundResource(R.mipmap.ic_gradient_color7);
            //getViewDataBinding().rlPlayController.setBackgroundResource(R.drawable.shape_button_orange_alpha_50);
            getViewDataBinding().btCurrentList.setBackgroundResource(R.drawable.ic_music_list_orange);

            getViewDataBinding().llAllPlay.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llSearch.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llSort.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llSettings.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llSearchBar.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llCancel.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llBack.setBackgroundResource(R.drawable.selector_normal_selected);

            getViewDataBinding().ivAllPlay.setBackgroundResource(R.drawable.ic_play_mini_orange);
            getViewDataBinding().ivSearch.setBackgroundResource(R.drawable.ic_search_orange);
            getViewDataBinding().ivSort.setBackgroundResource(R.drawable.ic_sort_orange);
            getViewDataBinding().ivSettings.setBackgroundResource(R.drawable.ic_settings_orange);
            getViewDataBinding().ivDeleteAll.setBackgroundResource(R.drawable.ic_delete_orange);
            getViewDataBinding().ivSearchMusic.setBackgroundResource(R.drawable.ic_search_orange);
            getViewDataBinding().ivBack.setBackgroundResource(R.drawable.ic_arrow_back_orange);
            getViewDataBinding().ivPanelLast.setBackgroundResource(R.drawable.selector_last_orange_selected);
            getViewDataBinding().ivPanelNext.setBackgroundResource(R.drawable.selector_next_orange_selected);

            getViewDataBinding().etSearchMusic.setHintTextColor(getResources().getColor(R.color.orange_0b));
            getViewDataBinding().etSearchMusic.setTextColor(getResources().getColor(R.color.orange_0b));

            if(playMode == 0) {
                getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_orange);
                getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_orange);
            } else if (playMode == 1) {
                getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_orange);
                getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_orange);
            } else if (playMode == 2) {
                getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_orange);
                getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_orange);
            }

            if(binder!=null) {
                if (binder.isPlay()) {
                    getViewDataBinding().btPlay.setBackgroundResource(R.drawable.selector_pause_orange_selected);
                    getViewDataBinding().ivPanelPlay.setBackgroundResource(R.drawable.selector_pause_circle_orange_selected);
                } else {
                    getViewDataBinding().btPlay.setBackgroundResource(R.drawable.selector_play_orange_selected);
                    getViewDataBinding().ivPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_orange_selected);
                }
            } else {
                getViewDataBinding().btPlay.setBackgroundResource(R.drawable.selector_play_orange_selected);
                getViewDataBinding().ivPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_orange_selected);
            }
            if(currentMusicImg!=null) {
                if(!currentMusicImg.equals("")) {
                    Glide.with(getApplication())
                            .setDefaultRequestOptions(requestOptions)
                            .load(currentMusicImg)
                            .transform(new CropCircleWithBorderTransformation(5, getResources().getColor(R.color.orange_0b)))
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(getViewDataBinding().ivMusicImg);
                }
            }
            //解决seekBar滚动条变形问题
            Rect r = getViewDataBinding().sbMusicBar.getProgressDrawable().getBounds();
            getViewDataBinding().sbMusicBar.setThumb(getResources().getDrawable(R.drawable.shape_seek_bar_thumb));
            getViewDataBinding().sbMusicBar.setProgressDrawable(getResources().getDrawable(R.drawable.layer_seek_bar_orange));
            getViewDataBinding().sbMusicBar.getProgressDrawable().setBounds(r);
            //getViewDataBinding().sbMusicBar.setProgressTintMode(PorterDuff.Mode.SRC_ATOP);
            //loading加载颜色
            getViewDataBinding().pbLoadingMusic.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.orange_f4), PorterDuff.Mode.SRC_IN);
            getViewDataBinding().prLoading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.orange_f4), PorterDuff.Mode.SRC_IN);
            getViewDataBinding().hpvProgress.setLinearGradient(R.color.orange_0b);
        } else if(rId == R.id.ll_theme_light) {
            getViewDataBinding().rlPlayControllerIn.setBackgroundResource(R.drawable.shape_button_white_3);
            getViewDataBinding().clCurrentMusicPanel.setBackgroundResource(R.drawable.shape_button_white_3);
            getViewDataBinding().clCurrentMusicList.setBackgroundResource(R.drawable.shape_button_white_3);
            getViewDataBinding().tvDiscover.setTextColor(getResources().getColor(R.color.white));
            getViewDataBinding().tvTitleBar.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvLiellaMusic.setTextColor(getResources().getColor(R.color.light_b5));
            getViewDataBinding().tvFourYuu.setTextColor(getResources().getColor(R.color.light_b5));
            getViewDataBinding().tvSunnyPassion.setTextColor(getResources().getColor(R.color.light_b5));
            getViewDataBinding().tvNijigasaki.setTextColor(getResources().getColor(R.color.light_b5));
            getViewDataBinding().tvListMsgName1.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvPlayAll.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvCancel.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvListMsgName2.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvSingle.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvCount.setTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().tvMusicCount.setTextColor(getResources().getColor(R.color.light_ff));

            getViewDataBinding().tvPlayMode.setTextColor(getResources().getColor(R.color.light_b5));
            getViewDataBinding().tvListSize.setTextColor(getResources().getColor(R.color.light_b5));
            getViewDataBinding().tvMusicName.setTextColor(getResources().getColor(R.color.light_b5));
            getViewDataBinding().tvSingerName.setTextColor(getResources().getColor(R.color.light_b5));
            getViewDataBinding().tvStartTime.setTextColor(getResources().getColor(R.color.light_b5));
            getViewDataBinding().tvAllTime.setTextColor(getResources().getColor(R.color.light_b5));
            getViewDataBinding().tvMusicLyric.setTextColor(getResources().getColor(R.color.light_b5));
            getViewDataBinding().tvMusicNoLyric.setTextColor(getResources().getColor(R.color.light_b5));

            getViewDataBinding().clBg.setBackgroundResource(R.mipmap.ic_gradient_color4);
            //getViewDataBinding().rlPlayController.setBackgroundResource(R.drawable.shape_button_light_alpha_50);
            getViewDataBinding().btCurrentList.setBackgroundResource(R.drawable.ic_music_list_light);

            getViewDataBinding().llAllPlay.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llSearch.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llSort.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llSettings.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llSearchBar.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llCancel.setBackgroundResource(R.drawable.selector_normal_selected);
            getViewDataBinding().llBack.setBackgroundResource(R.drawable.selector_normal_selected);

            getViewDataBinding().ivAllPlay.setBackgroundResource(R.drawable.ic_play_mini_light);
            getViewDataBinding().ivSearch.setBackgroundResource(R.drawable.ic_search_light);
            getViewDataBinding().ivSort.setBackgroundResource(R.drawable.ic_sort_light);
            getViewDataBinding().ivSettings.setBackgroundResource(R.drawable.ic_settings_light);
            getViewDataBinding().ivDeleteAll.setBackgroundResource(R.drawable.ic_delete_light);
            getViewDataBinding().ivSearchMusic.setBackgroundResource(R.drawable.ic_search_light);
            getViewDataBinding().ivBack.setBackgroundResource(R.drawable.ic_arrow_back_light);
            getViewDataBinding().ivPanelLast.setBackgroundResource(R.drawable.ic_last_purple_light);
            getViewDataBinding().ivPanelNext.setBackgroundResource(R.drawable.ic_next_light);

            getViewDataBinding().etSearchMusic.setHintTextColor(getResources().getColor(R.color.light_ff));
            getViewDataBinding().etSearchMusic.setTextColor(getResources().getColor(R.color.light_ff));

            if(playMode == 0) {
                getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_light);
                getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_light);
            } else if (playMode == 1) {
                getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_light);
                getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_light);
            } else if (playMode == 2) {
                getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_light);
                getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_light);
            }

            if(binder!=null) {
                if (binder.isPlay()) {
                    getViewDataBinding().btPlay.setBackgroundResource(R.drawable.selector_pause_light_selected);
                    getViewDataBinding().ivPanelPlay.setBackgroundResource(R.drawable.selector_pause_circle_light_selected);
                } else {
                    getViewDataBinding().btPlay.setBackgroundResource(R.drawable.selector_play_light_selected);
                    getViewDataBinding().ivPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_light_selected);
                }
            } else {
                getViewDataBinding().btPlay.setBackgroundResource(R.drawable.selector_play_light_selected);
                getViewDataBinding().ivPanelPlay.setBackgroundResource(R.drawable.selector_play_circle_light_selected);
            }
            if(currentMusicImg!=null) {
                if (!currentMusicImg.equals("")) {
                    Glide.with(getApplication())
                            .setDefaultRequestOptions(requestOptions)
                            .load(currentMusicImg)
                            .transform(new CropCircleWithBorderTransformation(5, getResources().getColor(R.color.light_b5)))
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(getViewDataBinding().ivMusicImg);
                }
            }
            //解决seekBar滚动条变形问题
            Rect r = getViewDataBinding().sbMusicBar.getProgressDrawable().getBounds();
            getViewDataBinding().sbMusicBar.setThumb(getResources().getDrawable(R.drawable.shape_seek_bar_thumb));
            getViewDataBinding().sbMusicBar.setProgressDrawable(getResources().getDrawable(R.drawable.layer_seek_bar_light));
            getViewDataBinding().sbMusicBar.getProgressDrawable().setBounds(r);
            //getViewDataBinding().sbMusicBar.setProgressTintMode(PorterDuff.Mode.SRC_ATOP);
            //loading加载颜色
            getViewDataBinding().pbLoadingMusic.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.light_b5), PorterDuff.Mode.SRC_IN);
            getViewDataBinding().prLoading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.light_b5), PorterDuff.Mode.SRC_IN);
            getViewDataBinding().hpvProgress.setLinearGradient(R.color.light_b5);
        }
        musicListAdapter.notifyDataSetChanged();
        playMusicListAdapter.notifyDataSetChanged();
    }

    /** 点击展示悬浮窗角色系统菜单 */
    public void showCharacterAuthMenu(View view) {
        final DialogCharacterMenuBinding characterMenuBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
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

        PopupWindow popupWindow  = new PopupWindow(characterMenuBinding.getRoot(),
                PxUtil.getInstance().dp2px(110, this),  WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_button_menu));
        popupWindow.showAsDropDown(view,  PxUtil.getInstance().dp2px(-60, this),  PxUtil.getInstance().dp2px(10, this));


    }

    /** 点击展示授权悬浮窗显示角色系统 */
    public void showCharacterAuth(final String characterName) {
        //判断是否已经授权显示悬浮窗
        if(isCanDrawOverlays()) {
             if(isWorked(this, CharacterService.class.getPackage().getName()
                        + "." + CharacterService.class.getSimpleName())) {

                 stopService(intentCharacterService);
                 MainVM.stopHandler();           //关闭角色并停止handler
                 MainVM.stopTalkHandler();
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
            DialogDeleteListAllBinding deleteListAllBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                    R.layout.dialog_delete_list_all, null, false);

            deleteListAllBinding.dialogSelectTitle.setText("开启悬浮窗权限以展示角色系统");

            //取消
            deleteListAllBinding.btSelectIconCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlertDialog.dismiss();
                }
            });

            //授权开启
            deleteListAllBinding.btSelectIconCommit.setOnClickListener(new View.OnClickListener() {
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
                    .setView(deleteListAllBinding.getRoot())
                    .create();
            Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_2);
            mAlertDialog.show();
        }

    }

    /** 点击当前音乐img */
    public void musicImgClick(View view) {
        //Toast.makeText(this, "click img", Toast.LENGTH_SHORT).show();
        showOrHideMusicPlayerPanel();
    }
    /** 点击播放按钮 */
    public void playButtonClick(View view)  {
        //getViewModel().pause();
        binder.pause(this, currentMusicName, currentMusicSinger, currentBitmap);
    }

    /** 点击切换播放模式按钮 */
    public void changePlayModeButtonClick(View view)  {
        playModeButtonClick(view);
    }

    /** 点击当前列表按钮 */
    public void currentListButtonClick(View view)  {
        showOrHideMusicPlayerList();
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
    public void lastOrNextMusic(boolean isNext) {
        if(playList.size()>0) {
            if(playList.size() == 1) {
                binder.showLyric(playList.get(0), (playMode == 2));
            } else {
                for(int i=0; i<playList.size(); i++) {
                    if(playList.get(i).isPlaying) {
                        //如果当前模式为随机模式，点击播放下一首为随机歌曲
                        if(playMode ==1) {
                            playList.get(i).isPlaying = false;
                            int rand = new Random().nextInt(playList.size());
                            while (i == rand) {
                                rand = new Random().nextInt(playList.size());
                            }
                            binder.showLyric(playList.get(rand), false);
                            playList.get(rand).isPlaying = true;
                            playMusicListAdapter.notifyDataSetChanged();
                        } else if(playMode == 2) {
                            binder.showLyric(playList.get(i), true);
                        } else {
                            if (isNext) {
                                if (i + 1 < playList.size()) {
                                    playList.get(i).isPlaying = false;
                                    //getViewModel().player(playList.get(i + 1), (playMode == 2));
                                    //binder.player(playList.get(i+1), (playMode == 2));
                                    binder.showLyric(playList.get(i + 1), false);
                                    playList.get(i + 1).isPlaying = true;
                                    playMusicListAdapter.notifyDataSetChanged();
                                } else {
                                    playList.get(i).isPlaying = false;
                                    //getViewModel().player(playList.get(0), (playMode == 2));
                                    //binder.player(playList.get(0), (playMode == 2));
                                    binder.showLyric(playList.get(0), false);
                                    playList.get(0).isPlaying = true;
                                }
                            } else {
                                if (i == 0) {
                                    playList.get(0).isPlaying = false;
                                    binder.showLyric(playList.get(playList.size() - 1), false);
                                    playList.get(playList.size() - 1).isPlaying = true;
                                } else {
                                    playList.get(i).isPlaying = false;
                                    binder.showLyric(playList.get(i - 1), false);
                                    playList.get(i - 1).isPlaying = true;
                                }
                            }
                        }
                        playMusicListAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    }

    /** 显示当前播放列表 */
    public void showOrHideMusicPlayerList() {
        //Log.e("CLICK:", "isClick: " +isClick + " isShowMusicPanel: " + isShowMusicPanel + " isShowMusicList: " + isShowMusicList);
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

    /** 显示当前播放面板 */
    public void showOrHideMusicPlayerPanel() {
        //Log.e("CLICK:", "isClick: " +isClick + " isShowMusicPanel: " + isShowMusicPanel + " isShowMusicList: " + isShowMusicList);
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
    public void allPlayMusic() {
        if(musicListSize!=0) {
            playList.clear();
            List<Music> list = new ArrayList<>();
            for(Music music: musicList) {
                if(!music.getMusicType().equals(" ")) {
                    list.add(music);
                }
            }
            playList.addAll(list);
            playMusicListAdapter.notifyDataSetChanged();
            //保存当前列表数据
            SPUtil.setListValue(this, "PlayListData", playList);
            //播放当前第一首音乐
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.PLAY_LIST_FIRST));
            getViewDataBinding().tvListSize.setText("("+ playList.size() + ")");
        }
    }

    /** 点击查找歌曲按钮 */
    public void searchMusic() {
        tempMusicList = new ArrayList<>();
        tempMusicList.addAll(musicList);
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
        sortMenuBinding.ivSortByTimeType.setBackgroundResource(isUpSortByTime? R.drawable.ic_sort_up : R.drawable.ic_sort_down);
        sortMenuBinding.ivSortByNameType.setBackgroundResource(isUpSortByName? R.drawable.ic_sort_up : R.drawable.ic_sort_down);
        sortMenuBinding.ivSortBySingerType.setBackgroundResource(isUpSortBySinger? R.drawable.ic_sort_up : R.drawable.ic_sort_down);

        /* 按时间排序 */
        sortMenuBinding.llSortByTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUpSortByTime = !isUpSortByTime;
                sortList(0);
                sortMenuBinding.ivSortByTimeType.setBackgroundResource(isUpSortByTime? R.drawable.ic_sort_up : R.drawable.ic_sort_down);
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
                sortMenuBinding.ivSortByNameType.setBackgroundResource(isUpSortByName? R.drawable.ic_sort_up : R.drawable.ic_sort_down);
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
                sortMenuBinding.ivSortBySingerType.setBackgroundResource(isUpSortBySinger? R.drawable.ic_sort_up : R.drawable.ic_sort_down);
                sortMenuBinding.ivSortByTimeType.setVisibility(View.INVISIBLE);
                sortMenuBinding.ivSortByNameType.setVisibility(View.INVISIBLE);
                sortMenuBinding.ivSortBySingerType.setVisibility(View.VISIBLE);
                clickSortType = 2;
            }
        });

        PopupWindow popupWindow  = new PopupWindow(sortMenuBinding.getRoot(),
                PxUtil.getInstance().dp2px(150, this),  WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_button_menu));
        popupWindow.showAsDropDown(view,  PxUtil.getInstance().dp2px(-60, this),  PxUtil.getInstance().dp2px(10, this));
    }

    /** 按类型排序歌曲 */
    public  void sortList(final int sortType) {
        //将空数据提取 排序后再放到列表最底部
        List<Music> nullData = new ArrayList<>();
        for (Music music : musicList) {
            if(music.getMusicType().equals(" ")) {
                nullData.add(music);
            }
        }
        if(nullData.size() >0) {
            for (Music nullMusic : nullData) {
                musicList.remove(nullMusic);
            }
        }
        Collections.sort(musicList, new Comparator<Music>() {
            @Override
            public int compare(Music o1, Music o2) {
                Collator collator = Collator.getInstance();
                if(1 == sortType) {
                    CollationKey key1 = collator
                            .getCollationKey(String.valueOf(((Music) o1).musicName));
                    CollationKey key2 = collator
                            .getCollationKey(String.valueOf(((Music) o2).musicName));
                    return isUpSortByName ? key1.compareTo(key2) : key2.compareTo(key1);
                } else if (2 == sortType) {
                    CollationKey key1 = collator
                            .getCollationKey(String.valueOf(((Music) o1).musicSinger));
                    CollationKey key2 = collator
                            .getCollationKey(String.valueOf(((Music) o2).musicSinger));
                    return isUpSortBySinger ? key1.compareTo(key2) : key2.compareTo(key1);
                } else {
                    CollationKey key1 = collator
                            .getCollationKey(String.valueOf(((Music) o1).musicId));
                    CollationKey key2 = collator
                            .getCollationKey(String.valueOf(((Music) o2).musicId));
                    return isUpSortByTime ? key1.compareTo(key2) : key2.compareTo(key1);
                }
            }
        });

        if(nullData.size() >0) {
            musicList.addAll(nullData);
        }
        musicListAdapter.notifyDataSetChanged();


    }

    /** 点击取消搜索 */
    public void searchCancel() {
        musicList.clear();
        if(tempMusicList!=null) {
            musicList.addAll(tempMusicList);
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
            if (null != versionList && versionList.size() > 0) {
                intent.putExtra("NewVersionUrl", versionList.get(0).getVersionUrl());
                intent.putExtra("NewVersionTitle", versionList.get(0).getVersionTitle());
                intent.putExtra("NewVersionContent", versionList.get(0).getVersionContent());
            }
        }
        startActivity(intent);
    }

    /** 指定改变播放模式 */
    public void changePlayMode(int changePlayMode) {
        switch (changePlayMode) {
            case 0: //顺序播放
                binder.setSingePlayMode(false);
                //getViewModel().setSingePlayMode(false);
                getViewDataBinding().tvPlayMode.setText("顺序播放");
                if(rThemeId!=0) {
                    if(rThemeId == R.id.ll_theme_normal) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                    } else if(rThemeId == R.id.ll_theme_dark) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_order_play);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play);
                    } else if(rThemeId == R.id.ll_theme_white) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_purple);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_purple);
                    } else if(rThemeId == R.id.ll_theme_orange) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_orange);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_orange);
                    } else if(rThemeId == R.id.ll_theme_light) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_light);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_light);
                    } else {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                    }
                } else {
                    getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                    getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                }
                break;
            case 1: //随机播放
                binder.setSingePlayMode(false);
                //getViewModel().setSingePlayMode(false);
                getViewDataBinding().tvPlayMode.setText("随机播放");
                if(rThemeId!=0) {
                    if(rThemeId == R.id.ll_theme_normal) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                    } else if(rThemeId == R.id.ll_theme_dark) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_random_play);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play);
                    } else if(rThemeId == R.id.ll_theme_white) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_purple);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_purple);
                    } else if(rThemeId == R.id.ll_theme_orange) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_orange);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_orange);
                    } else if(rThemeId == R.id.ll_theme_light) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_light);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_light);
                    } else {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                    }
                } else {
                    getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                    getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                }
                break;
            case 2: //单曲循环
                binder.setSingePlayMode(true);
                //getViewModel().setSingePlayMode(true);
                getViewDataBinding().tvPlayMode.setText("单曲循环");
                if(rThemeId!=0) {
                    if(rThemeId == R.id.ll_theme_normal) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                    } else if(rThemeId == R.id.ll_theme_dark) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_single_play);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play);
                    } else if(rThemeId == R.id.ll_theme_white) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_purple);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_purple);
                    } else if(rThemeId == R.id.ll_theme_orange) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_orange);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_orange);
                    } else if(rThemeId == R.id.ll_theme_light) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_light);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_light);
                    } else {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                    }
                } else {
                    getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                    getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                }
                break;
        }
    }

    /** 点击切换播放模式 */
    public void playModeButtonClick(View view) {
        playMode = playMode!=2 ? playMode+1 : 0;
        SPUtil.setStrValue(getApplicationContext(), "SavePlayMode", String.valueOf(playMode));
        switch (playMode) {
            case 0: //顺序播放
                binder.setSingePlayMode(false);
                //getViewModel().setSingePlayMode(false);
                getViewDataBinding().tvPlayMode.setText("顺序播放");
                if(rThemeId!=0) {
                    if(rThemeId == R.id.ll_theme_normal) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                    } else if(rThemeId == R.id.ll_theme_dark) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_order_play);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play);
                    } else if(rThemeId == R.id.ll_theme_white) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_purple);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_purple);
                    } else if(rThemeId == R.id.ll_theme_orange) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_orange);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_orange);
                    } else if(rThemeId == R.id.ll_theme_light) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_light);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_light);
                    } else {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                    }
                } else {
                    getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                    getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_order_play_black);
                }
                break;
            case 1: //随机播放
                binder.setSingePlayMode(false);
                //getViewModel().setSingePlayMode(false);
                getViewDataBinding().tvPlayMode.setText("随机播放");
                if(rThemeId!=0) {
                    if(rThemeId == R.id.ll_theme_normal) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                    } else if(rThemeId == R.id.ll_theme_dark) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_random_play);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play);
                    } else if(rThemeId == R.id.ll_theme_white) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_purple);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_purple);
                    } else if(rThemeId == R.id.ll_theme_orange) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_orange);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_orange);
                    } else if(rThemeId == R.id.ll_theme_light) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_light);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_light);
                    } else {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                    }
                } else {
                    getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                    getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_random_play_black);
                }
                break;
            case 2: //单曲循环
                binder.setSingePlayMode(true);
                //getViewModel().setSingePlayMode(true);
                getViewDataBinding().tvPlayMode.setText("单曲循环");
                if(rThemeId!=0) {
                    if(rThemeId == R.id.ll_theme_normal) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                    } else if(rThemeId == R.id.ll_theme_dark) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_single_play);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play);
                    } else if(rThemeId == R.id.ll_theme_white) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_purple);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_purple);
                    } else if(rThemeId == R.id.ll_theme_orange) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_orange);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_orange);
                    } else if(rThemeId == R.id.ll_theme_light) {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_light);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_light);
                    } else {
                        getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                        getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                    }
                } else {
                    getViewDataBinding().ivPlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                    getViewDataBinding().btChangePlayMode.setBackgroundResource(R.drawable.ic_single_play_black);
                }
                break;
        }

    }

    /** 点击删除当前歌单列表所有歌曲 */
    public void deletePlayListAll(View view) {
        DialogDeleteListAllBinding deleteListAllBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_delete_list_all, null, false);

        //取消
        deleteListAllBinding.btSelectIconCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        //删除
        deleteListAllBinding.btSelectIconCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playList.clear();
                playMusicListAdapter.notifyDataSetChanged();
                SPUtil.setListValue(getApplicationContext(), "PlayListData", playList);
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DELETE_MUSIC));
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog = new AlertDialog.Builder(this)
                .setView(deleteListAllBinding.getRoot())
                .create();
        Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_2);
        mAlertDialog.show();

       /* PopupWindow  deleteAllWindow  = new PopupWindow(deleteListAllBinding.getRoot(),
                WindowManager.LayoutParams.WRAP_CONTENT,  WindowManager.LayoutParams.WRAP_CONTENT, true);
        deleteAllWindow.setTouchable(true);
        deleteAllWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_button_white_2));
        deleteAllWindow.showAsDropDown(view);*/


    }


    private class MusicBarChangerListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //拖动时改变显示时间
            getViewDataBinding().tvStartTime.setText(getViewModel().rebuildTime(progress));
            //Log.e("LogByAB", "progress: " + progress);
            lyricScrollView.setMusicPlayerPos(progress);
            getViewDataBinding().hpvProgress.setCurrentCount(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            lyricScrollView.setIsRefreshDraw(true);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //跳到拖动位置播放
            binder.seekTo(seekBar);
            lyricScrollView.setIsRefreshDraw(false);
        }
    }

    /** 拖动进度条调整 （解决拖动时与进度条正常播放移动 显示错乱）*/
    private class ProgressBarTouchListener implements SeekBar.OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.e("ABMediaPlay", "Click: down ");
                    binder.posLock(true);
                    break;
                case MotionEvent.ACTION_UP:
                    Log.e("ABMediaPlay", "Click: up ");
                    binder.posLock(false);
                    break;
            }
            return false;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("ABMusicPlayer", "Stop - UnbindService");
        EventBus.getDefault().unregister(this);
        if(binder!=null) {
            binder.pauseImm(this, currentMusicName, currentMusicSinger, currentBitmap);
        }
        MainVM.stopHandler();
        MainVM.stopTalkHandler();
        unbindService(conn);
        stopService(intentService);
        stopService(intentCharacterService);
        BluetoothUtil.getInstance().unRegisterBluetoothReceiver(this);
        getViewModel().stop();
    }

    /** 屏蔽返回键 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(isShowMusicPanel || isShowMusicList) {
                hideAllMusicView();
                return true;
            }
            if(isNotMain) {
                searchCancel();
                ObjectAnimator mainPanelChangeObjectAnimator = MyAnimationUtil.objectAnimatorLeftOrRight(MainActivity.this, true, true, getViewDataBinding().clMain);
                ObjectAnimator detailPanelChangeObjectAnimator = MyAnimationUtil.objectAnimatorLeftOrRight(MainActivity.this, false, false, getViewDataBinding().clAlbumDetail);
                mainPanelChangeObjectAnimator.start();
                detailPanelChangeObjectAnimator.start();
                isNotMain = false;
                return true;
            }
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


    /** 音乐进度条 */
    @Override
    public void viewSeekBarPos(int pos) {
        getViewDataBinding().sbMusicBar.setProgress(pos);
    }

    /** 音乐进度条进度初始化 */
    @Override
    public void viewSeekBarResume() {
        getViewDataBinding().sbMusicBar.setProgress(0);
    }

    /** 播放或暂停 */
    @Override
    public void viewPause(boolean isPause) {
        getViewDataBinding().btPlay.setBackgroundResource(isPause ? R.drawable.selector_play_black_selected : R.drawable.selector_pause_black_selected);
        getViewDataBinding().ivPanelPlay.setBackgroundResource(isPause ? R.drawable.selector_play_circle_black_selected : R.drawable.selector_pause_circle_black_selected);
        if (objectAnimator != null) {
            if (!isPause) {
                objectAnimator.resume();
            } else {
                objectAnimator.pause();
            }
        }
    }

    /** 点击歌曲后显示音乐信息 */
    @Override
    public void viewMusicMsg(Music source, int allPos) {
        currentAllTime = allPos;
        getViewDataBinding().sbMusicBar.setMax(allPos);
        getViewDataBinding().tvAllTime.setText(getViewModel().rebuildTime(allPos));
        String musicMsg = source.musicName;
        getViewDataBinding().tvMusicName.setText(musicMsg);
        getViewDataBinding().tvSingerName.setText(source.musicSinger);
        //变更主题
        if(rThemeId!=0) {
            if(rThemeId == R.id.ll_theme_normal) {
                Glide.with(getApplication())
                        .setDefaultRequestOptions(requestOptions)
                        .load(source.getMusicImg())
                        .transform(new CropCircleWithBorderTransformation(5, getResources().getColor(R.color.light_ea)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(getViewDataBinding().ivMusicImg);
            } else if(rThemeId == R.id.ll_theme_dark) {
                Glide.with(getApplication())
                        .setDefaultRequestOptions(requestOptions)
                        .load(source.getMusicImg())
                        .transform(new CropCircleWithBorderTransformation(5, getResources().getColor(R.color.white)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(getViewDataBinding().ivMusicImg);
            } else if(rThemeId == R.id.ll_theme_white) {
                Glide.with(getApplication())
                        .setDefaultRequestOptions(requestOptions)
                        .load(source.getMusicImg())
                        .transform(new CropCircleWithBorderTransformation(5, getResources().getColor(R.color.purple_light)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(getViewDataBinding().ivMusicImg);
            } else if(rThemeId == R.id.ll_theme_orange) {
                Glide.with(getApplication())
                        .setDefaultRequestOptions(requestOptions)
                        .load(source.getMusicImg())
                        .transform(new CropCircleWithBorderTransformation(5, getResources().getColor(R.color.orange_0b)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(getViewDataBinding().ivMusicImg);
            } else if(rThemeId == R.id.ll_theme_light) {
                Glide.with(getApplication())
                        .setDefaultRequestOptions(requestOptions)
                        .load(source.getMusicImg())
                        .transform(new CropCircleWithBorderTransformation(5, getResources().getColor(R.color.light_b5)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(getViewDataBinding().ivMusicImg);
            } else {
                Glide.with(getApplication())
                        .setDefaultRequestOptions(requestOptions)
                        .load(source.getMusicImg())
                        .transform(new CropCircleWithBorderTransformation(5, getResources().getColor(R.color.light_ea)))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(getViewDataBinding().ivMusicImg);
            }
        } else {
            Glide.with(getApplication())
                    .setDefaultRequestOptions(requestOptions)
                    .load(source.getMusicImg())
                    .transform(new CropCircleWithBorderTransformation(5, getResources().getColor(R.color.light_ea)))
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(getViewDataBinding().ivMusicImg);
        }

        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        objectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().ivMusicImg, "rotation", 0f, 360.0f);
        objectAnimator.setDuration(15000);
        objectAnimator.setInterpolator(new LinearInterpolator());//不停顿
        objectAnimator.setRepeatCount(-1);//设置动画重复次数
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);//动画重复模式
        objectAnimator.start();
    }



    public static class MusicListViewHolder extends RecyclerView.ViewHolder {
        public MusicListViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class MusicListAdapter extends RecyclerView.Adapter<MusicListViewHolder> {

        private Context context;
        private List<Music> list;
        private View mViewHeader;

        public static final int ITEM_TYPE_HEADER =0;
        public static final int ITEM_TYPE_CONTENT =1;

        public MusicListAdapter(Context context, List<Music> list) {
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
                if(rThemeId!=0) {
                    if(rThemeId == R.id.ll_theme_normal) {
                        binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                        binding.tvMusicName.setTextColor(getResources().getColor(R.color.light_ff));
                        binding.tvSingerName.setTextColor(getResources().getColor(R.color.light_ff));
                        binding.ivAdd.setBackgroundResource(R.drawable.ic_add_light);
                    } else if(rThemeId == R.id.ll_theme_dark) {
                        binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                        binding.tvMusicName.setTextColor(getResources().getColor(R.color.white));
                        binding.tvSingerName.setTextColor(getResources().getColor(R.color.white));
                        binding.ivAdd.setBackgroundResource(R.drawable.ic_add);
                    } else if (rThemeId == R.id.ll_theme_white) {
                        binding.rlMusicAll.setBackgroundResource(R.drawable.selector_white_theme_selected2);
                        binding.tvMusicName.setTextColor(getResources().getColor(R.color.purple));
                        binding.tvSingerName.setTextColor(getResources().getColor(R.color.gray_purple_ac));
                        binding.ivAdd.setBackgroundResource(R.drawable.ic_add_gray_purple);
                    } else if (rThemeId == R.id.ll_theme_orange) {
                        binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                        binding.tvMusicName.setTextColor(getResources().getColor(R.color.orange_0b));
                        binding.tvSingerName.setTextColor(getResources().getColor(R.color.orange_0b));
                        binding.ivAdd.setBackgroundResource(R.drawable.ic_add_orange);
                    } else if(rThemeId == R.id.ll_theme_light) {
                        binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                        binding.tvMusicName.setTextColor(getResources().getColor(R.color.light_ff));
                        binding.tvSingerName.setTextColor(getResources().getColor(R.color.light_ff));
                        binding.ivAdd.setBackgroundResource(R.drawable.ic_add_light);
                    } else {
                        binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                        binding.tvMusicName.setTextColor(getResources().getColor(R.color.light_ff));
                        binding.tvSingerName.setTextColor(getResources().getColor(R.color.light_ff));
                        binding.ivAdd.setBackgroundResource(R.drawable.ic_add_light);
                    }
                }
                binding.rlMusicAll.setVisibility(list.get(position).getMusicType().equals(" ") ? GONE : View.VISIBLE);
                Glide.with(getApplication())
                        .setDefaultRequestOptions(requestOptions)
                        .load(list.get(position).getMusicImg())
                        .transform(new RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.ALL))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(binding.ivMusicImg);
                binding.tvMusicName.setText(list.get(position).musicName);
                binding.tvSingerName.setText(list.get(position).musicSinger);
                //点击播放歌曲
                binding.rlMusicAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(playList.size()>0){
                            for(int i=0; i<playList.size(); i++) {
                                if(playList.get(i).isPlaying) {
                                    playList.get(i).isPlaying = false;
                                    binder.showLyric(list.get(position), (playMode == 2));
                                    playList.add(i+1, setMusicMsg(list.get(position), true));
                                    playMusicListAdapter.notifyDataSetChanged();

                                    SPUtil.setListValue(context, "PlayListData", playList);
                                    return;
                                }
                            }
                            binder.showLyric(list.get(position), (playMode == 2));
                            playList.add(playList.size(), setMusicMsg(list.get(position), true));
                            playMusicListAdapter.notifyDataSetChanged();

                            SPUtil.setListValue(context, "PlayListData", playList);
                        } else {
                            binder.showLyric(list.get(position), (playMode == 2));
                            playList.add(setMusicMsg(list.get(position), true));
                            playMusicListAdapter.notifyDataSetChanged();

                            SPUtil.setListValue(context, "PlayListData", playList);
                        }

                    }
                });

                //点击添加歌曲到列表
                binding.llAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //变更主题
                        if(rThemeId!=0) {
                            if (rThemeId == R.id.ll_theme_normal) {
                                binding.ivAddAnimator.setVisibility(View.GONE);
                                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                                binding.ivAddAnimatorLight1.setVisibility(View.VISIBLE);
                                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorLight1);
                                animatorSet.start();
                            } else if(rThemeId == R.id.ll_theme_dark) {
                                binding.ivAddAnimator.setVisibility(View.GONE);
                                binding.ivAddAnimatorBlack.setVisibility(View.VISIBLE);
                                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorBlack);
                                animatorSet.start();

                            } else if (rThemeId == R.id.ll_theme_white) {
                                binding.ivAddAnimator.setVisibility(View.VISIBLE);
                                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimator);
                                animatorSet.start();
                            } else if (rThemeId == R.id.ll_theme_orange) {
                                binding.ivAddAnimator.setVisibility(View.GONE);
                                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                                binding.ivAddAnimatorOrange.setVisibility(View.VISIBLE);
                                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorOrange);
                                animatorSet.start();
                            } else if(rThemeId == R.id.ll_theme_light) {
                                binding.ivAddAnimator.setVisibility(View.GONE);
                                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorLight2);
                                animatorSet.start();
                            } else {
                                binding.ivAddAnimator.setVisibility(View.GONE);
                                binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                                binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                                binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                                binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                                AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorLight1);
                                animatorSet.start();
                            }
                        } else {
                            binding.ivAddAnimator.setVisibility(View.GONE);
                            binding.ivAddAnimatorBlack.setVisibility(View.GONE);
                            binding.ivAddAnimatorOrange.setVisibility(View.GONE);
                            binding.ivAddAnimatorLight1.setVisibility(View.GONE);
                            binding.ivAddAnimatorLight2.setVisibility(View.GONE);
                            AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorLight1);
                            animatorSet.start();
                        }


                        playList.add(setMusicMsg(list.get(position), false));

                        if(playList.size()==1) {
                            playList.get(0).isPlaying = true;
                            binder.showLyric(playList.get(0), (playMode == 2));
                        } else {
                            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_ADD_MUSIC));
                        }
                        playMusicListAdapter.notifyDataSetChanged();

                        SPUtil.setListValue(context, "PlayListData", playList);
                    }
                });

                binding.llFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "喜爱", Toast.LENGTH_SHORT).show();
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

    /** 默认存储Music值 */
    private Music setMusicMsg(Music musicMsg, boolean isPlaying) {
        Music music = new Music();
        music.setMusicId(musicMsg.getMusicId());
        music.setMusicName(musicMsg.getMusicName());
        music.setMusicSinger(musicMsg.getMusicSinger());
        music.setMusicType(musicMsg.getMusicType());
        music.setMusicImg(musicMsg.getMusicImg());
        music.setMusicURL(musicMsg.getMusicURL());
        music.setMusicFavorite(musicMsg.getMusicFavorite());
        music.setMusicLyric(musicMsg.getMusicLyric());
        music.isPlaying = isPlaying;
        return music;
    }

    /** 存储列表时默认所有歌单为未播放状态 */
    private void setPlayListDefault(List<Music> playList) {
        List<Music> list = playList;
        int size = list.size();
        for(int i=0; i<size; i++) {
            list.get(i).isPlaying = false;
        }
    }

    public static class PlayMusicListViewHolder extends RecyclerView.ViewHolder {
        public PlayMusicListViewHolder(@NonNull View itemView) { super(itemView); }
    }
    public class PlayMusicListAdapter extends RecyclerView.Adapter<PlayMusicListViewHolder> {

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

                //变更主题
                if(rThemeId!=0) {
                    if(rThemeId == R.id.ll_theme_normal) {
                        binding.tvOrderNum.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.light_ea) : getResources().getColor(R.color.black));
                        binding.tvMusicName.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.light_ea) : getResources().getColor(R.color.black));
                        binding.tvSingerName.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.light_ea) : getResources().getColor(R.color.black));
                        binding.ivDelete.setBackgroundResource(R.drawable.selector_delete_selected_2);
                        binding.ivMusicRail.setVisibility(list.get(position).isPlaying? View.VISIBLE : GONE);
                        binding.ivMusicRailDark.setVisibility(View.GONE);
                        binding.ivMusicRailPurple.setVisibility(View.GONE);
                        binding.ivMusicRailOrange.setVisibility(View.GONE);
                        binding.ivMusicRailLight.setVisibility(View.GONE);
                    } else if (rThemeId == R.id.ll_theme_dark) {
                        binding.tvOrderNum.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));
                        binding.tvMusicName.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));
                        binding.tvSingerName.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));
                        binding.ivDelete.setBackgroundResource(R.drawable.ic_delete);
                        binding.ivMusicRail.setVisibility(View.GONE);
                        binding.ivMusicRailDark.setVisibility(list.get(position).isPlaying? View.VISIBLE : GONE);
                        binding.ivMusicRailPurple.setVisibility(View.GONE);
                        binding.ivMusicRailOrange.setVisibility(View.GONE);
                        binding.ivMusicRailLight.setVisibility(View.GONE);
                    } else if (rThemeId == R.id.ll_theme_white) {
                        binding.tvOrderNum.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.purple_light) : getResources().getColor(R.color.purple));
                        binding.tvMusicName.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.purple_light) : getResources().getColor(R.color.purple));
                        binding.tvSingerName.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.purple_light) : getResources().getColor(R.color.gray_purple_ac));
                        binding.ivDelete.setBackgroundResource(R.drawable.ic_delete_purple);
                        binding.ivMusicRail.setVisibility(View.GONE);
                        binding.ivMusicRailDark.setVisibility(View.GONE);
                        binding.ivMusicRailPurple.setVisibility(list.get(position).isPlaying? View.VISIBLE : GONE);
                        binding.ivMusicRailOrange.setVisibility(View.GONE);
                        binding.ivMusicRailLight.setVisibility(View.GONE);
                    } else if (rThemeId == R.id.ll_theme_orange) {
                        binding.tvOrderNum.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.orange_f4) : getResources().getColor(R.color.orange_0b));
                        binding.tvMusicName.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.orange_f4) : getResources().getColor(R.color.orange_0b));
                        binding.tvSingerName.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.orange_f4) : getResources().getColor(R.color.orange_0b));
                        binding.ivDelete.setBackgroundResource(R.drawable.ic_delete_orange);
                        binding.ivMusicRail.setVisibility(View.GONE);
                        binding.ivMusicRailDark.setVisibility(View.GONE);
                        binding.ivMusicRailPurple.setVisibility(View.GONE);
                        binding.ivMusicRailOrange.setVisibility(list.get(position).isPlaying? View.VISIBLE : GONE);
                        binding.ivMusicRailLight.setVisibility(View.GONE);
                    } else if(rThemeId == R.id.ll_theme_light) {
                        binding.tvOrderNum.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.light_8a) : getResources().getColor(R.color.light_b5));
                        binding.tvMusicName.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.light_8a) : getResources().getColor(R.color.light_b5));
                        binding.tvSingerName.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.light_8a) : getResources().getColor(R.color.light_b5));
                        binding.ivDelete.setBackgroundResource(R.drawable.ic_delete_light);
                        binding.ivMusicRail.setVisibility(View.GONE);
                        binding.ivMusicRailDark.setVisibility(View.GONE);
                        binding.ivMusicRailPurple.setVisibility(View.GONE);
                        binding.ivMusicRailOrange.setVisibility(View.GONE);
                        binding.ivMusicRailLight.setVisibility(list.get(position).isPlaying? View.VISIBLE : GONE);

                    } else {
                        binding.tvOrderNum.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.light_ea) : getResources().getColor(R.color.black));
                        binding.tvMusicName.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.light_ea) : getResources().getColor(R.color.black));
                        binding.tvSingerName.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.light_ea) : getResources().getColor(R.color.black));
                        binding.ivMusicRail.setVisibility(list.get(position).isPlaying? View.VISIBLE : GONE);
                        binding.ivDelete.setBackgroundResource(R.drawable.selector_delete_selected_2);
                        binding.ivMusicRail.setVisibility(list.get(position).isPlaying? View.VISIBLE : GONE);
                        binding.ivMusicRailDark.setVisibility(View.GONE);
                        binding.ivMusicRailPurple.setVisibility(View.GONE);
                        binding.ivMusicRailOrange.setVisibility(View.GONE);
                        binding.ivMusicRailLight.setVisibility(View.GONE);
                    }
                } else {
                    binding.tvOrderNum.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.light_ea) : getResources().getColor(R.color.black));
                    binding.tvMusicName.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.light_ea) : getResources().getColor(R.color.black));
                    binding.tvSingerName.setTextColor(list.get(position).isPlaying? getResources().getColor(R.color.light_ea) : getResources().getColor(R.color.black));
                    binding.ivMusicRail.setVisibility(list.get(position).isPlaying? View.VISIBLE : GONE);
                    binding.ivDelete.setBackgroundResource(R.drawable.selector_delete_selected_2);
                    binding.ivMusicRail.setVisibility(list.get(position).isPlaying? View.VISIBLE : GONE);
                    binding.ivMusicRailDark.setVisibility(View.GONE);
                    binding.ivMusicRailPurple.setVisibility(View.GONE);
                    binding.ivMusicRailOrange.setVisibility(View.GONE);
                    binding.ivMusicRailLight.setVisibility(View.GONE);
                }


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
                binding.llDelete.setVisibility(list.get(position).isPlaying? View.GONE : View.VISIBLE);
                binding.llDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!list.get(position).isPlaying) {
                            list.remove(position);
                            playMusicListAdapter.notifyDataSetChanged();
                            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DELETE_MUSIC));
                            SPUtil.setListValue(context, "PlayListData", list);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public static class ScrollLyricViewHolder extends RecyclerView.ViewHolder {
        public ScrollLyricViewHolder(@NonNull View itemView) { super(itemView); }
    }
    public class ScrollLyricAdapter extends RecyclerView.Adapter<ScrollLyricViewHolder> {

        private Context context;
        private List<MusicLyric> musicLyricList;

        public ScrollLyricAdapter(Context context, List<MusicLyric> musicLyricList) {
            this.context = context;
            this.musicLyricList = musicLyricList;
        }

        @NonNull
        @Override
        public ScrollLyricViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemLyricListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_lyric_list, parent, false);
            return new ScrollLyricViewHolder(binding.getRoot());
        }

        @Override
        public void onBindViewHolder(@NonNull ScrollLyricViewHolder holder, int position) {
            ItemLyricListBinding binding = DataBindingUtil.getBinding(holder.itemView);
            if(binding != null) {
                binding.tvMusicLyric.setText(musicLyricList.get(position).getLyricContext());
                binding.tvMusicLyric2.setVisibility(View.GONE);
                if(null != musicLyricList.get(position).getLyricContext2()) {
                    if(0 != musicLyricList.get(position).getLyricContext2().trim().length()) {
                        binding.tvMusicLyric2.setVisibility(View.VISIBLE);
                        binding.tvMusicLyric2.setText(musicLyricList.get(position).getLyricContext2());
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return musicLyricList.size();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        for (int i=0; i<permissions.length; i++) {
            if(permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP, versionList.get(0).versionUrl));
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}