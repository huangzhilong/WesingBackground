package com.tencent.wesing.background.plugin.resource

import com.tencent.wesing.background.plugin.resource.shape.AttributeMask
import com.tencent.wesing.background.plugin.resource.shape.AttributeValueInfo
import com.tencent.wesing.background.plugin.util.BackgroundUtil
import com.tencent.wesing.background.plugin.util.LogUtil

/**
 * create by zlonghuang on 2021/4/20
 **/

class GenerateShapeConfigUtil {

    static final String TAG = "GenerateShapeConfigUtil"

    static final String JAVA_NAME = "TMEBackgroundShapeConfig"
    static final String JAVA_VALUE_TEMPLATE = "public static final String %s_background_%s = %s;"

    private static File initGenerateConfigJavaFile(String javaPath, String javaClassName) {
        File file = new File(javaPath + File.separator + javaClassName + ".java")
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
    static void generateConfigJavaCode(List<AttributeValueInfo> shapeInfoList, String packageName, String javaPath, String projectName) {
        String classPrefix = projectName.replaceAll(":", "").replace("-", "_").toUpperCase()
        String javaClassName = classPrefix + "_" + JAVA_NAME
        File javaFile = initGenerateConfigJavaFile(javaPath, javaClassName)
        if (!javaFile.exists()) {
            LogUtil.logI(TAG, "generateConfigJavaCode file is not exists!!!")
            return
        }
        List<String> codeList = new ArrayList<>()
        for (int i = 0; i < shapeInfoList.size(); i++) {
            String codeJava = genJavaProperties(shapeInfoList[i], projectName)
            codeList.add(codeJava)
            LogUtil.logI(TAG, "generateConfigJavaCode value: $codeJava")
        }
        javaFile.withWriter('utf-8') { writer ->
            writer.writeLine("package ${packageName};")
            writer.writeLine("import ${packageName}.R;")
            writer.writeLine("public class $javaClassName {")
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
     * 如：
     * public static final String app_param_kk = R.drawable.shape_test_4 + "23546447"(位运算，严格按照Attribute定义属性顺序） + " 123(使用Id的属性（严格按照Attribute定义属性顺序）" + "xxxx,xxxxx"(id的话用+R.color.red)
     *
     */
    private static String genJavaProperties(AttributeValueInfo info, String projectName) {
        String fileName = info.fileName.substring(0, info.fileName.size() - 4) // 去除.xml后缀
        String keyValue = "R.drawable." + fileName
        int attrValue = 0
        int idValue = 0
        StringBuilder stringValue = new StringBuilder()
        if (!BackgroundUtil.isEmpty(info.dither)) {
            attrValue = attrValue | AttributeMask.ditherMask
            boolean isId = BackgroundUtil.isIdValue(info.dither)
            if (isId) {
                idValue = idValue | AttributeMask.ditherMask
            }
            appendAttrValue(info.dither, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.shape)) {
            attrValue = attrValue | AttributeMask.shapeMask
            boolean isId = BackgroundUtil.isIdValue(info.shape)
            if (isId) {
                idValue = idValue | AttributeMask.shapeMask
            }
            appendAttrValue(info.shape, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.tint)) {
            attrValue = attrValue | AttributeMask.tintMask
            boolean isId = BackgroundUtil.isIdValue(info.tint)
            if (isId) {
                idValue = idValue | AttributeMask.tintMask
            }
            appendAttrValue(info.tint, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.radius)) {
            attrValue = attrValue | AttributeMask.radiusMask
            boolean isId = BackgroundUtil.isIdValue(info.radius)
            if (isId) {
                idValue = idValue | AttributeMask.radiusMask
            }
            appendAttrValue(info.radius, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.bottomLeftRadius)) {
            attrValue = attrValue | AttributeMask.bottomLeftRadiusMask
            boolean isId = BackgroundUtil.isIdValue(info.bottomLeftRadius)
            if (isId) {
                idValue = idValue | AttributeMask.bottomLeftRadiusMask
            }
            appendAttrValue(info.bottomLeftRadius, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.bottomRightRadius)) {
            attrValue = attrValue | AttributeMask.bottomRightRadiusMask
            boolean isId = BackgroundUtil.isIdValue(info.bottomRightRadius)
            if (isId) {
                idValue = idValue | AttributeMask.bottomRightRadiusMask
            }
            appendAttrValue(info.bottomRightRadius, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.topLeftRadius)) {
            attrValue = attrValue | AttributeMask.topLeftRadiusMask
            boolean isId = BackgroundUtil.isIdValue(info.topLeftRadius)
            if (isId) {
                idValue = idValue | AttributeMask.topLeftRadiusMask
            }
            appendAttrValue(info.topLeftRadius, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.topRightRadius)) {
            attrValue = attrValue | AttributeMask.topRightRadiusMask
            boolean isId = BackgroundUtil.isIdValue(info.topRightRadius)
            if (isId) {
                idValue = idValue | AttributeMask.topRightRadiusMask
            }
            appendAttrValue(info.topRightRadius, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.type)) {
            attrValue = attrValue | AttributeMask.typeMask
            boolean isId = BackgroundUtil.isIdValue(info.type)
            if (isId) {
                idValue = idValue | AttributeMask.typeMask
            }
            appendAttrValue(info.type, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.angle)) {
            attrValue = attrValue | AttributeMask.angleMask
            boolean isId = BackgroundUtil.isIdValue(info.angle)
            if (isId) {
                idValue = idValue | AttributeMask.angleMask
            }
            appendAttrValue(info.angle, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.centerColor)) {
            attrValue = attrValue | AttributeMask.centerColorMask
            boolean isId = BackgroundUtil.isIdValue(info.centerColor)
            if (isId) {
                idValue = idValue | AttributeMask.centerColorMask
            }
            appendAttrValue(info.centerColor, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.startColor)) {
            attrValue = attrValue | AttributeMask.startColorMask
            boolean isId = BackgroundUtil.isIdValue(info.startColor)
            if (isId) {
                idValue = idValue | AttributeMask.startColorMask
            }
            appendAttrValue(info.startColor, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.endColor)) {
            attrValue = attrValue | AttributeMask.endColorMask
            boolean isId = BackgroundUtil.isIdValue(info.endColor)
            if (isId) {
                idValue = idValue | AttributeMask.endColorMask
            }
            appendAttrValue(info.endColor, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.centerX)) {
            attrValue = attrValue | AttributeMask.centerXMask
            boolean isId = BackgroundUtil.isIdValue(info.centerX)
            if (isId) {
                idValue = idValue | AttributeMask.centerXMask
            }
            appendAttrValue(info.centerX, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.centerY)) {
            attrValue = attrValue | AttributeMask.centerYMask
            boolean isId = BackgroundUtil.isIdValue(info.centerY)
            if (isId) {
                idValue = idValue | AttributeMask.centerYMask
            }
            appendAttrValue(info.centerY, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.gradientRadius)) {
            attrValue = attrValue | AttributeMask.gradientRadiusMask
            boolean isId = BackgroundUtil.isIdValue(info.gradientRadius)
            if (isId) {
                idValue = idValue | AttributeMask.gradientRadiusMask
            }
            appendAttrValue(info.gradientRadius, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.top)) {
            attrValue = attrValue | AttributeMask.topMask
            boolean isId = BackgroundUtil.isIdValue(info.top)
            if (isId) {
                idValue = idValue | AttributeMask.topMask
            }
            appendAttrValue(info.top, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.left)) {
            attrValue = attrValue | AttributeMask.leftMask
            boolean isId = BackgroundUtil.isIdValue(info.left)
            if (isId) {
                idValue = idValue | AttributeMask.leftMask
            }
            appendAttrValue(info.left, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.bottom)) {
            attrValue = attrValue | AttributeMask.bottomMask
            boolean isId = BackgroundUtil.isIdValue(info.bottom)
            if (isId) {
                idValue = idValue | AttributeMask.bottomMask
            }
            appendAttrValue(info.bottom, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.right)) {
            attrValue = attrValue | AttributeMask.rightMask
            boolean isId = BackgroundUtil.isIdValue(info.right)
            if (isId) {
                idValue = idValue | AttributeMask.rightMask
            }
            appendAttrValue(info.right, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.height)) {
            attrValue = attrValue | AttributeMask.heightMask
            boolean isId = BackgroundUtil.isIdValue(info.height)
            if (isId) {
                idValue = idValue | AttributeMask.heightMask
            }
            appendAttrValue(info.height, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.width)) {
            attrValue = attrValue | AttributeMask.widthMask
            boolean isId = BackgroundUtil.isIdValue(info.width)
            if (isId) {
                idValue = idValue | AttributeMask.widthMask
            }
            appendAttrValue(info.width, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.solidColor)) {
            attrValue = attrValue | AttributeMask.solidColorMask
            boolean isId = BackgroundUtil.isIdValue(info.solidColor)
            if (isId) {
                idValue = idValue | AttributeMask.solidColorMask
            }
            appendAttrValue(info.solidColor, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.strokeColor)) {
            attrValue = attrValue | AttributeMask.strokeColorMask
            boolean isId = BackgroundUtil.isIdValue(info.strokeColor)
            if (isId) {
                idValue = idValue | AttributeMask.strokeColorMask
            }
            appendAttrValue(info.strokeColor, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.strokeWidth)) {
            attrValue = attrValue | AttributeMask.strokeWidthMask
            boolean isId = BackgroundUtil.isIdValue(info.strokeWidth)
            if (isId) {
                idValue = idValue | AttributeMask.strokeWidthMask
            }
            appendAttrValue(info.strokeWidth, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.dashGap)) {
            attrValue = attrValue | AttributeMask.dashGapMask
            boolean isId = BackgroundUtil.isIdValue(info.dashGap)
            if (isId) {
                idValue = idValue | AttributeMask.dashGapMask
            }
            appendAttrValue(info.dashGap, isId, stringValue)
        }
        if (!BackgroundUtil.isEmpty(info.dashWidth)) {
            attrValue = attrValue | AttributeMask.dashWidthMask
            boolean isId = BackgroundUtil.isIdValue(info.dashWidth)
            if (isId) {
                idValue = idValue | AttributeMask.dashWidthMask
            }
            appendAttrValue(info.dashWidth, isId, stringValue)
        }
        StringBuilder value = new StringBuilder()
        value.append(keyValue)
            .append("+").append("\",\"").append("+")
            .append(attrValue)
            .append("+").append("\",\"").append("+")
            .append(idValue)
            .append("+").append("\",\"").append("+")
            .append(stringValue.toString().substring(0, stringValue.length() - 5)) //减去后面多与的加号逗号
        return String.format(JAVA_VALUE_TEMPLATE, projectName, fileName, value.toString())
    }

    private static void appendAttrValue(String value, boolean isId, StringBuilder stringBuilder) {
        if (isId) {
            if (value.startsWith("@android")) {
                //系统id  @android:color/background_dark  -> android.R.color.background_dark
                value = value.replace("@android:", "android.R.").replace("/", ".")
            } else {
                //项目id @dimen/top_height -> R.dimen.top_height
                value = value.replace("@", "R.").replace("/", ".")
            }
            stringBuilder.append(value).append("+").append("\",\"").append("+")
        } else {
            stringBuilder.append("\"" + value +"\"").append("+").append("\",\"").append("+")
        }
    }
}