package com.tencent.wesing.background.lib.bean;


/**
 * create by zlonghuang on 2021/5/13
 **/

public class AttributeMask {

    public final static int ditherMask = 1;

    public final static int shapeMask = 1 << 1;

    public final static int tintMask = 1 << 2;

    //-- 圆角 --
    public final static int radiusMask = 1 << 3;
    public final static int bottomLeftRadiusMask = 1 << 4;
    public final static int bottomRightRadiusMask = 1 << 5;
    public final static int topLeftRadiusMask = 1 << 6;
    public final static int topRightRadiusMask = 1<< 7;


    //-- 渐变 --
    public final static int typeMask = 1 << 8;

    public final static int angleMask = 1 << 9;

    public final static int centerColorMask = 1 << 10;
    public final static int startColorMask = 1 << 11;
    public final static int endColorMask = 1 << 12;

    public final static int centerXMask = 1 << 13;
    public final static int centerYMask = 1 << 14;
    public final static int gradientRadiusMask = 1 << 15;

    //-- 内边距 --
    public final static int topMask = 1 << 16;
    public final static int leftMask = 1 << 17;
    public final static int bottomMask = 1 << 18;
    public final static int rightMask = 1 << 19;

    //-- 大小 --
    public final static int heightMask = 1 << 20;
    public final static int widthMask = 1 << 21;

    //-- 填充 --
    public final static int solidColorMask = 1 << 22;

    //-- 描边 --
    public final static int strokeColorMask = 1 << 23;
    public final static int strokeWidthMask = 1 << 24;
    public final static int dashGapMask = 1 << 25;
    public final static int dashWidthMask = 1 << 26;
}
