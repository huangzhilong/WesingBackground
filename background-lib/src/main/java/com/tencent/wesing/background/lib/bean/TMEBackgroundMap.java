package com.tencent.wesing.background.lib.bean;
import android.os.Build;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;

import com.tencent.wesing.background.lib.R;

import java.util.HashMap;

/**
 * create by zlonghuang on 2021/4/29
 *
 *  编译期会通过Transform把shape 属性插入到class中的initGradientDrawable，会自动填充到mBackgroundAttributeMap里
 *
 **/

public class TMEBackgroundMap {

    private static final String TAG = "TMEBackgroundMap";

    private static volatile boolean isDisable = true;

    private final static HashMap<Integer, GradientDrawableInfo> mBackgroundAttributeMap = new HashMap<>();

    public static HashMap<Integer, GradientDrawableInfo> getBackgroundAttributeMap() {
        if (isDisable) {
            return null;
        }
        return mBackgroundAttributeMap;
    }

    /**
     * 解析数据
     */
    public static void startParseAttribute() {
        isDisable = true;
        Looper.myQueue().addIdleHandler(() -> {
            //主线程空闲时解析attribute
            new Thread(() -> {
                Log.i(TAG, "start init initGradientDrawable");
                long startTime = System.currentTimeMillis();
                initGradientDrawable();
                Log.i(TAG, "end init initGradientDrawable costTime: " + (System.currentTimeMillis() - startTime) + "  mapSize: " + mBackgroundAttributeMap.size());
                isDisable = false;
            }).start();
            return false;
        });
    }


    public static boolean isContainsDrawableId(int drawableId) {
        if (isDisable) {
            return false;
        }
        if (mBackgroundAttributeMap.containsKey(drawableId)) {
            return true;
        }
        return false;
    }

    /**
     * 禁止删除
     */
    private static void initGradientDrawable() {
    }
}
