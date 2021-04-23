package com.tencent.wesing.background.plugin.resource

import com.android.build.gradle.internal.res.Aapt2CompileRunnable
import com.android.build.gradle.internal.tasks.Workers
import com.android.ide.common.resources.CompileResourceRequest
import com.android.ide.common.resources.MergedResourceWriter
import com.android.ide.common.resources.ResourceMergerItem
import com.tencent.wesing.background.plugin.util.LogUtil
import org.gradle.api.Action
import org.gradle.workers.ClassLoaderWorkerSpec
import org.gradle.workers.ProcessWorkerSpec
import org.gradle.workers.WorkQueue
import org.gradle.workers.WorkerConfiguration
import org.gradle.workers.WorkerExecutionException
import org.gradle.workers.WorkerExecutor
import org.gradle.workers.WorkerSpec


/**
 * create by zlonghuang on 2021/4/21
 **/

class MyWorkerExecutor implements WorkerExecutor {

    private static final String TAG = "MyWorkerExecutorFacade"

    private WorkerExecutor mWorkerExecutor

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
                    MergedResourceWriter.FileGenerationParameters fileGenerationParameters = (MergedResourceWriter.FileGenerationParameters) actionParameters.delegateParameters
                    ResourceMergerItem item = fileGenerationParameters.resourceItem
                    LogUtil.logI(TAG, "submit is  MergedResourceWriter.FileGenerationParameters ${item.name}  ${item.type}  ${item.getFile().path}")
                } else if (actionParameters.delegateParameters instanceof Aapt2CompileRunnable.Params) {
                    Exception e = new Exception("this is a log"); //相应的log标记

                    e.printStackTrace();

                    Aapt2CompileRunnable.Params aapt2Params = (Aapt2CompileRunnable.Params) (actionParameters.delegateParameters)
                    for (int i = 0; i < aapt2Params.requests.size(); i++) {
                        CompileResourceRequest request = aapt2Params.requests.get(i)
                        if (request.inputDirectoryName == "layout") {

                        }
                       // LogUtil.logI(TAG, "CompileResourceRequest ${request.inputFile.path}  ${request.inputDirectoryName}  ${request.originalInputFile.path} ")
                    }
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

}