package com.banlap.llmusic.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.banlap.llmusic.R;

/**
 * 水平进度条
 * */
public class HorizontalProgressBar extends View {

    private float maxCount = 100; //进度条最大值
    private float currentCount; //进度条当前值
    private Paint mPaint ;
    private int mWidth,mHeight;
    private Context mContext;
    private final RectF rectProgressBg = new RectF(0,0,0,0);
    private final RectF rectProgressBg2 = new RectF(0,0,0,0);
    private final RectF rectBg = new RectF(0, 0, 0, 0);

    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public HorizontalProgressBar(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mPaint = new Paint();
        mContext=context;
        invalidate();
    }

    private int bgColor = R.color.alpha_30;
    private int bgColor2 = R.color.white;
    //设置背景色
    public void setNoAlpha(){
        bgColor = R.color.alpha;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAntiAlias(true);
        int round = mHeight/2; //半径
        //Log.i("LogByAB", "mWidth: " + mWidth +  " mHeight: " + mHeight  + " round: " + round);
        mPaint.setColor(getResources().getColor(bgColor)); //设置边框背景颜色
        mPaint.setShader(getLinearGradient());
        rectBg.right = mWidth;
        rectBg.bottom = mHeight;
        canvas.drawRoundRect(rectBg, round, round, mPaint);//绘制 最外面的大 圆角矩形，背景为白色

        float section = currentCount/maxCount; //进度条的比例
        rectProgressBg.right = section * mWidth;
        rectProgressBg.bottom = mHeight;
        //Log.i("LogByAB", "section: " + section);

        //Paint设置setColor(白色无透明)和setShader，只让setShader生效；不然前面setColor设置了透明度，透明度会生效，和setShader效果叠加
        mPaint.setColor(getResources().getColor(bgColor2));
        mPaint.setShader(getLinearGradient());
        canvas.drawRoundRect(rectProgressBg, round, round, mPaint); //最左边的圆角矩形
        Log.i("LogByAB", "maxCount: " + maxCount + " currentCount: " + currentCount);

        if (maxCount != currentCount){ //如果不是100%，绘制第三段矩形
            rectProgressBg2.left = mWidth * section - round;
            rectProgressBg2.right = mWidth * section;
            rectProgressBg2.bottom = mHeight;
            mPaint.setShader(getLinearGradient());
            canvas.drawRect(rectProgressBg2, mPaint);
        }
    }

    private LinearGradient linearGradient;
    private LinearGradient getLinearGradient(){
        if(linearGradient == null) {
            Log.i("LogByAB", "getWidth: " + mWidth);
            linearGradient = new LinearGradient(0, 0, mWidth, mHeight, new int[]{
                    mContext.getResources().getColor(linearGradientColor),
                    mContext.getResources().getColor(linearGradientColor)
            }, null, Shader.TileMode.CLAMP); //根据R文件中的id获取到color
        }
        return linearGradient;
    }


    private int linearGradientColor = R.color.light_f9;
    public void setLinearGradient(int color){
        //Log.i("LogByAB", "setLinearGradient getWidth: " + mWidth);
        linearGradientColor = color;
        if(0 != color) {
            linearGradient = new LinearGradient(0, 0, mWidth, mHeight, new int[]{
                    mContext.getResources().getColor(color),
                    mContext.getResources().getColor(color)
            }, null, Shader.TileMode.CLAMP); //根据R文件中的id获取到color
            invalidate();
        }
    }

    private int dipToPx(int dip) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /***
     * 设置最大的进度值
     * @param maxCount 最大的进度值
     */
    public void setMaxCount(float maxCount) {
        this.maxCount = maxCount;
    }

    /***
     * 设置当前的进度值
     * @param currentCount 当前进度值
     */
    public void setCurrentCount(float currentCount) {
        this.currentCount = Math.min(currentCount, maxCount);
        invalidate();
    }

    public float getMaxCount() {
        return maxCount;
    }

    public float getCurrentCount() {
        return currentCount;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.EXACTLY || widthSpecMode == MeasureSpec.AT_MOST) {
            mWidth = widthSpecSize;
        } else {
            mWidth = 0;
        }
        if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            mHeight = dipToPx(4);
        } else {
            mHeight = heightSpecSize;
        }
        setMeasuredDimension(mWidth, mHeight);
        setLinearGradient(linearGradientColor);
    }
}