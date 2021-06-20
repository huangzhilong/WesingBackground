package com.tencent.wesing.background.lib.res;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.tencent.wesing.background.lib.TMEBackgroundContext;

import java.lang.reflect.Field;

/**
 * create by zlonghuang on 2021/6/19
 **/

public class TMEBackgroundHookResourcesUtil {

    private static final String TAG = "TMEBackgroundHookResourcesUtil";

    private static volatile boolean isHook = false;


    @SuppressLint("LongLogTag")
    public static void hookSystemResources(Context context) {
        if (isHook) {
            return;
        }

        try {
            Resources resources = context.getResources();
            TMEBackgroundResources tmeBackgroundResources = new TMEBackgroundResources(resources);

            //替换成自己的resource
            Class classA = context.getClass();
            Field field = classA.getDeclaredField("mResources");
            field.setAccessible(true);
            field.set(context, tmeBackgroundResources);

            Field mLoadApkField = classA.getDeclaredField("mPackageInfo");
            mLoadApkField.setAccessible(true);
            Object mLoadApk = mLoadApkField.get(context);
            Field resField = mLoadApk.getClass().getDeclaredField("mResources");
            resField.setAccessible(true);
            resField.set(mLoadApk, tmeBackgroundResources);

            TMEBackgroundContext.setTmeBackgroundResources(tmeBackgroundResources);
            isHook = true;
            Log.i("TMEBackgroundHookResourcesUtil", "hookSystemResources success");
        } catch (Exception e) {
            Log.i("TMEBackgroundHookResourcesUtil", "hookSystemResources happen ex", e);
        }
    }
}
