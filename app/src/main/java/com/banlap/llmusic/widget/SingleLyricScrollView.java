package com.banlap.llmusic.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.banlap.llmusic.R;
import com.banlap.llmusic.model.MusicLyric;
import com.banlap.llmusic.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 歌词滚动View
 * */
public class SingleLyricScrollView extends View {
    private final String TAG = SingleLyricScrollView.class.getSimpleName();
    private List<MusicLyric> musicLyrics;
    private int rThemeId =0;                                   //当前主题
    private Paint mPaint;

    private int currentPosition = 0, playerCurrentPosition = 0;
    private int lyricSize = 30;                          //歌词文字大小
    private int lyricSmallSize = 25;                     //歌词副文字大小
    private boolean mIsRefreshDraw = false;                    //是否刷新绘制：用于拖动滚动条时刷新
    private boolean isStop = false;                            //是否暂停绘制歌词
    private float yOffset = 70;                               //歌词绘制的纵向偏移量
    private int ySmallOffset = 50;                          //主副歌词之间距离
    private ValueAnimator scrollAnimator;

    public SingleLyricScrollView(@NonNull Context context) {
        super(context);
        init();
    }

    public SingleLyricScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SingleLyricScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
            lyricSmallSize = 15;
            ySmallOffset = 30;
            yOffset = 50;
        }
        musicLyrics = new ArrayList<>();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(lyricSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        currentPosition = 0;
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
     * 设置当前主题Id
     * */
    public void setThemeId(int themeId) { rThemeId = themeId; }

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
        setScrollY(0);
        invalidate();
    }

    private void smoothScrollToOffset(float targetOffset) {
        if (scrollAnimator != null && scrollAnimator.isRunning()) {
            scrollAnimator.cancel();
        }

        scrollAnimator = ValueAnimator.ofFloat(yOffset, targetOffset);
        scrollAnimator.setInterpolator(new LinearInterpolator());
        scrollAnimator.addUpdateListener(animation -> {
            yOffset = (float) animation.getAnimatedValue();
            invalidate();
        });
        scrollAnimator.setDuration(300); // 设置动画时长
        scrollAnimator.start();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final float width = getMeasuredWidth();
        final float height = getMeasuredHeight();
        float lyricX = width/2;
        float lyricY = height/2;

        if (musicLyrics == null || musicLyrics.size() == 0) {
            setHighLightLyric(false);
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("当前没有歌词", lyricX, lyricY, mPaint);
            return;
        }

        getMusicLyricPos();
        float centerY = getHeight() /4;

        // 绘制当前歌词和上下一行歌词
        for (int i = 0; i <= 1; i++) {
            int index = currentPosition + i;
            if (index >= 0 && index < musicLyrics.size()) {
                setHighLightLyric(true);
                float y = centerY + i * mPaint.getTextSize();
                if(i == 1) {
                    y = y + yOffset;
                    mPaint.setTextSize(lyricSize);
                    setHighLightLyric(false);
                }
                canvas.drawText(musicLyrics.get(index).lyricContext, getWidth() / 2, y, mPaint);
                mPaint.setTextSize(i == 1? lyricSmallSize  : lyricSmallSize +10);
                canvas.drawText(musicLyrics.get(index).lyricContext2, getWidth() / 2, y + ySmallOffset, mPaint);

            }
        }

        if(!isStop || mIsRefreshDraw) {
            postInvalidateDelayed(100);
        }

    }

    /**
     * 设置高亮歌词颜色
     * */
    public void setHighLightLyric(boolean isHighLight) {
        mPaint.setTextSize(isHighLight? lyricSize + 10 : lyricSize);
        if(0 != rThemeId) {
            if(rThemeId == R.id.ll_theme_normal) {
                mPaint.setColor(getResources().getColor(isHighLight? R.color.blue_ed : R.color.black));
            } else if(rThemeId == R.id.ll_theme_blue) {
                mPaint.setColor(getResources().getColor(isHighLight? R.color.light_f9 : R.color.white));
            } else if(rThemeId == R.id.ll_theme_dark) {
                mPaint.setColor(getResources().getColor(isHighLight? R.color.black : R.color.white));
            } else if(rThemeId == R.id.ll_theme_white) {
                mPaint.setColor(getResources().getColor(isHighLight? R.color.purple : R.color.gray_purple_ac));
            } else if(rThemeId == R.id.ll_theme_orange) {
                mPaint.setColor(getResources().getColor(isHighLight? R.color.orange_f4 : R.color.orange_0b));
            } else if(rThemeId == R.id.ll_theme_light) {
                mPaint.setColor(getResources().getColor(isHighLight? R.color.light_8a : R.color.light_b5));
            } else {
                mPaint.setColor(getResources().getColor(isHighLight? R.color.blue_ed : R.color.black));
            }
        } else {
            mPaint.setColor(getResources().getColor(isHighLight? R.color.blue_ed : R.color.black));
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
            Log.i(TAG, "currentTime: " + currentTime);
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

                            smoothScrollToOffset(yOffset);
                            return;
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.i(TAG, "v11");
            postInvalidateDelayed(100);
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
