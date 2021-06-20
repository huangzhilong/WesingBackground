package com.tencent.wesing.background.lib;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.tencent.wesing.background.lib.res.TMEBackgroundResources;

/**
 * create by zlonghuang on 2021/5/9
 **/

public class TMEBackgroundContext {

    private static Context mContext;

    private static TMEBackgroundResources tmeBackgroundResources;

    public static void setApplicationContext(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }


    public static void setTmeBackgroundResources(TMEBackgroundResources tmeBackgroundResources) {
        TMEBackgroundContext.tmeBackgroundResources = tmeBackgroundResources;
    }

    public static Drawable getDrawable(int drawableId) {
        //已经hook了，使用自己的
        if (tmeBackgroundResources != null) {
            return tmeBackgroundResources.getSuperDrawable(drawableId);
        }
        return getContext().getResources().getDrawable(drawableId);
    }
}
