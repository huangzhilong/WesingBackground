package com.tencent.wesing.background.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.api.BaseVariantImpl
import com.android.build.gradle.tasks.MergeResources
import com.tencent.wesing.background.plugin.resource.ResourceTransform
import com.tencent.wesing.background.plugin.resource.layout.MyWorkerExecutor
import com.tencent.wesing.background.plugin.resource.shape.ShapeScanTask
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
        if (!project.gradle.hasProperty("shapeContainer")) {
            LogUtil.logI(TAG, "create shapeContainer Property!!")
            project.gradle.ext.shapeContainer = new HashSet<String>()
        }
        def shapeScanTask = project.tasks.create("shapeScanTask", ShapeScanTask)
        StartParams startParams = new StartParams(project.gradle.getStartParameter())
        shapeScanTask.setShapeScanTaskParams(startParams, getGenerateJavaDir(project))

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
                    hookMergeResourcesTask(mergeResourcesTask, project)
                }
                mergeResourcesTask.doLast {
                    recoveryMergeResourcesTask(mergeResourcesTask)
                }
            }
        }

        //注册transform
        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(new ResourceTransform())
    }

    //父目录一定要layout，不然会报错，内部有校验
    private String getGenerateResDir(Project project) {
        String buildDirPath = project.getBuildDir().absolutePath
        String resPath = buildDirPath + File.separator + "background" + File.separator + "layout"

        // String resPath = buildDirPath + File.separator + "layout"
        // project.android.sourceSets.main.res.srcDirs += resPath
        return resPath
    }

    private String getGenerateJavaDir(Project project) {
        String buildDirPath = project.getBuildDir().absolutePath
        String javaPath = buildDirPath + File.separator + "background" + File.separator + "java"

        //生成的Java文件添加到src
        project.android.sourceSets.main.java.srcDirs += javaPath
        return javaPath
    }


    private void hookMergeResourcesTask(MergeResources mergeResources, Project project) {
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
            MyWorkerExecutor myWorkerExecutor = new MyWorkerExecutor(project, getGenerateResDir(project))
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