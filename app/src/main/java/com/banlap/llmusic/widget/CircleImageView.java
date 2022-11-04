package com.banlap.llmusic.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * @author Banlap on 2021/2/23
 */
public class CircleImageView extends AppCompatImageView {
    float width, height;

    public CircleImageView(@NonNull Context context) {
        this(context, null);
    }

    public CircleImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public CircleImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
        //init();
    }

    private void init() {
        setOnTouchListener(onTouchListener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width >= 150 && height > 150) {
            Path path = new Path();
            //四个圆角
            path.moveTo(15, 0);
            path.lineTo(width - 15, 0);
            path.quadTo(width, 0, width, 15);
            path.lineTo(width, height - 15);
            path.quadTo(width, height, width - 15, height);
            path.lineTo(15, height);
            path.quadTo(0, height, 0, height - 15);
            path.lineTo(0, 15);
            path.quadTo(0, 0, 15, 0);

            canvas.clipPath(path);
        }
        super.onDraw(canvas);
    }


    private OnTouchListener onTouchListener=new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    setColorFilter(null);
                    break;
                case MotionEvent.ACTION_DOWN:
                    changeLight();
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_CANCEL:
                    setColorFilter(null);
                    break;
                default:
                    break;
            }
            return true;
        }
    };
    private void changeLight() {
        int brightness=-80;
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(new float[] { 1, 0, 0, 0, brightness, 0, 1, 0, 0,
                brightness, 0, 0, 1, 0, brightness, 0, 0, 0, 1, 0 });
        setColorFilter(new ColorMatrixColorFilter(matrix));

    }
}
