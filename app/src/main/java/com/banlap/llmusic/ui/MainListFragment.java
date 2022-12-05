package com.banlap.llmusic.ui;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseFragment;
import com.banlap.llmusic.databinding.FragmentMainListBinding;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.uivm.MainListFVM;
import com.banlap.llmusic.utils.MyAnimationUtil;

import org.greenrobot.eventbus.EventBus;

public class MainListFragment extends BaseFragment<MainListFVM, FragmentMainListBinding>
    implements MainListFVM.MainListCallBack {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_list;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        getViewDataBinding().llList1.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList2.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList3.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList4.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList5.setOnClickListener(new ButtonClickListener());
        getViewDataBinding().llList6.setOnClickListener(new ButtonClickListener());
    }

    @Override
    public void viewBack() {

    }

    /** 点击按钮事件 */
    public static class ButtonClickListener implements View.OnClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
           if (v.getId() == R.id.ll_list_1) {
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_DATA_LIST_BY_LIELLA));
           } else if (v.getId() == R.id.ll_list_2) {
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_DATA_LIST_BY_FOUR_YUU));
           } else if (v.getId() == R.id.ll_list_3) {
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_DATA_LIST_BY_SUNNY_PASSION));
            } else if (v.getId() == R.id.ll_list_4) {
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_DATA_LIST_BY_NIJIGASAKI));
            } else if (v.getId() == R.id.ll_list_5) {
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_DATA_LIST_BY_AQOURS));
            } else if (v.getId() == R.id.ll_list_6) {
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_DATA_LIST_BY_US));
            }
        }
    }

}
