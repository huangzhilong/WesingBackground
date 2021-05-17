package com.tencent.wesing.background.base;


/**
 * create by zlonghuang on 2021/5/13
 **/

public class GradientDrawableUtil {

    public static boolean isSupportAttr(String attr) {
        if (attr == null || attr.length() == 0) {
            return false;
        }
        // 不支持的属性 innerRadius innerRadiusRatio thickness thicknessRatio   tintMode  五个属性需要android sdk 29才支持，先不支持不处理
        //  useLevel和 visible属性较少用，先不支持不处理
        if (attr.contains("innerRadius") || attr.contains("thickness") || attr.contains("tintMode") || attr.contains("visible") || attr.contains("useLevel")) {
            return false;
        }
        return true;
    }
}
