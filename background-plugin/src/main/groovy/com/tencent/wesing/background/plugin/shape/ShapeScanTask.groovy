package com.tencent.wesing.background.plugin.shape

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.BaseVariant
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

    ShapeScanTask() {
        LogUtil.logI(TAG, "projectName: ${project.name} create ShapeScanTask!!")
        mScanProject.clear()
        isRunning = false
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
        collection.all { variant ->
            LogUtil.logI(TAG, "-------- variant: $variant.name")
            if (variant.name == "debug") { //先写死debug，之后要根据具体构建方式判断
                variant.sourceSets?.each { sourceSet ->
                    LogUtil.logI(TAG, "sourceSets.${sourceSet.name} -->")
                    if (sourceSet.resDirectories.empty) return
                    XmlParser mXmlParser = new XmlParser()
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

        drawableNodeXmlList = doFilterRepeatXmlFile(drawableNodeXmlList)
        if (BackgroundUtil.getCollectSize(drawableNodeXmlList) > 0)  {
            for (int i = 0; i < drawableNodeXmlList.size(); i++) {
                collectShapeXml(drawableNodeXmlList.get(i), projectName)
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


    def collectShapeXml(XmlNodeInfo nodeInfo, String projectName) {
        if (nodeInfo == null || nodeInfo.xmlNode ==  null) {
            return
        }
        LogUtil.logI(TAG, "collectShapeXml projectName: $projectName  drawableName: ${nodeInfo.fileName}")
        Node xmlParseResult = nodeInfo.xmlNode
        if (SHAPE_TAG == xmlParseResult.name()) {
            ShapeInfo shapeInfo = ShapeParseUtil.getShapeInfoByParseNode(xmlParseResult)
            LogUtil.logI(TAG, "parse xmlFileName: ${nodeInfo.fileName}  shapeInfo: ${shapeInfo.getJsonString()} ")
        }
    }
}