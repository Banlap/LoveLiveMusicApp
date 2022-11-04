package com.banlap.llmusic.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class LyricScrollViewGroup extends ViewGroup {
    private Scroller scroller;
    private int mScreenHeight; //屏幕的高度

    public LyricScrollViewGroup(Context context) {
        super(context);
        init(context);
    }

    public LyricScrollViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LyricScrollViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public LyricScrollViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void init(Context context) {
        scroller = new Scroller(context);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        int count = getChildCount();
        MarginLayoutParams mlp = (MarginLayoutParams) getLayoutParams(); //设置viewGroup的高度
        mScreenHeight = mlp.height =getHeight()*count;
        setLayoutParams(mlp);
        for(int index =0 ; index< count; index++){
            View childView = getChildAt(index);
            if(childView.getVisibility() != GONE){
                childView.layout(i,index*getHeight(),i2,(index+1)*getHeight());
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for(int i =0; i < count; i++){
            View childView = getChildAt(i);
            measureChild(childView,widthMeasureSpec,heightMeasureSpec);
        }
    }

    private int mLastY = 0;
    private int mStart = 0;
    private int mEnd = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int y = (int) event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                mStart = getScrollY(); //记录开始位置
                break;
            case MotionEvent.ACTION_MOVE:
                if(!scroller.isFinished()){
                    scroller.abortAnimation();
                }
                int dy = mLastY - y;
                if(getScaleY() < 0){
                    dy = 0;
                }
                if(getScaleY()>getHeight()){
                    dy = 0;
                }
                scrollBy(0,dy);
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                mEnd = getScrollY();
                int dScrollY = mEnd - mStart;
                if(dScrollY >0){
                    if(dScrollY < getHeight()/3){
                        scroller.startScroll(0,getScrollY(),0,-dScrollY);
                    }else {
                        scroller.startScroll(0,getScrollY(),0,getHeight()-dScrollY);
                    }
                }else {
                    if(-dScrollY < getHeight()/3){
                        scroller.startScroll(0,getScrollY(),0,-dScrollY);
                    }else {
                        scroller.startScroll(0,getScrollY(),0,-getHeight()-dScrollY);
                    }
                }
                break;
        }
        postInvalidate();
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(scroller.computeScrollOffset()){
            scrollTo(0,scroller.getCurrY());
            postInvalidate();
        }
    }

}
