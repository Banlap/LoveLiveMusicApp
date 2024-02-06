package com.banlap.llmusic.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.banlap.llmusic.R;
import com.banlap.llmusic.model.MusicLyric;
import com.banlap.llmusic.utils.LLActivityManager;
import com.banlap.llmusic.utils.PxUtil;
import com.banlap.llmusic.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;
/**
 * 歌词滚动View
 * */
public class LyricScrollView extends View {
    private final String TAG = LyricScrollView.class.getSimpleName();
    private List<MusicLyric> musicLyrics;
    private int rThemeId =0;                                   //当前主题
    private Paint mPaint;
    private Paint mPaint2;

    private int currentPosition = 0, lastPosition = 0, playerCurrentPosition =0;
    private int lyricSize = 30;                          //歌词文字大小
    private int zoomSize = 10;                           //歌词文字放大
    private int mPaddingY = 150;                         //歌词间距
    private int mSecondsPaddingY = 15;                   //副歌词间距
    private int secondsLyricSize = 25;                   //副歌词文字大小
    private boolean isTouchLyric = false;                      //是否在触摸滚动歌词
    private boolean mIsRefreshDraw = false;                    //是否刷新绘制：用于拖动滚动条时刷新
    private boolean isStop = false;                            //是否暂停绘制歌词
    private boolean mIsResetLyricSize = false;                  //是否刷新绘制：用于设置歌词字体大小时刷新
    private long mStartTime;
    private int highLightLyricColor, defaultLyricColor;        //高亮颜色、默认颜色
    private boolean isUseLyricDetailColor = false;
    private LyricClickListener lyricClickListener;        //点击监听

    public LyricScrollView(@NonNull Context context) {
        super(context);
        init();
    }

    public LyricScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LyricScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化参数
     * */
    private void init() {
        //适配小型设备
        if(SystemUtil.getInstance().isSmallScaleDevice()) {
            lyricSize = 20;
            zoomSize = 10;
            secondsLyricSize = 15;
            mPaddingY = 100;
            mSecondsPaddingY = 15;
        }
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(lyricSize);
        mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint2.setTextSize(lyricSize);
        musicLyrics = new ArrayList<>();
    }

    /**
     * 自定义设置歌词大小
     * */
    public void setLyricSize(int size) {
        lyricSize = size + 5;
        secondsLyricSize = size;

        if(SystemUtil.getInstance().isSmallScaleDevice()) {
            mPaddingY = size + 85;
        } else {
            mPaddingY = size + 125;
        }

    }

    /**
     * 设置歌曲播放进度
     * */
    public void setMusicPlayerPos(int pos) {
        playerCurrentPosition = pos;
    }

    /**
     * 设置是否绘制歌词
     * */
    public void setIsRefreshDraw(boolean isRefreshDraw) {
        mIsRefreshDraw = isRefreshDraw;
        if(mIsRefreshDraw) {
            invalidate();
        }
    }

    /**
     * 设置状态：是否正在修改歌词字体大小
     * */
    public void setIsResetLyricSize(boolean isResetLyricSize) {
        mIsResetLyricSize = isResetLyricSize;
        if(mIsResetLyricSize) {
            invalidate();
        }
    }

    /**
     * 设置当前主题Id
     * */
    public void setThemeId(int themeId) { rThemeId = themeId; }

    /**
     * 是否转换颜色
     * */
    public void setIsUseLyricDetailColor(boolean isUse) { isUseLyricDetailColor = isUse; }

    /**
     * 设置当前歌曲的歌词
     * */
    public void setMusicLyrics(List<MusicLyric> list) {
        if(null != musicLyrics) {
            musicLyrics.clear();
            if(null != list) {
                musicLyrics.addAll(list);
            }
        }
    }

    /**
     * 开启绘制歌词
     * */
    public void initView() {
        currentPosition = 0;
        lastPosition = 0;
        setScrollY(0);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final float width = getMeasuredWidth();
        final float height = getMeasuredHeight();
        float lyricX = width/2;
        float lyricY = height/2;

        if(null == musicLyrics || 0 == musicLyrics.size()) {
            setLyricColor(isUseLyricDetailColor);
            mPaint.setColor(defaultLyricColor);
            mPaint.setTextSize(lyricSize);
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("当前没有歌词", lyricX, lyricY, mPaint);
            return;
        }

        drawLyric(canvas, lyricX, lyricY);

        if(!isStop || mIsRefreshDraw || isTouchLyric || mIsResetLyricSize) {
            if(!isTouchLyric) {
                getMusicLyricPos();
                int start = timeToMill(musicLyrics.get(currentPosition).lyricTime);
                float v = (playerCurrentPosition - start) >= 500 ?
                        currentPosition * mPaddingY :
                        lastPosition * mPaddingY + (currentPosition - lastPosition) * mPaddingY * ((playerCurrentPosition - start) / 500f);
               /* Log.i("ABMusicPlayer", "v: " + v + " p:" + playerCurrentPosition
                        + " s:" + start + " p-s:" + (playerCurrentPosition - start) + " c: " + currentPosition);
                */
                setScrollY((int) v);
                if (getScrollY() == currentPosition * mPaddingY) {
                    lastPosition = currentPosition;
                }
            } else {
                //Log.i("ABMusicPlayer", "currentPosition: " + currentPosition + " mPaddingY: " + mPaddingY);
                setScrollY((int) currentPosition * mPaddingY);
            }
            postInvalidateDelayed(100);
        }
    }

    /** 绘制暂停与恢复 */
    public void posLock(boolean isLock) {
        if(!isLock) {
            //恢复绘制
            isStop = false;
            invalidate();
        } else {
            isStop = true;
            mIsRefreshDraw = false;
        }
    }


    /**
     * 获取当前高亮歌词
     * */
    private void getMusicLyricPos() {
        try{
            int currentTime = playerCurrentPosition;

            if(null != musicLyrics) {
                if(!"".equals(musicLyrics.get(1).lyricTime)) {
                    if (currentTime < timeToMill(musicLyrics.get(0).lyricTime)) {
                        currentPosition = 0;
                        return;
                    }
                } else {
                    return;
                }

                if(!"".equals(musicLyrics.get(musicLyrics.size()-1).lyricTime)) {
                    if (currentTime > timeToMill(musicLyrics.get(musicLyrics.size()-1).lyricTime)) {
                        currentPosition = musicLyrics.size()-1;
                        return;
                    }
                } else {
                    if(!"".equals(musicLyrics.get(musicLyrics.size()-2).lyricTime)) {
                        if (currentTime > timeToMill(musicLyrics.get(musicLyrics.size()-2).lyricTime)) {
                            currentPosition = musicLyrics.size()-2;
                            return;
                        }
                    } else {
                        return;
                    }
                }

                for(int i =0; i < musicLyrics.size(); i++) {
                    if(!"".equals(musicLyrics.get(i).lyricTime)&& !"".equals(musicLyrics.get(i).lyricEndTime) ) {
                        if(currentTime >= timeToSec(musicLyrics.get(i).lyricTime) && currentTime < timeToMill(musicLyrics.get(i).lyricEndTime)){
                            currentPosition = i;
                            return;
                        }
                    }
                }
            }

        } catch (Exception e) {
            postInvalidateDelayed(100);
        }
    }

    /**
     * 绘制当前歌曲所有歌词内容
     * */
    private void drawLyric(Canvas canvas, float lyricX, float lyricY) {
        if(null != musicLyrics && musicLyrics.size() >0) {
            setLyricColor(isUseLyricDetailColor);
            for(int i=0; i< musicLyrics.size(); i++) {
                //绘制主歌词
                mPaint.setTextSize(i == currentPosition? lyricSize + zoomSize : lyricSize);
                mPaint.setTextAlign(Paint.Align.CENTER);
                if(!isStop && !isTouchLyric) {
                    mPaint.setColor(i == currentPosition? highLightLyricColor : defaultLyricColor);
                } else {
                    mPaint.setColor(defaultLyricColor);
                }
                mPaint.setFakeBoldText(i == currentPosition);

                canvas.drawText(musicLyrics.get(i).lyricContext, lyricX, lyricY + mPaddingY * i, mPaint);

                //绘制副歌词
                String lyricText2 = musicLyrics.get(i).lyricContext2;
                if(null != lyricText2 && !"".equals(lyricText2)) {
                    mPaint.setTextSize(i == currentPosition? secondsLyricSize + zoomSize : secondsLyricSize);
                    mPaint.setTextAlign(Paint.Align.CENTER);
                    float secondPadding = mSecondsPaddingY + secondsLyricSize;
                    canvas.drawText(musicLyrics.get(i).lyricContext2, lyricX, (lyricY + mPaddingY * i) + secondPadding, mPaint);

                }
            }
        }
    }

    /**
     *  绘制歌词颜色
     * */
    private void setLyricColor(boolean isLyricDetail) {
        if(0 != rThemeId) {
            if(rThemeId == R.id.ll_theme_normal) {
                highLightLyricColor = getResources().getColor(R.color.blue_ed);
                defaultLyricColor = getResources().getColor(R.color.black);
            } else if(rThemeId == R.id.ll_theme_blue) {
                highLightLyricColor = getResources().getColor(isLyricDetail? R.color.light_f9 : R.color.blue_0E);
                defaultLyricColor = getResources().getColor(isLyricDetail? R.color.white : R.color.blue_ed);
            } else if(rThemeId == R.id.ll_theme_dark) {
                highLightLyricColor = getResources().getColor(R.color.black);
                defaultLyricColor = getResources().getColor(R.color.white);
            } else if(rThemeId == R.id.ll_theme_white) {
                highLightLyricColor = getResources().getColor(R.color.purple);
                defaultLyricColor = getResources().getColor(R.color.gray_purple_ac);
            } else if(rThemeId == R.id.ll_theme_orange) {
                highLightLyricColor = getResources().getColor(R.color.orange_f4);
                defaultLyricColor = getResources().getColor(R.color.orange_0b);
            } else if(rThemeId == R.id.ll_theme_light) {
                highLightLyricColor = getResources().getColor(R.color.light_8a);
                defaultLyricColor = getResources().getColor(R.color.light_b5);
            } else if(rThemeId == R.id.ll_theme_red) {
                highLightLyricColor = getResources().getColor(isLyricDetail? R.color.red_1a : R.color.red_3a);
                defaultLyricColor = getResources().getColor(isLyricDetail? R.color.white : R.color.red_4d);
            } else {
                highLightLyricColor = getResources().getColor(R.color.blue_ed);
                defaultLyricColor = getResources().getColor(R.color.black);
            }
        } else {
            highLightLyricColor = getResources().getColor(R.color.blue_ed);
            defaultLyricColor = getResources().getColor(R.color.black);
        }
    }

    private final int mMinMove = 10;                           //最小触摸距离
    private float mLastY;  //记录点击初始位置
    private int mStartX, mStartY; //触摸时记录开始时的坐标 用于判断按下事件跟结束事件

    /**
     * 触摸滚动歌词
     **/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(null == musicLyrics || 0 == musicLyrics.size()) {
            Log.i(TAG, "super.onTouchEvent(event)：" + super.onTouchEvent(event));
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = (int) event.getRawX();
                mStartY = (int) event.getRawY();
                mStartTime = System.currentTimeMillis();
                isTouchLyric = true;
                mLastY = event.getY();
                postInvalidateOnAnimation();
                handler.removeCallbacksAndMessages(null);
                break;
            case MotionEvent.ACTION_MOVE:
                seekLyricByTouchMove(event);
                handler.removeCallbacksAndMessages(null);
                break;
            case MotionEvent.ACTION_UP:
                long endTime = System.currentTimeMillis();
                if(mStartTime - endTime <= 500) {
                    int mStopX = (int) event.getRawX();
                    int mStopY = (int) event.getRawY();
                    if (Math.abs(mStartX - mStopX) <= 10 || Math.abs(mStartY - mStopY) <= 10) {
                        if(lyricClickListener != null) {
                            lyricClickListener.onSingleTap();
                        }
                    }
                }
                android.os.Message message = new Message();
                message.what = 1;
                handler.sendMessageDelayed(message, 2000);
                break;
        }

        return true;
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1) {
                isTouchLyric = false;
            }
        }
    };

    /**
     * 设置点击回调
     * */
    public void setOnLyricClickListener(LyricClickListener lyricClickListener) {
        this.lyricClickListener = lyricClickListener;
    }

    public interface LyricClickListener {
        /** 点击回调 */
        void onSingleTap();
    }
    /**
     * 根据触摸滑动的距离滚动歌词
     * */
    private void seekLyricByTouchMove(MotionEvent event) {
        float y = event.getY();               //当前触摸位置
        float moveY = y - mLastY;       //第一次触摸位置 到 当前触摸位置 距离
        //滚动距离未达到最小距离则不滚动
        if(Math.abs(moveY) < mMinMove) {
            return;
        }
        //获取滚动歌词的行数
        int moveRow = Math.abs((int) moveY / lyricSize);
        //Log.i("ABMusicPlayer", "moveY: " + moveY);

        if(moveY <0) {  //向上滚动
            currentPosition += moveRow;
        } else if (moveY >0){  //向下滚动
            currentPosition -= moveRow;
        }
        //如果highLightRow小于0则 赋值最小为0行
        currentPosition = Math.max(0, currentPosition);
        //如果highLightRow大于0则 赋值最小为歌词总数减1
        currentPosition = Math.min(currentPosition, musicLyrics.size() -1);

        //滚动行数大于0则重绘制文本
        if(moveRow >0) {
            mLastY = y;
            postInvalidateOnAnimation();
        }
    }

    /** 时间转换为秒 */
    private int timeToSec(String time) {
        if(0 == time.trim().length()) {
            return 0;
        }
        String minute = time.substring(0, time.indexOf(":"));
        String sec = time.substring(time.indexOf(":")+1);
        if(Integer.parseInt(minute)>0) {
            return (Integer.parseInt(minute))*60 + Integer.parseInt(sec);
        } else {
            return Integer.parseInt(sec);
        }
    }

    /** 时间转换为毫秒 */
    private int timeToMill(String time) {
        if(0 == time.trim().length()) {
            return 0;
        }
        String minute = time.substring(0, time.indexOf(":"));
        String sec = time.substring(time.indexOf(":")+1);
        if(Integer.parseInt(minute)>0) {
            return ((Integer.parseInt(minute)) * 60 + Integer.parseInt(sec)) * 1000;
        } else {
            return Integer.parseInt(sec) * 1000;
        }
    }
}
