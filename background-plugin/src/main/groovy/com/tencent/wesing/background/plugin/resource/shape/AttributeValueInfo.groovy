package com.tencent.wesing.background.plugin.resource.shape;

/**
 * create by zlonghuang on 2021/5/13
 * 
 * 用于存储xml的值，都以String存储
 **/

class AttributeValueInfo {

    //文件名
    public String fileName;

    //头
    String dither;  
    String shape;
    String tint;
    
    //  圆角
    String radius;
    String bottomLeftRadius;
    String bottomRightRadius;
    String topLeftRadius;
    String topRightRadius;

    // 渐变
    String gradientUseLevel;
    String type;
    String angle;
    String centerColor;
    String startColor;
    String endColor;
    String centerX;
    String centerY;
    String gradientRadius;

    // 内边距
    String bottom;
    String left;
    String right;
    String top;

    // 大小
    String height;
    String width;

    //-- 填充 -->
    String solidColor;


    //-- 描边 -->
    String strokeColor;
    String strokeWidth;
    String dashGap;
    String dashWidth;


    String getJsonString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        boolean addPoint = false;
        if (!isEmpty(dither)) {
            stringBuilder.append("\"dither\": \"" + dither + "\"");
            addPoint = true;
        }
        if (!isEmpty(shape)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"shape\": \"" + shape + "\"");
        }
        if (!isEmpty(tint)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"tint\": \"" + tint + "\"");
        }
        if (!isEmpty(radius)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"radius\": \"" + radius + "\"");
        }
        if (!isEmpty(bottomLeftRadius)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"bottomLeftRadius\": \"" + bottomLeftRadius + "\"");
        }
        if (!isEmpty(bottomRightRadius)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"bottomRightRadius\": \"" + bottomRightRadius + "\"");
        }
        if (!isEmpty(topLeftRadius)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"topLeftRadius\": \"" + topLeftRadius + "\"");
        }
        if (!isEmpty(topRightRadius)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"topRightRadius\": \"" + topRightRadius + "\"");
        }
        if (!isEmpty(gradientUseLevel)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"gradientUseLevel\": \"" + gradientUseLevel + "\"");
        }
        if (!isEmpty(type)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"type\": \"" + type + "\"");
        }
        if (!isEmpty(angle)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"angle\": \"" + angle + "\"");
        }
        if (!isEmpty(centerColor)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"centerColor\": \"" + centerColor + "\"");
        }
        if (!isEmpty(startColor)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"startColor\": \"" + startColor + "\"");
        }
        if (!isEmpty(endColor)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"endColor\": \"" + endColor + "\"");
        }
        if (!isEmpty(centerX)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"centerX\": \"" + centerX + "\"");
        }
        if (!isEmpty(centerY)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"centerY\": \"" + centerY + "\"");
        }
        if (!isEmpty(gradientRadius)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"gradientRadius\": \"" + gradientRadius + "\"");
        }
        if (!isEmpty(bottom)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"bottom\": \"" + bottom + "\"");
        }
        if (!isEmpty(top)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"top\": \"" + top + "\"");
        }
        if (!isEmpty(left)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"left\": \"" + left + "\"");
        }
        if (!isEmpty(right)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"right\": \"" + right + "\"");
        }
        if (!isEmpty(height)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"height\": \"" + height + "\"");
        }
        if (!isEmpty(width)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"width\": \"" + width + "\"");
        }
        if (!isEmpty(solidColor)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"solidColor\": \"" + solidColor + "\"");
        }
        if (!isEmpty(strokeColor)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"strokeColor\": \"" + strokeColor + "\"");
        }
        if (!isEmpty(strokeWidth)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"strokeWidth\": \"" + strokeWidth + "\"");
        }
        if (!isEmpty(dashGap)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            addPoint = true;
            stringBuilder.append("\"dashGap\": \"" + dashGap + "\"");
        }
        if (!isEmpty(dashWidth)) {
            if (addPoint) {
                stringBuilder.append(", ");
            }
            stringBuilder.append("\"dashWidth\": \"" + dashWidth + "\"");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
    
    private boolean isEmpty(String text) {
        if (text == null || text.length() == 0) {
            return true;
        }
        return false;
    }
}
