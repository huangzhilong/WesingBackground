package com.tencent.wesing.background.plugin.code

import com.tencent.wesing.background.plugin.shape.ShapeInfo
import com.tencent.wesing.background.plugin.util.LogUtil
import groovy.json.JsonSlurper
import org.gradle.api.Project

/**
 * create by zlonghuang on 2021/4/20
 **/

class GenerateShapeConfigUtil {

    static final String TAG = "GenerateShapeConfigUtil"

    static final String JAVA_TEMPLATE = "public static final Object[] param%s  = { %s }"

    private static File initGenerateConfigJavaFile(Project project) {
        File file = new File("./ShapeBackground.java")
        if (file.exists()) {
            file.delete()
        }
        // 先创建目录
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs()
        }
        file.createNewFile()
        return file
    }

    /**
     * 生成对应属性代码
     */
    static void generateConfigJavaCode(Project project, List<ShapeInfo> shapeInfoList) {
        File javaFile = initGenerateConfigJavaFile(project)
        if (!javaFile.exists()) {
            LogUtil.logI(TAG, "generateConfigJavaCode file is not exists!!!")
            return
        }
        List<String> codeList = new ArrayList<>()
        for (int i = 0; i < shapeInfoList.size(); i++) {
            codeList.add(genJavaProperties(i + 1, shapeInfoList[i]))
        }
        javaFile.withWriter('utf-8') { writer ->
            writer.writeLine("package com.yy.base.env;")
            writer.writeLine("public class ShapeConfig {")
            codeList.each { codeLine ->
                writer.writeLine(codeLine)
            }
            writer.writeLine("}")
        }
    }


    // TODO 先写死debug
    private static String getJavaFilePath(Project project) {
        String path = project.getBuildDir().absolutePath + File.separator + "generated" + File.separator + "source" + File.separator + "shapeConfig" + File.separator + "debug"
            + File.separator + "com" + File.separator + "wesing" + File.separator + "background" + File.separator + "ShapeBackground.java"
        return path
    }

    /**
     *
     * @param id 标示id
     * @param shapeInfo
     * @return 生成的代码String
     */
    private static String genJavaProperties(int id, ShapeInfo shapeInfo) {
        String str = shapeInfo.getJsonString()
        def jsonSlurper = new JsonSlurper()
        def jsonObject = jsonSlurper.parseText(str)
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder.append(id).append(", ")
        stringBuilder.append("\"" + shapeInfo.fileName + "\"").append(", ")
        ((Map) jsonObject).each {
            LogUtil.logI(TAG, "genJavaProperties key: ${it.key}  value: ${it.value}")
            stringBuilder.append("\"" + it.key + "\"").append(", ")
            String value = it.value.toString()
            if (value.toString().startsWith("@android")) {
                //系统id  @android:color/background_dark  -> android.R.color.background_dark
                value = value.replace("@android:", "android.R.").replace("/", ".")
                stringBuilder.append(value).append(", ")
            } else if (it.value.toString().startsWith("@")) {
                //项目id @dimen/top_height -> R.dimen.top_height
                value = value.replace("@", "R.").replace("/", ".")
                stringBuilder.append(value).append(", ")
            } else {
                stringBuilder.append("\"" + it.value + "\"").append(", ")
            }
        }
        return String.format(JAVA_TEMPLATE, id, stringBuilder.toString())
    }
}