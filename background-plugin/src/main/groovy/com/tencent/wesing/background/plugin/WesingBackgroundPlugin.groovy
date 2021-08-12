package com.tencent.wesing.background.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.api.BaseVariantImpl
import com.android.build.gradle.internal.tasks.AndroidVariantTask
import com.android.build.gradle.tasks.MergeResources
import com.tencent.wesing.background.plugin.resource.ResourceTransform
import com.tencent.wesing.background.plugin.resource.layout.MyWorkerExecutor
import com.tencent.wesing.background.plugin.resource.shape.ShapeScanTask
import com.tencent.wesing.background.plugin.util.BackgroundUtil
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

    private boolean  isDebugType = false

    @Override
    void apply(Project project) {
        project.extensions.create("backgroundPluginConfig", WesingExtensionContainer)

        StartParams startParams = new StartParams(project.gradle.getStartParameter())
        if (!startParams.buildApk) {
            LogUtil.logI(TAG, "is not BuildApk!!")
            return
        }
        def shapeScanTask = null
        def variants
        if (project.plugins.hasPlugin("com.android.application")) {
            variants = (project.property("android") as AppExtension).applicationVariants
            //app module 注册transform
            def android = project.extensions.getByType(AppExtension)
            ResourceTransform transform = new ResourceTransform()
            transform.setProject(project)
            android.registerTransform(transform)


            //定义些参数，定义在application moudule中
            project.gradle.ext.shapeContainer = new ArrayList<String>()
            isDebugType = startParams.debug
            LogUtil.logI(TAG, "isDebugType: " + isDebugType)

            //由app module来遍历drawable xml文件
            shapeScanTask = project.tasks.create("shapeScanTask", ShapeScanTask)
            shapeScanTask.setJavaPath(getGenerateJavaDir(project), isDebugType)
        } else {
            variants = (project.property("android") as LibraryExtension).libraryVariants
        }

        project.afterEvaluate {
            variants.all { variant ->
                variant as BaseVariantImpl

                if (shapeScanTask != null) {
                    //扫描drawable xml task
                    shapeScanTask.run()
                }

                if (!project.backgroundPluginConfig.isOpen) {
                    LogUtil.logI(TAG, "backgroundPluginConfig not Open!! projectName: ${project.name}")
                    return
                }
                if (project.backgroundPluginConfig.isOnlyAnalysisShape) {
                    LogUtil.logI(TAG, "projectName: ${project.name} isOnlyAnalysisShape")
                    return
                }
                //hook 编译资处理
                if (project.plugins.hasPlugin("com.android.application")) {
                    MergeResources mergeResourcesTask = variant.getMergeResourcesProvider().get()
                    mergeResourcesTask.doFirst {
                        hookAndroidVariantTask(mergeResourcesTask, project)
                    }
                    mergeResourcesTask.doLast {
                        recoveryAndroidVariantTask(mergeResourcesTask)
                    }
                } else {
                    def typeName = isDebugType ? "Debug" : "Release"
                    def resTaskName = "compile" + typeName + "LibraryResources"
                    def resourcesTask = project.getTasksByName(resTaskName, true)
                    resourcesTask[0].doFirst {
                        hookAndroidVariantTask(resourcesTask[0], project)
                    }
                    resourcesTask[0].doLast {
                        recoveryAndroidVariantTask(resourcesTask[0])
                    }
                }
            }
        }
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


    private void hookAndroidVariantTask(AndroidVariantTask androidVariantTask, Project project) {
        //避免多次调用
        if (mWorkerExecutor != null) {
            return
        }
        try {
            //反射替换里面的线程池
            Field mWorkerExecutorField
            Field[] fields = androidVariantTask.class.getDeclaredFields()
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].type.toString().contains("org.gradle.workers.WorkerExecutor")) {
                    mWorkerExecutorField = fields[i]
                    break
                }
            }
            if (mWorkerExecutorField == null) {
                LogUtil.logI(TAG, "hookAndroidVariantTask not found mWorkerExecutorField!!")
                return
            }
            LogUtil.logI(TAG, "hookAndroidVariantTask get filed name: ${mWorkerExecutorField.name} getEnableGradleWorkers: ${androidVariantTask.getEnableGradleWorkers().get()}  getWorkerExecutor: ${androidVariantTask.getWorkerExecutor()}")

            mWorkerExecutor = androidVariantTask.getWorkerExecutor()
            MyWorkerExecutor myWorkerExecutor = new MyWorkerExecutor(project, getGenerateResDir(project))
            myWorkerExecutor.workerExecutor = mWorkerExecutor

            mWorkerExecutorField.setAccessible(true)
            //替换成自己的
            mWorkerExecutorField.set(androidVariantTask, myWorkerExecutor)
        } catch (Exception e) {
            LogUtil.logI(TAG, "hookAndroidVariantTask failed ex: $e")
        }
    }

    private void recoveryAndroidVariantTask(AndroidVariantTask androidVariantTask) {
        if (mWorkerExecutor == null) {
            return
        }
        try {
            //反射还原里面的线程池
            Field mWorkerExecutorField
            Field[] fields = androidVariantTask.class.getDeclaredFields()
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].type.toString().contains("org.gradle.workers.WorkerExecutor")) {
                    mWorkerExecutorField = fields[i]
                    break
                }
            }
            if (mWorkerExecutorField == null) {
                LogUtil.logI(TAG, "recoveryAndroidVariantTask not found mWorkerExecutorField!!")
                return
            }
            LogUtil.logI(TAG, "recoveryAndroidVariantTask get filed name: ${mWorkerExecutorField.name}  mWorkerExecutor: ${mWorkerExecutor}  curWorkerExecutor: ${androidVariantTask.getWorkerExecutor()}")
            mWorkerExecutorField.setAccessible(true)
            mWorkerExecutorField.set(androidVariantTask, mWorkerExecutor)
            mWorkerExecutor = null
        } catch (Exception e) {
            LogUtil.logI(TAG, "recoveryAndroidVariantTask failed ex: $e")
        }
    }
}