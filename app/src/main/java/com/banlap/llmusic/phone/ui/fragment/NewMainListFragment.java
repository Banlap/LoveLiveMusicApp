package com.banlap.llmusic.phone.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.banlap.llmusic.R;
import com.banlap.llmusic.base.BaseFragment;
import com.banlap.llmusic.databinding.FragmentNewMainListBinding;
import com.banlap.llmusic.databinding.ItemTeamMusicListBinding;
import com.banlap.llmusic.fixed.AppMusic;
import com.banlap.llmusic.model.TeamMusic;
import com.banlap.llmusic.phone.ui.activity.NewMainActivity;
import com.banlap.llmusic.phone.uivm.fvm.NewMainListFVM;
import com.banlap.llmusic.phone.uivm.vm.NewMainVM;
import com.mig35.carousellayoutmanager.CarouselLayoutManager;
import com.mig35.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.mig35.carousellayoutmanager.CenterScrollListener;

import java.util.List;

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
        CarouselLayoutManager carouselLayoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, true);
        carouselLayoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener(0.2f));

        mainTeamListAdapter = new MainTeamListAdapter(getContext(), AppMusic.getInstance().getTeamMusicList());
        getViewDataBinding().rvList.setLayoutManager(carouselLayoutManager);
        getViewDataBinding().rvList.setHasFixedSize(true);
        getViewDataBinding().rvList.setAdapter(mainTeamListAdapter);

        getViewDataBinding().rvList.addOnScrollListener(new CenterScrollListener());
        getViewDataBinding().rvList.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    getViewModel(NewMainVM.class).toggleViewPageTouch(true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    getViewModel(NewMainVM.class).toggleViewPageTouch(false);
                    break;
            }
            return false;
        });

        mainTeamListAdapter.notifyDataSetChanged();
    }

    public static class MainTeamListViewHolder extends RecyclerView.ViewHolder {
        public MainTeamListViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class MainTeamListAdapter extends RecyclerView.Adapter<MainTeamListViewHolder> {

        private final Context context;
        private final List<TeamMusic> list;

        public MainTeamListAdapter(Context context, List<TeamMusic> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public MainTeamListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemTeamMusicListBinding itemTeamMusicListBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                    R.layout.item_team_music_list, parent, false);
            return new MainTeamListViewHolder(itemTeamMusicListBinding.getRoot());
        }

        @Override
        public void onBindViewHolder(@NonNull MainTeamListViewHolder holder, int position) {
            ItemTeamMusicListBinding binding = DataBindingUtil.getBinding(holder.itemView);
            if(binding != null) {
                binding.ivTeamMusic.setImageResource(list.get(position).resId2);
                binding.tvTeamMusic.setText(list.get(position).titleName);
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
