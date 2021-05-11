package com.tencent.wesing.background.lib;

import android.content.Context;

/**
 * create by zlonghuang on 2021/5/9
 **/

public class TMEBackgroundContext {

    private static Context mContext;

    public static void setApplicationContext(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }
}
