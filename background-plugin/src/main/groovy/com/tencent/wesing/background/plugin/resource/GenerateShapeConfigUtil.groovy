package com.tencent.wesing.background.plugin.resource

import com.tencent.wesing.background.plugin.resource.shape.ShapeInfo
import com.tencent.wesing.background.plugin.util.LogUtil
import groovy.json.JsonSlurper

/**
 * create by zlonghuang on 2021/4/20
 **/

class GenerateShapeConfigUtil {

    static final String TAG = "GenerateShapeConfigUtil"

    static final String JAVA_NAME = "BackgroundShapeConfig"
    static final String JAVA_VALUE_TEMPLATE = "public static final String background_param%s = %s;"

    private static File initGenerateConfigJavaFile(String javaPath) {
        File file = new File(javaPath + File.separator + JAVA_NAME + ".java")
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
    static void generateConfigJavaCode(List<ShapeInfo> shapeInfoList, String packageName, String javaPath) {
        File javaFile = initGenerateConfigJavaFile(javaPath)
        if (!javaFile.exists()) {
            LogUtil.logI(TAG, "generateConfigJavaCode file is not exists!!!")
            return
        }
        List<String> codeList = new ArrayList<>()
        for (int i = 0; i < shapeInfoList.size(); i++) {
            String codeJava = genJavaProperties(i + 1, shapeInfoList[i])
            codeList.add(codeJava)
            LogUtil.logI(TAG, "generateConfigJavaCode value: $codeJava")
        }
        javaFile.withWriter('utf-8') { writer ->
            writer.writeLine("package ${packageName};")
            writer.writeLine("import ${packageName}.R;")
            writer.writeLine("public class $JAVA_NAME {")
            codeList.each { codeLine ->
                writer.writeLine(codeLine)
            }
            writer.writeLine("}")
        }
    }

    /**
     *
     * @param id 标示id
     * @param shapeInfo
     * @return 生成的代码String
     *
     */
    private static String genJavaProperties(int id, ShapeInfo shapeInfo) {
        String str = shapeInfo.getJsonString()
        def jsonSlurper = new JsonSlurper()
        def jsonObject = jsonSlurper.parseText(str)
        StringBuilder stringBuilder = new StringBuilder()
        String fileName = shapeInfo.fileName.substring(0, shapeInfo.fileName.size() - 4) // 去除.xml后缀
        String keyValue = "R.drawable." + fileName
        stringBuilder.append(keyValue).append("+")append("\"" +"," + "\"")
        //public static final String param3 = "bottom:10dp," + "top:10dp," + "dither: " + R.bool.yes + "," + "shape: oval," + "angle:" + R.integer.yyyy + "startColor: " + android.R.color.background_light;
        ((Map) jsonObject).each {
            stringBuilder.append("+")
            stringBuilder.append("\"" + it.key).append(":")
            String value = it.value.toString()
            if (value.toString().startsWith("@android")) {
                stringBuilder.append("1:")  //id类型使用1
                //系统id  @android:color/background_dark  -> android.R.color.background_dark
                value = value.replace("@android:", "android.R.").replace("/", ".")
                stringBuilder.append("\"" + "+").append(value).append("+" + "\"" + "," + "\"")
            } else if (it.value.toString().startsWith("@")) {
                stringBuilder.append("1:")
                //项目id @dimen/top_height -> R.dimen.top_height
                value = value.replace("@", "R.").replace("/", ".")
                stringBuilder.append("\"" + "+").append(value).append("+" + "\"" + "," + "\"")
            } else {
                stringBuilder.append("0:")
                stringBuilder.append(it.value).append("," + "\"")
            }
        }
        return String.format(JAVA_VALUE_TEMPLATE, id, stringBuilder.toString())
    }
}