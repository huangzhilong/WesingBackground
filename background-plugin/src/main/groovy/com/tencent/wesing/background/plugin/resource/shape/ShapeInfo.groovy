package com.tencent.wesing.background.plugin.resource.shape

import com.tencent.wesing.background.plugin.util.BackgroundUtil

/**
 * create by zlonghuang on 2021/4/19
 **/

class ShapeInfo {

    //文件名
    public String fileName

    //头
    String dither   //"false|true" 将在位图的像素配置与屏幕不同时（例如：ARGB 8888 位图和 RGB 565 屏幕）启用位图的抖动；值为“false”时则停用抖动。默认值为 true。
    String shape       //"rectangle|line|oval|ring" 分别为矩形、线、椭圆、环。默认为矩形rectangle
    String innerRadius  //"integer" shape为ring时可用，内环半径
    String innerRadiusRatio  //"float"  shape为ring时可用，内环的厚度比，即环的宽度比表示内环半径，默认为3，可被innerRadius值覆盖
    String thickness //"integer" shape为ring时可用，环的厚度
    String thicknessRatio  //"float" shape为ring时可用，环的厚度比，即环的宽度比表示环的厚度，默认为9，可被thickness值覆盖
    String tint  //"color" 给shape着色
    String tintMod    //"src_in|src_atop|src_over|add|multiply|screen" // 着色类型
    String useLevel   //"false|true" 较少用，一般设为false，否则图形不显示。为true时可在LevelListDrawable使用
    String visible    //"false|true"

    //  圆角
    String radius   //"integer" 圆角半径，该值设置时下面四个属性失效
    String bottomLeftRadius    //"integer"  // 左下角圆角半径
    String bottomRightRadius   //"integer" // 右下角圆角半径
    String topLeftRadius       //"integer"     // 左上角圆角半径
    String topRightRadius      //"integer"    // 右上角圆角半径

    // 渐变
    String gradientUseLevel  //"false|true"       // 与上面shape中该属性的一致
    String type    //"linear|radial|sweep"  渐变类型，分别为线性、放射性、扫描性渐变，默认为线性渐变linear
    String angle    //"integer"             // 渐变角度，当上面type为线性渐变linear时有效。角度为45的倍数，0度时从左往右渐变，角度方向逆时针
    String centerColor   //"color"         // 渐变中间位置颜色
    String startColor    //"color"          // 渐变开始位置颜色
    String endColor     //"color"            // 渐变结束位置颜色
    String centerX      //"float"             // type为放射性渐变radial时有效，设置渐变中心的X坐标，取值区间[0,1]，默认为0.5，即中心位置
    String centerY      //"float"             // type为放射性渐变radial时有效，设置渐变中心的Y坐标，取值区间[0,1]，默认为0.5，即中心位置
    String gradientRadius // "integer"    // type为放射性渐变radial时有效，渐变的半径

    // 内边距
    String bottom  //"integer"  // 设置底部边距
    String left   //"integer"    // 左边边距
    String right  //"integer"   // 右边
    String top    //"integer"     // 顶部

    // 大小
    String height  //"integer"  // 宽度
    String width  //"integer"   // 高度

    //-- 填充 -->
    String solidColor  //"color"     // shape的填充色


    //-- 描边 -->
    String strokeColor  //"color" 描边的颜色
    String strokeWidth  //"integer"     // 描边的宽度
    String dashGap     //"integer"   // 虚线间隔
    String dashWidth   //integer" // 虚线宽度


    String getJsonString() {
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder.append("{")
        boolean addPoint = false
        if (!BackgroundUtil.isEmpty(dither)) {
            stringBuilder.append("\"dither\": \"" + dither + "\"")
            addPoint = true
        }
        if (!BackgroundUtil.isEmpty(shape)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"shape\": \"" + shape + "\"")
        }
        if (!BackgroundUtil.isEmpty(innerRadius)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"innerRadius\": \"" + innerRadius + "\"")
        }
        if (!BackgroundUtil.isEmpty(innerRadiusRatio)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"innerRadiusRatio\": \"" + innerRadiusRatio + "\"")
        }
        if (!BackgroundUtil.isEmpty(thickness)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"thickness\": \"" + thickness + "\"")
        }
        if (!BackgroundUtil.isEmpty(thicknessRatio)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"thicknessRatio\": \"" + thicknessRatio + "\"")
        }
        if (!BackgroundUtil.isEmpty(tint)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"tint\": \"" + tint + "\"")
        }
        if (!BackgroundUtil.isEmpty(tintMod)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"tintMod\": \"" + tintMod + "\"")
        }
        if (!BackgroundUtil.isEmpty(useLevel)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"useLevel\": \"" + useLevel + "\"")
        }
        if (!BackgroundUtil.isEmpty(visible)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"visible\": \"" + visible + "\"")
        }
        if (!BackgroundUtil.isEmpty(radius)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"radius\": \"" + radius + "\"")
        }
        if (!BackgroundUtil.isEmpty(bottomLeftRadius)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"bottomLeftRadius\": \"" + bottomLeftRadius + "\"")
        }
        if (!BackgroundUtil.isEmpty(bottomRightRadius)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"bottomRightRadius\": \"" + bottomRightRadius + "\"")
        }
        if (!BackgroundUtil.isEmpty(topLeftRadius)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"topLeftRadius\": \"" + topLeftRadius + "\"")
        }
        if (!BackgroundUtil.isEmpty(topRightRadius)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"topRightRadius\": \"" + topRightRadius + "\"")
        }
        if (!BackgroundUtil.isEmpty(gradientUseLevel)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"gradientUseLevel\": \"" + gradientUseLevel + "\"")
        }
        if (!BackgroundUtil.isEmpty(type)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"type\": \"" + type + "\"")
        }
        if (!BackgroundUtil.isEmpty(angle)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"angle\": \"" + angle + "\"")
        }
        if (!BackgroundUtil.isEmpty(centerColor)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"centerColor\": \"" + centerColor + "\"")
        }
        if (!BackgroundUtil.isEmpty(startColor)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"startColor\": \"" + startColor + "\"")
        }
        if (!BackgroundUtil.isEmpty(endColor)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"endColor\": \"" + endColor + "\"")
        }
        if (!BackgroundUtil.isEmpty(centerX)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"centerX\": \"" + centerX + "\"")
        }
        if (!BackgroundUtil.isEmpty(centerY)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"centerY\": \"" + centerY + "\"")
        }
        if (!BackgroundUtil.isEmpty(gradientRadius)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"gradientRadius\": \"" + gradientRadius + "\"")
        }
        if (!BackgroundUtil.isEmpty(bottom)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"bottom\": \"" + bottom + "\"")
        }
        if (!BackgroundUtil.isEmpty(top)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"top\": \"" + top + "\"")
        }
        if (!BackgroundUtil.isEmpty(left)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"left\": \"" + left + "\"")
        }
        if (!BackgroundUtil.isEmpty(right)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"right\": \"" + right + "\"")
        }
        if (!BackgroundUtil.isEmpty(height)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"height\": \"" + height + "\"")
        }
        if (!BackgroundUtil.isEmpty(width)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"width\": \"" + width + "\"")
        }
        if (!BackgroundUtil.isEmpty(solidColor)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"solidColor\": \"" + solidColor + "\"")
        }
        if (!BackgroundUtil.isEmpty(strokeColor)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"strokeColor\": \"" + strokeColor + "\"")
        }
        if (!BackgroundUtil.isEmpty(strokeWidth)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"strokeWidth\": \"" + strokeWidth + "\"")
        }
        if (!BackgroundUtil.isEmpty(dashGap)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            addPoint = true
            stringBuilder.append("\"dashGap\": \"" + dashGap + "\"")
        }
        if (!BackgroundUtil.isEmpty(dashWidth)) {
            if (addPoint) {
                stringBuilder.append(", ")
            }
            stringBuilder.append("\"dashWidth\": \"" + dashWidth + "\"")
        }
        stringBuilder.append("}")
        return stringBuilder.toString()
    }
}