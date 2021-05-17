package com.tencent.wesing.background.plugin.resource.shape;


/**
 * create by zlonghuang on 2021/5/13
 **/

class AttributeMask {

    public final static ditherMask = 1;

    public final static shapeMask = 1 << 1;

    public final static tintMask = 1 << 2;

    //-- 圆角 --
    public final static radiusMask = 1 << 3
    public final static bottomLeftRadiusMask = 1 << 4;
    public final static bottomRightRadiusMask = 1 << 5;
    public final static topLeftRadiusMask = 1 << 6;
    public final static topRightRadiusMask = 1<< 7;


    //-- 渐变 --
    public final static typeMask = 1 << 8;

    public final static angleMask = 1 << 9;

    public final static centerColorMask = 1 << 10;
    public final static startColorMask = 1 << 11;
    public final static endColorMask = 1 << 12;

    public final static centerXMask = 1 << 13;
    public final static centerYMask = 1 << 14;
    public final static gradientRadiusMask = 1 << 15;

    //-- 内边距 --
    public final static topMask = 1 << 16;
    public final static leftMask = 1 << 17;
    public final static bottomMask = 1 << 18;
    public final static rightMask = 1 << 19;

    //-- 大小 --
    public final static heightMask = 1 << 20;
    public final static widthMask = 1 << 21;

    //-- 填充 --
    public final static solidColorMask = 1 << 22;

    //-- 描边 --
    public final static strokeColorMask = 1 << 23;
    public final static strokeWidthMask = 1 << 24;
    public final static dashGapMask = 1 << 25;
    public final static dashWidthMask = 1 << 26;
}
