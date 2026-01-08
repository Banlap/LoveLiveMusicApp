package com.banlap.llmusic.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

/**
 * 动画工具类
 * @author Banlap on 2021/12/1
 */
public class MyAnimationUtil {

    /** banlap：恢复动画位置 */
    public static ObjectAnimator objectAnimatorInit(Activity activity, ConstraintLayout constraintLayout) {
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        ObjectAnimator animator = ObjectAnimator.ofFloat(constraintLayout, "translationX", 0, 0);
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(@NonNull Animator animation, boolean isReverse) {
                super.onAnimationStart(animation, isReverse);
                Glide.with(activity.getApplicationContext()).pauseRequests();
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation, boolean isReverse) {
                super.onAnimationEnd(animation, isReverse);
                Glide.with(activity.getApplicationContext()).resumeRequests();
            }
        });
        return animator;
    }

    /**
     * banlap: 添加动画效果 左右显示
     *
     * @param isLeft true 设置向左; false 设置向右
     * @param isRun true 设置移动到中间; false 设置移动到两边(根据isLeft值判断)
     * */
    public static ObjectAnimator objectAnimatorLeftOrRight(Activity activity, boolean isLeft, boolean isRun, ViewGroup viewGroup) {
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int displayAxis = isLeft ? -display.getWidth() * 2 - 100 : display.getWidth() * 2 - 100;

        ObjectAnimator animator = isRun ?
                ObjectAnimator.ofFloat(viewGroup, "translationX", displayAxis, 0)
                : ObjectAnimator.ofFloat(viewGroup, "translationX", 0, displayAxis);
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(@NonNull Animator animation, boolean isReverse) {
                super.onAnimationStart(animation, isReverse);
                Glide.with(activity.getApplicationContext()).pauseRequests();
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation, boolean isReverse) {
                super.onAnimationEnd(animation, isReverse);
                Glide.with(activity.getApplicationContext()).resumeRequests();
            }
        });
        return animator;
    }

    public static ObjectAnimator objectAnimatorLeftOrRightNew(Activity activity, boolean isLeft, boolean isRun, int moveAxis, ViewGroup viewGroup) {
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int displayAxis = isLeft ? -moveAxis * 2 - 100 : moveAxis * 2 - 100;

        ObjectAnimator animator = isRun ?
                ObjectAnimator.ofFloat(viewGroup, "translationX", displayAxis, 0)
                : ObjectAnimator.ofFloat(viewGroup, "translationX", 0, displayAxis);
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(@NonNull Animator animation, boolean isReverse) {
                super.onAnimationStart(animation, isReverse);
                Glide.with(activity.getApplicationContext()).pauseRequests();
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation, boolean isReverse) {
                super.onAnimationEnd(animation, isReverse);
                Glide.with(activity.getApplicationContext()).resumeRequests();
            }
        });
        return animator;
    }

    /**
     * banlap: 添加动画效果 上下显示
     *
     * @param isDown true 向下移动; false 向上移动
     * */
    public static ObjectAnimator objectAnimatorUpOrDown(Activity activity, boolean isDown, int moveAxis, ViewGroup viewGroup) {
        ObjectAnimator animator = isDown ?
                ObjectAnimator.ofFloat(viewGroup, "translationY", 0, moveAxis)
                : ObjectAnimator.ofFloat(viewGroup, "translationY", moveAxis, 0);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(@NonNull Animator animation, boolean isReverse) {
                super.onAnimationStart(animation, isReverse);
                Glide.with(activity.getApplicationContext()).pauseRequests();
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation, boolean isReverse) {
                super.onAnimationEnd(animation, isReverse);
                Glide.with(activity.getApplicationContext()).resumeRequests();
            }
        });
        return animator;
    }

    /** banlap: 添加按钮 抛物线动画效果 */
    public static AnimatorSet animatorSetAddMusic(ImageView imageView) {
        imageView.setVisibility(View.VISIBLE);
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(imageView, "translationX", 0, 45);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(imageView, "translationY", 0, -20, 0, 100, 300);
        ObjectAnimator animatorR = ObjectAnimator.ofFloat(imageView, "rotation", 0, 360);
        animatorR.setDuration(1000);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorX, animatorY, animatorR);
        animatorSet.setDuration(800);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(@NonNull Animator animation, boolean isReverse) {
                super.onAnimationStart(animation, isReverse);
                Glide.with(imageView.getContext()).pauseRequests();
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation, boolean isReverse) {
                super.onAnimationEnd(animation, isReverse);
                Glide.with(imageView.getContext()).resumeRequests();
            }
        });
        return animatorSet;
    }

    /** banlap: 动画效果  隐藏与显示 */
    public static ObjectAnimator objectAnimatorShowOrHide(Activity activity, float startAxis, float endAxis, TextView textView) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(textView, "alpha", startAxis, endAxis);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.setDuration(120);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(@NonNull Animator animation, boolean isReverse) {
                super.onAnimationStart(animation, isReverse);
                Glide.with(activity.getApplicationContext()).pauseRequests();
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation, boolean isReverse) {
                super.onAnimationEnd(animation, isReverse);
                Glide.with(activity.getApplicationContext()).resumeRequests();
            }
        });
        return animator;
    }

    /** banlap: 添加动画效果 放大缩小 */
    public static AnimatorSet animatorSetEnlarge(View textView, float startAxis,float endAxis) {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(textView, "scaleX", startAxis, endAxis);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(textView, "scaleY", startAxis, endAxis);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorX, animatorY);
        animatorSet.setDuration(120);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(@NonNull Animator animation, boolean isReverse) {
                super.onAnimationStart(animation, isReverse);
                Glide.with(textView.getContext()).pauseRequests();
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation, boolean isReverse) {
                super.onAnimationEnd(animation, isReverse);
                Glide.with(textView.getContext()).resumeRequests();
            }
        });
        return animatorSet;
    }

    /** banlap: 动画效果  隐藏与显示 */
    public static AnimatorSet animatorSetMove(View view, boolean isRun) {

        view.setPivotX(0);
        ObjectAnimator animatorXStart = ObjectAnimator.ofFloat(view, "scaleX", 1, 2);
        ObjectAnimator animatorXEnd = ObjectAnimator.ofFloat(view, "scaleX", 2, 1);
        ObjectAnimator animatorMove = ObjectAnimator.ofFloat(view, "translationX", 0, PxUtil.getInstance().dp2px(90, view.getContext()));
        animatorXStart.setDuration(1000);
        animatorXEnd.setDuration(1000);

        if(!isRun) {
            view.setPivotX(view.getWidth());
            animatorMove = ObjectAnimator.ofFloat(view, "translationX", PxUtil.getInstance().dp2px(90, view.getContext()), 0);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorXStart, animatorMove, animatorXEnd);
        animatorSet.setDuration(120);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(@NonNull Animator animation, boolean isReverse) {
                super.onAnimationStart(animation, isReverse);
                Glide.with(view.getContext()).pauseRequests();
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation, boolean isReverse) {
                super.onAnimationEnd(animation, isReverse);
                Glide.with(view.getContext()).resumeRequests();
            }
        });
        return animatorSet;
    }
}
