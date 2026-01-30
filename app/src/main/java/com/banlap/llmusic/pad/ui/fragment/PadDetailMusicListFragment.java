package com.banlap.llmusic.pad.ui.fragment;

import static android.view.View.GONE;

import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseFragment;
import com.banlap.llmusic.databinding.ActivityPadMainBinding;
import com.banlap.llmusic.databinding.DialogSortMenuBinding;
import com.banlap.llmusic.databinding.FragmentPadDetailMusicListBinding;
import com.banlap.llmusic.databinding.ItemMusicListBinding;
import com.banlap.llmusic.fixed.AppMusic;
import com.banlap.llmusic.model.Music;
import com.banlap.llmusic.pad.ui.activity.PadMainActivity;
import com.banlap.llmusic.pad.uivm.fvm.PadDetailMusicListFVM;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.sql.room.RoomPlayMusic;
import com.banlap.llmusic.utils.MyAnimationUtil;
import com.banlap.llmusic.utils.PxUtil;
import com.banlap.llmusic.utils.RecyclerViewUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.OkHttpClient;

/**
 *
 * */
public class PadDetailMusicListFragment extends BaseFragment<PadDetailMusicListFVM, FragmentPadDetailMusicListBinding>
    implements PadDetailMusicListFVM.PadDetailMusicListCallBack {

    private static List<RoomPlayMusic> roomPlayMusicList;
    private List<RoomPlayMusic> roomTempMusicList;                  //临时音乐列表
    private RequestOptions requestOptions;
    private int rThemeId =0;                             //当前主题
    private int clickSortType = 0;                       //当前点击的排序类型
    private boolean isUpSortByTime = false;               //是否向上排序：时间
    private boolean isUpSortByName = false;               //是否向上排序：歌曲名称
    private boolean isUpSortBySinger = false;             //是否向上排序：歌手名称
    private boolean isSearchMusic = false;              //判断是否处于搜索音乐状态

    private ActivityPadMainBinding activityPadMainBinding;
    private MusicListAdapter musicListAdapter;
    private OnBackPressedCallback backPressedCallback;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pad_detail_music_list;
    }

    @Override
    protected void initData() {
        activityPadMainBinding = ((PadMainActivity) getActivity()).getBinding();
        roomPlayMusicList = new ArrayList<>();
    }


    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        getViewDataBinding().rlShowLoading.setVisibility(View.VISIBLE);

        initPadMainView();
        initListener();
    }

    private void initListener() {
        if(activityPadMainBinding != null){
            activityPadMainBinding.llBack.setVisibility(View.VISIBLE);
            activityPadMainBinding.llBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_PAD_CHANGE_LAST_FRAGMENT));
                }
            });
        }

        backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };

        getViewDataBinding().etSearchMusic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String name = getViewDataBinding().etSearchMusic.getText().toString();
                if (!name.equals("")) {

                    List<RoomPlayMusic> nullData = roomTempMusicList.stream()
                            .filter(tempMusic -> tempMusic.musicType.equals(" "))
                            .collect(Collectors.toList());

                    List<RoomPlayMusic> searchList = roomTempMusicList.stream()
                            .filter(tempMusic -> tempMusic.musicName.toLowerCase().contains(name.toLowerCase()))
                            .collect(Collectors.toList());

                    if (searchList.size() > 0) {
                        roomPlayMusicList.clear();
                        roomPlayMusicList.addAll(searchList);
                        roomPlayMusicList.addAll(nullData);
                        musicListAdapter.notifyDataSetChanged();
                        getViewDataBinding().tvNoSearchMusic.setVisibility(View.GONE);
                    } else {
                        roomPlayMusicList.clear();
                        musicListAdapter.notifyDataSetChanged();
                        getViewDataBinding().tvNoSearchMusic.setVisibility(View.VISIBLE);
                    }
                } else {
                    roomPlayMusicList.clear();
                    if(roomTempMusicList!=null) {
                        roomPlayMusicList.addAll(roomTempMusicList);
                    }
                    musicListAdapter.notifyDataSetChanged();
                    getViewDataBinding().tvNoSearchMusic.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getViewDataBinding().llSort.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llAllPlay.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llSearch.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llCancel.setOnClickListener(new ButtonClickListener());
    }

    private void initPadMainView() {

        musicListAdapter = new MusicListAdapter(getActivity(), roomPlayMusicList);
        getViewDataBinding().rvMusicList.setLayoutManager(new LinearLayoutManager(getActivity()));
        getViewDataBinding().rvMusicList.setAdapter(musicListAdapter);

        //当滑动列表时停止加载图片资源，不滑动时继续加载图片资源
        RecyclerViewUtils.scrollSuspend(getActivity(), getViewDataBinding().rvMusicList);
        //加大RecyclerView 的缓存，用空间换时间，来提高滚动的流畅性
        RecyclerViewUtils.setViewCache(getViewDataBinding().rvMusicList);

        musicListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
    @Override
    public void viewBack() {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden) {
            getViewDataBinding().rlShowLoading.setVisibility(View.VISIBLE);
            roomPlayMusicList.clear();
            roomPlayMusicList.addAll(AppMusic.getInstance().getNullMusicData());
            musicListAdapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEvent(ThreadEvent event) {
        switch (event.msgCode) {
            case ThreadEvent.VIEW_GET_ALBUM_LIST_SUCCESS:
                getViewDataBinding().rlShowLoading.setVisibility(View.GONE);
                roomPlayMusicList.clear();
                roomPlayMusicList.addAll(event.tList);
                roomPlayMusicList.addAll(AppMusic.getInstance().getNullMusicData());
                sortList(0);
                clickSortType = 0;
                musicListAdapter.notifyDataSetChanged();
                break;
            case ThreadEvent.VIEW_GET_ALBUM_COUNT_SUCCESS:
                getViewDataBinding().tvMusicCount.setText(""+event.i);
                break;
        }
    }

    /** 点击按钮事件 */
    public class ButtonClickListener implements View.OnClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            if(isDoubleClick()) { return; }
            if(v.getId() == R.id.ll_sort) {
                showSortMenuDialog(v);
            } else if(v.getId() == R.id.ll_all_play) {
                EventBus.getDefault().post(new ThreadEvent<RoomPlayMusic>(ThreadEvent.VIEW_PAD_PLAY_ALL_MUSIC, roomPlayMusicList));
            } else if(v.getId() == R.id.ll_search) {
                isSearchMusic = true;
                searchMusic();
            } else if (v.getId() == R.id.ll_cancel) {
                isSearchMusic = false;
                searchCancel();
            }
        }
    }

    /** 点击排序按钮 */
    public void showSortMenuDialog(View view) {
        final DialogSortMenuBinding sortMenuBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
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
        //ThemeHelper.getInstance().sortMenuButtonTheme(rThemeId, sortMenuBinding, isUpSortByTime, isUpSortByName, isUpSortBySinger);
        sortMenuBinding.ivSortByTimeType.setBackgroundResource(isUpSortByTime? R.drawable.ic_sort_up : R.drawable.ic_sort_down);
        sortMenuBinding.ivSortByNameType.setBackgroundResource(isUpSortByName? R.drawable.ic_sort_up : R.drawable.ic_sort_down);
        sortMenuBinding.ivSortBySingerType.setBackgroundResource(isUpSortBySinger? R.drawable.ic_sort_up : R.drawable.ic_sort_down);

        /* 按时间排序 */
        sortMenuBinding.llSortByTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUpSortByTime = !isUpSortByTime;
                sortList(0);
                //变更主题
                //ThemeHelper.getInstance().sortTypeButtonTheme(rThemeId, sortMenuBinding.ivSortByTimeType, isUpSortByTime);
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
                //变更主题
                //ThemeHelper.getInstance().sortTypeButtonTheme(rThemeId, sortMenuBinding.ivSortByNameType, isUpSortByName);
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
                //变更主题
                //ThemeHelper.getInstance().sortTypeButtonTheme(rThemeId, sortMenuBinding.ivSortBySingerType, isUpSortBySinger);
                sortMenuBinding.ivSortBySingerType.setBackgroundResource(isUpSortBySinger? R.drawable.ic_sort_up : R.drawable.ic_sort_down);

                sortMenuBinding.ivSortByTimeType.setVisibility(View.INVISIBLE);
                sortMenuBinding.ivSortByNameType.setVisibility(View.INVISIBLE);
                sortMenuBinding.ivSortBySingerType.setVisibility(View.VISIBLE);
                clickSortType = 2;
            }
        });

        PopupWindow sortMenuPopupWindow  = new PopupWindow(sortMenuBinding.getRoot(),
                PxUtil.getInstance().dp2px(150, getContext()),  WindowManager.LayoutParams.WRAP_CONTENT, true);
        sortMenuPopupWindow.setTouchable(true);
        //变更主题
        //ThemeHelper.getInstance().sortMenuTheme(getContext(), rThemeId, sortMenuPopupWindow, sortMenuBinding);

        sortMenuPopupWindow.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.shape_button_menu_normal));
        sortMenuBinding.tvSortByTime.setTextColor(getContext().getResources().getColor(R.color.white));
        sortMenuBinding.tvSortByName.setTextColor(getContext().getResources().getColor(R.color.white));
        sortMenuBinding.tvSortBySinger.setTextColor(getContext().getResources().getColor(R.color.white));
        sortMenuBinding.ivSortByTime.setBackgroundResource(R.drawable.ic_sort_by_time);
        sortMenuBinding.ivSortByName.setBackgroundResource(R.drawable.ic_sort_by_name);
        sortMenuBinding.ivSortBySinger.setBackgroundResource(R.drawable.ic_sort_by_singer);
        sortMenuBinding.vLine.setBackgroundColor(getContext().getResources().getColor(R.color.gray_c9));
        sortMenuBinding.vLine2.setBackgroundColor(getContext().getResources().getColor(R.color.gray_c9));

        sortMenuPopupWindow.showAsDropDown(view,  PxUtil.getInstance().dp2px(-105, getContext()),  PxUtil.getInstance().dp2px(10, getContext()));
    }

    /**
     * 按类型排序歌曲
     * @param sortType 1：顺序 2：反序
     * */
    public void sortList(final int sortType) {
        //将空数据提取 排序后再放到列表最底部
        List<RoomPlayMusic> nullData = new ArrayList<>();
        for (RoomPlayMusic music : roomPlayMusicList) {
            if(music.musicType.equals(" ")) {
                nullData.add(music);
            }
        }
        if(!nullData.isEmpty()) {
            for (RoomPlayMusic nullMusic : nullData) {
                roomPlayMusicList.remove(nullMusic);
            }
        }
        Collections.sort(roomPlayMusicList, new Comparator<RoomPlayMusic>() {
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
            roomPlayMusicList.addAll(nullData);
        }
        musicListAdapter.notifyDataSetChanged();


    }

    /** 点击查找歌曲按钮 */
    public void searchMusic() {
        roomTempMusicList = new ArrayList<>();
        roomTempMusicList.addAll(roomPlayMusicList);
        getViewDataBinding().llShowNormalBar.setVisibility(View.GONE);
        getViewDataBinding().llShowSearchBar.setVisibility(View.VISIBLE);
    }

    /** 点击取消搜索 */
    public void searchCancel() {
        roomPlayMusicList.clear();
        if(roomTempMusicList!=null) {
            roomPlayMusicList.addAll(roomTempMusicList);
        }
        musicListAdapter.notifyDataSetChanged();
        getViewDataBinding().etSearchMusic.setText("");
        getViewDataBinding().llShowNormalBar.setVisibility(View.VISIBLE);
        getViewDataBinding().llShowSearchBar.setVisibility(View.GONE);
        hintKeyBoard();
    }


    public void hintKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        View v = getActivity().getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
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
        private OkHttpClient client;

        public static final int ITEM_TYPE_HEADER =0;
        public static final int ITEM_TYPE_CONTENT =1;

        public MusicListAdapter(Context context, List<RoomPlayMusic> list) {
            this.context = context;
            this.list = list;
            this.client = new OkHttpClient();
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
                //ThemeHelper.getInstance().musicListAddButtonTheme(getContext(), rThemeId, binding);
                binding.rlMusicAll.setBackgroundResource(R.drawable.selector_tab_selected3);
                binding.tvMusicName.setTextColor(context.getResources().getColor(R.color.light_ff));
                binding.tvSingerName.setTextColor(context.getResources().getColor(R.color.light_ff));
                binding.ivAdd.setBackgroundResource(R.drawable.ic_add_light);
                binding.ivMore.setBackgroundResource(R.drawable.ic_more_light_ff);

                binding.llMore.setVisibility(View.GONE);

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
                        //playMusic(list, position);
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAD_PLAY_MUSIC, list, position));
                    }
                });

                //点击添加歌曲到列表
                binding.llAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //变更主题
                        //ThemeHelper.getInstance().musicListAddButtonAnimatorTheme(rThemeId, binding);
                        binding.ivAddAnimatorLight1.setVisibility(View.VISIBLE);
                        AnimatorSet animatorSet = MyAnimationUtil.animatorSetAddMusic(binding.ivAddAnimatorLight1);
                        animatorSet.start();
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_PAD_ADD_MUSIC, list, position));

                        //addMusic(list, position);
                    }
                });

                binding.llMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //showMoreMenu(v, list, position);
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

}
