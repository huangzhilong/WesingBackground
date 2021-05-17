package com.tencent.wesing.background.base;

/**
 * create by zlonghuang on 2021/5/13
 **/


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
    public int strokeWidth = 0;
    public float dashGap = 0.0f;
    public float dashWidth = 0.0f;
}

