package com.tencent.wesing.background.lib;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.tencent.wesing.background.lib.res.TMEBackgroundHookResourcesUtil;

class TMEBackgroundActivityLifecycleRegister implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        //TMEBackgroundInflater.inject(activity);
        TMEBackgroundHookResourcesUtil.hookSystemResources(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
