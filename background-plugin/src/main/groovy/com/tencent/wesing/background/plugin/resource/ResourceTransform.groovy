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
import com.tencent.wesing.background.plugin.util.JarZipUtils
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
 **/

class ResourceTransform extends Transform {


    private final static String TAG = "ResourceTransform"
    private final static String TME_BACKGROUND_LIB_NAME = "com.tencent.wesing:wesingBackgroundLib" //属性map的值

    private List<AttributeInfo> mParseShapeXmlAttributeList = new ArrayList<>()
    private List<String> mProjectNameList = new ArrayList<>()
    private Project mProject
    private JarInput mBackgroundLibJar
    private boolean appModuleSupportPlugin = false  //application module是否支持该插件

    void setProject(Project project) {
        mProject = project
    }

    private void generateProjectList() {
        //获取项目的所有支持该插件的module name
        Iterator<Project> iterator = mProject.rootProject.allprojects.iterator()
        while (iterator.hasNext()) {
            Project p = iterator.next()
            if (p.name == p.rootProject.name) {
                continue
            }
            if (!isSupportPlugin(p)) {
                continue
            }
            mProjectNameList.add(p.name)
        }
        LogUtil.logI(TAG, "generateProjectList list: ${mProjectNameList} ")
    }

    private boolean isSubProjectLib(String jarInputName) {
        String jarName = jarInputName.replace(":", "")
        for (int i = 0; i < mProjectNameList.size(); i++) {
            if (jarName == mProjectNameList.get(i)) {
                return true
            }
        }
        return false
    }

    private boolean isSupportPlugin(Project p) {
        if (p.plugins.hasPlugin("com.tencent.wesing.background") && p.backgroundPluginConfig.isOpen) {
            return true
        }
        return false
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
        generateProjectList()
        appModuleSupportPlugin = isSupportPlugin(mProject)
        LogUtil.logI(TAG, "start doTransform  appModuleSupportPlugin: $appModuleSupportPlugin  isIncremental: ${transformInvocation.isIncremental()}")
        doTransform(transformInvocation)
        afterTransform(transformInvocation)
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
        LogUtil.logI(TAG, "processJarInput name: ${jarInput.name}  ${jarInput.file.name}")
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
                    if (jarInput.name.contains(TME_BACKGROUND_LIB_NAME)) {
                        mBackgroundLibJar = jarInput
                    } else {
                        doTransformJar(jarInput, dest, outputProvider)
                    }
                    break
                default:
                    break
            }
        } else  {
            if (jarInput.name.contains(TME_BACKGROUND_LIB_NAME)) {
                mBackgroundLibJar = jarInput
            } else {
                doTransformJar(jarInput, dest, outputProvider)
            }
        }
    }

    private void doTransformJar(JarInput jarInput, File dest, TransformOutputProvider outputProvider) {
        // 只处理本项目的project以及 用于插入xml 属性的lib库
        if (isSubProjectLib(jarInput.name) || jarInput.name.contains(TME_BACKGROUND_LIB_NAME)) {
            LogUtil.logI(TAG, "start doTransformJar support jar: ${jarInput.name}")
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
                    byte [] newCodeByte = onHandleEachClass(true, inputStream, entryName, entryName)
                    if (newCodeByte == null || newCodeByte.length == 0) {
                        jarOutputStream.write(IOUtils.toByteArray(inputStream))
                    } else {
                        jarOutputStream.write(newCodeByte)
                    }
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
        } else {
            FileUtils.copyFile(jarInput.getFile(), dest)
        }
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
                        onHandleDirectoryEachFile(directoryInput.name, file, false)
                        FileUtils.copyFile(file, destFile)
                        break
                    default:
                        break
                }
            }
        } else {
            //遍历操作每个文件再进行copy的输出目录
            eachFileToDirectory(directoryInput.name, directoryInput.file, false)
            FileUtils.copyDirectory(directoryInput.file, dest)
        }
    }

    private void eachFileToDirectory(String name, File file, boolean isJarInput) {
        if (file == null || !file.exists()) {
            return
        }
        if (file.isDirectory()) {
            File [] fileList = file.listFiles()
            for (int i = 0; i < fileList.length; i++) {
                File subFile = fileList[i]
                if (subFile.isDirectory()) {
                    eachFileToDirectory(name, subFile, isJarInput)
                } else {
                    onHandleDirectoryEachFile(name, subFile, isJarInput)
                }
            }
        } else {
            onHandleDirectoryEachFile(name, file, isJarInput)
        }
    }


    private void onHandleDirectoryEachFile(String name, File file, boolean isJarInput) {
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
            return
        }
        // 项目module（除app其他module会以jar :BModule  classes.jar 形式) 主app module才会以文件夹的形式，当主module支持该插件时进行getDrawable hook
        if (appModuleSupportPlugin) {
            try {
                FileInputStream fis = new FileInputStream(file)
                byte [] newCodeByte = onHandleEachClass(false, fis, file.name, file.absolutePath)
                FileOutputStream fos = new FileOutputStream(file)
                //覆盖自己
                if (newCodeByte != null && newCodeByte.length > 0) {
                    fos.write(newCodeByte)
                }
                fos.close()
                fis.close()
            } catch (Exception e) {
                LogUtil.logI(TAG, "onHandleDirectoryEachFile onHandleEachClass f: ${file.absolutePath}  ex: $e")
            }
        }
    }

    private byte[] onHandleEachClass(boolean isJarInput, InputStream inputStream, String fileName,String path) {
        byte [] newCodeByte = null
        LogUtil.logI(TAG, "onHandleEachClass isJarInput: $isJarInput  fileName: $fileName")
        if (fileName.contains("TMEBackgroundMap.class")) {
            LogUtil.logI(TAG, "handleInsertBackgroundLibMap: ${path}  attribute Size: ${BackgroundUtil.getCollectSize(mParseShapeXmlAttributeList)}")
            if (BackgroundUtil.getCollectSize(mParseShapeXmlAttributeList) > 0) {
                newCodeByte = AmsUtil.InsertTMEBackgroundMapClassAttribute(inputStream, path, mParseShapeXmlAttributeList)
            }
        } else {
            // hook 代码中的getDrawable
            newCodeByte = AmsUtil.doHookCodeCreateDrawable(inputStream, path)
        }
        return newCodeByte
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
            doTransformJar(mBackgroundLibJar, dest, mOutputProvider)
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