package com.banlap.llmusic.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.banlap.llmusic.R;
import com.banlap.llmusic.model.MusicLyric;
import com.banlap.llmusic.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 歌词滚动View - 显示两行
 * */
public class SingleLyricScrollView extends View {
    private final String TAG = SingleLyricScrollView.class.getSimpleName();
    private List<MusicLyric> musicLyrics;
    private List<MusicLyric> musicLyricsTemp;

    private int rThemeId =0;                                   //当前主题
    private Paint mPaint;
    private Paint mPaint2;

    private int currentPosition = 0, playerCurrentPosition = 0, lastPosition = 0;
    private int lyricSize = 30;                          //歌词文字大小
    private int lyricSmallSize = 25;                     //歌词副文字大小
    private int zoomSize = 10;

    private float yOffset = 90;                               //歌词绘制的纵向偏移量
    private int ySmallOffset = 40;                            //主副歌词之间距离
    private int yScrollOffset = 100;                          //滚动纵向偏移量

    private boolean mIsRefreshDraw = false;                    //是否刷新绘制：用于拖动滚动条时刷新
    private boolean isStop = false;                            //是否暂停绘制歌词
    private boolean mIsResetLyricSize = false;                  //是否刷新绘制：用于设置歌词字体大小时刷新
    private boolean isCleanOnce = false;                       //是否清理一次滚动歌词的缓存

    /** 处歌词理显示效果：先执行isFirstInto和isSecondInto后再做效果处理 */
    private boolean isFirstInto = false;
    private boolean isSecondInto = false;

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
            yOffset = 50;
            ySmallOffset = 30;
            yScrollOffset = 80;
        }
        musicLyrics = new ArrayList<>();
        musicLyricsTemp = new ArrayList<>();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(lyricSize);
        mPaint.setTextAlign(Paint.Align.CENTER);

        mPaint2 = new Paint();
        mPaint2.setAntiAlias(true);
        mPaint2.setTextSize(lyricSize);
        mPaint2.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * 自定义设置歌词大小
     * */
    public void setLyricSize(int size) {
        lyricSize = size + 5;
        lyricSmallSize = size;

        if(SystemUtil.getInstance().isSmallScaleDevice()) {
            yOffset = size + 35;
            ySmallOffset = size + 15;
            yScrollOffset = size + 65;
        } else {
            yOffset = size + 65;
            ySmallOffset = size + 15;
            yScrollOffset = size + 75;
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
        } else {
            musicLyricsTemp.clear();
            Log.i(TAG,"SingleLyric currentPosition: " + currentPosition);
            if(currentPosition <= 3) {
                isFirstInto = false;
                isSecondInto = false;
            }
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
        Log.i(TAG,"SingleLyric initView");
        currentPosition = 0;
        musicLyricsTemp.clear();
        isFirstInto = false;
        isSecondInto = false;
        setScrollY(0);
        invalidate();
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

        showLyric(canvas);
    }

    /**
     * 展示两行歌词
     * */
    private void showLyric(Canvas canvas) {
        getMusicLyricPos();
        float centerY = getHeight() /4;
        boolean isClear = false;
        // 绘制当前歌词和上下一行歌词
        for (int i = 0; i <= 2; i++) {
            int index = currentPosition + i;
            if (index >= 0 && index < musicLyrics.size()) {
                float y = centerY + i * mPaint.getTextSize();

                if(i != 0) {
                    setHighLightLyric(false);
                    y += (i == 1) ? yOffset : yOffset*2;
                } else {
                    setHighLightLyric(true);
                }

                int start = timeToMill(musicLyrics.get(currentPosition).lyricTime);
                //Log.i(TAG, "(playerCurrentPosition - start): " + ((playerCurrentPosition - start) <500));
                if ((playerCurrentPosition - start) < 500) {
                    //先执行一次isFirstInto和isSecondInto后再走显示效果
                    if(!isFirstInto) {
                        isFirstInto = true;
                    } else {
                        if(isSecondInto) {
                            if(musicLyricsTemp.size()>=3) {
                                float duration = (playerCurrentPosition - start) / 500f;
                                if(i == 0) {
                                    mPaint.setAlpha((int) (255 - (float) 255 * duration));
                                    mPaint.setTextSize((lyricSize + zoomSize) - (zoomSize) * duration);

                                } else if(i == 1) {
                                    mPaint.setAlpha(255);
                                    mPaint.setTextSize(lyricSize + (zoomSize * duration) );
                                } else {
                                    mPaint.setAlpha((int) (255 * duration));
                                    mPaint.setTextSize(lyricSize);
                                }

                                y -= yScrollOffset * duration;
                                if(y >300) {
                                    y = 300;
                                }

                                canvas.drawText(musicLyricsTemp.get(i).lyricContext, getWidth() / 2, y, mPaint);

                                if(i == 0) {
                                    mPaint.setTextSize((lyricSmallSize + zoomSize) - (zoomSize) * duration);
                                } else if(i == 1) {
                                    mPaint.setTextSize(lyricSmallSize + (zoomSize * duration) );
                                } else {
                                    mPaint.setTextSize(lyricSmallSize);
                                }
                                canvas.drawText(musicLyricsTemp.get(i).lyricContext2, getWidth() / 2, y + ySmallOffset, mPaint);
                            } else {
                                mPaint.setAlpha((i == 2) ?  0 : 255);
                                setLyricCanvas(canvas, i, index, y);
                            }
                        } else {
                            mPaint.setAlpha((i == 2) ?  0 : 255);
                            setLyricCanvas(canvas, i, index, y);
                        }
                    }

                } else {
                    if(isFirstInto) { isSecondInto = true; }
                    mPaint.setAlpha(i != 2 ? 255 : 0);
                    setLyricCanvas(canvas, i, index, y);
                    isClear = true;
                }
                if(musicLyricsTemp.size() <=2) {
                    musicLyricsTemp.add(musicLyrics.get(index));
                }
            }
        }

        //每次仅清理一次：歌词滚动完成后再清理
        if(isClear) {
            if(!isCleanOnce) {
                musicLyricsTemp.clear();
                isCleanOnce = true;
            }
        } else {
            isCleanOnce = false;
        }

        if(!isStop || mIsRefreshDraw || mIsResetLyricSize) {
            postInvalidateDelayed(100);
        }
    }

    /**
     * 绘制歌词布局
     * */
    private void setLyricCanvas(Canvas canvas, int i, int index, float y) {
        mPaint.setTextSize(i == 0 ? lyricSize + zoomSize : lyricSize);
        canvas.drawText(musicLyrics.get(index).lyricContext, getWidth() / 2, y, mPaint);
        mPaint.setTextSize(i == 0 ? lyricSmallSize + zoomSize : lyricSmallSize);
        canvas.drawText(musicLyrics.get(index).lyricContext2, getWidth() / 2, y + ySmallOffset, mPaint);
    }

    /**
     * 设置高亮歌词颜色
     * */
    public void setHighLightLyric(boolean isHighLight) {
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
            } else if(rThemeId == R.id.ll_theme_red) {
                mPaint.setColor(getResources().getColor(isHighLight? R.color.red_1a : R.color.white));
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
            //Log.i(TAG, "currentTime: " + currentTime);
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
                            //smoothScrollToOffset(yOffset);
                            return;
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.i(TAG, "e: " + e.getMessage());
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
