package com.tencent.wesing.background.lib;

import android.app.Application;
import com.tencent.wesing.background.lib.bean.TMEBackgroundMap;
import com.tencent.wesing.background.lib.res.TMEBackgroundHookResourcesUtil;

/**
 * create by zlonghuang on 2021/8/9
 **/
public class TMEBackgroundLib {

    /**
     * @param application
     */
    public static void initBackgroundLib(Application application) {
        if (application == null) {
            return;
        }
        TMEBackgroundInflater.inject(application);
        TMEBackgroundContext.setApplicationContext(application);
        TMEBackgroundMap.startParseAttribute();
        //hook application
        TMEBackgroundHookResourcesUtil.hookSystemResources(application.getBaseContext());
        application.registerActivityLifecycleCallbacks(new TMEBackgroundActivityLifecycleRegister());
    }
}
