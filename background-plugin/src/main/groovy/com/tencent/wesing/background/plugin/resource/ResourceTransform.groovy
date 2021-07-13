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
import com.tencent.wesing.background.plugin.ams.AmsUtil
import com.tencent.wesing.background.plugin.ams.ClassShapeXmlAdapterVisitor
import com.tencent.wesing.background.plugin.ams.bean.AttributeInfo
import com.tencent.wesing.background.plugin.util.BackgroundUtil
import com.tencent.wesing.background.plugin.util.LogUtil
import org.gradle.api.Project

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.compress.utils.IOUtils

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * create by zlonghuang on 2021/4/21
 *
 * 读取xml生成的属性值，插入lib库的map中
 **/

class ResourceTransform extends Transform {


    private final static String TAG = "ResourceTransform"
    private final static String TME_BACKGROUND_LIB_NAME = "com.tme.wesing:fast_background_lib" //属性map的值

    private List<AttributeInfo> mParseShapeXmlAttributeList = new ArrayList<>()
    private Project mProject
    private JarInput mBackgroundLibJar

    void setProject(Project project) {
        mProject = project
    }

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
        LogUtil.logI(TAG, "start ResourceTransform doTransform isIncremental: ${transformInvocation.isIncremental()}")
        long startTime = System.currentTimeMillis()
        doTransform(transformInvocation)
        afterTransform(transformInvocation)
        long costTime = System.currentTimeMillis() -  startTime
        mProject.rootProject.gradle.ext.pluginCostTime = mProject.rootProject.gradle.ext.pluginCostTime + costTime
        LogUtil.logI(TAG, "transform costTime: $costTime  pluginCostTime: ${mProject.rootProject.gradle.ext.pluginCostTime}")
    }

    private void doTransform(TransformInvocation transformInvocation) {
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

            //处理源码文件，一般就是app module，其他module会以jar文件。这里判断下app module是否支持插件
            input.directoryInputs.each { DirectoryInput directoryInput ->
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
                    doTransformJar(jarInput, dest, outputProvider)
                    break
                default:
                    break
            }
        } else {
            doTransformJar(jarInput, dest, outputProvider)
        }
    }

    private void doTransformJar(JarInput jarInput, File dest, TransformOutputProvider outputProvider) {
        if (jarInput.name.contains(TME_BACKGROUND_LIB_NAME)) {
            mBackgroundLibJar = jarInput
        } else {
            FileUtils.copyFile(jarInput.getFile(), dest)
        }
    }

    private void onHandleBackgroundLibJar(JarInput jarInput, File dest, TransformOutputProvider outputProvider) {
        LogUtil.logI(TAG, "start doTransformJar support jarName: ${jarInput.name} file size: ${jarInput.file.length()} ")
        //重命名输出文件，避免同名覆盖
        String jarName = jarInput.name
        String md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
        if (jarName.endsWith(".jar")) {
            jarName = jarName.substring(0, jarName.length - 4)
        }
        JarFile jarFile = new JarFile(jarInput.file)
        Enumeration<JarEntry> enumeration = jarFile.entries()
        File tmpFile = new File(jarInput.file.parent + File.separator + "class_tmp.jar")
        if (tmpFile.exists()) {
            tmpFile.delete()
        }
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile))
        //保存
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            String entryName = jarEntry.name
            ZipEntry zipEntry = new ZipEntry(entryName)
            InputStream inputStream = jarFile.getInputStream(jarEntry)
            //插装
            if (needTransform(entryName)) {
                jarOutputStream.putNextEntry(zipEntry)
                byte [] newCodeByte = onHandleEachClass(inputStream, entryName, entryName)
                jarOutputStream.write(newCodeByte)
            } else {
                jarOutputStream.putNextEntry(zipEntry)
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        jarFile.close()
        dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
        FileUtils.copyFile(tmpFile, dest)
        tmpFile.delete()
    }

    private void processDirectoryInputs(DirectoryInput directoryInput, TransformOutputProvider outputProvider, boolean isIncremental) {
        File dest = outputProvider.getContentLocation(directoryInput.getName(),
                directoryInput.getContentTypes(), directoryInput.getScopes(),
                Format.DIRECTORY)
        LogUtil.logI(TAG, "processDirectoryInputs name: ${directoryInput.name}  ${directoryInput.file.name}")
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


    //属性生成的application module中，会以文件夹形式遍历到
    private void onHandleDirectoryEachFile(String name, File file) {
        String fileName = file.name
        if (!needTransform(fileName)) {
            return
        }
        // 读取生成的属性，列表存起来，用于插入到lib去。找到shape xml生成属性的class文件, 因为属性都是生成在application module，所以是文件夹遍历
        if (file.name.contains(GenerateShapeConfigUtil.JAVA_NAME + ".class")) {
            LogUtil.logI(TAG, "start getParseXmlAttributeInfoByClass name: $name fileName: ${fileName}")
            AmsUtil.getParseXmlAttributeInfoByClass(file, new ClassShapeXmlAdapterVisitor.IVisitListener() {
                @Override
                void onGetShapeXmlAttribute(List<AttributeInfo> list) {
                    LogUtil.logI(TAG, "getParseXmlAttributeInfoByClass size: ${BackgroundUtil.getCollectSize(list)}")
                    mParseShapeXmlAttributeList.addAll(list)
                }
            })
        }
    }

    private byte[] onHandleEachClass(InputStream inputStream, String fileName, String path) {
        //lib的map class类
        if (fileName.contains("TMEBackgroundMap.class")) {
            LogUtil.logI(TAG, "handleInsertBackgroundLibMap: ${path}  attribute Size: ${BackgroundUtil.getCollectSize(mParseShapeXmlAttributeList)}")
            if (BackgroundUtil.getCollectSize(mParseShapeXmlAttributeList) > 0) {
                return AmsUtil.InsertTMEBackgroundMapClassAttribute(inputStream, path, mParseShapeXmlAttributeList)
            }
        }
        return inputStream.getBytes()
    }

    private void afterTransform(TransformInvocation transformInvocation) {
        LogUtil.logI(TAG, "afterTransform!!")
        TransformOutputProvider mOutputProvider = transformInvocation.getOutputProvider()
        //要保证先解析生成的属性再插入属性，所以放到doTransform之后处理
        if (mBackgroundLibJar != null) {
            File dest = mOutputProvider.getContentLocation(
                    mBackgroundLibJar.getFile().getAbsolutePath(),
                    mBackgroundLibJar.getContentTypes(),
                    mBackgroundLibJar.getScopes(),
                    Format.JAR)
            onHandleBackgroundLibJar(mBackgroundLibJar, dest, mOutputProvider)
        } else {
            LogUtil.logI(TAG, "afterTransform not find mBackgroundLibJar!!")
        }
    }

    private boolean needTransform(String fileName) {
        if (BackgroundUtil.isEmpty(fileName) || !fileName.endsWith(".class") || fileName.endsWith("R.class") || fileName.endsWith("BuildConfig.class")
                || fileName.contains("R\$")) {
            return false
        }
        return true
    }

}