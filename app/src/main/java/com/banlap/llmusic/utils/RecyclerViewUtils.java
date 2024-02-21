package com.banlap.llmusic.utils;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
/**
 * RecyclerView工具类
 * */
public class RecyclerViewUtils {
    /**
     * @desc 在RecyclerView滑动时停止加载图片，在滑动停止时开始加载图片（基于RecyclerView跟Glide）
     */
    public static void scrollSuspend(Context context, RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    Glide.with(context).pauseRequests();
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(context).resumeRequests();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    /**
     * 加大RecyclerView 的缓存，用空间换时间，来提高滚动的流畅性
     * */
    public static void setViewCache(RecyclerView recyclerView) {
        recyclerView.setItemViewCacheSize(20);       //设置RecyclerView中要保留的视图的数量
        recyclerView.setDrawingCacheEnabled(true);   //设置RecyclerView是否启用绘制缓存
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH); //设置绘制缓存的质量
    }
}
