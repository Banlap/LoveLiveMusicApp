package com.banlap.llmusic.ui.fragment;

import android.annotation.SuppressLint;
import android.view.View;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseFragment;
import com.banlap.llmusic.databinding.FragmentMainListBinding;
import com.banlap.llmusic.model.Music;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.ui.ThemeHelper;
import com.banlap.llmusic.uivm.fvm.MainListFVM;
import com.banlap.llmusic.utils.SPUtil;
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

    private static List<Music> recommendList;
    private RequestOptions requestOptions;
    private int rThemeId =0;                             //当前主题

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_list;
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        recommendList = new ArrayList<>();
        initRequestOptions();
    }

    private void initRequestOptions() {
        requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        //requestOptions.override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL); //关键代码，加载原始大小
        requestOptions.format(DecodeFormat.PREFER_RGB_565); //设置为这种格式去掉透明度通道，可以减少内存占有
    }

    @Override
    protected void initView() {
        changeTheme();
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
    }

    /**
     * 改变主题
     * */
    private void changeTheme() {
        String strThemeId = SPUtil.getStrValue(getContext(), SPUtil.SaveThemeId);
        if(strThemeId!=null) {
            if(!strThemeId.equals("")) {
                rThemeId = Integer.parseInt(strThemeId);
                ThemeHelper.getInstance().mainListFragmentTheme(getContext(), rThemeId, getViewDataBinding());
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        changeTheme();
    }

    @Override
    public void viewBack() {

    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEvent(ThreadEvent event) {
        switch (event.msgCode) {
            case ThreadEvent.GET_RECOMMEND_SUCCESS:
                if(event.musicList.size() >0) {
                    recommendList.clear();
                    recommendList.addAll(event.musicList);

                    SPUtil.setListValue(getContext(), SPUtil.RecommendListData, recommendList);

                    Glide.with(getContext())
                            .setDefaultRequestOptions(requestOptions)
                            .load(recommendList.get(0).getMusicImg())
                            .transform(new RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.ALL))
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(getViewDataBinding().ivRecommend1);

                    getViewDataBinding().tvRecommend1.setText(recommendList.get(0).musicName);

                    Glide.with(getContext())
                            .setDefaultRequestOptions(requestOptions)
                            .load(recommendList.get(1).getMusicImg())
                            .transform(new RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.ALL))
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(getViewDataBinding().ivRecommend2);

                    getViewDataBinding().tvRecommend2.setText(recommendList.get(1).musicName);

                    Glide.with(getContext())
                            .setDefaultRequestOptions(requestOptions)
                            .load(recommendList.get(2).getMusicImg())
                            .transform(new RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.ALL))
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(getViewDataBinding().ivRecommend3);

                    getViewDataBinding().tvRecommend3.setText(recommendList.get(2).musicName);
                }
                break;
        }
    }

    /** 点击按钮事件 */
    public static class ButtonClickListener implements View.OnClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.iv_recommend_1) {
                if(recommendList.size() >0) {
                    EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.PLAY_RECOMMEND_MUSIC, recommendList.get(0)));
                }
            } else if (v.getId() == R.id.iv_recommend_2) {
                if(recommendList.size() >0) {
                    EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.PLAY_RECOMMEND_MUSIC, recommendList.get(1)));
                }
            } else if (v.getId() == R.id.iv_recommend_3) {
                if(recommendList.size() >0) {
                    EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.PLAY_RECOMMEND_MUSIC, recommendList.get(2)));
                }
            } else if (v.getId() == R.id.ll_list_1) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.GET_DATA_LIST_BY_LIELLA));
            } else if (v.getId() == R.id.ll_list_2) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.GET_DATA_LIST_BY_FOUR_YUU));
            } else if (v.getId() == R.id.ll_list_3) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.GET_DATA_LIST_BY_SUNNY_PASSION));
            } else if (v.getId() == R.id.ll_list_4) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.GET_DATA_LIST_BY_NIJIGASAKI));
            } else if (v.getId() == R.id.ll_list_5) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.GET_DATA_LIST_BY_AQOURS));
            } else if (v.getId() == R.id.ll_list_6) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.GET_DATA_LIST_BY_US));
            } else if (v.getId() == R.id.ll_list_7) {
               EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.GET_DATA_LIST_BY_HASUNOSORA));
            } else if (v.getId() == R.id.ll_list_8) {
               EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.GET_DATA_LIST_BY_SAINT_SNOW));
            } else if (v.getId() == R.id.ll_list_9) {
               EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.GET_DATA_LIST_BY_A_RISE));
            }
        }
    }

}
