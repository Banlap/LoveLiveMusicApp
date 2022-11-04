package com.banlap.llmusic.widget;

import android.animation.FloatEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.banlap.llmusic.R;

import java.util.Random;

/**
 * @author Banlap on 2021/12/9
 */
public class MusicLoadingView extends View implements Runnable {
    /** 随机数*/
    private static Random mRandom = new Random();
    /** View默认最小宽度 */
    private static final int DEFAULT_MIN_WIDTH = 65;
    /** 默认4条音轨 */
    private static final int DEFAULT_RAIL_COUNT = 4;
    /** 控件宽 */
    private int mViewWidth;
    /** 控件高 */
    private int mViewHeight;
    /** 画笔 */
    private Paint mPaint;
    /** 音轨数量 */
    private int mRailCount;
    /** 音轨颜色 */
    private int mRailColor;
    /** 每条音轨的线宽 */
    private float mRailLineWidth;
    /** Float类型估值器，用于在指定数值区域内进行估值 */
    private FloatEvaluator mFloatEvaluator;

    public MusicLoadingView(Context context) {
        this(context, null);
    }

    public MusicLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        initAttr(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setColor(mRailColor);
        mPaint.setStrokeWidth(mRailLineWidth);
        mPaint.setStyle(Paint.Style.FILL);
        //设置笔触为方形
        mPaint.setStrokeCap(Paint.Cap.SQUARE);
        mPaint.setAntiAlias(true);
        mFloatEvaluator = new FloatEvaluator();
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MusicLoadingView, defStyleAttr, 0);
        mRailCount = array.getInt(R.styleable.MusicLoadingView_rail_count, DEFAULT_RAIL_COUNT);
        mRailColor = array.getColor(R.styleable.MusicLoadingView_rail_color, Color.argb(255, 255, 255, 255));
        mRailLineWidth = array.getDimension(R.styleable.MusicLoadingView_rail_width, dip2px(context, 1f));
        array.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //计算可用高度
        float totalAvailableHeight = mViewHeight - getPaddingBottom() - getPaddingTop();
        //计算每条音轨平分宽度后的位置
        float averageBound = (mViewWidth * 1.0f) / mRailCount;
        //计算每条音轨的x坐标位置
        float x = averageBound - mRailLineWidth;
        float y = getPaddingBottom();
        //旋转画布，按控件中心旋转180度，即可让音轨反转
        canvas.rotate(180, mViewWidth / 2f, mViewHeight / 2f);
        //保存画布
        canvas.save();
        for (int i = 1; i <= mRailCount; i++) {
            //估值x坐标
            float fraction = nextRandomFloat(1.0f);
            float evaluateY = (mFloatEvaluator.evaluate(fraction, 0.3f, 0.9f)) * totalAvailableHeight;
            //第一个不需要偏移
            if (i == 1) {
                canvas.drawLine(x, y, x, evaluateY, mPaint);
            } else {
                //后续，每个音轨都固定偏移间距后，再画
                canvas.translate(x, 0);
                canvas.drawLine(x, y, x, evaluateY, mPaint);
            }
        }
        //恢复画布
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(handleMeasure(widthMeasureSpec), handleMeasure(heightMeasureSpec));
    }

    /** 处理MeasureSpec */
    private int handleMeasure(int measureSpec) {
        int result = DEFAULT_MIN_WIDTH;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            //处理wrap_content的情况
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    @Override
    public void run() {
        invalidate();
        postDelayed(this, 100);
    }

    public void start() {
        postDelayed(this, 700);
    }

    private void stop() {
        removeCallbacks(this);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 产生一个随机float
     * @param sl 随机数范围[0,sl)
     */
    public static float nextRandomFloat(float sl) { return mRandom.nextFloat() * sl; }
}