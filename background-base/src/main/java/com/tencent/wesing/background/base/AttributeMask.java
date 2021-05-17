package com.tencent.wesing.background.base;


/**
 * create by zlonghuang on 2021/5/13
 **/

public class AttributeMask {

    public int ditherMask = 1;

    public int shapeMask = 1 << 1;

    public int tintMask = 1 << 2;

    //-- 圆角 --
    public int radiusMask = 1 << 3;
    public int bottomLeftRadiusMask = 1 << 4;
    public int bottomRightRadiusMask = 1 << 5;
    public int topLeftRadiusMask = 1 << 6;
    public int topRightRadiusMask = 1<< 7;


    //-- 渐变 --
    public int typeMask = 1 << 8;

    public int angleMask = 1 << 9;

    public int centerColorMask = 1 << 10;
    public int startColorMask = 1 << 11;
    public int endColorMask = 1 << 12;

    public int centerXMask = 1 << 13;
    public int centerYMask = 1 << 14;
    public int gradientRadiusMask = 1 << 15;

    //-- 内边距 --
    public int topMask = 1 << 16;
    public int leftMask = 1 << 17;
    public int bottomMask = 1 << 18;
    public int rightMask = 1 << 19;

    //-- 大小 --
    public int heightMask = 1 << 20;
    public int widthMask = 1 << 21;

    //-- 填充 --
    public int solidColorMask = 1 << 22;

    //-- 描边 --
    public int strokeColorMask = 1 << 23;
    public int strokeWidthMask = 1 << 24;
    public int dashGapMask = 1 << 25;
    public int dashWidthMask = 1 << 26;
}
