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

/**
 * create by zlonghuang on 2021/4/21
 **/

class ResourceTransform extends Transform {


    private final static String TAG = "ResourceTransform"
    private final static String TME_BACKGROUND_LIB_NAME = "background-lib" //属性map的值

    private List<AttributeInfo> mParseShapeXmlAttributeList = new ArrayList<>()
    private List<String> mProjectNameList = new ArrayList<>()
    private Project mProject
    private JarInput mBackgroundLibJar

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
            if (!p.plugins.hasPlugin("com.tencent.wesing.background") || !p.backgroundPluginConfig.isOpen) {
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
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        generateProjectList()
        LogUtil.logI(TAG, "start doTransform  isIncremental: ${transformInvocation.isIncremental()}")
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
            if (p.plugins.hasPlugin("com.tencent.wesing.background") && p.backgroundPluginConfig.isOpen) {
                input.directoryInputs.each { DirectoryInput directoryInput ->
                    processDirectoryInputs(directoryInput, mOutputProvider, isIncremental)
                }
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
                        doTransformJar(jarInput, dest)
                    }
                    break
                default:
                    break
            }
        } else  {
            if (jarInput.name.contains(TME_BACKGROUND_LIB_NAME)) {
                mBackgroundLibJar = jarInput
            } else {
                doTransformJar(jarInput, dest)
            }
        }
    }

    private void doTransformJar(JarInput jarInput, File dest) {
        // 只处理本项目的project
        if (isSubProjectLib(jarInput.name) || jarInput.name.contains(TME_BACKGROUND_LIB_NAME)) {
            LogUtil.logI(TAG, "start doTransformJar jar: ${jarInput.name}")
            String unzipTmp = "${mProject.getBuildDir().absolutePath}${File.separator}tmp${File.separator}" + getName()
            unzipTmp = "${unzipTmp}${File.separator}${jarInput.name.replace(':', '')}"

            JarZipUtils.unzipJarZip(jarInput.file.absolutePath, unzipTmp)
            File f = new File(unzipTmp)

            eachFileToDirectory(jarInput.name, f)

            //修改完再压缩生成jar再copy到输出目录
            JarZipUtils.zipJarZip(unzipTmp, dest.absolutePath)
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
        String fileName = file.name
        if (!fileName.endsWith(".class") || fileName.endsWith("R.class") || fileName.endsWith("BuildConfig.class")
                || fileName.contains("R\$")) {
            return
        }

        // 找到shape xml生成属性的class文件
        if (file.name.contains(GenerateShapeConfigUtil.JAVA_NAME + ".class")) {
            LogUtil.logI(TAG, "start getParseXmlAttributeInfoByClass name: $name fileName: ${fileName}")
            AmsUtil.getParseXmlAttributeInfoByClass(file, new ClassShapeXmlAdapterVisitor.IVisitListener() {
                @Override
                void onGetShapeXmlAttribute(List<AttributeInfo> list) {
                    LogUtil.logI(TAG, "getParseXmlAttributeInfoByClass size: ${BackgroundUtil.getCollectSize(list)}")
                    mParseShapeXmlAttributeList.addAll(list)
                }
            })
        } else if (fileName.contains("TMEBackgroundMap.class")) {
            //插入
            handleInsertBackgroundLibMap(file)
        } else {
            //不是mBackgroundLibJar的进行hook
            if (mBackgroundLibJar == null || name != mBackgroundLibJar.name) {
                AmsUtil.doHookCodeCreateDrawable(file)
            }
        }
    }

    private void handleInsertBackgroundLibMap(File f) {
        LogUtil.logI(TAG, "handleInsertBackgroundLibMap: ${f.name}  attribute Size: ${BackgroundUtil.getCollectSize(mParseShapeXmlAttributeList)}")
        if (BackgroundUtil.getCollectSize(mParseShapeXmlAttributeList) > 0) {
            AmsUtil.InsertTMEBackgroundMapClassAttribute(f, mParseShapeXmlAttributeList)
        }
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
            doTransformJar(mBackgroundLibJar, dest)
        }
    }
}