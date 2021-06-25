package com.tencent.wesing.background.lib;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.tencent.wesing.background.lib.res.TMEBackgroundResources;

/**
 * create by zlonghuang on 2021/5/9
 **/

public class TMEBackgroundContext {

    private static Context mContext;

    private static Resources mSystemResource; //存储一个系统的Resource，hook后getResource得到的是TMEBackgroundResources

    public static void setApplicationContext(Context context) {
        mContext = context;
        mSystemResource = context.getResources();
    }

    public static Context getContext() {
        return mContext;
    }


    public static Drawable getDrawable(int drawableId) {
        //return getContext().getResources().getDrawable(drawableId); hook后会死循环，不能这样写
        return mSystemResource.getDrawable(drawableId);
    }
}
