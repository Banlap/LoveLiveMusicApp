package com.banlap.llmusic.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.banlap.llmusic.R;

import java.util.Date;
import java.util.Random;

public class CharacterHelper {

    private static ImageView mImageView;
    public static final String CHARACTER_NAME_KEKE = "CHARACTER_NAME_KEKE";
    public static final String CHARACTER_NAME_KANON = "CHARACTER_NAME_KANON";

    private static final int CHANGE_FACE_VALUE = 2;         //设置状态帧数
    private static int changeFace = CHANGE_FACE_VALUE;      //改变状态帧数

    private static final int CHANGE_HELLO_VALUE = 7;                 //设置打招呼谈话语句上限值
    private static final int CHANGE_GOOD_VALUE = 5;                  //设置称赞谈话语句上限值

    public static void initCharacter(ImageView imageView) {
        mImageView = imageView;
    }

    /** banlap: 正常状态*/
    public static void showNormalStatusCharacter(String characterName) {
        if(CHARACTER_NAME_KEKE.equals(characterName)) {
            if(changeFace !=0) {
                mImageView.setBackgroundResource(R.mipmap.ic_character_keke);
                changeFace--;
            } else {
                mImageView.setBackgroundResource(R.mipmap.ic_character_keke_c);
                changeFace = CHANGE_FACE_VALUE;
            }
        } else if (CHARACTER_NAME_KANON.equals(characterName)) {
            if(changeFace !=0) {
                mImageView.setBackgroundResource(R.mipmap.ic_character_kanon);
                changeFace--;
            } else {
                mImageView.setBackgroundResource(R.mipmap.ic_character_kanon_c);
                changeFace = CHANGE_FACE_VALUE;
            }
        }
    }

    /** banlap: 角色动态状态 */
    public static void showMoveStatusCharacter(String characterName) {
        if(CHARACTER_NAME_KEKE.equals(characterName)) {
            mImageView.setBackgroundResource(R.mipmap.ic_character_keke2);
        } else if (CHARACTER_NAME_KANON.equals(characterName)) {
            mImageView.setBackgroundResource(R.mipmap.ic_character_kanon2);
        }
    }

    /** banlap: 角色听歌状态 */
    public static void showListenStatusCharacter(String characterName, boolean isLeft) {
        if(CHARACTER_NAME_KEKE.equals(characterName)) {
            mImageView.setBackgroundResource(isLeft ?
                    R.mipmap.ic_character_keke_listen_left : R.mipmap.ic_character_keke_listen_right);
        } else if (CHARACTER_NAME_KANON.equals(characterName)) {
            mImageView.setBackgroundResource(isLeft ?
                    R.mipmap.ic_character_kanon_listen_left : R.mipmap.ic_character_kanon_listen_right);
        }
    }

    /** banlap: 改变角色移动位置 */
    public static AnimatorSet moveCharacterView(ConstraintLayout constraintLayout, float x, float y) {
        // 属性动画移动
        ObjectAnimator xx = ObjectAnimator.ofFloat(constraintLayout, "x", constraintLayout.getX(), x);
        ObjectAnimator yy = ObjectAnimator.ofFloat(constraintLayout, "y", constraintLayout.getY(), y);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(xx, yy);
        animatorSet.setDuration(0);
        animatorSet.start();

        return animatorSet;
    }

    public static String sayHelloContent(String characterName) {
        String content="";
        int rand = new Random().nextInt(CHANGE_HELLO_VALUE);
        if(CharacterHelper.CHARACTER_NAME_KANON.equals(characterName)) {
            if(0 == rand) {
                content = getSayHelloTipFromTime();
            } else if (1 == rand) {
                content = "你好，谢谢，小笼包，再见！";
            } else if (2 == rand) {
                content = "咦，我是前辈？";
            } else if (3 == rand) {
                content = "汉堡肉也不错噢，Foo～";
            } else if (4 == rand) {
                content = "虽然很紧张,但是真的很喜欢唱歌";
            } else if (5 == rand) {
                content = "manmaru，我出门啦";
            } else {
                content = "Song for Me！Song for You！";
            }
        } else  {
            if(0 == rand) {
                content = getSayHelloTipFromTime();
            } else if (1 == rand) {
                content = "太好听了吧!";
            } else if (2 == rand) {
                content = "可可想吃可丽饼~";
            } else if (3 == rand) {
                content = "一起做学院偶像吧";
            } else if (4 == rand) {
                content = "欢迎欢迎desuwa!";
            } else if (5 == rand) {
                content = "哟哟切克闹，唐可可我最闪耀！";
            } else {
                content = "你快点掐一下可可的脸呀";
            }
        }
        return content;
    }

    public static String sayGoodContent(String characterName) {
        String content="";
        int rand = new Random().nextInt(CHANGE_GOOD_VALUE);
        if(CharacterHelper.CHARACTER_NAME_KANON.equals(characterName)) {
            if(0 == rand) {
                content = "呀，谢谢你";
            } else if (1 == rand) {
                content = "目标！LoveLive夺冠！";
            } else if (2 == rand) {
                content = "呜哇，我好开心呀";
            } else if (3 == rand) {
                content = "一起加油吧！";
            } else {
                content = "谢谢支持！！";
            }
        } else {
            if(0 == rand) {
                content = "谢谢！我会继续加油努力的噢~";
            } else if (1 == rand) {
                content = "呜哇啊啊啊，谢谢你";
            } else if (2 == rand) {
                content = "真爱，这是真爱呀";
            } else if (3 == rand) {
                content = "嘻嘻！我唐可可是最棒的呀！";
            } else {
                content = "斯巴拉西~";
            }
        }
        return content;
    }

    public static String getSayHelloTipFromTime() {
        Date d = new Date();
        if (d.getHours()<6){
            return "凌晨好!";
        } else if (d.getHours() < 11) {
            return "早上好!";
        } else if (d.getHours() < 13) {
            return "中午好!";
        } else if (d.getHours() < 18) {
            return "下午好!";
        } else {
            return "晚上好!";
        }
    }

}
