package com.banlap.llmusic.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.banlap.llmusic.R;
import com.banlap.llmusic.model.MusicLyric;

import java.util.ArrayList;
import java.util.List;

/**
 *  已弃用
 * */
@Deprecated
public class LyricView extends View {

    private List<MusicLyric> musicLyrics;
    private int rThemeId =0;                             //当前主题

    private Paint mPaint;
    private int highLightRow =0;                               //高亮歌词
    private boolean isTouchLyric = false;                      //是否在触摸滚动歌词

    private final int lyricSize = 30;                          //歌词文字大小
    private final int mPaddingY = 100;                         //歌词间距
    private final int mSecondsPaddingY = 10;                   //第二歌词间距
    private final int mMinMove = 10;                           //最小触摸距离
    private int mCurrentTime=0;

    public LyricView(Context context) {
        super(context);
        init();
    }

    public LyricView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(lyricSize);

        musicLyrics = new ArrayList<>();
    }

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
        postInvalidateOnAnimation();
    }

    /** 设置当前主题ID */
    public void setThemeId(int themeId) {
        rThemeId = themeId;
        postInvalidateOnAnimation();
    }

    /**
     * 根据时间进行滚动歌词
     *
     * */
    public void seekLyricByTime(String time) {
        if(null == musicLyrics || 0 == musicLyrics.size()) {
            return;
        }
        mCurrentTime = timeToSec(time);
        for(int i=0; i < musicLyrics.size(); i++ ) {
            if(time.equals(musicLyrics.get(i).lyricTime)) {
                seekLyric(i);
                return;
            }
            /*if(null != musicLyrics.get(i).lyricTime && !"".equals(musicLyrics.get(i).lyricTime)) {
                int currentTime = timeToSec(time);
                int thisTime = timeToSec(musicLyrics.get(i).lyricTime);
                int nextTime = 0;
                if( i+1 < musicLyrics.size() && null != musicLyrics.get(i+1).lyricTime && !"".equals(musicLyrics.get(i+1).lyricTime)) {
                    nextTime = timeToSec(musicLyrics.get(i+1).lyricTime);
                }
                if(currentTime >= thisTime && currentTime <nextTime) {
                    Log.e("LogByAB", "currentTime:" + currentTime + " thisTime: " + thisTime + " nextTime: " + nextTime );
                    seekLyric(i);
                    return;
                }
            }*/
        }
    }


    /**
     * 滚动歌词
     * */
    public void seekLyric(int position) {
        if(null == musicLyrics || 0 == musicLyrics.size() || isTouchLyric) {
            return;
        }
        highLightRow = position;
        postInvalidateOnAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final float width = getWidth();
        final float height = getHeight();
        float highLightLyricX = width/2;
        float highLightLyricY = height/2;

        if(null ==musicLyrics || 0 == musicLyrics.size()) {
            if(0 != rThemeId) {
                if(rThemeId == R.id.ll_theme_normal) {
                    mPaint.setColor(getResources().getColor(R.color.black));
                } else if(rThemeId == R.id.ll_theme_dark) {
                    mPaint.setColor(getResources().getColor(R.color.white));
                } else if(rThemeId == R.id.ll_theme_white) {
                    mPaint.setColor(getResources().getColor(R.color.gray_purple_ac));
                } else if(rThemeId == R.id.ll_theme_orange) {
                    mPaint.setColor(getResources().getColor(R.color.orange_alpha_70));
                } else if(rThemeId == R.id.ll_theme_light) {
                    mPaint.setColor(getResources().getColor(R.color.light_b5));
                } else {
                    mPaint.setColor(getResources().getColor(R.color.black));
                }
            } else {
                mPaint.setColor(getResources().getColor(R.color.black));
            }
            mPaint.setTextSize(lyricSize);
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("当前没有歌词", highLightLyricX, highLightLyricY, mPaint);
            return;
        }
        //绘制当前高亮歌词
        drawHighLyricRow(canvas, highLightLyricX, highLightLyricY);

        //绘制高亮歌词所有前部分
        drawHighLyricLastRowAll(canvas, highLightLyricX, highLightLyricY);

        //绘制高亮歌词所有后部分
        drawHighLyricNextRowAll(canvas, height, highLightLyricX, highLightLyricY);

    }

    /**
     * 绘制当前高亮歌词
     * */
    private void drawHighLyricRow(Canvas canvas, float highLightLyricX, float highLightLyricY) {
        String highlightText = musicLyrics.get(highLightRow).lyricContext;
        if(!TextUtils.isEmpty(highlightText)) {
            if(0 != rThemeId) {
                if(rThemeId == R.id.ll_theme_normal) {
                    mPaint.setColor(getResources().getColor(R.color.light_ea));
                } else if(rThemeId == R.id.ll_theme_dark) {
                    mPaint.setColor(getResources().getColor(R.color.black));
                } else if(rThemeId == R.id.ll_theme_white) {
                    mPaint.setColor(getResources().getColor(R.color.purple));
                } else if(rThemeId == R.id.ll_theme_orange) {
                    mPaint.setColor(getResources().getColor(R.color.orange_f4));
                } else if(rThemeId == R.id.ll_theme_light) {
                    mPaint.setColor(getResources().getColor(R.color.light_8a));
                } else {
                    mPaint.setColor(getResources().getColor(R.color.light_ea));
                }
            } else {
                mPaint.setColor(getResources().getColor(R.color.light_ea));
            }
            mPaint.setTextSize(lyricSize);
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(highlightText, highLightLyricX, highLightLyricY, mPaint);
            //绘制副歌词文本
            String highlightText2 = musicLyrics.get(highLightRow).lyricContext2;
            if(null != highlightText2 && !"".equals(highlightText2)) {
                float secondPadding = mSecondsPaddingY + lyricSize;
                if(0 != rThemeId) {
                    if(rThemeId == R.id.ll_theme_normal) {
                        mPaint.setColor(getResources().getColor(R.color.light_ea));
                    } else if(rThemeId == R.id.ll_theme_dark) {
                        mPaint.setColor(getResources().getColor(R.color.black));
                    } else if(rThemeId == R.id.ll_theme_white) {
                        mPaint.setColor(getResources().getColor(R.color.purple));
                    } else if(rThemeId == R.id.ll_theme_orange) {
                        mPaint.setColor(getResources().getColor(R.color.orange_f4));
                    } else if(rThemeId == R.id.ll_theme_light) {
                        mPaint.setColor(getResources().getColor(R.color.light_8a));
                    } else {
                        mPaint.setColor(getResources().getColor(R.color.light_ea));
                    }
                } else {
                    mPaint.setColor(getResources().getColor(R.color.light_ea));
                }
                mPaint.setTextSize(lyricSize);
                mPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(highlightText2, highLightLyricX, highLightLyricY + secondPadding, mPaint);
            }
        }

    }


    /**
     * 逐行显示高亮歌词
     * */
    private void drawDynamicHighLyricRow(Canvas canvas, float highLightLyricX, float highLightLyricY) {

        String highlightText = musicLyrics.get(highLightRow).lyricContext;
        mPaint.setColor(getResources().getColor(R.color.gray_36));
        mPaint.setTextSize(lyricSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(highlightText, highLightLyricX, highLightLyricY, mPaint);
        //绘制副歌词文本
        String highlightText2 = musicLyrics.get(highLightRow).lyricContext2;
        if(null != highlightText2 && !"".equals(highlightText2)) {
            float secondPadding = mSecondsPaddingY + lyricSize;
            mPaint.setColor(getResources().getColor(R.color.gray_36));
            mPaint.setTextSize(lyricSize);
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(highlightText2, highLightLyricX, highLightLyricY + secondPadding, mPaint);
        }

        if(null != musicLyrics.get(highLightRow).lyricTime && !"".equals(musicLyrics.get(highLightRow).lyricTime)) {
            int highLightRowWidth = (int) mPaint.measureText(highlightText);
            float leftStart = (highLightLyricX - highLightRowWidth) /2;
            int startTime = timeToSec(musicLyrics.get(highLightRow).lyricTime);
            if(highLightRow+1 < musicLyrics.size()) {
                if(null != musicLyrics.get(highLightRow+1).lyricTime && !"".equals(musicLyrics.get(highLightRow+1).lyricTime)) {
                    int endTime = timeToSec(musicLyrics.get(highLightRow+1).lyricTime);
                    if(startTime < endTime) {
                        float width = ((mCurrentTime - startTime)*1.0f  / (endTime - startTime) ) * highLightRowWidth;
                        Log.e("LogByAB", "mCurrentTime: " + mCurrentTime + " startTime:" + startTime
                                + " endTime: " + endTime + " highLightRowWidth: " + highLightRowWidth
                                + " width: " + width );
                        if(width >0) {
                            mPaint.setColor(getResources().getColor(R.color.light_ea));
                            Bitmap textBitmap = Bitmap.createBitmap((int)width, (int) highLightLyricY + mPaddingY, Bitmap.Config.ARGB_8888);
                            Canvas textCanvas = new Canvas(textBitmap);
                            textCanvas.drawText(highlightText,  (float) highLightRowWidth / 2, highLightLyricY, mPaint);
                            canvas.drawBitmap(textBitmap, leftStart, 0, mPaint);
                        }
                    }
                }
            }

        }


    }

    private int timeToSec(String time) {
        String minute = time.substring(0, time.indexOf(":"));
        String sec = time.substring(time.indexOf(":")+1);
        if(Integer.parseInt(minute)>0) {
            return (Integer.parseInt(minute))*60 + Integer.parseInt(sec);
        } else {
            return Integer.parseInt(sec);
        }

    }

    /**
     * 绘制高亮歌词所有前部分
     * */
    private void drawHighLyricLastRowAll(Canvas canvas, float lyricX, float lyricY) {
        int rowNum = highLightRow - 1;
        float rowY = lyricY - mPaddingY - lyricSize;
        if(0 != rThemeId) {
            if(rThemeId == R.id.ll_theme_normal) {
                mPaint.setColor(getResources().getColor(R.color.black));
            } else if(rThemeId == R.id.ll_theme_dark) {
                mPaint.setColor(getResources().getColor(R.color.white));
            } else if(rThemeId == R.id.ll_theme_white) {
                mPaint.setColor(getResources().getColor(R.color.gray_purple_ac));
            } else if(rThemeId == R.id.ll_theme_orange) {
                mPaint.setColor(getResources().getColor(R.color.orange_alpha_70));
            } else if(rThemeId == R.id.ll_theme_light) {
                mPaint.setColor(getResources().getColor(R.color.light_b5));
            } else {
                mPaint.setColor(getResources().getColor(R.color.black));
            }
        } else {
            mPaint.setColor(getResources().getColor(R.color.black));
        }
        mPaint.setTextSize(lyricSize);
        mPaint.setTextAlign(Paint.Align.CENTER);

        while(rowY >= lyricSize && rowNum>=0) {
            String lyricText = musicLyrics.get(rowNum).lyricContext;
            if(!TextUtils.isEmpty(lyricText)) {
                canvas.drawText(lyricText, lyricX, rowY, mPaint);
                //绘制副歌词文本
                String lyricText2 = musicLyrics.get(rowNum).lyricContext2;
                if(null != lyricText2 && !"".equals(lyricText2)) {
                    float secondPadding = mSecondsPaddingY + lyricSize;
                    if(0 != rThemeId) {
                        if(rThemeId == R.id.ll_theme_normal) {
                            mPaint.setColor(getResources().getColor(R.color.black));
                        } else if(rThemeId == R.id.ll_theme_dark) {
                            mPaint.setColor(getResources().getColor(R.color.white));
                        } else if(rThemeId == R.id.ll_theme_white) {
                            mPaint.setColor(getResources().getColor(R.color.gray_purple_ac));
                        } else if(rThemeId == R.id.ll_theme_orange) {
                            mPaint.setColor(getResources().getColor(R.color.orange_alpha_70));
                        } else if(rThemeId == R.id.ll_theme_light) {
                            mPaint.setColor(getResources().getColor(R.color.light_b5));
                        } else {
                            mPaint.setColor(getResources().getColor(R.color.black));
                        }
                    } else {
                        mPaint.setColor(getResources().getColor(R.color.black));
                    }
                    mPaint.setTextSize(lyricSize);
                    mPaint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(lyricText2, lyricX, rowY + secondPadding, mPaint);
                    rowY += secondPadding;
                }
                rowY -= (mPaddingY + lyricSize);
                rowNum--;
            }
        }
    }

    /**
     * 绘制高亮歌词所有后部分
     * */
    private void drawHighLyricNextRowAll(Canvas canvas, float height, float lyricX, float lyricY) {
        int rowNum = highLightRow + 1;
        float rowY = lyricY + mPaddingY + lyricSize;
        if(0 != rThemeId) {
            if(rThemeId == R.id.ll_theme_normal) {
                mPaint.setColor(getResources().getColor(R.color.black));
            } else if(rThemeId == R.id.ll_theme_dark) {
                mPaint.setColor(getResources().getColor(R.color.white));
            } else if(rThemeId == R.id.ll_theme_white) {
                mPaint.setColor(getResources().getColor(R.color.gray_purple_ac));
            } else if(rThemeId == R.id.ll_theme_orange) {
                mPaint.setColor(getResources().getColor(R.color.orange_alpha_70));
            } else if(rThemeId == R.id.ll_theme_light) {
                mPaint.setColor(getResources().getColor(R.color.light_b5));
            } else {
                mPaint.setColor(getResources().getColor(R.color.black));
            }
        } else {
            mPaint.setColor(getResources().getColor(R.color.black));
        }
        mPaint.setTextSize(lyricSize);
        mPaint.setTextAlign(Paint.Align.CENTER);

        while(rowY < height && rowNum < musicLyrics.size()) {
            String lyricText = musicLyrics.get(rowNum).lyricContext;
            if(!TextUtils.isEmpty(lyricText)) {
                canvas.drawText(lyricText, lyricX, rowY, mPaint);
                //绘制副歌词文本
                String lyricText2 = musicLyrics.get(rowNum).lyricContext2;
                if(null != lyricText2 && !"".equals(lyricText2)) {
                    float secondPadding = mSecondsPaddingY + lyricSize;
                    if(0 != rThemeId) {
                        if(rThemeId == R.id.ll_theme_normal) {
                            mPaint.setColor(getResources().getColor(R.color.black));
                        } else if(rThemeId == R.id.ll_theme_dark) {
                            mPaint.setColor(getResources().getColor(R.color.white));
                        } else if(rThemeId == R.id.ll_theme_white) {
                            mPaint.setColor(getResources().getColor(R.color.gray_purple_ac));
                        } else if(rThemeId == R.id.ll_theme_orange) {
                            mPaint.setColor(getResources().getColor(R.color.orange_alpha_70));
                        } else if(rThemeId == R.id.ll_theme_light) {
                            mPaint.setColor(getResources().getColor(R.color.light_b5));
                        } else {
                            mPaint.setColor(getResources().getColor(R.color.black));
                        }
                    } else {
                        mPaint.setColor(getResources().getColor(R.color.black));
                    }
                    mPaint.setTextSize(lyricSize);
                    mPaint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(lyricText2, lyricX, rowY + secondPadding, mPaint);
                    rowY += secondPadding;
                }
                rowY += (mPaddingY + lyricSize);
                rowNum++;
            }
        }
    }


    private float mLastY;  //记录点击初始位置
    /**
     * 触摸滚动歌词
     **/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(null == musicLyrics || 0 == musicLyrics.size()) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouchLyric = true;
                mLastY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                seekLyricByTouchMove(event);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isTouchLyric = false;
                invalidate();
                break;
        }

        return true;
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

        if(moveY <0) {
            //向上滚动
            highLightRow += moveRow;
        } else if (moveY >0){
            //向下滚动
            highLightRow -= moveRow;
        }
        //如果highLightRow小于0则 赋值最小为0行
        highLightRow = Math.max(0, highLightRow);
        //如果highLightRow大于0则 赋值最小为歌词总数减1
        highLightRow = Math.min(highLightRow, musicLyrics.size() -1);

        //滚动行数大于0则重绘制文本
        if(moveRow >0) {
            mLastY = y;
            postInvalidateOnAnimation();
        }
    }
}
