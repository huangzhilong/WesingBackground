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
        //此次是否是增量
        boolean isIncremental = transformInvocation.isIncremental()

        //非增量清空旧的输出内容
        if (!isIncremental) {
            mOutputProvider.deleteAll()
        }
        transformInvocation.inputs.each { TransformInput input ->
            input.jarInputs.each { JarInput jarInput ->
                //处理Jar
                processJarInput(jarInput, mOutputProvider, isIncremental)
            }

            input.directoryInputs.each { DirectoryInput directoryInput ->
                //处理源码文件
                processDirectoryInputs(directoryInput, mOutputProvider, isIncremental)
            }
        }
    }

    private void processJarInput(JarInput jarInput, TransformOutputProvider outputProvider, boolean isIncremental) {
        File dest = outputProvider.getContentLocation(
                jarInput.getFile().getAbsolutePath(),
                jarInput.getContentTypes(),
                jarInput.getScopes(),
                Format.JAR)
        if (isIncremental) {
            Status status = jarInput.getStatus()
            switch (status) {
                case Status.NOTCHANGED:
                    break
                case Status.REMOVED:
                    if (dest.exists()) {
                        FileUtils.delete(dest)
                    }
                    break
                case Status.CHANGED:
                case Status.ADDED:
                    doTransformJar(jarInput, dest)
                    break
                default:
                    break
            }
        } else  {
            doTransformJar(jarInput, dest)
        }
    }

    private void doTransformJar(JarInput jarInput, File dest) {
        //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
        LogUtil.logI(TAG, "processJarInput fileName: ${jarInput.getFile().absolutePath}  ${jarInput.name}  dest: ${dest.absolutePath}")
        FileUtils.copyFile(jarInput.getFile(), dest)
    }

    private void processDirectoryInputs(DirectoryInput directoryInput, TransformOutputProvider outputProvider, boolean isIncremental) {
        File dest = outputProvider.getContentLocation(directoryInput.getName(),
                directoryInput.getContentTypes(), directoryInput.getScopes(),
                Format.DIRECTORY)
        //建立文件夹
        FileUtils.mkdirs(dest)
        if (isIncremental) {
            Map<File, Status> statusMap = directoryInput.getChangedFiles()
            Iterator<File> iterator = statusMap.keySet().iterator()
            //输出目录文件夹地址和输入文件夹地址
            String destPath = dest.getAbsolutePath()
            String directoryPath = directoryInput.file.getAbsolutePath()
            while (iterator.hasNext()) {
                File file = iterator.next()
                Status status = statusMap.get(file)
                //得到输出目录的文件路径
                String fileDestPath = file.getAbsolutePath().replace(directoryPath, destPath)
                File destFile = new File(fileDestPath)
                switch (status) {
                    case Status.NOTCHANGED:
                        break
                    case Status.REMOVED:
                        if (destFile.exists()) {
                            FileUtils.delete(destFile)
                        }
                        break
                    case Status.ADDED:
                    case Status.CHANGED:
                        //操作修改对输入file，然后copy到输出目录
                        onHandleDirectoryEachFile(directoryInput.name, file)
                        FileUtils.copyFile(file, destFile)
                        break
                    default:
                        break
                }
            }
        } else {
            //遍历操作每个文件再进行copy的输出目录
            eachFileToDirectory(directoryInput.name, directoryInput.file)
            FileUtils.copyDirectory(directoryInput.file, dest)
        }
    }

    private void eachFileToDirectory(String name, File file) {
        if (file == null || !file.exists()) {
            return
        }
        if (file.isDirectory()) {
            File [] fileList = file.listFiles()
            for (int i = 0; i < fileList.length; i++) {
                File subFile = fileList[i]
                if (subFile.isDirectory()) {
                    eachFileToDirectory(name, subFile)
                } else {
                    onHandleDirectoryEachFile(name, subFile)
                }
            }
        } else {
            onHandleDirectoryEachFile(name, file)
        }
    }


    private void onHandleDirectoryEachFile(String name, File file) {
        LogUtil.logI(TAG, "onHandleDirectoryEachFile name: $name  fileName: ${file.name}")
    }
}