package com.banlap.llmusic.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * @author Banlap on 2021/12/1
 */
public class MyAnimationUtil {

    /** banlap：恢复动画位置 */
    public static ObjectAnimator objectAnimatorInit(Activity activity, ConstraintLayout constraintLayout) {
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int displayAxis = -display.getWidth() * 2 - 100;
        float curTranslationX = constraintLayout.getTranslationX();
        ObjectAnimator animator = ObjectAnimator.ofFloat(constraintLayout, "translationX", 0, 0);
        animator.setDuration(500);
        return animator;

    }

    /**
     * banlap: 添加动画效果 左右显示
     *
     * @param isLeft true 设置向左; false 设置向右
     * @param isRun true 设置移动到中间; false 设置移动到两边(根据isLeft值判断)
     * */
    public static ObjectAnimator objectAnimatorLeftOrRight(Activity activity, boolean isLeft, boolean isRun, ConstraintLayout constraintLayout) {
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int displayAxis = isLeft ? -display.getWidth() * 2 - 100 : display.getWidth() * 2 - 100;

        ObjectAnimator animator = isRun ?
                ObjectAnimator.ofFloat(constraintLayout, "translationX", displayAxis, 0)
                : ObjectAnimator.ofFloat(constraintLayout, "translationX", 0, displayAxis);
        animator.setDuration(500);
        return animator;
    }

    /** banlap: 添加动画效果 上下显示 */
    public static ObjectAnimator objectAnimatorUpOrDown(Activity activity, boolean isUp, int moveAxis, RelativeLayout relativeLayout) {
        ObjectAnimator animator = isUp ?
                ObjectAnimator.ofFloat(relativeLayout, "translationY", 0, moveAxis)
                : ObjectAnimator.ofFloat(relativeLayout, "translationY", moveAxis, 0);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.setDuration(500);
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
        return animatorSet;
    }

    public static ObjectAnimator objectAnimatorShowOrHide(Activity activity, float startAxis, float endAxis, TextView textView) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(textView, "alpha", startAxis, endAxis);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.setDuration(200);
        return animator;
    }



}
