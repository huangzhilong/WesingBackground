package com.tencent.wesing.background.plugin.shape

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import com.tencent.wesing.background.plugin.util.LogUtil
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
    private XmlParser mXmlParser = new XmlParser()

    ShapeScanTask() {
        LogUtil.logI(TAG, "projectName: ${project.name} create ShapeScanTask!!")
        mScanProject.clear()
    }

    @TaskAction
    def run() {
        getSourcesDirs(project.rootProject)
    }

    def getSourcesDirs(Project root) {
        List<File> dirs = []
        Set<Project> projectSet = root.getAllprojects()
        for (int i = 0; i < projectSet.size(); i++) {
            Project temp = projectSet[i]
            if (temp.plugins.hasPlugin(AppPlugin)) {
                getSourcesDirsWithVariant((DomainObjectCollection<BaseVariant>) temp.android.applicationVariants, temp.name)
            } else if (temp.plugins.hasPlugin(LibraryPlugin)) {
                getSourcesDirsWithVariant((DomainObjectCollection<BaseVariant>) temp.android.libraryVariants, temp.name)
            }
        }
        return dirs
    }

    def getSourcesDirsWithVariant(DomainObjectCollection<BaseVariant> collection, String projectName) {
        collection.all { variant ->
            variant.sourceSets?.each { sourceSet ->
                if (sourceSet.resDirectories.empty) return
                sourceSet.resDirectories.each { res ->
                    if (res.exists()) {
                        LogUtil.logI(TAG, "projectName: ${projectName}   res: ${res.path}")
                        res.eachDir {
                            //收集所有drawable目录里的shape中定义的xml
                            if (it.directory && (it.name.startsWith("drawable"))) {
                                collectShapeXml(it, projectName)
                            }
                        }
                    }
                }
            }
        }
    }

    def collectShapeXml(File drawableDir, String projectName) {
        if (drawableDir == null || drawableDir.listFiles() == null || drawableDir.listFiles().size() == 0) {
            LogUtil.logI(TAG, "collectShapeXml projectName: $projectName  drawableName: ${drawableDir.name} is empty!!")
            return
        }
        LogUtil.logI(TAG, "collectShapeXml projectName: $projectName  drawableName: ${drawableDir.name}  size: ${drawableDir.listFiles().size()}")
        List<File> drawableList = drawableDir.listFiles()
        for (int i = 0; i < drawableList.size(); i++) {
            File xmlFile = drawableList[i]
            if (xmlFile == null || xmlFile.size() <= 0 || !xmlFile.name.endsWith(".xml"))  {
                continue
            }
            Node xmlParseResult = mXmlParser.parse(xmlFile)//处理shape标签
            if (xmlParseResult == null) {
                LogUtil.logI(TAG, "collectShapeXml parse error node is null xmlFileName: ${xmlFile.name}")
                return
            }
            if (SHAPE_TAG == xmlParseResult.name()) {
                LogUtil.logI(TAG, "parse xmlFileName: ${xmlFile.name}")
                ShapeInfo shapeInfo = ShapeParseUtil.getShapeInfoByParseNode(xmlParseResult)
            }
        }
    }
}