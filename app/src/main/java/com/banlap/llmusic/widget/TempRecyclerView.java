package com.banlap.llmusic.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Bruce .
 */
 
public class TempRecyclerView extends RecyclerView {
    public TempRecyclerView(@NonNull Context context) {
        this(context,null);
    }
 
    public TempRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
 
    private void init() {
 
    }
    LinearLayoutManager linearLayoutManager;
    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        linearLayoutManager = (LinearLayoutManager) layout;
    }
 
    int viewHeight = 0;
    @Override
    public void onScrollStateChanged(int state) {
        Log.d("bruce","state:"+state);
        super.onScrollStateChanged(state);
        if(state == 0){
            int postion = linearLayoutManager.findFirstVisibleItemPosition();
            View view = linearLayoutManager.findViewByPosition(postion);
            int top = view.getTop();
            int offset = 0;
            if(viewHeight == 0){
                viewHeight = view.getHeight();
            }
            if(top == 0){
                return;
            }
            else if(-top < viewHeight/2){
                offset = top;
            }
            else {
                offset = viewHeight+top;
            }
            smoothScrollBy(0, offset);
        }
    }
 
    int offsetY = 0;
 
 
    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        offsetY+=dy;
 
        int first = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        int last = linearLayoutManager.findLastCompletelyVisibleItemPosition();
        View firstview = linearLayoutManager.findViewByPosition(first);
        if(viewHeight == 0){
            if(firstview != null) {
                viewHeight = firstview.getHeight();
            }
        }

        int offseta = 0;
        if(firstview != null) {
            offseta = firstview.getTop();
        }
        float sx = 1f+(float) offseta/viewHeight;
        if(offsetY == 0){
            View view = linearLayoutManager.findViewByPosition(first+1);
            if(view != null) {
                view.setScaleX(2);
            }
        }

        if(firstview != null) {
            firstview.setScaleX(sx);
        }

        View lastview = linearLayoutManager.findViewByPosition(last);
        int lastBottom = 0;
        if(lastview != null) {
            lastBottom = lastview.getBottom();
        }
        offseta = getHeight()-lastBottom;
        sx = 1f+(float) offseta/viewHeight;
        if(firstview != null) {
            firstview.setScaleX(sx);
        }
    }
 
 
}
