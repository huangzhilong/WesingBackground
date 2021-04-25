package com.tencent.wesing.background.plugin.resource

import com.android.build.gradle.internal.res.Aapt2CompileRunnable
import com.android.build.gradle.internal.tasks.Workers
import com.android.ide.common.resources.CompileResourceRequest
import com.android.ide.common.resources.MergedResourceWriter
import com.android.ide.common.resources.ResourceMergerItem
import com.tencent.wesing.background.plugin.util.LogUtil
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.workers.ClassLoaderWorkerSpec
import org.gradle.workers.ProcessWorkerSpec
import org.gradle.workers.WorkQueue
import org.gradle.workers.WorkerConfiguration
import org.gradle.workers.WorkerExecutionException
import org.gradle.workers.WorkerExecutor
import org.gradle.workers.WorkerSpec

import java.lang.reflect.Field


/**
 * create by zlonghuang on 2021/4/21
 **/

class MyWorkerExecutor implements WorkerExecutor {

    private static final String TAG = "MyWorkerExecutorFacade"

    private WorkerExecutor mWorkerExecutor
    private Project mProject
    private String mResDir

    MyWorkerExecutor(Project project, String dir) {
        mProject = project
        mResDir = dir
    }

    void setWorkerExecutor(WorkerExecutor workerExecutor) {
        LogUtil.logI(TAG, "setWorkerExecutor ${workerExecutor} ")
        mWorkerExecutor = workerExecutor
    }

    @Override
    void submit(Class<? extends Runnable> aClass, Action<? super WorkerConfiguration> action) {
        if (mWorkerExecutor != null) {
            //获取params
            MyWorkerConfiguration configuration = new MyWorkerConfiguration()
            action.execute(configuration)
            List<Object> params = configuration.getParamsList()
            if (params.size() > 0) {
                Object value = params.get(0)
                Workers.ActionParameters actionParameters = (Workers.ActionParameters) value
                if (actionParameters.delegateParameters instanceof MergedResourceWriter.FileGenerationParameters) {
                    //来源MergeResources -> MergedResourceWriter.addItem
                    MergedResourceWriter.FileGenerationParameters fileGenerationParameters = (MergedResourceWriter.FileGenerationParameters) actionParameters.delegateParameters
                    ResourceMergerItem item = fileGenerationParameters.resourceItem
                    LogUtil.logI(TAG, "submit is  MergedResourceWriter.FileGenerationParameters  ${Thread.currentThread().id} ${item.name}  ${item.type}  ${item.getFile().path}")
                } else if (actionParameters.delegateParameters instanceof Aapt2CompileRunnable.Params) {
                    //来源CompileSourceSetResources 提交的任务
                    Aapt2CompileRunnable.Params aapt2Params = (Aapt2CompileRunnable.Params) (actionParameters.delegateParameters)
                    LogUtil.logI(TAG, "submit is  Aapt2CompileRunnable.Params ${aapt2Params.requests.size()}")
                    for (int i = 0; i < aapt2Params.requests.size(); i++) {
                        CompileResourceRequest request = aapt2Params.requests.get(i)
                        if (request.inputDirectoryName == "layout" && !request.getInputFileIsFromDependency() && request.inputFile.name.endsWith(".xml")) {
                            LogUtil.logI(TAG, "CompileResourceRequest  ${Thread.currentThread().id} ${request.inputFile.path}  ${request.inputDirectoryName}  ${request.originalInputFile.path} ${request.inputFileIsFromDependency}")
                            hookAndroidBackground(request)
                        } else if (request.inputDirectoryName.startsWith("drawable")) {
                            LogUtil.logI(TAG, "CompileResourceRequest  ${Thread.currentThread().id} ${request.inputFile.path}  ${request.inputDirectoryName}  ${request.originalInputFile.path} ${request.inputFileIsFromDependency}")
                        }
                    }

                    //移除后layout xml background中找不到！！
//                    for (int i = 0; i < aapt2Params.requests.size(); i++) {
//                        CompileResourceRequest request = aapt2Params.requests.get(i)
//                        if (request.inputFile.name.contains("shape_text_3")) {
//                            aapt2Params.requests.remove(i)
//                            LogUtil.logI(TAG, "remove!!!!!!!!")
//                            break
//                        }
//                    }
                }
            }
            mWorkerExecutor.submit(aClass, action)
        }
    }


    @Override
    WorkQueue noIsolation() {
        if (mWorkerExecutor != null) {
            return mWorkerExecutor.noIsolation()
        }
        return null
    }

    @Override
    WorkQueue classLoaderIsolation() {
        if (mWorkerExecutor != null) {
            return mWorkerExecutor.classLoaderIsolation()
        }
        return null
    }

    @Override
    WorkQueue processIsolation() {
        if (mWorkerExecutor != null) {
            retun mWorkerExecutor.processIsolation()
        }
        return null
    }

    @Override
    WorkQueue noIsolation(Action<? super WorkerSpec> action) {
        if (mWorkerExecutor != null) {
            return mWorkerExecutor.noIsolation(action)
        }
        return null
    }

    @Override
    WorkQueue classLoaderIsolation(Action<? super ClassLoaderWorkerSpec> action) {
        if (mWorkerExecutor != null) {
            return mWorkerExecutor.classLoaderIsolation(action)
        }
        return null
    }

    @Override
    WorkQueue processIsolation(Action<? super ProcessWorkerSpec> action) {
        if (mWorkerExecutor != null) {
            return mWorkerExecutor.processIsolation(action)
        }
        return null
    }

    @Override
    void await() throws WorkerExecutionException {
        if (mWorkerExecutor != null) {
            mWorkerExecutor.await()
        }
    }

    private void hookAndroidBackground(CompileResourceRequest request) {
        File file = new File(mResDir)
        if (!file.exists()) {
            file.mkdirs()
        }
        File tempLayout = new File(mResDir + File.separator + request.inputFile.name)
        byte[] contentByte = request.inputFile.readBytes()
        if (contentByte == null || contentByte.size() == 0) {
            LogUtil.logI(TAG, "hookAndroidBackground ${request.inputFile.path} contentByte is empty!!")
            return
        }
        String content = new String(contentByte)
        content = content.replaceAll("android:background=\"@drawable/shape_text_3\"", " android:background=\"@drawable/shape_text_4\"")
        tempLayout.write(content)

        //反射修改成修改后的文件
        try {
            Field inputFileField = request.getClass().getDeclaredField("inputFile")
            Field originalInputFileField = request.getClass().getDeclaredField("originalInputFile")
            inputFileField.setAccessible(true)
            originalInputFileField.setAccessible(true)
            inputFileField.set(request, tempLayout)
            originalInputFileField.set(request, tempLayout)
        } catch (Exception e) {
            LogUtil.logI(TAG, "hookAndroidBackground ex: $e")
        }
    }

}