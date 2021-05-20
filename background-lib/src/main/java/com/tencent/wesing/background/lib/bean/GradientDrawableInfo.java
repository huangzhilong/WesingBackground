package com.tencent.wesing.background.lib.bean;

/**
 * create by zlonghuang on 2021/5/13
 **/

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;

import com.tencent.wesing.background.lib.TMEBackgroundContext;
import com.tencent.wesing.background.lib.util.DimensionUtil;

/**
 * GradientDrawable 属性
 */
public class GradientDrawableInfo {

    //将在位图的像素配置与屏幕不同时（例如：ARGB 8888 位图和 RGB 565 屏幕）启用位图的抖动；值为“false”时则停用抖动。默认值为 false。
    public boolean dither = false;

    //分别为矩形、线、椭圆、环。默认为矩形rectangle
    public int shape = 0;

    //给shape着色
    public int tint = 0;

    //-- 圆角 --
    public float radius = 0.0f;
    public float bottomLeftRadius = 0.0f;
    public float bottomRightRadius = 0.0f;
    public float topLeftRadius = 0.0f;
    public float topRightRadius = 0.0f;


    //-- 渐变 --
    // 渐变类型，分别为线性、放射性、扫描性渐变，默认为线性渐变linear
    public int type = 0;
    // 渐变角度，当上面type为线性渐变linear时有效。角度为45的倍数，0度时从左往右渐变，角度方向逆时针
    public int angle = 0;

    // 渐变中间位置颜色  开始位置颜色  结束位置颜色
    public int centerColor = 0;
    public int startColor = 0;
    public int endColor = 0;

    //type为放射性渐变radial时有效，设置渐变中心的X(Y)坐标，取值区间[0,1]，默认为0.5，即中心位置
    public float centerX = 0.5f;
    public float centerY = 0.5f;

    //type为放射性渐变radial时有效，渐变的半径
    public float gradientRadius = 0.5f;

    //-- 内边距 --
    public float top = 0.0f;
    public float left = 0.0f;
    public float bottom = 0.0f;
    public float right = 0.0f;

    //-- 大小 --
    public float height = 0.0f;
    public float width = 0.0f;

    //-- 填充 --
    public int solidColor = 0;

    //-- 描边 --
    public int strokeColor = 0;
    public float strokeWidth = 0;
    public float dashGap = 0.0f;
    public float dashWidth = 0.0f;


    public boolean isDisable = false;

    /**
     * 解析属性
     */
    public GradientDrawableInfo(Object[] values) {
        if (values == null || values.length <= 2) {
            isDisable = true;
            return;
        }
        try {
            //不能根据是不是使用了整数就是Id，因为颜色也是整数，需要用id的位置来判断
            int attrValue = (int) values[0];
            int idValue = (int) values[1];

            boolean isId;
            int index = 2; //第二个开始是属性值，根据位运算来处理，属性顺序是根据AttributeMask顺序来的
            if (hasAttribute(attrValue, AttributeMask.ditherMask)) {
                boolean dither;
                if (isId(idValue, AttributeMask.ditherMask)) {
                    dither = TMEBackgroundContext.getContext().getResources().getBoolean((int) values[index]);
                } else {
                    dither = Boolean.parseBoolean((String) values[index]);
                }
                this.dither = dither;
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.shapeMask)) {
                int type = 0;
                isId = isId(idValue, AttributeMask.shapeMask);
                if (!isId) {
                    String value = (String) values[index];
                    switch (value) {
                        case "rectangle":
                            type = 0;
                            break;
                        case "oval":
                            type = 1;
                            break;
                        case "line":
                            type = 2;
                            break;
                        case "ring":
                            type = 3;
                            break;
                    }
                } else {
                    type = TMEBackgroundContext.getContext().getResources().getInteger((int) values[index]);
                }
                index++;
                this.shape = type;
            }
            if (hasAttribute(attrValue, AttributeMask.tintMask)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    isId = isId(idValue, AttributeMask.tintMask);
                    int color;
                    if (isId) {
                        color = TMEBackgroundContext.getContext().getResources().getColor((int) values[index]);
                    } else {
                        color = Color.parseColor((String) values[index]);
                    }
                    this.tint = color;
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.radiusMask)) {
                isId = isId(idValue, AttributeMask.radiusMask);
                float radius;
                if (isId) {
                    radius = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    radius = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
                index++;
                this.radius = radius;
            }
            if (hasAttribute(attrValue, AttributeMask.bottomLeftRadiusMask)) {
                isId = isId(idValue, AttributeMask.bottomLeftRadiusMask);
                float radius;
                if (isId) {
                    radius = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    radius = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
                bottomLeftRadius = radius;
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.bottomRightRadiusMask)) {
                isId = isId(idValue, AttributeMask.bottomRightRadiusMask);
                float radius;
                if (isId) {
                    radius = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    radius = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
                bottomRightRadius = radius;
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.topLeftRadiusMask)) {
                isId = isId(idValue, AttributeMask.topLeftRadiusMask);
                float radius;
                if (isId) {
                    radius = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    radius = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
                topLeftRadius = radius;
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.topRightRadiusMask)) {
                isId = isId(idValue, AttributeMask.topRightRadiusMask);
                float radius;
                if (isId) {
                    radius = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    radius = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
                this.topRightRadius = radius;
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.typeMask)) {
                isId = isId(idValue, AttributeMask.typeMask);
                int gradientType = GradientDrawable.LINEAR_GRADIENT;
                if (isId) {
                    gradientType = TMEBackgroundContext.getContext().getResources().getInteger((int) values[index]);
                } else {
                    String value = (String) values[index];
                    if (value.equals("radial")) {
                        gradientType = GradientDrawable.RADIAL_GRADIENT;
                    } else if (value.equals("sweep")) {
                        gradientType = GradientDrawable.SWEEP_GRADIENT;
                    }
                }
                type = gradientType;
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.angleMask)) {
                isId = isId(idValue, AttributeMask.angleMask);
                int gradientAngle;
                if (isId) {
                    gradientAngle = TMEBackgroundContext.getContext().getResources().getInteger((int) values[index]);
                } else {
                    gradientAngle = (int) values[index];
                }
                this.angle = gradientAngle;
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.centerColorMask)) {
                isId = isId(idValue, AttributeMask.centerColorMask);
                if (isId) {
                    centerColor = TMEBackgroundContext.getContext().getResources().getColor((int) values[index]);
                } else {
                    centerColor = Color.parseColor((String) values[index]);
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.startColorMask)) {
                isId = isId(idValue, AttributeMask.startColorMask);
                if (isId) {
                    startColor = TMEBackgroundContext.getContext().getResources().getColor((int) values[index]);
                } else {
                    startColor = Color.parseColor((String) values[index]);
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.endColorMask)) {
                isId = isId(idValue, AttributeMask.endColorMask);
                if (isId) {
                    endColor = TMEBackgroundContext.getContext().getResources().getColor((int) values[index]);
                } else {
                    endColor = Color.parseColor((String) values[index]);
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.centerXMask)) {
                isId = isId(idValue, AttributeMask.centerXMask);
                if (isId) {
                    centerX = TMEBackgroundContext.getContext().getResources().getFraction((int) values[index], 1, 1);
                } else {
                    centerX = Float.parseFloat((String) values[index]);
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.centerYMask)) {
                isId = isId(idValue, AttributeMask.centerYMask);
                if (isId) {
                    centerY = TMEBackgroundContext.getContext().getResources().getFraction((int) values[index], 1, 1);
                } else {
                    centerY = Float.parseFloat((String) values[index]);
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.gradientRadiusMask)) {
                isId = isId(idValue, AttributeMask.gradientRadiusMask);
                if (isId) {
                    gradientRadius = TMEBackgroundContext.getContext().getResources().getInteger((int) values[index]);
                } else {
                    gradientRadius = (int) values[index];
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.topMask)) {
                isId = isId(idValue, AttributeMask.topMask);
                if (isId) {
                    top = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    top = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.leftMask)) {
                isId = isId(idValue, AttributeMask.leftMask);
                if (isId) {
                    left = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    left = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.bottomMask)) {
                isId = isId(idValue, AttributeMask.bottomMask);
                if (isId) {
                    bottom = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    bottom = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.rightMask)) {
                isId = isId(idValue, AttributeMask.rightMask);
                if (isId) {
                    right = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    right = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.heightMask)) {
                isId = isId(idValue, AttributeMask.heightMask);
                if (isId) {
                    height = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    height = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.widthMask)) {
                isId = isId(idValue, AttributeMask.widthMask);
                if (isId) {
                    width = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    width = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.solidColorMask)) {
                isId = isId(idValue, AttributeMask.solidColorMask);
                int solidColor;
                if (isId) {
                    solidColor = TMEBackgroundContext.getContext().getResources().getColor((int) values[index]);
                } else {
                    solidColor = Color.parseColor((String) values[index]);
                }
                index++;
                this.solidColor = solidColor;
            }
            if (hasAttribute(attrValue, AttributeMask.strokeColorMask)) {
                isId = isId(idValue, AttributeMask.strokeColorMask);
                if (isId) {
                    strokeColor = TMEBackgroundContext.getContext().getResources().getColor((int) values[index]);
                } else {
                    strokeColor = Color.parseColor((String) values[index]);
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.strokeWidthMask)) {
                isId = isId(idValue, AttributeMask.strokeWidthMask);
                if (isId) {
                    strokeWidth = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    strokeWidth = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.dashGapMask)) {
                isId = isId(idValue, AttributeMask.dashGapMask);
                if (isId) {
                    dashGap = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    dashGap = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.dashWidthMask)) {
                isId = isId(idValue, AttributeMask.dashWidthMask);
                if (isId) {
                    dashWidth = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    dashWidth = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
            }
        } catch (Exception e) {
            Log.i("longpo", "parse attr ex: " + e);
            isDisable = true;
        }
    }

    private boolean isId(int idValue, int mask) {
        return (idValue & mask) > 0;
    }

    private boolean hasAttribute(int attrValue, int mask) {
        return (attrValue & mask) > 0;
    }
}

