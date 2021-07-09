package com.tencent.wesing.background.lib;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.tencent.wesing.background.lib.res.TMEBackgroundResources;

/**
 * create by zlonghuang on 2021/5/9
 **/

public class TMEBackgroundContext {

    private static final String TAG = "TMEBackgroundContext";

    private static Context mContext;

    private static Resources mSystemResource; //存储一个系统的Resource，hook后getResource得到的是TMEBackgroundResources

    private static volatile boolean isAvailable = true; //插件是否可用，可通过设置为false禁用，走系统

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

    /**
     * 可通过设置这个参数来禁用插件
     * @param available  false 为禁用   true为开启，默认开启
     */
    public static void setBackgroundAvailable(boolean available) {
        Log.i(TAG, "setBackgroundAvailable available: " + available);
        isAvailable = available;
    }

    public static boolean isAvailable() {
        return isAvailable;
    }
}
