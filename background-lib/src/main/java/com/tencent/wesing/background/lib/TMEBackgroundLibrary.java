package com.tencent.wesing.background.lib;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.LayoutInflaterCompat;

import java.lang.reflect.Field;


public class TMEBackgroundLibrary {

    public static LayoutInflater inject(Context context) {
        LayoutInflater inflater;
        if (context instanceof Activity) {
            inflater = ((Activity) context).getLayoutInflater();
        } else {
            inflater = LayoutInflater.from(context);
        }
        if (inflater == null) {
            return null;
        }
        if (inflater.getFactory2() == null) {
            TMEBackgroundFactory factory = setDelegateFactory(context);
            inflater.setFactory2(factory);
        } else if (!(inflater.getFactory2() instanceof TMEBackgroundFactory)) {
            forceSetFactory2(inflater);
        }
        return inflater;
    }

    @NonNull
    private static TMEBackgroundFactory setDelegateFactory(Context context) {
        TMEBackgroundFactory factory = new TMEBackgroundFactory();
        if (context instanceof AppCompatActivity) {
            final AppCompatDelegate delegate = ((AppCompatActivity) context).getDelegate();
            factory.setInterceptFactory(new LayoutInflater.Factory() {
                @Override
                public View onCreateView(String name, Context context, AttributeSet attrs) {
                    return delegate.createView(null, name, context, attrs);
                }
            });
        }
        return factory;
    }


    private static void forceSetFactory2(LayoutInflater inflater) {
        Class<LayoutInflaterCompat> compatClass = LayoutInflaterCompat.class;
        Class<LayoutInflater> inflaterClass = LayoutInflater.class;
        try {
            Field sCheckedField = compatClass.getDeclaredField("sCheckedField");
            sCheckedField.setAccessible(true);
            sCheckedField.setBoolean(inflater, false);
            Field mFactory = inflaterClass.getDeclaredField("mFactory");
            mFactory.setAccessible(true);
            Field mFactory2 = inflaterClass.getDeclaredField("mFactory2");
            mFactory2.setAccessible(true);
            TMEBackgroundFactory factory = new TMEBackgroundFactory();
            if (inflater.getFactory2() != null) {
                factory.setInterceptFactory2(inflater.getFactory2());
            } else if (inflater.getFactory() != null) {
                factory.setInterceptFactory(inflater.getFactory());
            }
            mFactory2.set(inflater, factory);
            mFactory.set(inflater, factory);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
