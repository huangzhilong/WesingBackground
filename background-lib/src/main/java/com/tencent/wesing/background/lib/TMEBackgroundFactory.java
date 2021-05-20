package com.tencent.wesing.background.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.tencent.wesing.background.lib.drawable.GradientDrawableFactory;

class TMEBackgroundFactory implements LayoutInflater.Factory2 {

    private LayoutInflater.Factory mViewCreateFactory;
    private LayoutInflater.Factory2 mViewCreateFactory2;

    public void setInterceptFactory(LayoutInflater.Factory factory) {
        mViewCreateFactory = factory;
    }

    public void setInterceptFactory2(LayoutInflater.Factory2 factory) {
        mViewCreateFactory2 = factory;
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return onCreateView(name, context, attrs);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {

        View view = null;

        //防止与其他调用factory库冲突，例如字体、皮肤替换库，用已经设置的factory来创建view
        if (mViewCreateFactory2 != null) {
            view = mViewCreateFactory2.onCreateView(name, context, attrs);
            if (view == null) {
                view = mViewCreateFactory2.onCreateView(null, name, context, attrs);
            }
        } else if (mViewCreateFactory != null) {
            view = mViewCreateFactory.onCreateView(name, context, attrs);
        }
        return setViewBackground(context, attrs, view);
    }

    @Nullable
    public static View setViewBackground(Context context, AttributeSet attrs, View view) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TMEBackground);
        if (ta.getIndexCount() <= 0) {
            ta.recycle();
            return view;
        }
        GradientDrawable gradientDrawable = GradientDrawableFactory.getInstance().getNeedGradientDrawable(ta);
        if (gradientDrawable != null) {
            view.setBackground(gradientDrawable);
        }
        ta.recycle();
        return view;
    }
}
