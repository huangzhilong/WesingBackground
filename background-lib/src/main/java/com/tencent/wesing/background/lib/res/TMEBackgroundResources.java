package com.tencent.wesing.background.lib.res;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.tencent.wesing.background.lib.drawable.TMEBackgroundDrawableFactory;

/**
 * create by zlonghuang on 2021/6/19
 **/

public class TMEBackgroundResources extends Resources {

    private final static String TAG = "TMEBackgroundResources";

    private Resources mSystemResources;

    TMEBackgroundResources(Resources resources) {
        super(resources.getAssets(), resources.getDisplayMetrics(), resources.getConfiguration());
        mSystemResources = resources;
    }

    @Override
    public Drawable getDrawable(int id) throws NotFoundException {
        Log.i(TAG, "getDrawable id: " + id);
        return TMEBackgroundDrawableFactory.createDrawableById(id);
    }

    /**
     *  TMEBackgroundDrawableFactory.createDrawableById获取失败时要调用 系统本来的getDrawable，
     *  因为系统的getDrawable被重写为了TMEBackgroundDrawableFactory.createDrawableById(id);，避免死循环，调用此方法
     */
    public Drawable getSuperDrawable(int id) throws NotFoundException {
        return mSystemResources.getDrawable(id);
    }
}
