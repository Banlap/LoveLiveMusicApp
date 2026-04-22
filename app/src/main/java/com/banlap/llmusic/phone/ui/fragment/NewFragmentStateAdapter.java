package com.banlap.llmusic.phone.ui.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

/** 页面碎片切换 */
public class NewFragmentStateAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {

    private final List<Fragment> fragmentList;

    public NewFragmentStateAdapter(FragmentActivity fragmentActivity, List<Fragment> fragmentList) {
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