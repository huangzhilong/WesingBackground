package com.tencent.wesing.background.plugin.resource.shape


/**
 * create by zlonghuang on 2021/4/19
 **/

class AttributeInfoParseUtil {


    static AttributeValueInfo getAttributeInfoByParseNode(Node xmlParseResult, String fileName) {
        if (xmlParseResult == null) {
            return null
        }
        AttributeValueInfo attributeInfo = new AttributeValueInfo()
        attributeInfo.fileName = fileName
        parseHeader(xmlParseResult, attributeInfo)
        parseGradient(xmlParseResult, attributeInfo)
        parseCorners(xmlParseResult, attributeInfo)
        parsePadding(xmlParseResult, attributeInfo)
        parseSize(xmlParseResult, attributeInfo)
        parseSolid(xmlParseResult, attributeInfo)
        parseStroke(xmlParseResult, attributeInfo)
        return attributeInfo
    }

    private static void parseHeader(Node xmlParseResult, AttributeValueInfo attributeInfo) {
        // 头
        xmlParseResult.attributes().each {
            attr ->
                if (attr != null && attr.key != null && attr.value != null) {
                    String key = attr.key.toString()
                    String value = attr.value.toString()
                    //LogUtil.logI(TAG, "parseHeader $key  $value")
                    if (key.endsWith("dither")) {
                        attributeInfo.dither = value
                    }
                    if (key.endsWith("shape")) {
                        attributeInfo.shape = value
                    }
                    if (key.endsWith("tint")) {
                        attributeInfo.tint = value
                    }
                }
        }
    }

    private static void parseGradient(Node xmlParseResult,  AttributeValueInfo attributeInfo) {
        if (xmlParseResult.gradient[0] == null) {
            return
        }
        xmlParseResult.gradient[0].attributes().each {
            attr ->
                if (attr != null && attr.key != null && attr.value != null) {
                    String key = attr.key.toString()
                    String value = attr.value.toString()
                    if (key.endsWith("type")) {
                        attributeInfo.type = value
                    }
                    if (key.endsWith("angle")) {
                        attributeInfo.angle = value
                    }
                    if (key.endsWith("centerColor")) {
                        attributeInfo.centerColor = value
                    }
                    if (key.endsWith("startColor")) {
                        attributeInfo.startColor = value
                    }
                    if (key.endsWith("endColor")) {
                        attributeInfo.endColor = value
                    }
                    if (key.endsWith("centerX")) {
                        attributeInfo.centerX = value
                    }
                    if (key.endsWith("centerY")) {
                        attributeInfo.centerY = value
                    }
                    if (key.endsWith("centerY")) {
                        attributeInfo.centerY = value
                    }
                    if (key.endsWith("gradientRadius")) {
                        attributeInfo.gradientRadius = value
                    }
                }
        }
    }

    private static void parseCorners(Node xmlParseResult, AttributeValueInfo attributeInfo) {
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
                        attributeInfo.radius = value
                    }
                    if (key.endsWith("bottomLeftRadius")) {
                        attributeInfo.bottomLeftRadius = value
                    }
                    if (key.endsWith("bottomRightRadius")) {
                        attributeInfo.bottomRightRadius = value
                    }
                    if (key.endsWith("topLeftRadius")) {
                        attributeInfo.topLeftRadius = value
                    }
                    if (key.endsWith("topRightRadius")) {
                        attributeInfo.topRightRadius = value
                    }
                }
        }
    }

    private static void parsePadding(Node xmlParseResult, AttributeValueInfo attributeInfo) {
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
                        attributeInfo.bottom = value
                    }
                    if (key.endsWith("left")) {
                        attributeInfo.left = value
                    }
                    if (key.endsWith("top")) {
                        attributeInfo.top = value
                    }
                    if (key.endsWith("right")) {
                        attributeInfo.right = value
                    }
                }
        }
    }

    private static void parseSize(Node xmlParseResult, AttributeValueInfo attributeInfo) {
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
                        attributeInfo.height = value
                    }
                    if (key.endsWith("width")) {
                        attributeInfo.width = value
                    }
                }
        }
    }

    private static void parseSolid(Node xmlParseResult, AttributeValueInfo attributeInfo) {
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
                        attributeInfo.solidColor = value
                    }
                }
        }
    }

    private static void parseStroke(Node xmlParseResult, AttributeValueInfo attributeInfo) {
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
                        attributeInfo.strokeColor = value
                    }
                    if (key.endsWith("width")) {
                        attributeInfo.strokeWidth = value
                    }
                    if (key.endsWith("dashGap")) {
                        attributeInfo.dashGap = value
                    }
                    if (key.endsWith("dashWidth")) {
                        attributeInfo.dashWidth = value
                    }
                }
        }
    }

}