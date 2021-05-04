package com.tencent.wesing.background.plugin.resource

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Status
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.api.transform.Format
import com.android.utils.FileUtils
import com.tencent.wesing.background.plugin.util.LogUtil

/**
 * create by zlonghuang on 2021/4/21
 **/

class ResourceTransform extends Transform {

    final static String TAG = "ResourceTransform"

    @Override
    String getName() {
        return TAG
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        //表示只输入class文件，还可以是RESOURCES
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        try {
            doTransform(transformInvocation)
        } catch (Exception e) {
           LogUtil.logI(TAG, "doTransform get ex: $e")
        }
    }

    private void doTransform(TransformInvocation transformInvocation) {
        Collection<TransformInput> mInputCollection = transformInvocation.getInputs()
        // 创建一个对应名称表示的输出目录 位于app/build/intermediates/transforms/(transform的getName)
        // 是从0 、1、2开始递增。如果是目录，名称就是对应的数字，如果是jar包就类似0.jar
        TransformOutputProvider mOutputProvider = transformInvocation.getOutputProvider()
        transformInvocation.inputs.each { TransformInput input ->
            input.jarInputs.each { JarInput jarInput ->
                //处理Jar
                processJarInput(jarInput, mOutputProvider)
            }

            input.directoryInputs.each { DirectoryInput directoryInput ->
                //处理源码文件
                processDirectoryInputs(directoryInput, mOutputProvider)
            }
        }
    }

    private void processJarInput(JarInput jarInput, TransformOutputProvider outputProvider) {
        File dest = outputProvider.getContentLocation(
                jarInput.getFile().getAbsolutePath(),
                jarInput.getContentTypes(),
                jarInput.getScopes(),
                Format.JAR)

        //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了

        LogUtil.logI(TAG, "processJarInput fileName: ${jarInput.getFile().absolutePath}  ${jarInput.name}  dest: ${dest.absolutePath}")
        FileUtils.copyFile(jarInput.getFile(), dest)
    }

    private void processDirectoryInputs(DirectoryInput directoryInput, TransformOutputProvider outputProvider) {
        File dest = outputProvider.getContentLocation(directoryInput.getName(),
                directoryInput.getContentTypes(), directoryInput.getScopes(),
                Format.DIRECTORY)
        //建立文件夹
        FileUtils.mkdirs(dest)

        LogUtil.logI(TAG, "processDirectoryInputs fileName: ${directoryInput.getFile().absolutePath}  ${directoryInput.name}  dest: ${dest.absolutePath}")
        //需要使用文件夹遍历，这种只针对增量
//        Map<File, Status> statusMap = directoryInput.getChangedFiles()
//        Iterator<File> iterator = statusMap.keySet().iterator()
//        while (iterator.hasNext()) {
//            File file = iterator.next()
//            Status status = statusMap.get(file)
//            LogUtil.logI(TAG, "find BackgroundShapeConfig path: ${file.absolutePath}")
//        }

        //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
        FileUtils.copyDirectory(directoryInput.getFile(), dest)
    }
}