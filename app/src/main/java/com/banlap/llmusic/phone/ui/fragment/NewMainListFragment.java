package com.banlap.llmusic.phone.ui.fragment;

import android.annotation.SuppressLint;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseFragment;
import com.banlap.llmusic.databinding.FragmentNewMainListBinding;
import com.banlap.llmusic.fixed.AppMusic;
import com.banlap.llmusic.model.TeamMusic;
import com.banlap.llmusic.phone.uivm.fvm.NewMainListFVM;
import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;
import com.zhpan.bannerview.constants.PageStyle;
import com.zhpan.indicator.enums.IndicatorSlideMode;

public class NewMainListFragment extends BaseFragment<NewMainListFVM, FragmentNewMainListBinding> {
    private final String TAG = NewMainListFragment.class.getSimpleName();
    private MainTeamListAdapter mainTeamListAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_new_main_list;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        initMainView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initMainView() {
        getViewDataBinding().bvpList.setAdapter(new MainTeamListAdapter())
                .setLifecycleRegistry(getLifecycle())
                .setIndicatorSlideMode(IndicatorSlideMode.SCALE)
                .setPageStyle(PageStyle.MULTI_PAGE_OVERLAP)
                .setRevealWidth(10, 10)
                .create();

        getViewDataBinding().bvpList.refreshData(AppMusic.getInstance().getTeamMusicList());
    }


    public static class MainTeamListAdapter extends BaseBannerAdapter<TeamMusic> {

        @Override
        protected void onBind(NetViewHolder holder, BannerData data, int position, int pageSize) {
            holder.bindData(data, position, pageSize);
        }

        @Override
        public NetViewHolder createViewHolder(View itemView, int viewType) {
            return new NetViewHolder(itemView);
        }
        @Override
        public int getLayoutId(int i) {
            return R.layout.item_team_music_list;
        }
    }
}
