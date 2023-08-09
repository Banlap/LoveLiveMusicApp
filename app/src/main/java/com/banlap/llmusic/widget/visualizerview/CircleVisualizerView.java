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

public class CircleVisualizerView extends View {
    private static final String TAG = CircleVisualizerView.class.getSimpleName();
    private VisualizerHelper visualizerHelper;

    private Paint mPaint;
    private Path mPath;
    private RectF mRect;

    private float[] mRawAudioBytes;

    private float mStrokeWidth;
    private float mItemMargin;
    private float mSpectrumRatio;
    private float centerX, centerY;
    private int mSpectrumCount;

    private boolean mIsStop;


    public CircleVisualizerView(Context context) {
        super(context);
        init();
    }

    public CircleVisualizerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleVisualizerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mStrokeWidth = 5;
        mItemMargin = 12f;
        mSpectrumRatio = 5.0f; //长度
        mSpectrumCount = 60;

        mPath = new Path();

        mRect = new RectF();

        mPaint = new Paint();
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(getResources().getColor(R.color.light_ff));
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.SOLID));

        visualizerHelper = new VisualizerHelper();
        visualizerHelper.setVisualizerCallback(new VisualizerHelper.VisualizerCallback() {
            @Override
            public void onDataReturn(float[] parseData) {
                //Log.i(TAG, "parseData: " + Arrays.toString(parseData));
                mRawAudioBytes = parseData;
            }
        });
        visualizerHelper.setVisualCount(mSpectrumCount);
        invalidate();
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


    /** 是否暂停 */
    public void posLock(boolean isStop) {
        mIsStop = isStop;
        if(!mIsStop) {
            postInvalidateDelayed(100);
        }
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
        centerX = mRect.width() / 2;
        centerY = mRect.height() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float radius = 150;
        mStrokeWidth = (float) ((Math.PI * 2 * radius - (mSpectrumCount - 1) * mItemMargin) / mSpectrumCount * 1.0f);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        canvas.drawCircle(centerX, centerY, radius, mPaint);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.FILL);
        mPath.moveTo(0, centerY);

        if(!mIsStop && mRawAudioBytes != null) {
            Log.i(TAG, "mRawAudioBytes: " + Arrays.toString(mRawAudioBytes));
        }
        for (int i = 0; i < mSpectrumCount; i++) {
            double angel = ((360d/ mSpectrumCount *1.0d) * (i+1));
            double startX = centerX + (radius + mStrokeWidth/2) * Math.sin(Math.toRadians(angel));
            double startY = centerY + (radius + mStrokeWidth/2) * Math.cos(Math.toRadians(angel));
            double stopX = centerX + (radius + mStrokeWidth/2 + (mIsStop ? 0 : (mRawAudioBytes == null)? 0 : mSpectrumRatio * mRawAudioBytes[i])) * Math.sin(Math.toRadians(angel));
            double stopY = centerY + (radius + mStrokeWidth/2 + (mIsStop ? 0 : (mRawAudioBytes == null)? 0 : mSpectrumRatio * mRawAudioBytes[i])) * Math.cos(Math.toRadians(angel));
            canvas.drawLine((float) startX, (float) startY, (float) stopX, (float) stopY, mPaint);
        }

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
