package com.banlap.llmusic.phone.ui.fragment;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseFragment;
import com.banlap.llmusic.databinding.FragmentNewMainListBinding;
import com.banlap.llmusic.databinding.ItemAddMusicLocalListBinding;
import com.banlap.llmusic.databinding.ItemTeamMusicListBinding;
import com.banlap.llmusic.fixed.AppMusic;
import com.banlap.llmusic.model.TeamMusic;
import com.banlap.llmusic.phone.ui.activity.MainActivity;
import com.banlap.llmusic.phone.ui.adapter.DailyMusicListAdapter;
import com.banlap.llmusic.phone.uivm.fvm.NewMainListFVM;
import com.banlap.llmusic.sql.room.RoomPlayMusic;
import com.banlap.llmusic.utils.LLAnimationUtil;
import com.google.android.material.appbar.AppBarLayout;
import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;
import com.zhpan.bannerview.constants.PageStyle;
import com.zhpan.indicator.enums.IndicatorSlideMode;

import java.util.ArrayList;
import java.util.List;

public class NewMainListFragment extends BaseFragment<NewMainListFVM, FragmentNewMainListBinding> {
    private final String TAG = NewMainListFragment.class.getSimpleName();
    private MainTeamListAdapter mainTeamListAdapter;
    private List<RoomPlayMusic> dailyMusicList = new ArrayList<>();
    private DailyMusicListAdapter dailyMusicListAdapter;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_new_main_list;
    }

    @Override
    protected void initData() {
        dailyMusicList.addAll(AppMusic.getInstance().setTempData(new RoomPlayMusic(), 10));
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
                .create(AppMusic.getInstance().getTeamMusicList());
        dailyMusicListAdapter = new DailyMusicListAdapter(getContext(), dailyMusicList);
        getViewDataBinding().rvDailyList.setLayoutManager(new LinearLayoutManager(getContext()));
        getViewDataBinding().rvDailyList.setAdapter(dailyMusicListAdapter);
        dailyMusicListAdapter.notifyDataSetChanged();

        getViewDataBinding().ablAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //Toast.makeText(MainActivity.this, "vo: " + verticalOffset, Toast.LENGTH_SHORT).show();
                if(Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {

                } else {

                }
            }
        });
    }

    /**
     * 企划列表
     * */
    public static class MainTeamListAdapter extends BaseBannerAdapter<TeamMusic> {

        @Override
        protected void bindData(BaseViewHolder<TeamMusic> holder, TeamMusic data, int position, int pageSize) {
            holder.setImageResource(R.id.iv_team_music, data.getResId2());
        }

        @Override
        public int getLayoutId(int i) {
            return R.layout.item_team_music_list;
        }

    }
}
