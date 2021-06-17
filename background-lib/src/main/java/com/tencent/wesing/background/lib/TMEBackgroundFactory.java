package com.tencent.wesing.background.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.tencent.wesing.background.lib.drawable.TMEBackgroundDrawableFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

class TMEBackgroundFactory implements LayoutInflater.Factory2 {

    private LayoutInflater.Factory mViewCreateFactory;
    private LayoutInflater.Factory2 mViewCreateFactory2;

    private static final Class<?>[] sConstructorSignature = new Class[]{Context.class, AttributeSet.class};
    private static final Object[] mConstructorArgs = new Object[2];
    private static final Map<String, Constructor<? extends View>> sConstructorMap = new HashMap<>();

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
        return setViewBackground(name, context, attrs, view);
    }

    @Nullable
    public static View setViewBackground(String name, Context context, AttributeSet attrs, View view) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TMEBackground);
        if (ta.getIndexCount() <= 0) {
            ta.recycle();
            return view;
        }
        if (view == null) {
            view = createViewFromTag(context, name, attrs);
        }
        if (view == null) {
            return null;
        }
        Drawable drawable = TMEBackgroundDrawableFactory.getInstance().getNeedGradientDrawable(ta);
        if (drawable != null) {
            view.setBackground(drawable);
        }
        ta.recycle();
        return view;
    }

    private static View createViewFromTag(Context context, String name, AttributeSet attrs) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        if (name.equals("view")) {
            name = attrs.getAttributeValue(null, "class");
        }
        try {
            mConstructorArgs[0] = context;
            mConstructorArgs[1] = attrs;

            if (-1 == name.indexOf('.')) {
                View view = null;
                if ("View".equals(name)) {
                    view = createView(context, name, "android.view.");
                }
                if (view == null) {
                    view = createView(context, name, "android.widget.");
                }
                if (view == null) {
                    view = createView(context, name, "android.webkit.");
                }
                return view;
            } else {
                return createView(context, name, null);
            }
        } catch (Exception e) {
            Log.w("TMEBackgroundFactory", "cannot create 【" + name + "】 : ");
            return null;
        } finally {
            mConstructorArgs[0] = null;
            mConstructorArgs[1] = null;
        }
    }

    private static View createView(Context context, String name, String prefix) throws InflateException {
        Constructor<? extends View> constructor = sConstructorMap.get(name);
        try {
            if (constructor == null) {
                Class<? extends View> clazz = context.getClassLoader().loadClass(
                        prefix != null ? (prefix + name) : name).asSubclass(View.class);

                constructor = clazz.getConstructor(sConstructorSignature);
                sConstructorMap.put(name, constructor);
            }
            constructor.setAccessible(true);
            return constructor.newInstance(mConstructorArgs);
        } catch (Exception e) {
            Log.w("TMEBackgroundFactory", "cannot create 【" + name + "】 : ");
            return null;
        }
    }

}
