package com.tencent.wesing.background.lib.res;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.ContextThemeWrapper;
import androidx.appcompat.app.AppCompatActivity;
import java.lang.reflect.Field;

/**
 * create by zlonghuang on 2021/6/19
 **/

public class TMEBackgroundHookResourcesUtil {

    private static final String TAG = "TMEBackgroundHookResourcesUtil";


    @SuppressLint("LongLogTag")
    public static void hookSystemResources(Context context) {
        Log.i(TAG, "start hookSystemResources  isActivity: " + (context instanceof Activity));
        long startTime = System.currentTimeMillis();
        Resources resources = context.getResources();
        TMEBackgroundResources tmeBackgroundResources = new TMEBackgroundResources(resources);
        try {
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
                context = ((Activity) context).getBaseContext();
            }

            //处理activity重写attachBaseContext 再次设置为 ContextThemeWrapper
            if (context instanceof ContextThemeWrapper) {
                Class ContextThemeWrapperField = ContextThemeWrapper.class;
                Field field1 = ContextThemeWrapperField.getDeclaredField("mResources");
                field1.setAccessible(true);
                field1.set(context, tmeBackgroundResources);
                context = ((ContextThemeWrapper) context).getBaseContext();
            } else if (context instanceof androidx.appcompat.view.ContextThemeWrapper) {
                Class ContextThemeWrapperField = androidx.appcompat.view.ContextThemeWrapper.class;
                Field field1 = ContextThemeWrapperField.getDeclaredField("mResources");
                field1.setAccessible(true);
                field1.set(context, tmeBackgroundResources);
                context = ((androidx.appcompat.view.ContextThemeWrapper) context).getBaseContext();
            }

            //ContextWrapper的mBase（ContextImpl）替换成自己的resource
            Class classA = context.getClass();
            if (classA.getName().contains("ContextImpl")) {
                Field field = classA.getDeclaredField("mResources");
                field.setAccessible(true);
                field.set(context, tmeBackgroundResources);
            }
            Log.i(TAG, "hookSystemResources success");
        } catch (Exception e) {
            Log.e(TAG, "hookSystemResources happen ex", e);
        }
        Log.i(TAG, "hookSystemResources costTime: " + (System.currentTimeMillis() - startTime));

    }
}
