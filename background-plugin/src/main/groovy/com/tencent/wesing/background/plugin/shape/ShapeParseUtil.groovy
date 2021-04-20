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
        return shapeInfo
    }

    private static void parseHeader(Node xmlParseResult, ShapeInfo shapeInfo) {
        // 头
        xmlParseResult.attributes().each {
            attr ->
                if (attr != null && attr.key != null && attr.value != null) {
                    String key = attr.key.toString()
                    String value = attr.value.toString()
                    //LogUtil.logI(TAG, "parseHeader $key  $value")
                    if (key.endsWith("dither")) {
                        shapeInfo.dither = value
                    }
                    if (key.endsWith("shape")) {
                        shapeInfo.shape = value
                    }
                    if (key.endsWith("innerRadius")) {
                        shapeInfo.innerRadius = value
                    }
                    if (key.endsWith("innerRadiusRatio")) {
                        shapeInfo.innerRadiusRatio = value
                    }
                    if (key.endsWith("thickness")) {
                        shapeInfo.thickness = value
                    }
                    if (key.endsWith("thicknessRatio")) {
                        shapeInfo.thicknessRatio = value
                    }
                    if (key.endsWith("tint")) {
                        shapeInfo.tint = value
                    }
                    if (key.endsWith("tintMode")) {
                        shapeInfo.tintMod = value
                    }
                    if (key.endsWith("useLevel")) {
                        shapeInfo.useLevel = value
                    }
                    if (key.endsWith("visible")) {
                        shapeInfo.visible = value
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
                    //LogUtil.logI(TAG, "parseGradient $key  $value")
                    if (key.endsWith("type")) {
                        shapeInfo.type = value
                    }
                    if (key.endsWith("useLevel")) {
                        shapeInfo.gradientUseLevel = value
                    }
                    if (key.endsWith("angle")) {
                        shapeInfo.angle = value
                    }
                    if (key.endsWith("centerColor")) {
                        shapeInfo.centerColor = value
                    }
                    if (key.endsWith("startColor")) {
                        shapeInfo.startColor = value
                    }
                    if (key.endsWith("endColor")) {
                        shapeInfo.endColor = value
                    }
                    if (key.endsWith("centerX")) {
                        shapeInfo.centerX = value
                    }
                    if (key.endsWith("centerY")) {
                        shapeInfo.centerY = value
                    }
                    if (key.endsWith("centerY")) {
                        shapeInfo.centerY = value
                    }
                    if (key.endsWith("gradientRadius")) {
                        shapeInfo.gradientRadius = value
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
                    //LogUtil.logI(TAG, "parseCorners $key  $value")
                    if (key.endsWith("radius")) {
                        shapeInfo.radius = value
                    }
                    if (key.endsWith("bottomLeftRadius")) {
                        shapeInfo.bottomLeftRadius = value
                    }
                    if (key.endsWith("bottomRightRadius")) {
                        shapeInfo.bottomRightRadius = value
                    }
                    if (key.endsWith("topLeftRadius")) {
                        shapeInfo.topLeftRadius = value
                    }
                    if (key.endsWith("topRightRadius")) {
                        shapeInfo.topRightRadius = value
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
                    //LogUtil.logI(TAG, "parsePadding $key  $value")
                    if (key.endsWith("bottom")) {
                        shapeInfo.bottom = value
                    }
                    if (key.endsWith("left")) {
                        shapeInfo.left = value
                    }
                    if (key.endsWith("top")) {
                        shapeInfo.top = value
                    }
                    if (key.endsWith("right")) {
                        shapeInfo.right = value
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
                    //LogUtil.logI(TAG, "parseSize $key  $value")
                    if (key.endsWith("height")) {
                        shapeInfo.height = value
                    }
                    if (key.endsWith("width")) {
                        shapeInfo.width = value
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
                    //LogUtil.logI(TAG, "parseSolid $key  $value")
                    if (key.endsWith("color")) {
                        shapeInfo.solidColor = value
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
                    //LogUtil.logI(TAG, "parseStroke $key  $value")
                    if (key.endsWith("color")) {
                        shapeInfo.strokeColor = value
                    }
                    if (key.endsWith("width")) {
                        shapeInfo.strokeWidth = value
                    }
                    if (key.endsWith("dashGap")) {
                        shapeInfo.dashGap = value
                    }
                    if (key.endsWith("dashWidth")) {
                        shapeInfo.dashWidth = value
                    }
                }
        }
    }

}