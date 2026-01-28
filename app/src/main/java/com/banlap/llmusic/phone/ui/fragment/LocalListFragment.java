package com.banlap.llmusic.phone.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseActivity;
import com.banlap.llmusic.base.BaseApplication;
import com.banlap.llmusic.base.BaseFragment;
import com.banlap.llmusic.databinding.DialogDefaultBinding;
import com.banlap.llmusic.databinding.DialogInputContentBinding;
import com.banlap.llmusic.databinding.DialogLocalPlayListMenuBinding;
import com.banlap.llmusic.databinding.FragmentLocalListBinding;
import com.banlap.llmusic.databinding.ItemLocalMusicListBinding;
import com.banlap.llmusic.databinding.ItemLocalPlayListAddBinding;
import com.banlap.llmusic.databinding.ItemLocalPlayListBinding;
import com.banlap.llmusic.model.Music;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.phone.ui.activity.MainActivity;
import com.banlap.llmusic.phone.ui.ThemeHelper;
import com.banlap.llmusic.phone.uivm.fvm.LocalListFVM;
import com.banlap.llmusic.service.MusicPlayService;
import com.banlap.llmusic.sql.AppData;
import com.banlap.llmusic.sql.room.Converters;
import com.banlap.llmusic.sql.room.RoomCustomPlay;
import com.banlap.llmusic.sql.room.RoomFavoriteMusic;
import com.banlap.llmusic.sql.room.RoomLocalFile;
import com.banlap.llmusic.sql.room.RoomPlayMusic;
import com.banlap.llmusic.utils.AppExecutors;
import com.banlap.llmusic.utils.BitmapUtil;
import com.banlap.llmusic.utils.LLActivityManager;
import com.banlap.llmusic.utils.PermissionUtil;
import com.banlap.llmusic.utils.PxUtil;
import com.banlap.llmusic.utils.RecyclerViewUtils;
import com.banlap.llmusic.utils.SelectImgHelper;
import com.banlap.llmusic.utils.SystemUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import es.dmoral.toasty.Toasty;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * PlayList页面
 * */
public class LocalListFragment extends BaseFragment<LocalListFVM, FragmentLocalListBinding>
    implements LocalListFVM.LocalListCallBack {

    private final static String TAG = LocalListFragment.class.getSimpleName();
    private Context mContext;
    private AlertDialog mAlertDialog;                   //弹窗
    private List<RoomCustomPlay> roomCustomPlayList;    //自建歌单数据
    private List<RoomLocalFile> roomLocalFileList;       //本地歌单数据
    private List<RoomFavoriteMusic> roomFavoriteList;    //收藏夹歌单数据
    private PlayListAdapter playListAdapter;            //自建歌单适配器
    private LocalListAdapter localListAdapter;          //本地歌单适配器
    private FavoriteListAdapter favoriteListAdapter;    //收藏夹歌单适配器
    private boolean isShowLocalMusic = false;            //是否点击显示本地歌曲
    private boolean isShowFavoriteMusic = false;         //是否点击显示收藏歌曲

    private RequestOptions requestOptions;
    private int rThemeId =0;                             //当前主题
    private ActivityResultLauncher<Intent> intentSelectMusicLauncher; //选择音乐

    private DialogInputContentBinding inputContentBinding;
    private SelectImgHelper.SelectImgListener selectImgListener;   //自建歌单：图片裁切
    private Bitmap mBitmapImage;                         //自建歌单：裁切后的图片

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context; //保证Fragment即使在onDetach后，持有Activity的引用（有引起内存泄露的风险）
    }
        @Override
    protected int getLayoutId() {
        return R.layout.fragment_local_list;
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        roomCustomPlayList = new ArrayList<>();
        //自建歌单缓存列表
        if(!AppData.roomCustomPlayList.isEmpty()) {
            roomCustomPlayList.addAll(AppData.roomCustomPlayList);
        }
        AppData.addNullDataCustomPlay(roomCustomPlayList, 3);

        //本地歌曲缓存列表
        roomLocalFileList = new ArrayList<>();
        if(!AppData.roomLocalFileList.isEmpty()) {
            roomLocalFileList.addAll(AppData.roomLocalFileList);
        }
        AppData.addNullDataLocalFile(roomLocalFileList);

        //最爱歌曲缓存列表
        roomFavoriteList = new ArrayList<>();
        if(!AppData.roomFavoriteMusicList.isEmpty()) {
            roomFavoriteList.addAll(AppData.roomFavoriteMusicList);
        }
        AppData.addNullDataForFavorite(roomFavoriteList);

        requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.format(DecodeFormat.PREFER_RGB_565); //设置为这种格式去掉透明度通道，可以减少内存占有

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {
        //changeTheme();
        getViewDataBinding().clMain.setVisibility(View.VISIBLE);
        getViewDataBinding().clLocalMusicMain.setVisibility(View.GONE);

        playListAdapter = new PlayListAdapter(getContext(), roomCustomPlayList);
        getViewDataBinding().rvLocalPlayList.setLayoutManager(new LinearLayoutManager(getContext()));
        getViewDataBinding().rvLocalPlayList.setAdapter(playListAdapter);

        //当滑动列表时停止加载图片资源，不滑动时继续加载图片资源
        RecyclerViewUtils.scrollSuspend(getContext(), getViewDataBinding().rvLocalPlayList);
        //加大RecyclerView 的缓存，用空间换时间，来提高滚动的流畅性
        RecyclerViewUtils.setViewCache(getViewDataBinding().rvLocalPlayList);
        playListAdapter.notifyDataSetChanged();

        localListAdapter = new LocalListAdapter(getContext(), roomLocalFileList);
        getViewDataBinding().rvLocalMusicList.setLayoutManager(new LinearLayoutManager(getContext()));
        getViewDataBinding().rvLocalMusicList.setAdapter(localListAdapter);

        //当滑动列表时停止加载图片资源，不滑动时继续加载图片资源
        RecyclerViewUtils.scrollSuspend(getContext(), getViewDataBinding().rvLocalMusicList);
        //加大RecyclerView 的缓存，用空间换时间，来提高滚动的流畅性
        RecyclerViewUtils.setViewCache(getViewDataBinding().rvLocalMusicList);
        localListAdapter.notifyDataSetChanged();

        favoriteListAdapter = new FavoriteListAdapter(getContext(), roomFavoriteList);
        getViewDataBinding().rvFavoriteMusicList.setLayoutManager(new LinearLayoutManager(getContext()));
        getViewDataBinding().rvFavoriteMusicList.setAdapter(favoriteListAdapter);

        //当滑动列表时停止加载图片资源，不滑动时继续加载图片资源
        RecyclerViewUtils.scrollSuspend(getContext(), getViewDataBinding().rvFavoriteMusicList);
        //加大RecyclerView 的缓存，用空间换时间，来提高滚动的流畅性
        RecyclerViewUtils.setViewCache(getViewDataBinding().rvFavoriteMusicList);
        favoriteListAdapter.notifyDataSetChanged();

        if(roomLocalFileList != null) {
            getViewDataBinding().llLocalListNull.setVisibility(roomLocalFileList.size()>2 ? View.GONE : View.VISIBLE);
            int musicCount = roomLocalFileList.size()-2;
            getViewDataBinding().tvLocalMusicCount.setText("" + musicCount);
        }

        if(roomFavoriteList != null) {
            int musicCount = roomFavoriteList.size()-2;
            getViewDataBinding().tvFavoriteMusicCount.setText("" + musicCount);
        }

        intentSelectMusicLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(null != result.getData()) {
                            getViewModel().selectFile(result.getData());
                        }
                    }
                }
        );

        getViewDataBinding().llLocalMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowLocalMusic = true;
                isShowFavoriteMusic = false;
                getViewDataBinding().clMain.setVisibility(View.GONE);
                getViewDataBinding().rvLocalMusicList.setVisibility(View.VISIBLE);
                getViewDataBinding().rvFavoriteMusicList.setVisibility(View.GONE);
                getViewDataBinding().clLocalMusicMain.setVisibility(View.VISIBLE);

                getViewDataBinding().tvNullLocalList.setText("点击上方的菜单按钮导入本地歌曲");

                if(roomLocalFileList != null) {
                    int musicCount = roomLocalFileList.size()-2;
                    getViewDataBinding().tvMusicCount.setText("" + musicCount);
                    getViewDataBinding().llLocalListNull.setVisibility(musicCount >0 ? View.GONE : View.VISIBLE);
                }
                //编辑按钮重置
                if(roomLocalFileList != null && !roomLocalFileList.isEmpty()) {
                    for(int i=0; i< roomLocalFileList.size(); i++) {
                        roomLocalFileList.get(i).isDelete = false;
                    }
                    localListAdapter.notifyItemRangeChanged(0, roomLocalFileList.size(), R.id.iv_delete);
                }
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_CLICK_LOCAL_OR_FAVORITE));
            }
        });

        getViewDataBinding().llFavoriteMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowLocalMusic = false;
                isShowFavoriteMusic = true;
                getViewDataBinding().clMain.setVisibility(View.GONE);
                getViewDataBinding().rvLocalMusicList.setVisibility(View.GONE);
                getViewDataBinding().rvFavoriteMusicList.setVisibility(View.VISIBLE);
                getViewDataBinding().clLocalMusicMain.setVisibility(View.VISIBLE);

                getViewDataBinding().tvNullLocalList.setText("当前没有收藏歌曲");
                if(roomFavoriteList != null) {
                    int musicCount = roomFavoriteList.size()-2;
                    getViewDataBinding().tvMusicCount.setText("" + musicCount);
                    getViewDataBinding().llLocalListNull.setVisibility(musicCount >0 ? View.GONE : View.VISIBLE);
                }
                //编辑按钮重置
                if(roomFavoriteList != null && !roomFavoriteList.isEmpty()) {
                    for(int i=0; i< roomFavoriteList.size(); i++) {
                        roomFavoriteList.get(i).isDelete = false;
                    }
                    favoriteListAdapter.notifyItemRangeChanged(0, roomFavoriteList.size(), R.id.iv_delete);
                }
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_CLICK_LOCAL_OR_FAVORITE));
            }
        });

        getViewDataBinding().llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMain();
            }
        });

        getViewDataBinding().llEditDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShowLocalMusic) {
                    if(roomLocalFileList != null && !roomLocalFileList.isEmpty()) {
                        for(int i=0; i< roomLocalFileList.size(); i++) {
                            roomLocalFileList.get(i).isDelete = !roomLocalFileList.get(i).isDelete;
                        }
                        localListAdapter.notifyItemRangeChanged(0, roomLocalFileList.size(), R.id.iv_delete);
                    }
                } else if(isShowFavoriteMusic) {
                    if(roomFavoriteList != null && !roomFavoriteList.isEmpty()) {
                        for(int i=0; i< roomFavoriteList.size(); i++) {
                            roomFavoriteList.get(i).isDelete = !roomFavoriteList.get(i).isDelete;
                        }
                        favoriteListAdapter.notifyItemRangeChanged(0, roomFavoriteList.size(), R.id.iv_delete);
                    }
                }
            }
        });

        getViewDataBinding().ablAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

            }
        });

        getViewModel(LocalListFVM.class).getThemeId().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer themeId) {
                changeTheme(themeId);
            }
        });

    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEvent(ThreadEvent event) {
        switch (event.msgCode) {
            case ThreadEvent.VIEW_SCAN_LOCAL_FILE_BY_CHECK_PERMISSION:
                checkScanPermission(event.str);
                break;
            case ThreadEvent.VIEW_SCAN_LOCAL_FILE_SUCCESS:
                if(roomLocalFileList != null  && event.tList != null) {
                    roomLocalFileList.clear();
                    roomLocalFileList.addAll(event.tList);
                    getViewDataBinding().llLocalListNull.setVisibility(roomLocalFileList.size()>2 ? View.GONE : View.VISIBLE);
                    localListAdapter.notifyDataSetChanged();

                    getViewDataBinding().tvMusicCount.setText("" + (roomLocalFileList.size()-2));
                    getViewDataBinding().tvLocalMusicCount.setText("" + (roomLocalFileList.size()-2));
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(roomLocalFileList.isEmpty()) {
                                    return;
                                }
                                Thread.sleep(10);
                                AppData.saveLocalFileMusicList(roomLocalFileList);
                            } catch (Exception e) {
                                Log.e(TAG, "id error");
                            }
                        }
                    });

                   // SPUtil.setListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.LocalListData, mLocalMusicList);
                }
                break;
            case ThreadEvent.VIEW_SCAN_LOCAL_FILE_ERROR:
                break;
            case ThreadEvent.VIEW_SELECT_LOCAL_FILE_SUCCESS:
                if(roomLocalFileList != null) {
                    List<RoomLocalFile> localFileList = new ArrayList<>(roomLocalFileList);

                    roomLocalFileList.clear();
                    roomLocalFileList.addAll(event.tList);
                    roomLocalFileList.addAll(localFileList);

                    getViewDataBinding().llLocalListNull.setVisibility(roomLocalFileList.size()>2 ? View.GONE : View.VISIBLE);
                    localListAdapter.notifyDataSetChanged();

                    getViewDataBinding().tvMusicCount.setText("" + (roomLocalFileList.size()-2));
                    getViewDataBinding().tvLocalMusicCount.setText("" + (roomLocalFileList.size()-2));
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(roomLocalFileList.isEmpty()) {
                                    return;
                                }
                                Thread.sleep(10);
                                AppData.saveLocalFileMusicList(roomLocalFileList);
                            } catch (Exception e) {
                                Log.e(TAG, "roomLocalFileList id error");
                            }
                        }
                    });
                    //SPUtil.setListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.LocalListData, mLocalMusicList);
                }
                break;

            case ThreadEvent.VIEW_SELECT_IMG_FILE_SUCCESS:
                if(selectImgListener != null) {
                    SelectImgHelper.getInstance().startSelectImg(getContext(), selectImgListener);
                    selectImgListener = null;
                }
                break;
            case ThreadEvent.VIEW_DELETE_LOCAL_MUSIC:
                getViewDataBinding().tvMusicCount.setText(""+(roomLocalFileList.size()-2));
                getViewDataBinding().tvLocalMusicCount.setText("" + (roomLocalFileList.size()-2));
                getViewDataBinding().llLocalListNull.setVisibility(roomLocalFileList.size()>2 ? View.GONE : View.VISIBLE);
                break;
            case ThreadEvent.VIEW_DELETE_FAVORITE_MUSIC:
                getViewDataBinding().tvMusicCount.setText(""+(roomFavoriteList.size()-2));
                getViewDataBinding().tvFavoriteMusicCount.setText("" + (roomFavoriteList.size()-2));
                break;
            case ThreadEvent.VIEW_SAVE_FAVORITE_MUSIC:
                if(event.roomPlayMusic != null) {
                    String roomFavoriteMusicStr = Converters.fromPlayMusicList(Collections.singletonList(event.roomPlayMusic));
                    List<RoomFavoriteMusic> list = Converters.toFavoriteMusicList(roomFavoriteMusicStr);

                    BaseActivity<? extends ViewModel, ? extends ViewDataBinding> activity = LLActivityManager.getInstance().getTopActivity();
                    if (activity == null || activity.isFinishing() || list == null) return;

                    final RoomFavoriteMusic roomPlayMusic = list.get(0);

                    //mFavoriteList = Collections.synchronizedList(new ArrayList<>());
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            Set<RoomFavoriteMusic> uniqueMusicSet = new LinkedHashSet<>(); // 使用LinkedHashSet去重并保留顺序
                            uniqueMusicSet.add(roomPlayMusic);
                            uniqueMusicSet.addAll(roomFavoriteList);
                            List<RoomFavoriteMusic> newFavoriteList = new ArrayList<>(uniqueMusicSet);
                            AppData.saveFavoriteList(newFavoriteList);
//                            List<RoomPlayMusic> spList2 = SPUtil.getListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.FavoriteListData, Music.class);
                            //List<Music> newFavoriteList = new ArrayList<>(uniqueMusicSet);
                            //SPUtil.setListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.FavoriteListData, newFavoriteList);
                            activity.runOnUiThread(()->{
                                roomFavoriteList.clear();
                                roomFavoriteList.addAll(uniqueMusicSet);
                                favoriteListAdapter.notifyDataSetChanged();

                                Toasty.success(activity, "添加收藏成功", Toast.LENGTH_SHORT, true).show();

                                getViewDataBinding().tvFavoriteMusicCount.setText(""+(roomFavoriteList.size()-2));
                                getViewDataBinding().tvMusicCount.setText(""+(roomFavoriteList.size()-2));
                                //刷新ui
                                if(roomFavoriteList != null) {
                                    int musicCount = roomFavoriteList.size()-2;
                                    getViewDataBinding().llLocalListNull.setVisibility(musicCount >0 ? View.GONE : View.VISIBLE);
                                }
                                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_FRESH_FAVORITE_MUSIC));
                                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_UPDATE_IS_CAN_CLICK_FAVORITE));
                            });
                        }
                    });
                }
                break;
            case ThreadEvent.VIEW_CANCEL_FAVORITE_MUSIC:
                if(!roomFavoriteList.isEmpty()) {
                    for(int i=0; i< roomFavoriteList.size(); i++) {
                        if(event.roomPlayMusic.musicId == roomFavoriteList.get(i).musicId) {
                            RoomFavoriteMusic deleteFavoriteMusic = roomFavoriteList.get(i);
                            roomFavoriteList.remove(i);
                            favoriteListAdapter.notifyItemRemoved(i);
                            favoriteListAdapter.notifyItemRangeChanged(i, roomFavoriteList.size());
                            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DELETE_FAVORITE_MUSIC));
                            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    AppData.deleteFavoriteMusic(deleteFavoriteMusic);
                                }
                            });
                            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_UPDATE_IS_CAN_CLICK_FAVORITE));
                            break;
                        }
                    }

                    //刷新ui
                    int musicCount = roomFavoriteList.size()-2;
                    getViewDataBinding().llLocalListNull.setVisibility(musicCount >0 ? View.GONE : View.VISIBLE);

                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_FRESH_FAVORITE_MUSIC));
                }
                break;
            case ThreadEvent.VIEW_ADD_MUSIC_TO_LOCAL_PLAY_LIST:
            case ThreadEvent.VIEW_DELETE_MUSIC_TO_LOCAL_PLAY_LIST:
                List<RoomCustomPlay> roomCustomPlays = AppData.roomCustomPlayList;
                if(!roomCustomPlays.isEmpty()) {
                    roomCustomPlayList.clear();
                    roomCustomPlayList.addAll(roomCustomPlays);
                    playListAdapter.notifyDataSetChanged();
                }
                break;
            case ThreadEvent.VIEW_HIDE_LOCAL_OR_FAVORITE:
                backToMain();
                break;
        }
    }

    /** 返回到主页 */
    private void backToMain() {
        getViewDataBinding().clMain.setVisibility(View.VISIBLE);
        getViewDataBinding().clLocalMusicMain.setVisibility(View.GONE);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 变更主题
     * */
    private void changeTheme(int themeId) {
        rThemeId = themeId;
        //主题变更
        ThemeHelper.getInstance().localListFragmentTheme(getContext(), themeId, getViewDataBinding());
        if(playListAdapter != null) {
            playListAdapter.notifyDataSetChanged();
        }
        if(localListAdapter != null) {
            localListAdapter.notifyDataSetChanged();
        }
        if(favoriteListAdapter != null) {
            favoriteListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void viewBack() {

    }

    /** 检查权限 */
    private void checkScanPermission(String type) {
        String[] permissions = { Manifest.permission.READ_EXTERNAL_STORAGE };
        //Android 13中 READ_EXTERNAL_STORAGE 已失效,则使用新的权限 READ_MEDIA_AUDIO
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{ Manifest.permission.READ_MEDIA_AUDIO };
        }
        //检查权限是否开启
        if(PermissionUtil.getInstance().checkPermission(getContext(), permissions)) {
            //申请权限
            if("scan".equals(type)) {
                ActivityCompat.requestPermissions(getActivity(), permissions, MainActivity.REQUEST_CODE_SCAN_LOCAL_FILE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), permissions, MainActivity.REQUEST_CODE_SELECT_LOCAL_FILE);
            }
            return;
        }

        if("scan".equals(type)) {
            scan();
        } else {
            selectFile();
        }

    }

    /** 扫描文件 */
    private void scan() {
        getViewModel().scan();
    }

    /** 文件选择 */
    private void selectFile() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intentSelectMusicLauncher.launch(intent);
    }


    /** 弹窗 新增自建歌单列表 */
    private void addNewPlayList() {
        showEditPlayListDialog("新建歌单", "请输入歌单名称", 0,true);
    }

    /** 弹窗 修改自建歌单列表 */
    private void editPlayListMsg(int position) {
        showEditPlayListDialog("修改歌单", "请输入歌单名称", position,false);
    }

    /** 弹窗 自建歌单 */
    private void showEditPlayListDialog(String titleName, String content, int position, boolean isAddList) {
        if(mBitmapImage != null) {
            mBitmapImage.recycle();
            mBitmapImage = null;
        }
        inputContentBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_input_content, null, false);
        inputContentBinding.tvTitle.setText(titleName);
        inputContentBinding.etInput.setHint(content);
        final RoomCustomPlay roomCustomPlay = roomCustomPlayList.get(position);
        //修改歌单时显示对应信息
        if(!isAddList) {
            inputContentBinding.etInput.setText(roomCustomPlay.playListName);
            if(roomCustomPlay.playListImgByte != null) {
                Bitmap bitmap = BitmapUtil.getInstance().showBitmapOrigin(roomCustomPlay.playListImgByte);
                inputContentBinding.civImage.setImageBitmap(bitmap);
            }
        }
        inputContentBinding.etInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        inputContentBinding.civImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImgHelper.getInstance().startSelectImg(getActivity(), new SelectImgHelper.SelectImgListener() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        if(inputContentBinding != null) {
                            inputContentBinding.civImage.setImageBitmap(bitmap);
                            mBitmapImage = bitmap;
                        }
                    }

                    @Override
                    public void onError() {
                        //获取bitmap失败
                    }

                    @Override
                    public void onNeedPermission(Context context, String[] permission, SelectImgHelper.SelectImgListener listener) {
                        ActivityCompat.requestPermissions(getActivity(), permission, MainActivity.REQUEST_CODE_SELECT_IMG_FILE);
                        selectImgListener = listener;
                    }
                });
            }
        });

        inputContentBinding.btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAlertDialog != null) {
                    mAlertDialog.dismiss();
                }
            }
        });

        inputContentBinding.btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputContentBinding.etInput.getText().toString();
                if(!TextUtils.isEmpty(content)) {
                    if(mAlertDialog != null) {
                        mAlertDialog.dismiss();
                    }
                    //分支判断 是否添加歌单
                    if(isAddList) {
                        if(roomCustomPlayList.size()>=3) {
                            roomCustomPlayList.clear();

                            RoomCustomPlay roomCustomPlay = new RoomCustomPlay();
                            roomCustomPlay.playListId = (int) MusicPlayService.createMusicId();
                            roomCustomPlay.playListName = content;
                            roomCustomPlay.playListCount = 0;
                            roomCustomPlay.musicListJson = "";

                            if(mBitmapImage != null) {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                mBitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] bitmapByte = baos.toByteArray();
                                roomCustomPlay.playListImgByte = bitmapByte;
                            }

                            roomCustomPlayList.add(roomCustomPlay);

                            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    List<RoomCustomPlay> customPlayList = BaseApplication.llMusicDatabase.customPlayDao().getAllCustomPlay();
                                    LLActivityManager.getInstance().getTopActivity().runOnUiThread(() -> {
                                        if(!customPlayList.isEmpty()) {
                                            roomCustomPlayList.addAll(customPlayList);
                                        }
                                        AppData.addNullDataCustomPlay(roomCustomPlayList, 3);
                                        playListAdapter.notifyDataSetChanged();
                                    });
                                    AppData.saveRoomCustomPlay(roomCustomPlay);
                                    AppData.roomCustomPlayList.clear();
                                    AppData.roomCustomPlayList.addAll(roomCustomPlayList);
                                }
                            });
                        }
                    } else {
                        int playListId = roomCustomPlay.playListId;
                        byte[] bytes = null;
                        if(mBitmapImage != null) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            mBitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            bytes =  baos.toByteArray();
                        }
                        final byte[] byteImg = bytes;
                        roomCustomPlayList.get(position).playListName = content;
                        roomCustomPlayList.get(position).playListImgByte = byteImg;
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                AppData.updateRoomCustomPlay(playListId, customPlay -> {
                                    customPlay.playListName = content;
                                    if(byteImg != null) {
                                        customPlay.playListImgByte = byteImg;
                                    }
                                });
                            }
                        });
                        //SPUtil.setListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.LocalPlayListData, mLocalPlayList);
                        playListAdapter.notifyDataSetChanged();
                    }
                    
                } else {
                    Toasty.warning(LLActivityManager.getInstance().getTopActivity(), "请输入歌单名称!", Toast.LENGTH_SHORT, true).show();
                }
            }
        });

        mAlertDialog = new AlertDialog.Builder(getContext())
                .setView(inputContentBinding.getRoot())
                .create();

        Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_3);
        mAlertDialog.show();
    }

    /** 弹窗 自建歌单菜单选项 */
    @SuppressLint("SetTextI18n")
    private void showPlayListMenu(View view, int position, String playListName, int playListCount) {
        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_SHOW_OR_HIDE_MASKING_BACKGROUND, true));
        DialogLocalPlayListMenuBinding localPlayListMenuBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_local_play_list_menu, null, false);
        localPlayListMenuBinding.tvPlayListName.setText(playListName);
        localPlayListMenuBinding.tvPlayListCount.setText(playListCount + " 首");

        PopupWindow localPlayListMenuPopup  = new PopupWindow(localPlayListMenuBinding.getRoot(),
                PxUtil.getInstance().dp2px(300, view.getContext()),  WindowManager.LayoutParams.WRAP_CONTENT, true);
        localPlayListMenuPopup.setTouchable(true);
        localPlayListMenuPopup.showAtLocation(getView(), Gravity.CENTER, 0,0);

        localPlayListMenuPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_SHOW_OR_HIDE_MASKING_BACKGROUND, false));
            }
        });

        //点击编辑歌单信息
        localPlayListMenuBinding.llEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localPlayListMenuPopup.dismiss();
                editPlayListMsg(position);
            }
        });
        
        //点击删除当前歌单
        localPlayListMenuBinding.llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localPlayListMenuPopup.dismiss();
                DialogDefaultBinding defaultBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_default, null, false);
                defaultBinding.dialogSelectTitle.setText("是否删除当前歌单？");

                defaultBinding.btSelectIconCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mAlertDialog != null) {
                            mAlertDialog.dismiss();
                        }
                    }
                });

                //删除当前歌单
                defaultBinding.btSelectIconCommit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mAlertDialog != null) {
                            mAlertDialog.dismiss();
                        }
                        if(roomCustomPlayList.size() > position) {
                            RoomCustomPlay roomCustomPlay = roomCustomPlayList.get(position);
                            roomCustomPlayList.remove(position);
                            playListAdapter.notifyItemRemoved(position);  //移除了视图但没有进行重新bind的过程，所以此position还是之前的position，因此需要调用notifyItemRangeChanged
                            playListAdapter.notifyItemRangeChanged(position, roomCustomPlayList.size());
                            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    AppData.deleteCustomPlayList(roomCustomPlay);
                                    AppData.roomCustomPlayList.clear();
                                    AppData.roomCustomPlayList.addAll(roomCustomPlayList);
                                }
                            });
                        }

                    }
                });

                mAlertDialog = new AlertDialog.Builder(getContext())
                        .setView(defaultBinding.getRoot())
                        .create();

                Objects.requireNonNull(mAlertDialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_button_white_2);
                mAlertDialog.show();
                mAlertDialog.getWindow().setLayout((int)(SystemUtil.getInstance().getDM(LLActivityManager.getInstance().getTopActivity()).widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);

            }
        });
    }

    /** 自建歌曲清单列表 */
    public static class PlayListViewHolder extends RecyclerView.ViewHolder {
        public PlayListViewHolder(@NonNull View itemView) { super(itemView); }
    }
    public class PlayListAdapter extends RecyclerView.Adapter<PlayListViewHolder> {

        private Context context;
        private List<RoomCustomPlay> customPlayList;
        private final int viewTypeAddList = 0;
        private final int viewTypeShowList = 1;


        public PlayListAdapter(Context context, List<RoomCustomPlay> localPlayList) {
            this.context = context;
            this.customPlayList = localPlayList;
        }

        @Override
        public int getItemViewType(int position) {
            if(position == customPlayList.size()-3) { //倒数前两条未空数据
                return viewTypeAddList;
            }
            return viewTypeShowList;
        }

        @NonNull
        @Override
        public PlayListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType == viewTypeAddList) {
                ItemLocalPlayListAddBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_local_play_list_add, parent, false);
                return new PlayListViewHolder(binding.getRoot());
            }
            ItemLocalPlayListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_local_play_list, parent, false);
            return new PlayListViewHolder(binding.getRoot());
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull PlayListViewHolder holder, @SuppressLint("RecyclerView") int position) {
            if(holder.getItemViewType() == viewTypeAddList) {
                ItemLocalPlayListAddBinding binding = DataBindingUtil.getBinding(holder.itemView);
                if(binding != null) {
                   ThemeHelper.getInstance().addLocalPlayListTheme(context, rThemeId, binding);
                   binding.rlPlayListAdd.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           if(roomCustomPlayList.size() >=20) {
                               Toasty.warning(LLActivityManager.getInstance().getTopActivity(), "仅支持添加20个歌单!", Toast.LENGTH_SHORT, true).show();
                               return;
                           }
                           addNewPlayList();
                       }
                   });
                }
            } else {
                ItemLocalPlayListBinding binding = DataBindingUtil.getBinding(holder.itemView);
                if(binding != null) {
                    ThemeHelper.getInstance().localPlayListTheme(context, rThemeId, binding);
                    if(TextUtils.isEmpty(customPlayList.get(position).playListName)) {
                        binding.rlMusicAll.setVisibility(View.INVISIBLE);
                    } else {
                        binding.rlMusicAll.setVisibility(View.VISIBLE);
                        binding.tvPlayListName.setText(customPlayList.get(position).playListName);
                        binding.tvPlayListCount.setText(customPlayList.get(position).playListCount + " 首");
                        if(customPlayList.get(position).playListImgByte != null) {
                            Bitmap bitmap = BitmapUtil.getInstance().showBitmapOrigin(customPlayList.get(position).playListImgByte);
                            binding.ivMusicImg.setImageBitmap(bitmap);
                        } else {
                            binding.ivMusicImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_music_cover_4));
                        }
                        binding.rlMusicAll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.THREAD_GET_DATA_LIST_BY_LOCAL_PLAY, customPlayList.get(position)));
                            }
                        });
                        binding.llMenu.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showPlayListMenu(v, position, customPlayList.get(position).playListName, customPlayList.get(position).playListCount);
                            }
                        });
                    }
                }
            }

        }

        @Override
        public int getItemCount() {
            return customPlayList.size();
        }
    }

    /** 本地歌曲列表ViewHolder */
    public static class LocalListViewHolder extends RecyclerView.ViewHolder {
        public LocalListViewHolder(@NonNull View itemView) { super(itemView); }
    }
    public class LocalListAdapter extends RecyclerView.Adapter<LocalListViewHolder> {

        private Context context;
        private List<RoomLocalFile> localFileList;

        public LocalListAdapter(Context context, List<RoomLocalFile> localFileList) {
            this.context = context;
            this.localFileList = localFileList;
        }

        @NonNull
        @Override
        public LocalListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemLocalMusicListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_local_music_list, parent, false);
            return new LocalListViewHolder(binding.getRoot());
        }

        @Override
        public void onBindViewHolder(@NonNull LocalListViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            final ItemLocalMusicListBinding binding = DataBindingUtil.getBinding(holder.itemView);
            if (binding != null) {
                //变更主题
                ThemeHelper.getInstance().localListTheme(context, rThemeId, binding);

                binding.llDelete.setVisibility(localFileList.get(position).isDelete? View.VISIBLE :View.GONE);
                binding.rlMusicAll.setVisibility(localFileList.get(position).title !=null? View.VISIBLE :View.GONE);
                binding.tvMusicName.setText(localFileList.get(position).title);
                binding.tvSingerName.setText(localFileList.get(position).artist);

                if(null != localFileList.get(position).pic) {
                    byte[] b = localFileList.get(position).pic;
                    Bitmap bitmap = BitmapUtil.getInstance().showBitmapOrigin(b);
                    binding.ivMusicImg.setImageBitmap(bitmap);
                } else {
                    binding.ivMusicImg.setImageResource(R.mipmap.ic_llmp_new);
                }

                //点击播放歌曲列表
                binding.rlMusicAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = new Random().nextInt(99999) + 10000;
                        RoomPlayMusic music = new RoomPlayMusic();
                        music.musicId = id;
                        music.musicName = localFileList.get(position).title;
                        music.musicSinger = localFileList.get(position).artist;
                        music.musicURL = localFileList.get(position).path;
                        music.musicImgByte = localFileList.get(position).pic;
                        music.isLocal = true;
                        EventBus.getDefault().post(new ThreadEvent<RoomPlayMusic>(ThreadEvent.VIEW_PLAY_LOCAL_MUSIC, music, false));
                    }
                });

                //点击添加歌曲到列表
                binding.llAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //变更主题
                        ThemeHelper.getInstance().localListAddButtonAnimatorTheme(rThemeId, binding);

                        int id = new Random().nextInt(99999) + 10000;
                        RoomPlayMusic music = new RoomPlayMusic();
                        music.musicId = id;
                        music.musicName = localFileList.get(position).title;
                        music.musicSinger = localFileList.get(position).artist;
                        music.musicURL = localFileList.get(position).path;
                        music.musicImgByte = localFileList.get(position).pic;
                        music.isLocal = true;
                        EventBus.getDefault().post(new ThreadEvent<Music>(ThreadEvent.VIEW_ADD_LOCAL_MUSIC, music, false));
                    }
                });

                //点击删除歌曲
                binding.llDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RoomLocalFile roomLocalFile = localFileList.get(position);
                        localFileList.remove(position);
                        localListAdapter.notifyItemRemoved(position);
                        localListAdapter.notifyItemRangeChanged(position, roomLocalFileList.size() - position);

                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                AppData.deleteLocalFileMusic(roomLocalFile);
                            }
                        });

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DELETE_LOCAL_MUSIC));
                        }, 200);
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return localFileList.size();
        }
    }

    /** 收藏歌曲列表ViewHolder */
    public static class FavoriteListViewHolder extends RecyclerView.ViewHolder {
        public FavoriteListViewHolder(@NonNull View itemView) { super(itemView); }
    }
    public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListViewHolder> {

        private Context context;
        private List<RoomFavoriteMusic> favoriteList;

        public FavoriteListAdapter(Context context, List<RoomFavoriteMusic> favoriteList) {
            this.context = context;
            this.favoriteList = favoriteList;
        }

        @NonNull
        @Override
        public FavoriteListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemLocalMusicListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_local_music_list, parent, false);
            return new FavoriteListViewHolder(binding.getRoot());
        }

        @Override
        public void onBindViewHolder(@NonNull FavoriteListViewHolder holder, @SuppressLint("RecyclerView") int position) {
            final ItemLocalMusicListBinding binding = DataBindingUtil.getBinding(holder.itemView);
            if (binding != null) {
                //变更主题
                ThemeHelper.getInstance().localListTheme(context, rThemeId, binding);

                binding.llDelete.setVisibility(favoriteList.get(position).isDelete ? View.VISIBLE : View.GONE);
                binding.rlMusicAll.setVisibility(favoriteList.get(position).musicName !=null? View.VISIBLE :View.GONE);
                binding.tvMusicName.setText(favoriteList.get(position).musicName);
                binding.tvSingerName.setText(favoriteList.get(position).musicSinger);

                if(favoriteList.get(position).isLocal) {
                    if(null != favoriteList.get(position).musicImgByte) {
                        byte[] b = favoriteList.get(position).musicImgByte;
                        Bitmap bitmap = BitmapUtil.getInstance().showBitmapOrigin(b);
                        binding.ivMusicImg.setImageBitmap(bitmap);
                    } else {
                        binding.ivMusicImg.setImageResource(R.mipmap.ic_llmp_new);
                    }
                } else {
                    Glide.with(context)
                            .setDefaultRequestOptions(requestOptions)
                            .load(favoriteList.get(position).musicImg)
                            .transform(new RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.ALL))
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(binding.ivMusicImg);
                }

                //点击播放列表的歌曲
                binding.rlMusicAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new ThreadEvent<RoomFavoriteMusic>(ThreadEvent.VIEW_PLAY_FAVORITE_MUSIC, favoriteList, position));
                    }
                });

                //点击添加歌曲到列表
                binding.llAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //变更主题
                        ThemeHelper.getInstance().localListAddButtonAnimatorTheme(rThemeId, binding);
                        EventBus.getDefault().post(new ThreadEvent<RoomFavoriteMusic>(ThreadEvent.VIEW_ADD_FAVORITE_MUSIC, favoriteList, position));
                    }
                });


                //点击删除歌曲
                binding.llDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RoomFavoriteMusic deleteFavoriteMusic = favoriteList.get(position);
                        favoriteList.remove(position);
                        favoriteListAdapter.notifyItemRemoved(position);
                        favoriteListAdapter.notifyItemRangeChanged(position, favoriteList.size());
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_DELETE_FAVORITE_MUSIC));
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                AppData.deleteFavoriteMusic(deleteFavoriteMusic);
                            }
                        });
                        //SPUtil.setListValue(LLActivityManager.getInstance().getTopActivity(), SPUtil.FavoriteListData, favoriteList);
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_FRESH_FAVORITE_MUSIC));
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return favoriteList.size();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
