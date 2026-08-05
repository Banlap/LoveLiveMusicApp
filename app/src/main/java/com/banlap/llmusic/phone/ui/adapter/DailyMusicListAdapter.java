package com.banlap.llmusic.phone.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.banlap.llmusic.R;
import com.banlap.llmusic.databinding.ItemDailyMusicListBinding;
import com.banlap.llmusic.sql.room.RoomPlayMusic;

import java.util.List;

public class DailyMusicListAdapter extends RecyclerView.Adapter<DailyMusicListAdapter.DailyMusicListViewHolder>{

    private final Context context;
    private final List<RoomPlayMusic> list;

    public DailyMusicListAdapter(Context context, List<RoomPlayMusic> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DailyMusicListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDailyMusicListBinding dailyMusicListBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.item_daily_music_list, parent, false);
        return new DailyMusicListViewHolder(dailyMusicListBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull DailyMusicListViewHolder holder, int position) {
        ItemDailyMusicListBinding dailyMusicListBinding = DataBindingUtil.getBinding(holder.itemView);
        if(dailyMusicListBinding != null) {

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class DailyMusicListViewHolder extends RecyclerView.ViewHolder {
        public DailyMusicListViewHolder(@NonNull View itemView) { super(itemView); }
    }

}
