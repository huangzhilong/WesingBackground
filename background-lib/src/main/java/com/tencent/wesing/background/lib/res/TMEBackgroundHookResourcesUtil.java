package com.tencent.wesing.background.lib.res;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.ContextThemeWrapper;

import androidx.appcompat.app.AppCompatActivity;

import com.tencent.wesing.background.lib.TMEBackgroundContext;
import java.lang.reflect.Field;

/**
 * create by zlonghuang on 2021/6/19
 **/

public class TMEBackgroundHookResourcesUtil {

    private static final String TAG = "TMEBackgroundHookResourcesUtil";


    @SuppressLint("LongLogTag")
    public static void hookSystemResources(Context context) {
        long startTime = System.currentTimeMillis();
        Log.i(TAG, "start hookSystemResources!!");
        try {
            Resources resources = context.getResources();
            TMEBackgroundResources tmeBackgroundResources = new TMEBackgroundResources(resources);

            // 替换activity 的 mResources
            if (context instanceof Activity) {
                // AppCompatActivity 内部有个mResources也hook住
                if (context instanceof AppCompatActivity) {
                    Class classA = AppCompatActivity.class;
                    Field field = classA.getDeclaredField("mResources");
                    field.setAccessible(true);
                    field.set(context, tmeBackgroundResources);
                }

                Class ContextThemeWrapperField = ContextThemeWrapper.class;
                Field field1 = ContextThemeWrapperField.getDeclaredField("mResources");
                field1.setAccessible(true);
                field1.set(context, tmeBackgroundResources);

                Log.i(TAG, "hookSystemResources Activity mResources success");
                context = ((Activity) context).getBaseContext();
            }

            //ContextWrapper的mBase（ContextImpl）替换成自己的resource
            Class classA = context.getClass();
            if (classA.getName().contains("ContextImpl")) {
                Field field = classA.getDeclaredField("mResources");
                field.setAccessible(true);
                field.set(context, tmeBackgroundResources);
            }

            TMEBackgroundContext.setTmeBackgroundResources(tmeBackgroundResources);
            Log.i(TAG, "hookSystemResources success");
        } catch (Exception e) {
            Log.e(TAG, "hookSystemResources happen ex", e);
        }
        Log.i(TAG, "hookSystemResources costTime: " + (System.currentTimeMillis() - startTime));

    }
}
