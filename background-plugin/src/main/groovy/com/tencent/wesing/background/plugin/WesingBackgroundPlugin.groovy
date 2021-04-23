package com.tencent.wesing.background.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.api.BaseVariantImpl
import com.android.build.gradle.tasks.MergeResources
import com.tencent.wesing.background.plugin.resource.MyWorkerExecutor
import com.tencent.wesing.background.plugin.shape.ShapeScanTask
import com.tencent.wesing.background.plugin.util.LogUtil
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.workers.WorkerExecutor

import java.lang.reflect.Field

/**
 * create by zlonghuang on 2021/4/19
 **/

class WesingBackgroundPlugin implements Plugin<Project> {

    static final String TAG = "WesingBackgroundPlugin"

    private WorkerExecutor mWorkerExecutor

    @Override
    void apply(Project project) {
        LogUtil.logI(TAG, "apply Plugin!!")
        def shapeScanTask = project.tasks.create("shapeScanTask", ShapeScanTask)
        StartParams startParams = new StartParams(project.gradle.getStartParameter())
        shapeScanTask.setShapeScanTaskParams(startParams, getGenerateJavaFile(project))

        def variants
        if (project.plugins.hasPlugin("com.android.application")) {
            variants = (project.property("android") as AppExtension).applicationVariants
        } else {
            variants = (project.property("android") as LibraryExtension).libraryVariants
        }

        project.afterEvaluate {

            variants.all { variant ->
                variant as BaseVariantImpl

                def preBuildTask = variant.getPreBuildProvider().get()
                preBuildTask.dependsOn(shapeScanTask)

                MergeResources mergeResourcesTask = variant.getMergeResourcesProvider().get()
                mergeResourcesTask.doFirst {
                    hookMergeResourcesTask(mergeResourcesTask)
                }
                mergeResourcesTask.doLast {
                    recoveryMergeResourcesTask(mergeResourcesTask)
                }
            }
        }
    }

    private String getGenerateJavaFile(Project project) {
        String buildDirPath = project.getBuildDir().absolutePath
        String javaPath = buildDirPath + File.separator + "background"

        //生成的Java文件添加到src
        project.android.sourceSets.main.java.srcDirs += javaPath
        return javaPath
    }


    private void hookMergeResourcesTask(MergeResources mergeResources) {
        try {
            //反射替换里面的线程池
            Field mWorkerExecutorField
            Field[] fields = mergeResources.class.getDeclaredFields()
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].type.toString().contains("org.gradle.workers.WorkerExecutor")) {
                    mWorkerExecutorField = fields[i]
                    break
                }
            }
            if (mWorkerExecutorField == null) {
                LogUtil.logI(TAG, "hookMergeResourcesTask not found mWorkerExecutorField!!")
                return
            }
            LogUtil.logI(TAG, "hookMergeResourcesTask get filed name: ${mWorkerExecutorField.name} getEnableGradleWorkers: ${mergeResources.getEnableGradleWorkers().get()}  getWorkerExecutor: ${mergeResources.getWorkerExecutor()}")

            mWorkerExecutor = mergeResources.getWorkerExecutor()
            MyWorkerExecutor myWorkerExecutor = new MyWorkerExecutor()
            myWorkerExecutor.workerExecutor = mWorkerExecutor

            mWorkerExecutorField.setAccessible(true)
            //替换成自己的
            mWorkerExecutorField.set(mergeResources, myWorkerExecutor)
        } catch (Exception e) {
            LogUtil.logI(TAG, "hookMergeResourcesTask failed ex: $e")
        }
    }

    private void recoveryMergeResourcesTask(MergeResources mergeResources) {
        try {
            //反射还原里面的线程池
            Field mWorkerExecutorField
            Field[] fields = mergeResources.class.getDeclaredFields()
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].type.toString().contains("org.gradle.workers.WorkerExecutor")) {
                    mWorkerExecutorField = fields[i]
                    break
                }
            }
            if (mWorkerExecutorField == null) {
                LogUtil.logI(TAG, "recoveryMergeResourcesTask not found mWorkerExecutorField!!")
                return
            }
            LogUtil.logI(TAG, "recoveryMergeResourcesTask get filed name: ${mWorkerExecutorField.name}  mWorkerExecutor: ${mWorkerExecutor}  curWorkerExecutor: ${mergeResources.getWorkerExecutor()}")
            mWorkerExecutorField.setAccessible(true)
            if (mWorkerExecutor == null) {
                LogUtil.logI(TAG, "recoveryMergeResourcesTask myWorkerExecutor.workerExecutor == null !!")
                return
            }
            mWorkerExecutorField.set(mergeResources, mWorkerExecutor)
        } catch (Exception e) {
            LogUtil.logI(TAG, "recoveryMergeResourcesTask failed ex: $e")
        }
    }
}