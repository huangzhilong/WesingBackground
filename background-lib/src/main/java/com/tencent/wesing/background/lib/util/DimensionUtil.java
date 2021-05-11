package com.tencent.wesing.background.lib.util;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * create by zlonghuang on 2021/5/10
 **/

public class DimensionUtil {

    private static final float scale = Resources.getSystem().getDisplayMetrics().density;
    private static final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;


    public static int dp2px(final float dpValue) {
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(final float spValue) {
        return (int) (spValue * fontScale + 0.5f);
    }

    public static float applyDimension(final float value, final int unit) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        switch (unit) {
            case TypedValue.COMPLEX_UNIT_PX:
                return value;
            case TypedValue.COMPLEX_UNIT_DIP:
                return value * metrics.density;
            case TypedValue.COMPLEX_UNIT_SP:
                return value * metrics.scaledDensity;
            case TypedValue.COMPLEX_UNIT_PT:
                return value * metrics.xdpi * (1.0f / 72);
            case TypedValue.COMPLEX_UNIT_IN:
                return value * metrics.xdpi;
            case TypedValue.COMPLEX_UNIT_MM:
                return value * metrics.xdpi * (1.0f / 25.4f);
        }
        return 0;
    }


    public static float getDimensionPxByAttrValue(String attr) {
        if (TextUtils.isEmpty(attr)) {
            return 0;
        }
        float value = Float.parseFloat(attr.substring(0, attr.length() - 2));
        if (attr.endsWith("dp")) {
            return dp2px(value);
        }
        if (attr.endsWith("sp")) {
            return sp2px(value);
        }
        if (attr.endsWith("px")) {
            return value;
        }
        if (attr.endsWith("pt")) {
            return applyDimension(value, TypedValue.COMPLEX_UNIT_PT);
        }
        if (attr.endsWith("in")) {
            return applyDimension(value, TypedValue.COMPLEX_UNIT_IN);
        }
        if (attr.endsWith("mm")) {
            return applyDimension(value, TypedValue.COMPLEX_UNIT_MM);
        }
        return 0;
    }
}
