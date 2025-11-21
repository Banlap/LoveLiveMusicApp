package com.banlap.llmusic.pad.ui.fragment;


import android.annotation.SuppressLint;
import android.view.View;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseFragment;
import com.banlap.llmusic.databinding.FragmentPadLoveliveBinding;
import com.banlap.llmusic.model.Music;
import com.banlap.llmusic.pad.ui.activity.PadMainActivity;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.pad.uivm.fvm.PadLoveLiveFVM;
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
 *
 * */
public class PadLoveLiveFragment extends BaseFragment<PadLoveLiveFVM, FragmentPadLoveliveBinding>
    implements PadLoveLiveFVM.PadLoveLiveCallBack {

    private static List<Music> recommendList;
    private RequestOptions requestOptions;
    private int rThemeId =0;                             //当前主题

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pad_lovelive;
    }

    @Override
    protected void initData() {
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
        EventBus.getDefault().register(this);

        ((PadMainActivity) getActivity()).getBinding().llBack.setVisibility(View.GONE);
        ((PadMainActivity) getActivity()).getViewModel().showRecommendData(getContext());
        changeTheme();
        //每日推荐
        getViewDataBinding().llRecommend1.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llRecommend2.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llRecommend3.setOnClickListener(new ButtonClickListener());

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
    }

    /**
     * 改变主题
     * */
    private void changeTheme() {
//        String strThemeId = SPUtil.getStrValue(getContext(), SPUtil.SaveThemeId);
//        if(strThemeId!=null) {
//            if(!strThemeId.equals("")) {
//                rThemeId = Integer.parseInt(strThemeId);
//                ThemeHelper.getInstance().mainListFragmentTheme(getContext(), rThemeId, getViewDataBinding());
//            }
//        }
    }


    @Override
    public void onResume() {
        super.onResume();
        changeTheme();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void viewBack() {

    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEvent(ThreadEvent event) {
        switch (event.msgCode) {
            case ThreadEvent.VIEW_GET_RECOMMEND_SUCCESS:
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
    public class ButtonClickListener implements View.OnClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            if(isDoubleClick()) { return; }
            if (v.getId() == R.id.ll_recommend_1) {
                if(recommendList.size() >0) {
                    EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_PLAY_RECOMMEND_MUSIC, recommendList.get(0)));
                }
            } else if (v.getId() == R.id.ll_recommend_2) {
                if(recommendList.size() >0) {
                    EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_PLAY_RECOMMEND_MUSIC, recommendList.get(1)));
                }
            } else if (v.getId() == R.id.ll_recommend_3) {
                if(recommendList.size() >0) {
                    EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_PLAY_RECOMMEND_MUSIC, recommendList.get(2)));
                }
            } else if (v.getId() == R.id.ll_list_1) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_PAD_CHANGE_FRAGMENT, ThreadEvent.ALBUM_LIELLA, PadMainActivity.VIEW_PAD_FRAGMENT_DETAIL));
            } else if (v.getId() == R.id.ll_list_2) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_PAD_CHANGE_FRAGMENT, ThreadEvent.ALBUM_FOUR_YUU, PadMainActivity.VIEW_PAD_FRAGMENT_DETAIL));
            } else if (v.getId() == R.id.ll_list_3) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_PAD_CHANGE_FRAGMENT, ThreadEvent.ALBUM_SUNNY_PASSION, PadMainActivity.VIEW_PAD_FRAGMENT_DETAIL));
            } else if (v.getId() == R.id.ll_list_4) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_PAD_CHANGE_FRAGMENT, ThreadEvent.ALBUM_NIJIGASAKI, PadMainActivity.VIEW_PAD_FRAGMENT_DETAIL));
            } else if (v.getId() == R.id.ll_list_5) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_PAD_CHANGE_FRAGMENT, ThreadEvent.ALBUM_AQOURS, PadMainActivity.VIEW_PAD_FRAGMENT_DETAIL));
            } else if (v.getId() == R.id.ll_list_6) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_PAD_CHANGE_FRAGMENT, ThreadEvent.ALBUM_US, PadMainActivity.VIEW_PAD_FRAGMENT_DETAIL));
            } else if (v.getId() == R.id.ll_list_7) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_PAD_CHANGE_FRAGMENT, ThreadEvent.ALBUM_HASUNOSORA, PadMainActivity.VIEW_PAD_FRAGMENT_DETAIL));
            } else if (v.getId() == R.id.ll_list_8) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_PAD_CHANGE_FRAGMENT, ThreadEvent.ALBUM_SAINT_SNOW, PadMainActivity.VIEW_PAD_FRAGMENT_DETAIL));
            } else if (v.getId() == R.id.ll_list_9) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_PAD_CHANGE_FRAGMENT, ThreadEvent.ALBUM_A_RISE, PadMainActivity.VIEW_PAD_FRAGMENT_DETAIL));
            } else if (v.getId() == R.id.ll_list_10) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_PAD_CHANGE_FRAGMENT, ThreadEvent.ALBUM_OTHER, PadMainActivity.VIEW_PAD_FRAGMENT_DETAIL));
            } else if (v.getId() == R.id.ll_list_11) {
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.VIEW_PAD_CHANGE_FRAGMENT, ThreadEvent.ALBUM_BLUEBIRD, PadMainActivity.VIEW_PAD_FRAGMENT_DETAIL));
            }
        }
    }

}
