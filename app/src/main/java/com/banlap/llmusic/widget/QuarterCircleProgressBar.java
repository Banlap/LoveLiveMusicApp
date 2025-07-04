package com.banlap.llmusic.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;

import com.banlap.llmusic.R;

/**
 * 播放控制器 新版2
 * */
public class QuarterCircleProgressBar extends ViewGroup {
    private Paint backgroundPaint;
    private Paint progressPaint;
    private float progress = 0.01f; // 0到1之间的进度值
    private float maxProgress = 100; //最大值
    private int strokeWidth = 100; // 进度条宽度
    private RectF arcRect; // 用于定义圆弧的矩形区域
    private Path backgroundPath;
    private Path progressPath;
    private final int shadowPadding = 20; // 新增：为阴影留出的额外空间
    private boolean isShowProgress = false; //是否展示
    private Drawable musicImgIcon, musicPlayIcon, settingIcon;  //歌曲图片 播放按钮 设置按钮

    private int iconLeft, iconTop, iconRight, iconBottom;

    private int lastX, lastY;
    private int startX, startY; // 记录按下时的初始位置
    private long mLastTime;
    private boolean isDragging = false; // 明确标记是否在拖动
    private int parentWidth, parentHeight;

    //小圆形点击播放或暂停
    private Paint smallCirclePaint; // 点击播放按钮画笔
    private final float smallCircleRadius = 50f; // 点击播放按钮半径
    private final int smallCircleColor = Color.WHITE; // 点击播放按钮背景颜色（可自定义）
    private boolean isPlayerButtonTouched = false; //是否触摸点击播放按钮
    private OnFloatingPlayerButtonClickListener floatingPlayerButtonClickListener; //点击播放事件

    // 左上角小圆形相关变量
    private Paint settingCirclePaint;
    private final float settingCircleRadius = 50f; // 可根据需要调整大小
    private boolean isSettingButtonTouched = false; //是否触摸点击设置按钮
    private OnFloatingSettingButtonClickListener floatingSettingButtonClickListener; //点击设置按钮事件


    //加载圈
    private ProgressBar loadingProgress;

    public QuarterCircleProgressBar(Context context) {
        super(context);
        init();
    }

    public QuarterCircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }



    private void init() {
        // 设置视图的内边距，为阴影留出空间
        setPadding(shadowPadding, shadowPadding, shadowPadding, shadowPadding);

        // 背景画笔
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.WHITE);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        backgroundPaint.setShadowLayer(
                5f, // 阴影模糊半径
                2f,  // X轴偏移
                2f,  // Y轴偏移
                getResources().getColor(R.color.gray_7b) // 半透明黑色阴影
        );
        // 需要开启硬件加速层才能显示阴影
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        // 进度画笔
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(getResources().getColor(R.color.light_f9));
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        backgroundPath = new Path();
        progressPath = new Path();

        //设置默认歌曲图片及按钮图标
        musicImgIcon = ContextCompat.getDrawable(getContext(), R.mipmap.ic_music_default_new);
        musicPlayIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_play_2_black);
        settingIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_menu_black);

        // 初始化小圆形画笔
        smallCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        smallCirclePaint.setColor(smallCircleColor);
        smallCirclePaint.setStyle(Paint.Style.FILL);
        smallCirclePaint.setShadowLayer(
                3f, 1f, 1f,
                getResources().getColor(R.color.gray_7b)
        );


        // 初始化左上角小圆形画笔
        settingCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        settingCirclePaint.setColor(Color.WHITE); // 设置颜色
        settingCirclePaint.setStyle(Paint.Style.FILL);
        settingCirclePaint.setShadowLayer(
                3f, 1f, 1f,
                getResources().getColor(R.color.gray_7b)
        );

        //初始化加载圈
        //loadingProgress = new ProgressBar(getContext(), null, android.R.attr.progressBarStyle);
        loadingProgress = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
        loadingProgress.setIndeterminate(true);
        loadingProgress.setIndeterminateTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_f9)));
        addView(loadingProgress);
        loadingProgress.setVisibility(View.GONE);

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // 先绘制自定义内容
        drawCustomContent(canvas);
        // 再绘制子View（ProgressBar）
        super.dispatchDraw(canvas);
    }

    /** 绘制内容 */
    private void drawCustomContent(Canvas canvas) {
        if(isShowProgress) {
            // 绘制背景路径
            canvas.drawPath(backgroundPath, backgroundPaint);

            // 计算当前进度对应的角度
            float sweepAngle = -90 * progress;

            // 绘制进度路径
            progressPath.reset();
            progressPath.arcTo(arcRect, 0, sweepAngle);
            canvas.drawPath(progressPath, progressPaint);

            drawSmallPlayerCircle(canvas);
            drawSettingCircle(canvas);
        }

        drawMusicImgCircle(canvas);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 确保arcRect已初始化
        if (arcRect == null) {
            int padding = strokeWidth / 2 + shadowPadding;
            arcRect = new RectF(
                    padding,
                    padding,
                    getWidth() - padding,
                    getHeight() - padding
            );
        }

        // 布局ProgressBar
        if (loadingProgress != null && loadingProgress.getVisibility() == VISIBLE) {
            int size = (int) (smallCircleRadius * 1.5f);
            loadingProgress.layout(
                    (int)(arcRect.right - strokeWidth/2 - size/2),
                    (int)(arcRect.bottom - strokeWidth - size/2),
                    (int)(arcRect.right - strokeWidth/2 + size/2),
                    (int)(arcRect.bottom - strokeWidth + size/2)
            );
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 计算视图的最小所需大小
        int minWidth = (int)(strokeWidth * 2.5f) + shadowPadding * 2;  // 适当增加宽度以容纳圆形
        int minHeight = (int)(strokeWidth * 2.5f) + shadowPadding * 2; // 适当增加高度以容纳圆形

        int width = resolveSize(minWidth, widthMeasureSpec);
        int height = resolveSize(minHeight, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // 调整arcRect的位置，保持原有圆弧和圆形位置
        int padding = strokeWidth / 2 + shadowPadding;
        arcRect = new RectF(
                padding,
                padding,
                w - padding,
                h - padding
        );

        // 保持原有路径计算
        backgroundPath.reset();
        backgroundPath.arcTo(arcRect, 0, -90);

        // 获取父容器尺寸
        View parent = (View) getParent();
        if (parent != null) {
            parentWidth = parent.getWidth();
            parentHeight = parent.getHeight();
        }
    }

    /** 绘制歌曲圆形图片 */
    private void drawMusicImgCircle(Canvas canvas) {
        // 创建中心圆形的画笔
        Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(getResources().getColor(R.color.white)); // 设置圆形颜色
        circlePaint.setStyle(Paint.Style.FILL); // 实心圆

        circlePaint.setShadowLayer(
                5f, // 阴影模糊半径
                2f,  // X轴偏移
                2f,  // Y轴偏移
                getResources().getColor(R.color.gray_7b) // 半透明黑色阴影
        );

        // 计算中心圆的位置和半径
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(getWidth(), getHeight()) / 6f; // 半径为View尺寸的1/4

        // 绘制中心圆形
        canvas.drawCircle(centerX, centerY, radius, circlePaint);

        // 添加图片绘制逻辑
        if (musicImgIcon != null) {
            // 计算图片绘制区域（比圆形稍小）
            int iconSize = (int) (radius * 2f); // 图片大小
            iconLeft = (int) (centerX - iconSize/2);
            iconTop = (int) (centerY - iconSize/2);
            iconRight = iconLeft + iconSize;
            iconBottom = iconTop + iconSize;

            musicImgIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            musicImgIcon.draw(canvas);
        }
    }


    /** 在又下角绘制播放暂停按钮圆形 */
    private void drawSmallPlayerCircle(Canvas canvas) {
        if (!isShowProgress) return;

        // 1. 计算圆弧终点坐标
        float centerX = arcRect.right - strokeWidth / 2f; // 圆弧右侧中心
        float centerY = arcRect.bottom - strokeWidth; // 圆弧下方

        canvas.drawCircle(
                centerX,
                centerY,
                smallCircleRadius,
                smallCirclePaint
        );

        if(musicPlayIcon != null) {
            // 计算图片绘制区域（比圆形稍小）
            int iconSize = (int) (smallCircleRadius); // 图片大小
            iconLeft = (int) (centerX - iconSize/2);
            iconTop = (int) (centerY - iconSize/2);
            iconRight = iconLeft + iconSize;
            iconBottom = iconTop + iconSize;

            musicPlayIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            musicPlayIcon.draw(canvas);
        }
    }

    /** 在左上角绘制设置按钮圆形 */
    private void drawSettingCircle(Canvas canvas) {
        if (!isShowProgress) return;

        // 计算位置（圆弧起点）
        float centerX = arcRect.left + strokeWidth; // 圆弧左侧
        float centerY = arcRect.top + strokeWidth /2f ;      // 圆弧上方

        canvas.drawCircle(
                centerX,
                centerY,
                settingCircleRadius,
                settingCirclePaint
        );

        if(settingIcon != null) {
            // 计算图片绘制区域（比圆形稍小）
            int iconSize = (int) (settingCircleRadius); // 图片大小
            iconLeft = (int) (centerX - iconSize/2);
            iconTop = (int) (centerY - iconSize/2);
            iconRight = iconLeft + iconSize;
            iconBottom = iconTop + iconSize;

            settingIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            settingIcon.draw(canvas);
        }
    }


    /** 点击播放按钮监听 */
    public interface OnFloatingPlayerButtonClickListener {
        void onFloatingPlayerButtonClickListener();
    }

    public void setOnFloatingPlayerButtonClickListener(OnFloatingPlayerButtonClickListener listener) {
        this.floatingPlayerButtonClickListener = listener;
    }

    /** 点击设置按钮监听 */
    public interface OnFloatingSettingButtonClickListener {
        void onFloatingSettingButtonClickListener();
    }

    public void setOnFloatingSettingButtonClickListener(OnFloatingSettingButtonClickListener listener) {
        this.floatingSettingButtonClickListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        // 计算小圆形中心坐标（与drawSmallPlayerCircle中一致）
        float smallCircleX = arcRect.right - strokeWidth / 2f;
        float smallCircleY = arcRect.bottom - strokeWidth;

        // 计算左上角小圆形中心坐标
        float topLeftCircleX = arcRect.left + strokeWidth;
        float topLeftCircleY = arcRect.top + strokeWidth /2f;

        // 判断是否点击在小圆形内
        boolean isTouchingSmallCircle = isPointInCircle(
                touchX, touchY,
                smallCircleX, smallCircleY,
                smallCircleRadius
        );

        // 判断是否点击在左上角小圆形内
        boolean isTouchingSettingCircle = isPointInCircle(
                touchX, touchY,
                topLeftCircleX, topLeftCircleY,
                settingCircleRadius
        );

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //拦截当前是否触摸点击播放按钮
                if (isTouchingSmallCircle) {
                    isPlayerButtonTouched = true;
                    return true; // 拦截事件
                }

                //拦截当前是否触摸点击设置按钮
                if(isTouchingSettingCircle) {
                    isSettingButtonTouched = true;
                    return true; // 拦截事件
                }

                isDragging = false;
                mLastTime = System.currentTimeMillis();
                // 记录初始位置和当前触摸点
                startX = lastX = (int) event.getRawX();
                startY = lastY = (int) event.getRawY();

                // 检查是否点击在中心圆形区域
                float centerX = getWidth() / 2f;
                float centerY = getHeight() / 2f;
                float centerRadius = Math.min(getWidth(), getHeight()) / 6f;

                float distance = (float) Math.sqrt(Math.pow(touchX - centerX, 2) + Math.pow(touchY - centerY, 2));

                // 只有点击在圆形区域内才处理
                return distance <= centerRadius;

            case MotionEvent.ACTION_MOVE:
                if (isPlayerButtonTouched) {
                    return true; // 拦截事件
                }

                if(isSettingButtonTouched) {
                    return true; // 拦截事件
                }

                // 计算偏移量
                float dx = event.getRawX() - lastX;
                float dy = event.getRawY() - lastY;

                // 检查是否达到拖动阈值(如5像素)
                if (!isDragging && (Math.abs(event.getRawX() - startX) > 5 ||
                        Math.abs(event.getRawY() - startY) > 5)) {
                    isDragging = true;
                    // 拖动开始时取消按下状态

                    // 可以在这里通知监听器拖动开始了
//                    if (onDragListener != null) {
//                        onDragListener.onDragStart();
//                    }
                }

                if (isDragging) {
                    // 更新View位置
                    float newX = getX() + dx;
                    float newY = getY() + dy;

                    // 计算圆形半径
                    float circleRadius = Math.min(getWidth(), getHeight()) / 6f;

                    // 调整边界检查，让圆形可以贴近边缘
                    newX = Math.max(-circleRadius, Math.min(newX, parentWidth - (getWidth() - circleRadius)));
                    newY = Math.max(-circleRadius, Math.min(newY, parentHeight - (getHeight() - circleRadius)));

                    setX(newX);
                    setY(newY);

                    // 可以在这里通知监听器拖动位置变化
//                    if (onDragListener != null) {
//                        onDragListener.onDragging(newX, newY);
//                    }
                }

                // 重新记录坐标
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                //点击播放按钮事件拦截
                if (isPlayerButtonTouched) {
                    isPlayerButtonTouched = false;
                    if (isTouchingSmallCircle && floatingPlayerButtonClickListener != null) {
                        floatingPlayerButtonClickListener.onFloatingPlayerButtonClickListener();
                    }
                    return true;
                }

                //点击设置按钮事件拦截
                if (isSettingButtonTouched) {
                    isSettingButtonTouched = false;
                    if (isTouchingSettingCircle && floatingSettingButtonClickListener != null) {
                        floatingSettingButtonClickListener.onFloatingSettingButtonClickListener();
                    }
                    return true;
                }

                long mCurrentTime = System.currentTimeMillis();
                if (mCurrentTime - mLastTime < 500 &&
                        Math.abs(event.getRawX() - startX) < 10 &&
                        Math.abs(event.getRawY() - startY) < 10) {
                    // 满足点击条件
                    if (!isDragging) {
                        // 执行点击操作
                        performClick();
                    }
                    return false; // 让点击事件继续传递
                }

                // 拖动结束处理
//                if (isDragging && onDragListener != null) {
//                    onDragListener.onDragEnd(v.getX(), v.getY());
//                }

                isDragging = false;
                break;

            case MotionEvent.ACTION_CANCEL:
                isDragging = false;
                break;
        }

        return true; // 始终消费触摸事件
    }

    /** 判断点是否在圆内 */
    private boolean isPointInCircle(float x, float y, float circleX, float circleY, float radius) {
        float dx = x - circleX;
        float dy = y - circleY;
        return dx * dx + dy * dy <= radius * radius;
    }


    ////对外方法
    //

    /** 设置最大进度 */
    public void setMaxProgress(float progress) {
        this.maxProgress = progress;
    }

    /** 设置当前进度 */
    public void setProgress(float progress) {
        float newProgress = (progress*100/maxProgress)/100;
        this.progress = Math.min(1, Math.max(0, newProgress)); // 确保在0-1之间
        invalidate(); // 重绘视图
    }

    /** 是否展示进度条及播放按钮 */
    public void showProgress(boolean isShow) {
        isShowProgress = isShow;
        invalidate(); // 重绘视图
    }

    /** 设置歌曲图标 */
    public void setIcon(Bitmap bitmap) {
        Bitmap circleBitmap = getCircleBitmap(bitmap);
        this.musicImgIcon = new BitmapDrawable(getResources(), circleBitmap);
        invalidate();
    }

    /** 设置点击播放按钮图标 */
    public void setPlayerIcon(int iconId) {
        //0作为loading判断
        if(iconId == 0) {
            this.musicPlayIcon = null;
            loadingProgress.setVisibility(View.VISIBLE);
        } else {
            this.musicPlayIcon = ContextCompat.getDrawable(getContext(), iconId);
            loadingProgress.setVisibility(View.GONE);
        }

        invalidate();
    }


    public void setProgressColor(int color) {
        progressPaint.setColor(color);
        invalidate();
    }


    public void setStrokeWidth(int width) {
        strokeWidth = width;
        backgroundPaint.setStrokeWidth(width);
        progressPaint.setStrokeWidth(width);
        invalidate();
    }

    // 圆形裁剪算法
    private Bitmap getCircleBitmap(Bitmap bitmap) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // 绘制圆形蒙版
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        // 裁剪图片
        Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect dstRect = new Rect(0, 0, size, size);
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

        return output;
    }
}