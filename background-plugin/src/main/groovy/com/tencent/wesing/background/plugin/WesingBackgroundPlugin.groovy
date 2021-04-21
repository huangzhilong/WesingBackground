package com.tencent.wesing.background.plugin

import com.tencent.wesing.background.plugin.shape.ShapeScanTask
import com.tencent.wesing.background.plugin.util.LogUtil
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * create by zlonghuang on 2021/4/19
 **/

class WesingBackgroundPlugin implements Plugin<Project> {

    static final String TAG = "WesingBackgroundPlugin"

    @Override
    void apply(Project project) {
        LogUtil.logI(TAG, "apply Plugin!!")
        def shapeScanTask = project.tasks.create("shapeScanTask", ShapeScanTask)
        StartParams startParams = new StartParams(project.gradle.getStartParameter())

        project.afterEvaluate {
            LogUtil.logI(TAG, " dependsOn apply Plugin!!")
            def preBuildTask = project.getTasksByName("preBuild", true)
            preBuildTask[0].dependsOn(shapeScanTask)
        }
    }
}