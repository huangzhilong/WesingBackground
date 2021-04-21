package com.tencent.wesing.background.plugin.shape

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.BaseVariant
import com.tencent.wesing.background.plugin.StartParams
import com.tencent.wesing.background.plugin.code.GenerateShapeConfigUtil
import com.tencent.wesing.background.plugin.util.LogUtil
import com.tencent.wesing.background.plugin.util.BackgroundUtil
import org.gradle.api.DefaultTask
import org.gradle.api.DomainObjectCollection
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

/**
 * create by zlonghuang on 2021/4/19
 *
 * 扫描整个项目的drawable xml 的shape标签，
 * 生成属性代码和唯一Id
 **/

class ShapeScanTask extends DefaultTask {

    static final String TAG = "ShapeScanTask"
    static final String SHAPE_TAG = "shape"

    private HashSet<String> mScanProject = new HashSet<>()
    private volatile boolean isRunning = false
    private StartParams mStartParams
    private String packageName  // 包名

    ShapeScanTask() {
        LogUtil.logI(TAG, "projectName: ${project.name} create ShapeScanTask!!")
        mScanProject.clear()
        isRunning = false
        packageName = null
    }

    void setStartParams(StartParams param) {
        mStartParams = param
    }

    @TaskAction
    def run() {
        //避免调用多次
        if (!isRunning) {
            isRunning = true
            getSourcesDirs(project.rootProject)
        }
    }

    def getSourcesDirs(Project root) {
        Set<Project> projectSet = root.getAllprojects()
        for (int i = 0; i < projectSet.size(); i++) {
            Project temp = projectSet[i]
            if (temp.plugins.hasPlugin(AppPlugin)) {
                getSourcesDirsWithVariant((DomainObjectCollection<BaseVariant>) temp.android.applicationVariants, temp.name)
            }
            //先只处理App module
//            else if (temp.plugins.hasPlugin(LibraryPlugin)) {
//                getSourcesDirsWithVariant((DomainObjectCollection<BaseVariant>) temp.android.libraryVariants, temp.name)
//            }
        }
    }

    def getSourcesDirsWithVariant(DomainObjectCollection<BaseVariant> collection, String projectName) {
        List<XmlNodeInfo> drawableNodeXmlList = new ArrayList<>()
        String tempPackageName
        boolean isDebug = false
        if (mStartParams != null) {
            isDebug = mStartParams.debug
        }
        String variantName = isDebug ? "debug" : "release"
        collection.all { variant ->
            //有debug和release两种情况
            if (variant.name == variantName) {
                variant.sourceSets?.each { sourceSet ->
                    //这里能拿到 sourceSet.name 为 release和main ，release和main目录src/release/AndroidManifest.xml  src/release/AndroidManifest.xml，忽略点
                    //项目中一般不存在的
                    if (sourceSet.name == "main") {
                        XmlParser mXmlParser = new XmlParser()

                        //解析manifest文件提取包名
                        if (sourceSet.manifestFile.exists()) {
                            Node xmlNode = mXmlParser.parse(sourceSet.manifestFile)
                            if (xmlNode != null) {
                                xmlNode.attributes().each {
                                    attr ->
                                        if (attr != null && attr.key != null && attr.value != null) {
                                            String key = attr.key.toString()
                                            String value = attr.value.toString()
                                            if (key == "package") {
                                                tempPackageName = value
                                            }
                                        }
                                }
                            }
                            LogUtil.logI(TAG, "projectName: ${projectName}  path: ${sourceSet.manifest}  packageName: $tempPackageName")
                            if (BackgroundUtil.isEmpty(tempPackageName)) {
                                LogUtil.logI(TAG, "projectName: ${projectName}  get packageName failed!!!")
                                return
                            }
                        }
                        //代码目录
                        sourceSet.javaDirectories.each { dir ->
                        }

                        //解析res目录
                        sourceSet.resDirectories.each { res ->
                            if (res.exists()) {
                                LogUtil.logI(TAG, "projectName: ${projectName}   res: ${res.path}")
                                res.eachDir {
                                    //收集所有drawable目录里的shape中定义的xml
                                    if (it.directory && it.name.startsWith("drawable") && it.listFiles() != null && it.listFiles().size() > 0) {
                                        List<File> drawableList = it.listFiles()
                                        for (int i = 0; i < drawableList.size(); i++) {
                                            File xmlFile = drawableList.get(i)
                                            if (xmlFile == null || xmlFile.size() <= 0 || !xmlFile.name.endsWith(".xml")) {
                                                continue
                                            }
                                            Node xmlParseResult = mXmlParser.parse(xmlFile)
                                            if (xmlParseResult == null) {
                                                LogUtil.logI(TAG, "collectShapeXml parse error node is null xmlFileName: ${xmlFile.name}")
                                                continue
                                            }
                                            if (SHAPE_TAG == xmlParseResult.name()) {
                                                XmlNodeInfo info = new XmlNodeInfo()
                                                info.fileName = xmlFile.name
                                                info.xmlNode = xmlParseResult
                                                drawableNodeXmlList.add(info)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        drawableNodeXmlList = doFilterRepeatXmlFile(drawableNodeXmlList)
        packageName = tempPackageName
        if (BackgroundUtil.getCollectSize(drawableNodeXmlList) > 0)  {
            List<ShapeInfo> shapeInfoList = new ArrayList<>()
            for (int i = 0; i < drawableNodeXmlList.size(); i++) {
                ShapeInfo info = collectShapeXml(drawableNodeXmlList.get(i), projectName)
                if (info != null) {
                    shapeInfoList.add(info)
                }
            }

            if (BackgroundUtil.getCollectSize(shapeInfoList) > 0) {
                String buildDirPath = project.getBuildDir().absolutePath
                String packagePath = packageName.replaceAll("\\.", File.separator)
                String javaPath = buildDirPath + File.separator + "generated" + File.separator + "background" + File.separator + packagePath

                GenerateShapeConfigUtil.generateConfigJavaCode(project, shapeInfoList, packageName, javaPath)
                //生成的Java文件添加到src
                project.android.sourceSets.main.java.srcDirs += javaPath
            }
        }
    }

    /**
     * 去除相同的shape xml定义在多个drawable中，这种不处理，这种情况比较少
     * 暂时不知道如何处理，代码生成时不知道拿那个目录的drawable
     */
    List<XmlNodeInfo> doFilterRepeatXmlFile(List <XmlNodeInfo> list) {
        if (list == null || list.size() <= 1) {
            return list
        }
        List<XmlNodeInfo> mData = new ArrayList<>()
        for (int i = 0; i < list.size(); i++) {
            boolean inList = false
            for (int j = 0; j < list.size(); j++) {
                if (i != j && list.get(i).fileName == list.get(j).fileName) {
                    inList = true
                    break
                }
            }
            if (!inList) {
                mData.add(list.get(i))
            }
        }
        return mData
    }


    ShapeInfo collectShapeXml(XmlNodeInfo nodeInfo, String projectName) {
        if (nodeInfo == null || nodeInfo.xmlNode == null) {
            return null
        }
        LogUtil.logI(TAG, "collectShapeXml projectName: $projectName  drawableName: ${nodeInfo.fileName}")
        Node xmlParseResult = nodeInfo.xmlNode
        ShapeInfo shapeInfo = ShapeParseUtil.getShapeInfoByParseNode(xmlParseResult, nodeInfo.fileName)
        LogUtil.logI(TAG, "parse xmlFileName: ${nodeInfo.fileName}  shapeInfo: ${shapeInfo.getJsonString()}")
        return shapeInfo
    }
}