package com.tencent.wesing.background.plugin.resource.layout

import com.android.build.gradle.internal.res.Aapt2CompileRunnable
import com.android.build.gradle.internal.tasks.Workers
import com.android.ide.common.resources.CompileResourceRequest
import com.tencent.wesing.background.plugin.util.BackgroundUtil
import com.tencent.wesing.background.plugin.util.LogUtil
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.workers.*

import java.lang.reflect.Field

/**
 * create by zlonghuang on 2021/4/21
 **/

class MyWorkerExecutor implements WorkerExecutor {

    private static final String TAG = "MyWorkerExecutorFacade"

    private static final BACKGROUND_TAG = "android:background"
    private static final DRAWABLE_TAG = "@drawable/"
    private static final CUSTOM_APP_TAG = "wesingBackground"

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
                //主要是app图标资源，不处理
//                if (actionParameters.delegateParameters instanceof MergedResourceWriter.FileGenerationParameters) {
//                    //来源MergeResources -> MergedResourceWriter.addItem
//                    MergedResourceWriter.FileGenerationParameters fileGenerationParameters = (MergedResourceWriter.FileGenerationParameters) actionParameters.delegateParameters
//                    ResourceMergerItem item = fileGenerationParameters.resourceItem
//                }

                if (actionParameters.delegateParameters instanceof Aapt2CompileRunnable.Params) {
                    Aapt2CompileRunnable.Params aapt2Params = (Aapt2CompileRunnable.Params) (actionParameters.delegateParameters)
                    if (BackgroundUtil.getCollectSize(aapt2Params.requests) > 0) {
                        for (int i = 0; i < aapt2Params.requests.size(); i++) {
                            CompileResourceRequest request = aapt2Params.requests.get(i)
                            if (request.inputDirectoryName == "layout" && !request.getInputFileIsFromDependency() && request.inputFile.name.endsWith(".xml")) {
                                //LogUtil.logI(TAG, "CompileResourceRequest  ${Thread.currentThread().id} ${request.inputFile.path}  ${request.inputDirectoryName}  ${request.originalInputFile.path} ${request.inputFileIsFromDependency}")
                                hookAndroidBackground(request)
                            } else if (request.inputDirectoryName.startsWith("drawable")) {
                                //LogUtil.logI(TAG, "CompileResourceRequest  ${Thread.currentThread().id} ${request.inputFile.path}  ${request.inputDirectoryName}  ${request.originalInputFile.path} ${request.inputFileIsFromDependency}")
                            }
                        }
                    }
                } else {
                    //针对私有类，用反射
                    Class aClass1 = actionParameters.delegateParameters.class

                    // 针对library的资源
                    if (aClass1.name.contains("CompileLibraryResourcesParams")) {
                        Field field = aClass1.getDeclaredField("requests")
                        field.setAccessible(true)
                        Object o = field.get(actionParameters.delegateParameters)
                        List<CompileResourceRequest> requestList = (List<CompileResourceRequest>) o
                        if (BackgroundUtil.getCollectSize(requestList) > 0) {
                            for (int i = 0; i < requestList.size(); i++) {
                                CompileResourceRequest request = requestList.get(i)
                                if (request.inputDirectoryName == "layout" && !request.getInputFileIsFromDependency() && request.inputFile.name.endsWith(".xml")) {
                                    hookAndroidBackground(request)
                                } else if (request.inputDirectoryName.startsWith("drawable")) {
                                    //LogUtil.logI(TAG, "CompileResourceRequest  ${Thread.currentThread().id} ${request.inputFile.path}  ${request.inputDirectoryName}  ${request.originalInputFile.path} ${request.inputFileIsFromDependency}")
                                }
                            }
                        }
                    }
                }
                mWorkerExecutor.submit(aClass, action)
            }
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

    private LayoutInfo addCustomTagToXml(String content, String customTag) {
        LayoutInfo layoutInfo = new LayoutInfo()
        String customTxt = "\nxmlns:$customTag=\"http://schemas.android.com/apk/res-auto\""
        if (content.indexOf("http://schemas.android.com/apk/res/android\"") > 0) {
            content = content.replace("http://schemas.android.com/apk/res/android\"", "http://schemas.android.com/apk/res/android\"$customTxt")
            layoutInfo.content = content
            layoutInfo.result = true
        } else {
            layoutInfo.content = content
            layoutInfo.result = false
        }
        return layoutInfo
    }

    private void hookAndroidBackground(CompileResourceRequest request) {
        boolean needCheckInclude = true  //检查是否需要添加头
        boolean needHookFile = false
        byte[] contentByte = request.inputFile.readBytes()
        if (contentByte == null || contentByte.size() == 0) {
            LogUtil.logI(TAG, "hookAndroidBackground ${request.inputFile.path} contentByte is empty!!")
            return
        }
        String content = new String(contentByte)
        int index = 0
        // 查找android:background属性
        while ((index = content.indexOf(BACKGROUND_TAG, index)) > 0) {
            //往后面找两个"号，就是完整的一句background属性啦
            int endIndex = index + BACKGROUND_TAG.length()
            int count = 0
            String attribute = ""
            String value = ""
            int firstMark = 0
            //解析value
            while ((endIndex = content.indexOf("\"", endIndex)) > 0) {
                count ++
                endIndex++
                if (count == 1) {
                    firstMark = endIndex
                } else if (count > 1) {
                    attribute = content.substring(index, endIndex)
                    value = content.substring(firstMark, endIndex - 1)
                    break
                }
            }
            //是drawable的background
            if (!BackgroundUtil.isEmpty(value) && !BackgroundUtil.isEmpty(attribute) && value.contains(DRAWABLE_TAG)) {
                String drawableName = value.substring(value.indexOf(DRAWABLE_TAG) + DRAWABLE_TAG.length())

                //是需要hook的drawable
                if (isContainsTargetDrawable(drawableName)) {
                    if (needCheckInclude) {
                        needCheckInclude = false
                        LayoutInfo layoutInfo = addCustomTagToXml(content, CUSTOM_APP_TAG)
                        content = layoutInfo.content
                        if (!layoutInfo.result) {
                            LogUtil.logI(TAG, "hookAndroidBackground failed add customTag inputName:  ${request.inputFile.name}")
                            return
                        }
                    }
                    String newAttribute = "$CUSTOM_APP_TAG:tme_background=\"@drawable/$drawableName\""
                    LogUtil.logI(TAG, "hookAndroidBackground inputName: ${request.inputFile.name}  attribute: $attribute  value: $value  newAttribute: $newAttribute   drawableName: $drawableName")
                    content = content.replaceAll(attribute, newAttribute)
                    needHookFile = true
                }
            }
            index++ //加一查找下一个
        }
        if (!needHookFile) {
            return
        }
        File file = new File(mResDir)
        if (!file.exists()) {
            file.mkdirs()
        }
        File tempLayout = new File(mResDir + File.separator + request.inputFile.name)
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

    private boolean isContainsTargetDrawable(String drawableName) {
        for (int i = 0; i < mProject.rootProject.gradle.ext.shapeContainer.size(); i++) {
            String name = mProject.rootProject.gradle.ext.shapeContainer.get(i)
            if (name == drawableName) {
                return true
            }
        }
        return false
    }
}