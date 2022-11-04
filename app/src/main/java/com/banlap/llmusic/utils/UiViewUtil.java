package com.banlap.llmusic.utils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.lang.reflect.Field;
import java.util.List;

public class UiViewUtil {
    public static UiViewUtil getInstance(){ return new UiViewUtil();}

    public <T> void changeViewTheme(List<T> list, int value){
        for(T t : list) {
            if(t.getClass() == TextView.class) {
                ((TextView) t).setTextColor(value);
            } else if (t.getClass() == LinearLayout.class)  {
                ((LinearLayout) t).setBackgroundResource(value);
            } else if (t.getClass() == ImageView.class) {
                ((ImageView) t).setBackgroundResource(value);
            } else if (t.getClass() == ConstraintLayout.class) {
                ((ConstraintLayout) t).setBackgroundResource(value);
            } else if (t.getClass() == RelativeLayout.class) {
                ((RelativeLayout) t).setBackgroundResource(value);
            } else if (t.getClass() == Button.class) {
                ((Button) t).setBackgroundResource(value);
            } else if (t.getClass() == EditText.class) {
                ((EditText) t).setTextColor(value);
                ((EditText) t).setHintTextColor(value);
            }
        }
    }

    public <T> void changeSingleViewTheme(T t, int value){
        if(t.getClass() == TextView.class) {
            ((TextView) t).setTextColor(value);
        } else if (t.getClass() == LinearLayout.class)  {
            ((LinearLayout) t).setBackgroundResource(value);
        } else if (t.getClass() == ImageView.class) {
            ((ImageView) t).setBackgroundResource(value);
        } else if (t.getClass() == ConstraintLayout.class) {
            ((ConstraintLayout) t).setBackgroundResource(value);
        } else if (t.getClass() == RelativeLayout.class) {
            ((RelativeLayout) t).setBackgroundResource(value);
        } else if (t.getClass() == Button.class) {
            ((Button) t).setBackgroundResource(value);
        } else if (t.getClass() == EditText.class) {
            ((EditText) t).setTextColor(value);
            ((EditText) t).setHintTextColor(value);
        }
    }
}
