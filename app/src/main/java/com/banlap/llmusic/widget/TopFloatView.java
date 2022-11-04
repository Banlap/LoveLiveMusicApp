package com.banlap.llmusic.widget;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.banlap.llmusic.R;
import com.banlap.llmusic.utils.PxUtil;

import java.util.Objects;


public class TopFloatView {
    private boolean hasExpand = false;
    public static TopFloatView getInstance() { return new TopFloatView(); }

    public void install(Activity activity) {
        install(activity, false);
    }

    public void install(final Activity activity, boolean isExpand){
        try {
            ViewGroup  viewGroup = (ViewGroup) activity.getWindow().getDecorView();    //获取WindowManage
            final View viewTopFloat = LayoutInflater.from(activity).inflate(R.layout.view_top_float, null);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.END | Gravity.BOTTOM;
            layoutParams.bottomMargin = 200;
            viewGroup.addView(viewTopFloat,layoutParams);

            viewTopFloat.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   viewTopFloat.setVisibility(View.GONE);
                }
            });
            viewTopFloat.findViewById(R.id.iv_user).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!hasExpand) {
                        ObjectAnimator objectAnimator = objectAnimatorLeftOrRight(PxUtil.getInstance().dp2px(75, activity), 0, 800,true, viewTopFloat);
                        objectAnimator.start();
                        hasExpand = true;
                    } else {
                        Toast.makeText(activity, "onClick", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if(!isExpand) {
                ObjectAnimator objectAnimator = objectAnimatorLeftOrRight(PxUtil.getInstance().dp2px(75, activity), 0, 0,false, viewTopFloat);
                objectAnimator.start();
                hasExpand = false;
            } else {
                hasExpand = true;
            }

        } catch (Exception e) {
            Log.e("error:", "is not start TopFloatView");
        }

    }

    public static ObjectAnimator objectAnimatorLeftOrRight(int displayAxis, int displayAxis2, int duration, boolean isRun, View view) {
        ObjectAnimator animator = isRun ?
                ObjectAnimator.ofFloat(view, "translationX", displayAxis, 0, displayAxis2)
                : ObjectAnimator.ofFloat(view, "translationX", displayAxis2, 0, displayAxis);
        animator.setDuration(duration);
        return animator;
    }


}
