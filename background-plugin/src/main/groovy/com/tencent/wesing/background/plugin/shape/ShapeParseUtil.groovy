package com.tencent.wesing.background.plugin.shape

import com.tencent.wesing.background.plugin.util.LogUtil

/**
 * create by zlonghuang on 2021/4/19
 **/

class ShapeParseUtil {

    static final TAG = "ShapeParseUtil"

    static ShapeInfo getShapeInfoByParseNode(Node xmlParseResult) {
        if (xmlParseResult == null) {
            return null
        }
        ShapeInfo shapeInfo = new ShapeInfo()
        parseHeader(xmlParseResult, shapeInfo)
        parseGradient(xmlParseResult, shapeInfo)
        parseCorners(xmlParseResult, shapeInfo)
        parsePadding(xmlParseResult, shapeInfo)
        parseSize(xmlParseResult, shapeInfo)
        parseSolid(xmlParseResult, shapeInfo)
        parseStroke(xmlParseResult, shapeInfo)
    }

    private static void parseHeader(Node xmlParseResult, ShapeInfo shapeInfo) {
        // 头
        xmlParseResult.attributes().each {
            attr ->
                if (attr != null && attr.key != null && attr.value != null) {
                    String key = attr.key.toString()
                    String value = attr.value.toString()
                    LogUtil.logI(TAG, "parseHeader $key  $value")
                    if (key.endsWith("dither")) {
                        shapeInfo.dither = createShapeValueInfo(value)
                    }
                    if (key.endsWith("shape")) {
                        shapeInfo.shape = createShapeValueInfo(value)
                    }
                    if (key.endsWith("innerRadius")) {
                        shapeInfo.innerRadius = createShapeValueInfo(value)
                    }
                    if (key.endsWith("innerRadiusRatio")) {
                        shapeInfo.innerRadiusRatio = createShapeValueInfo(value)
                    }
                    if (key.endsWith("thickness")) {
                        shapeInfo.thickness = createShapeValueInfo(value)
                    }
                    if (key.endsWith("thicknessRatio")) {
                        shapeInfo.thicknessRatio = createShapeValueInfo(value)
                    }
                    if (key.endsWith("tint")) {
                        shapeInfo.tint = createShapeValueInfo(value)
                    }
                    if (key.endsWith("tintMode")) {
                        shapeInfo.tintMod = createShapeValueInfo(value)
                    }
                    if (key.endsWith("useLevel")) {
                        shapeInfo.useLevel = createShapeValueInfo(value)
                    }
                    if (key.endsWith("visible")) {
                        shapeInfo.visible = createShapeValueInfo(value)
                    }
                }
        }
    }

    private static void parseGradient(Node xmlParseResult, ShapeInfo shapeInfo) {
        if (xmlParseResult.gradient[0] == null) {
            return
        }
        xmlParseResult.gradient[0].attributes().each {
            attr ->
                if (attr != null && attr.key != null && attr.value != null) {
                    String key = attr.key.toString()
                    String value = attr.value.toString()
                    LogUtil.logI(TAG, "parseGradient $key  $value")
                    if (key.endsWith("type")) {
                        shapeInfo.type = createShapeValueInfo(value)
                    }
                    if (key.endsWith("useLevel")) {
                        shapeInfo.gradientUseLevel = createShapeValueInfo(value)
                    }
                    if (key.endsWith("angle")) {
                        shapeInfo.angle = createShapeValueInfo(value)
                    }
                    if (key.endsWith("centerColor")) {
                        shapeInfo.centerColor = createShapeValueInfo(value)
                    }
                    if (key.endsWith("startColor")) {
                        shapeInfo.startColor = createShapeValueInfo(value)
                    }
                    if (key.endsWith("endColor")) {
                        shapeInfo.endColor = createShapeValueInfo(value)
                    }
                    if (key.endsWith("centerX")) {
                        shapeInfo.centerX = createShapeValueInfo(value)
                    }
                    if (key.endsWith("centerY")) {
                        shapeInfo.centerY = createShapeValueInfo(value)
                    }
                    if (key.endsWith("centerY")) {
                        shapeInfo.centerY = createShapeValueInfo(value)
                    }
                    if (key.endsWith("gradientRadius")) {
                        shapeInfo.gradientRadius = createShapeValueInfo(value)
                    }
                }
        }
    }

    private static void parseCorners(Node xmlParseResult, ShapeInfo shapeInfo) {
        if (xmlParseResult.corners[0] == null) {
            return
        }
        xmlParseResult.corners[0].attributes().each {
            attr ->
                if (attr != null && attr.key != null && attr.value != null) {
                    String key = attr.key.toString()
                    String value = attr.value.toString()
                    LogUtil.logI(TAG, "parseCorners $key  $value")
                    if (key.endsWith("radius")) {
                        shapeInfo.radius = createShapeValueInfo(value)
                    }
                    if (key.endsWith("bottomLeftRadius")) {
                        shapeInfo.bottomLeftRadius = createShapeValueInfo(value)
                    }
                    if (key.endsWith("bottomRightRadius")) {
                        shapeInfo.bottomRightRadius = createShapeValueInfo(value)
                    }
                    if (key.endsWith("topLeftRadius")) {
                        shapeInfo.topLeftRadius = createShapeValueInfo(value)
                    }
                    if (key.endsWith("topRightRadius")) {
                        shapeInfo.topRightRadius = createShapeValueInfo(value)
                    }
                }
        }
    }

    private static void parsePadding(Node xmlParseResult, ShapeInfo shapeInfo) {
        if (xmlParseResult.padding[0] == null) {
            return
        }
        xmlParseResult.padding[0].attributes().each {
            attr ->
                if (attr != null && attr.key != null && attr.value != null) {
                    String key = attr.key.toString()
                    String value = attr.value.toString()
                    LogUtil.logI(TAG, "parsePadding $key  $value")
                    if (key.endsWith("bottom")) {
                        shapeInfo.bottom = createShapeValueInfo(value)
                    }
                    if (key.endsWith("left")) {
                        shapeInfo.left = createShapeValueInfo(value)
                    }
                    if (key.endsWith("top")) {
                        shapeInfo.top = createShapeValueInfo(value)
                    }
                    if (key.endsWith("right")) {
                        shapeInfo.right = createShapeValueInfo(value)
                    }
                }
        }
    }

    private static void parseSize(Node xmlParseResult, ShapeInfo shapeInfo) {
        if (xmlParseResult.size[0] == null) {
            return
        }
        xmlParseResult.size[0].attributes().each {
            attr ->
                if (attr != null && attr.key != null && attr.value != null) {
                    String key = attr.key.toString()
                    String value = attr.value.toString()
                    LogUtil.logI(TAG, "parseSize $key  $value")
                    if (key.endsWith("height")) {
                        shapeInfo.height = createShapeValueInfo(value)
                    }
                    if (key.endsWith("width")) {
                        shapeInfo.height = createShapeValueInfo(value)
                    }
                }
        }
    }

    private static void parseSolid(Node xmlParseResult, ShapeInfo shapeInfo) {
        if (xmlParseResult.solid[0] == null) {
            return
        }
        xmlParseResult.solid[0].attributes().each {
            attr ->
                if (attr != null && attr.key != null && attr.value != null) {
                    String key = attr.key.toString()
                    String value = attr.value.toString()
                    LogUtil.logI(TAG, "parseSolid $key  $value")
                    if (key.endsWith("color")) {
                        shapeInfo.solidColor = createShapeValueInfo(value)
                    }
                }
        }
    }

    private static void parseStroke(Node xmlParseResult, ShapeInfo shapeInfo) {
        if (xmlParseResult.stroke[0] == null) {
            return
        }
        xmlParseResult.stroke[0].attributes().each {
            attr ->
                if (attr != null && attr.key != null && attr.value != null) {
                    String key = attr.key.toString()
                    String value = attr.value.toString()
                    LogUtil.logI(TAG, "parseStroke $key  $value")
                    if (key.endsWith("color")) {
                        shapeInfo.strokeColor = createShapeValueInfo(value)
                    }
                    if (key.endsWith("width")) {
                        shapeInfo.strokeWidth = createShapeValueInfo(value)
                    }
                    if (key.endsWith("dashGap")) {
                        shapeInfo.dashGap = createShapeValueInfo(value)
                    }
                    if (key.endsWith("dashWidth")) {
                        shapeInfo.dashWidth = createShapeValueInfo(value)
                    }
                }
        }
    }

    private static ShapeValueInfo createShapeValueInfo(String value) {
        ShapeValueInfo info = new ShapeValueInfo()
        //使用id
        if (value.startsWith("@")) {
            info.id = value
        } else {
            info.value = value
        }
        LogUtil.logI(TAG, "createShapeValueInfo value: $value")
        return info
    }
}