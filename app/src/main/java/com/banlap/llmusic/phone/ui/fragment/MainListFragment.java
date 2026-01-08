package com.banlap.llmusic.phone.ui.fragment;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.Observer;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseFragment;
import com.banlap.llmusic.databinding.FragmentMainListBinding;
import com.banlap.llmusic.model.Music;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.phone.ui.ThemeHelper;
import com.banlap.llmusic.phone.uivm.fvm.MainListFVM;
import com.banlap.llmusic.sql.AppData;
import com.banlap.llmusic.sql.room.Converters;
import com.banlap.llmusic.sql.room.RoomPlayMusic;
import com.banlap.llmusic.sql.room.RoomRecommendMusic;
import com.banlap.llmusic.utils.AppExecutors;
import com.banlap.llmusic.utils.LLActivityManager;
import com.banlap.llmusic.utils.SPUtil;
import com.banlap.llmusic.utils.SystemUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Discover主页
 * */
public class MainListFragment extends BaseFragment<MainListFVM, FragmentMainListBinding>
    implements MainListFVM.MainListCallBack {
    private static final String TAG = MainListFragment.class.getSimpleName();
    public List<RoomPlayMusic> roomPlayMusicList;

    private RequestOptions requestOptions;

    private int musicNewTotalLiella = 0, musicNewTotalLiyuu = 0, musicNewTotalSunnyPassion = 0, musicNewTotalNIJIGASAKI = 0, musicNewTotalAqours = 0;
    private int musicNewTotalUS = 0, musicNewTotalHASUNOSORA = 0, musicNewTotalSAINTSNOW = 0, musicNewTotalARISE = 0,  musicNewTotalOther = 0;

    private int musicTotalLiella = 0, musicTotalLiyuu = 0, musicTotalSunnyPassion = 0, musicTotalNIJIGASAKI = 0, musicTotalAqours = 0;
    private int musicTotalUS = 0, musicTotalHASUNOSORA = 0, musicTotalSAINTSNOW = 0, musicTotalARISE = 0,  musicTotalOther = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_list;
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        initRequestOptions();
        //updateMusicNew();
        roomPlayMusicList = new ArrayList<>();
    }

    private void updateMusicNew() {
        musicNewTotalLiella = SPUtil.getIntValue(getContext(), SPUtil.MusicNewAllTotalByLiella);
        musicNewTotalLiyuu = SPUtil.getIntValue(getContext(), SPUtil.MusicNewAllTotalByLiyuu);
        musicNewTotalSunnyPassion = SPUtil.getIntValue(getContext(), SPUtil.MusicNewAllTotalBySunnyPassion);
        musicNewTotalNIJIGASAKI = SPUtil.getIntValue(getContext(), SPUtil.MusicNewAllTotalByNIJIGASAKI);
        musicNewTotalAqours = SPUtil.getIntValue(getContext(), SPUtil.MusicNewAllTotalByAqours);
        musicNewTotalUS = SPUtil.getIntValue(getContext(), SPUtil.MusicNewAllTotalByUS);
        musicNewTotalHASUNOSORA = SPUtil.getIntValue(getContext(), SPUtil.MusicNewAllTotalByHASUNOSORA);
        musicNewTotalSAINTSNOW = SPUtil.getIntValue(getContext(), SPUtil.MusicNewAllTotalBySAINTSNOW);
        musicNewTotalARISE = SPUtil.getIntValue(getContext(), SPUtil.MusicNewAllTotalByARISE);
        musicNewTotalOther = SPUtil.getIntValue(getContext(), SPUtil.MusicNewAllTotalByOther);
    }

    private void initRequestOptions() {
        requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        //requestOptions.override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL); //关键代码，加载原始大小
        requestOptions.format(DecodeFormat.PREFER_RGB_565); //设置为这种格式去掉透明度通道，可以减少内存占有
    }

    @Override
    protected void initView() {
        //每日推荐
        getViewDataBinding().ivRecommend1.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().ivRecommend2.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().ivRecommend3.setOnClickListener(new ButtonClickListener());

        //列表
        getViewDataBinding().llList1.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList2.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList3.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList4.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList5.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList6.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList7.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList8.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList9.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList10.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList11.setOnClickListener(new ButtonClickListener());

        //观察主题id值变化
        getViewModel(MainListFVM.class).getThemeId().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer themeId) {
                changeTheme(themeId);
            }
        });

    }

    /**
     * 变更主题
     * */
    private void changeTheme(int rThemeId) {
        ThemeHelper.getInstance().mainListFragmentTheme(getContext(), rThemeId, getViewDataBinding());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void refreshTotalNew() {
        getViewDataBinding().tvLiellaNew.setVisibility(musicNewTotalLiella == musicTotalLiella? View.GONE : View.VISIBLE);
        getViewDataBinding().tvFourYuuNew.setVisibility(musicNewTotalLiyuu == musicTotalLiyuu? View.GONE : View.VISIBLE);
        getViewDataBinding().tvSunnyPassionNew.setVisibility(musicNewTotalSunnyPassion == musicTotalSunnyPassion? View.GONE : View.VISIBLE);
        getViewDataBinding().tvNijigasakiNew.setVisibility(musicNewTotalNIJIGASAKI == musicTotalNIJIGASAKI? View.GONE : View.VISIBLE);
        getViewDataBinding().tvAqoursNew.setVisibility(musicNewTotalAqours == musicTotalAqours? View.GONE : View.VISIBLE);
        getViewDataBinding().tvUsNew.setVisibility(musicNewTotalUS == musicTotalUS? View.GONE : View.VISIBLE);
        getViewDataBinding().tvHausNew.setVisibility(musicNewTotalHASUNOSORA == musicTotalHASUNOSORA? View.GONE : View.VISIBLE);
        getViewDataBinding().tvSaintSnowNew.setVisibility(musicNewTotalSAINTSNOW == musicTotalSAINTSNOW? View.GONE : View.VISIBLE);
        getViewDataBinding().tvARiseNew.setVisibility(musicNewTotalARISE == musicTotalARISE? View.GONE : View.VISIBLE);
        getViewDataBinding().tvOtherNew.setVisibility(musicNewTotalOther == musicTotalOther? View.GONE : View.VISIBLE);
    }

    @Override
    public void viewBack() {

    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEvent(ThreadEvent event) {
        switch (event.msgCode) {
            case ThreadEvent.VIEW_GET_RECOMMEND_SUCCESS:
                if(!event.tList.isEmpty()) {
                    List<RoomRecommendMusic> roomRecommendMusicList = new ArrayList<>(event.tList);
                    String data = Converters.fromRecommendMusicList(roomRecommendMusicList);
                    List<RoomPlayMusic> playMusicList = new ArrayList<>(Converters.toPlayMusicList(data));

                    if(roomRecommendMusicList.isEmpty()) {
                        return;
                    }

                    roomPlayMusicList.clear();
                    roomPlayMusicList.addAll(playMusicList);
                    if(roomPlayMusicList == null || roomPlayMusicList.isEmpty()) {
                        return;
                    }

                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(roomRecommendMusicList.isEmpty()) {
                                    return;
                                }
                                for(RoomRecommendMusic music: roomRecommendMusicList) {
                                    Thread.sleep(1);
                                    music.id = System.currentTimeMillis() * SystemUtil.STEP;
                                }
                                Thread.sleep(10);
                                AppData.saveRecommendList(roomRecommendMusicList);
                            } catch (Exception e) {
                                Log.e(TAG, "roomRecommendMusicList id error");
                            }
                        }
                    });

                    Glide.with(LLActivityManager.getInstance().getTopActivity())
                            .setDefaultRequestOptions(requestOptions)
                            .load(roomPlayMusicList.get(0).musicImg)
                            .transform(new RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.ALL))
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(getViewDataBinding().ivRecommend1);

                    getViewDataBinding().tvRecommend1.setText(roomPlayMusicList.get(0).musicName);

                    Glide.with(LLActivityManager.getInstance().getTopActivity())
                            .setDefaultRequestOptions(requestOptions)
                            .load(roomPlayMusicList.get(1).musicImg)
                            .transform(new RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.ALL))
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(getViewDataBinding().ivRecommend2);

                    getViewDataBinding().tvRecommend2.setText(roomPlayMusicList.get(1).musicName);

                    Glide.with(LLActivityManager.getInstance().getTopActivity())
                            .setDefaultRequestOptions(requestOptions)
                            .load(roomPlayMusicList.get(2).musicImg)
                            .transform(new RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.ALL))
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(getViewDataBinding().ivRecommend3);

                    getViewDataBinding().tvRecommend3.setText(roomPlayMusicList.get(2).musicName);
                }
                break;
            case ThreadEvent.VIEW_GET_TOTAL_LIELLA_SUCCESS:
                musicTotalLiella = event.i;
                getViewDataBinding().tvLiellaNew.setVisibility(musicNewTotalLiella == musicTotalLiella? View.GONE : View.VISIBLE);
                break;
            case ThreadEvent.VIEW_GET_TOTAL_LIYUU_SUCCESS:
                musicTotalLiyuu = event.i;
                getViewDataBinding().tvFourYuuNew.setVisibility(musicNewTotalLiyuu == musicTotalLiyuu? View.GONE : View.VISIBLE);
                break;
            case ThreadEvent.VIEW_GET_TOTAL_SUNNY_PASSION_SUCCESS:
                musicTotalSunnyPassion = event.i;
                getViewDataBinding().tvSunnyPassionNew.setVisibility(musicNewTotalSunnyPassion == musicTotalSunnyPassion? View.GONE : View.VISIBLE);
                break;
            case ThreadEvent.VIEW_GET_TOTAL_NIJIGASAKI_SUCCESS:
                musicTotalNIJIGASAKI = event.i;
                getViewDataBinding().tvNijigasakiNew.setVisibility(musicNewTotalNIJIGASAKI == musicTotalNIJIGASAKI? View.GONE : View.VISIBLE);
                break;
            case ThreadEvent.VIEW_GET_TOTAL_AQOURS_SUCCESS:
                musicTotalAqours = event.i;
                getViewDataBinding().tvAqoursNew.setVisibility(musicNewTotalAqours == musicTotalAqours? View.GONE : View.VISIBLE);
                break;
            case ThreadEvent.VIEW_GET_TOTAL_US_SUCCESS:
                musicTotalUS = event.i;
                getViewDataBinding().tvUsNew.setVisibility(musicNewTotalUS == musicTotalUS? View.GONE : View.VISIBLE);
                break;
            case ThreadEvent.VIEW_GET_TOTAL_HASUNOSORA_SUCCESS:
                musicTotalHASUNOSORA = event.i;
                getViewDataBinding().tvHausNew.setVisibility(musicNewTotalHASUNOSORA == musicTotalHASUNOSORA? View.GONE : View.VISIBLE);
                break;
            case ThreadEvent.VIEW_GET_TOTAL_SAINT_SNOW_SUCCESS:
                musicTotalSAINTSNOW = event.i;
                getViewDataBinding().tvSaintSnowNew.setVisibility(musicNewTotalSAINTSNOW == musicTotalSAINTSNOW? View.GONE : View.VISIBLE);
                break;
            case ThreadEvent.VIEW_GET_TOTAL_A_RISE_SUCCESS:
                musicTotalARISE = event.i;
                getViewDataBinding().tvARiseNew.setVisibility(musicNewTotalARISE == musicTotalARISE? View.GONE : View.VISIBLE);
                break;
            case ThreadEvent.VIEW_GET_TOTAL_OTHER_SUCCESS:
                musicTotalOther = event.i;
                getViewDataBinding().tvOtherNew.setVisibility(musicNewTotalOther == musicTotalOther? View.GONE : View.VISIBLE);
                break;
        }
    }

    /** 点击按钮事件 */
    public class ButtonClickListener implements View.OnClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.iv_recommend_1) {
                if(!roomPlayMusicList.isEmpty()) {
                    EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_PLAY_RECOMMEND_MUSIC, roomPlayMusicList.get(0)));
                }
            } else if (v.getId() == R.id.iv_recommend_2) {
                if(!roomPlayMusicList.isEmpty()) {
                    EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_PLAY_RECOMMEND_MUSIC, roomPlayMusicList.get(1)));
                }
            } else if (v.getId() == R.id.iv_recommend_3) {
                if(!roomPlayMusicList.isEmpty()) {
                    EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_PLAY_RECOMMEND_MUSIC, roomPlayMusicList.get(2)));
                }
            } else if (v.getId() == R.id.ll_list_1) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.THREAD_GET_DATA_LIST_BY_LIELLA));
            } else if (v.getId() == R.id.ll_list_2) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.THREAD_GET_DATA_LIST_BY_FOUR_YUU));
            } else if (v.getId() == R.id.ll_list_3) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.THREAD_GET_DATA_LIST_BY_SUNNY_PASSION));
            } else if (v.getId() == R.id.ll_list_4) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.THREAD_GET_DATA_LIST_BY_NIJIGASAKI));
            } else if (v.getId() == R.id.ll_list_5) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.THREAD_GET_DATA_LIST_BY_AQOURS));
            } else if (v.getId() == R.id.ll_list_6) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.THREAD_GET_DATA_LIST_BY_US));
            } else if (v.getId() == R.id.ll_list_7) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.THREAD_GET_DATA_LIST_BY_HASUNOSORA));
            } else if (v.getId() == R.id.ll_list_8) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.THREAD_GET_DATA_LIST_BY_SAINT_SNOW));
            } else if (v.getId() == R.id.ll_list_9) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.THREAD_GET_DATA_LIST_BY_A_RISE));
            } else if (v.getId() == R.id.ll_list_10) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.THREAD_GET_DATA_LIST_BY_OTHER));
            } else if (v.getId() == R.id.ll_list_11) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.THREAD_GET_DATA_LIST_BY_BLUEBIRD));
            }
        }
    }

}
