package com.banlap.llmusic.widget.visualizerview;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.banlap.llmusic.R;
import com.banlap.llmusic.service.MusicPlayService;
import com.banlap.llmusic.utils.VisualizerHelper;

import java.util.Arrays;

public class SingleVisualizerView extends View {
    private static final String TAG = SingleVisualizerView.class.getSimpleName();
    private VisualizerHelper visualizerHelper;

    private Paint mPaint;
    private RectF mRect;

    private float[] mRawAudioBytes;

    private float mStrokeWidth;
    private float mItemMargin;

    private int mSpectrumCount;

    private RectF barRect;
    private int barWidth;
    private int barSpacing;

    private boolean mIsStop;

    public SingleVisualizerView(Context context) {
        super(context);
        init();
    }

    public SingleVisualizerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SingleVisualizerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mStrokeWidth = 5;
        mItemMargin = 12f;
        mSpectrumCount = 60;

        barWidth = 10; // 设置频谱柱的宽度
        barSpacing = 5; // 设置频谱柱之间的间距
        barRect = new RectF();


        mRect = new RectF();

        mPaint = new Paint();
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(getResources().getColor(R.color.light_ff));
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.SOLID));

       /* visualizerHelper = new VisualizerHelper();
        visualizerHelper.setVisualizerCallback(new VisualizerHelper.VisualizerCallback() {
            @Override
            public void onDataReturn(float[] parseData) {
                //Log.i(TAG, "parseData: " + Arrays.toString(parseData));
                mRawAudioBytes = parseData;
            }
        });
        visualizerHelper.setVisualCount(mSpectrumCount);
        invalidate();*/
    }

    /**
     * 开始启动
     * */
    public void startVisualizer() {
        if(visualizerHelper != null) {
            visualizerHelper.startVisualizer(MusicPlayService.getAudioSessionId());
        }
        mIsStop = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int finallyWidth;
        int finallyHeight;
        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (wSpecMode == MeasureSpec.EXACTLY) {
            finallyWidth = wSpecSize;
        } else {
            finallyWidth = 500;
        }

        if (hSpecMode == MeasureSpec.EXACTLY) {
            finallyHeight = hSpecSize;
        } else {
            finallyHeight = 500;
        }

        setMeasuredDimension(finallyWidth, finallyHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mRect.set(0, 0, getWidth(), getHeight() - 50);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mStrokeWidth = (mRect.width() - (mSpectrumCount - 1) * mItemMargin) / mSpectrumCount * 1.0f;
        //Log.i(TAG, "mStrokeWidth:" + mStrokeWidth +  " mSpectrumCount: " + mSpectrumCount + " mRawAudioBytes: " + Arrays.toString(mRawAudioBytes));
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.FILL);


        int width = getWidth();
        int height = getHeight()/2;
        int halfHeight = height /2;

        int totalBarWidth = mSpectrumCount * barWidth + (mSpectrumCount - 1) * barSpacing;
        int startX = (width - totalBarWidth) / 2; // 计算绘制频谱柱的起始位置

        for (int i = 0; i < mSpectrumCount; i++) {
            int value = 0;
            if (mRawAudioBytes != null) {
                value = (int) mRawAudioBytes[i];
            }

            int barHeight = (Math.abs(value) * halfHeight / 128) + 10; // 将频谱数据转换为高度

            int left = startX + i * (barWidth + barSpacing); // 计算每个频谱柱的左边位置
            int top = height - barHeight;
            int right = left + barWidth;
            int bottom = height;

            // 设置带有圆角的矩形的位置
            barRect.set(left, top, right, bottom);

            // 绘制带有圆角的实心频谱柱
            canvas.drawRoundRect(barRect, 10, 10, mPaint);
        }

        if(!mIsStop) {
            postInvalidateDelayed(100);
        }
    }

    /** 是否暂停 */
    public void posLock(boolean isStop) {
        mIsStop = isStop;
        if(!mIsStop) {
            postInvalidateDelayed(100);
        }
    }


    /** 销毁时调用 */
    public void release() {
        if (visualizerHelper != null) {
            visualizerHelper.release();
        }
        mIsStop = true;
    }

}
