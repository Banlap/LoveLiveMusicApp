package com.banlap.llmusic.phone.ui.fragment;

import android.content.Context;
import android.view.LayoutInflater;
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

    private void initMainView() {
        mainTeamListAdapter = new MainTeamListAdapter(getContext(), AppMusic.getInstance().getTeamMusicList());
        getViewDataBinding().trvList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        getViewDataBinding().trvList.setAdapter(mainTeamListAdapter);

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
