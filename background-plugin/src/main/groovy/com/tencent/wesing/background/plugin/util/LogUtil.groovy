package com.tencent.wesing.background.plugin.util

/**
 * create by zlonghuang on 2021/4/19
 **/

class LogUtil {

    final static LOG_TA = "WesingBackgroundPlugin_"

    static void logI(String tag, String text) {
        text = LOG_TA + tag + "  " + text
        println(text)
    }
}
