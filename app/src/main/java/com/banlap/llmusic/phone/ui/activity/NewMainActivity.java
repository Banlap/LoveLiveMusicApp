package com.banlap.llmusic.phone.ui.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseActivity;
import com.banlap.llmusic.databinding.ActivityNewMainBinding;
import com.banlap.llmusic.databinding.ItemAddMusicLocalListBinding;
import com.banlap.llmusic.databinding.ItemTeamMusicListBinding;
import com.banlap.llmusic.fixed.AppMusic;
import com.banlap.llmusic.model.TeamMusic;
import com.banlap.llmusic.phone.ui.fragment.NewFragmentStateAdapter;
import com.banlap.llmusic.phone.ui.fragment.NewLocalListFragment;
import com.banlap.llmusic.phone.ui.fragment.NewMainListFragment;
import com.banlap.llmusic.phone.uivm.vm.NewMainVM;
import com.banlap.llmusic.sql.AppData;
import com.banlap.llmusic.sql.room.RoomCustomPlay;
import com.banlap.llmusic.utils.LLAnimationUtil;
import com.banlap.llmusic.utils.PxUtil;

import java.util.ArrayList;
import java.util.List;

public class NewMainActivity extends BaseActivity<NewMainVM, ActivityNewMainBinding> implements NewMainVM.NewMainCallBack {
    private final String TAG = NewMainActivity.class.getSimpleName();
    private NewFragmentStateAdapter mainFragmentStateAdapter;     //主页与本地页面切换Adapter

    private boolean isLandSpace = false; //是否横屏
    private int downHeightByPortrait = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_main;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        getViewDataBinding().setVm(getViewModel());
        getViewDataBinding().setVdb(this);
        getViewModel().setCallBack(this);

        initMainView();
        initFragment();
    }

    /**
     * 初始化主页内容
     */
    private void initMainView() {
        getViewDataBinding().setMusicName("LoveLiveMusic");

        getViewDataBinding().rlMusicDetailMain.setVisibility(View.VISIBLE);
        getViewDataBinding().rlMusicDetailMain.post(() -> {
            downHeightByPortrait = getViewDataBinding().rlMusicDetailMain.getHeight() - getViewDataBinding().vDisplay.getHeight() - getViewDataBinding().llMusicDetailTopView.getHeight();
            LLAnimationUtil.objectAnimatorUpOrDown(NewMainActivity.this, true, downHeightByPortrait, getViewDataBinding().rlMusicDetailMain);
        });

        getViewModel().getMlIsClickMusicController().observe(this, b -> LLAnimationUtil.objectAnimatorUpOrDown(NewMainActivity.this, !b, downHeightByPortrait, getViewDataBinding().rlMusicDetailMain));
        getViewModel().getMlIsViewPageTouch().observe(this, b -> getViewDataBinding().vp2Main.requestDisallowInterceptTouchEvent(b));
    }


    /**
     * 初始化碎片
     */
    private void initFragment() {
        if (mainFragmentStateAdapter == null) {
            List<Fragment> fragmentList = new ArrayList<>();
            fragmentList.add(new NewMainListFragment());
            fragmentList.add(new NewLocalListFragment());
            mainFragmentStateAdapter = new NewFragmentStateAdapter(this, fragmentList);
        }

        getViewDataBinding().vp2Main.setOffscreenPageLimit(2);
        getViewDataBinding().vp2Main.setAdapter(mainFragmentStateAdapter);

        getViewDataBinding().vp2Main.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (0 == position) {
                    LLAnimationUtil.animatorSetEnlarge(getViewDataBinding().tvDiscover, 1, (float) 1.5);
                    LLAnimationUtil.animatorSetEnlarge(getViewDataBinding().tvLocal, (float) 1.5, 1);
                    int x = getViewDataBinding().vLine.getWidth();
                    LLAnimationUtil.animatorSetMoveOrigin(getViewDataBinding().vLine, false, x);
                } else if (1 == position) {
                    LLAnimationUtil.animatorSetEnlarge(getViewDataBinding().tvDiscover, (float) 1.5, 1);
                    LLAnimationUtil.animatorSetEnlarge(getViewDataBinding().tvLocal, 1, (float) 1.5);
                    int x = getViewDataBinding().vLine.getWidth();
                    LLAnimationUtil.animatorSetMoveOrigin(getViewDataBinding().vLine, true, x);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        });


        View view = getViewDataBinding().vp2Main.getChildAt(0);
        if (view instanceof RecyclerView) {
            view.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }

    /**
     * 碎片页面切换
     */
    public void vpClick(int item) {
        getViewDataBinding().vp2Main.setCurrentItem(item);
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        isLandSpace = config.orientation != Configuration.ORIENTATION_PORTRAIT;
        Log.e(TAG, "onConfigurationChanged: " + (!isLandSpace ? "竖屏" : "横屏"));

        getViewDataBinding().rlMusicDetailMain.postDelayed(() -> {
            downHeightByPortrait = getViewDataBinding().rlMusicDetailMain.getHeight() - getViewDataBinding().vDisplay.getHeight() - getViewDataBinding().llMusicDetailTopView.getHeight();
            Log.e(TAG, "downHeightByPortrait: " + downHeightByPortrait);
            LLAnimationUtil.objectAnimatorUpOrDown(NewMainActivity.this, !Boolean.TRUE.equals(getViewModel().getMlIsClickMusicController().getValue()), downHeightByPortrait, getViewDataBinding().rlMusicDetailMain);
        }, 50);
    }

}