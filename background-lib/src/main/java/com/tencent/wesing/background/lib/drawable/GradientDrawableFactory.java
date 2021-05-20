package com.tencent.wesing.background.lib.drawable;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import com.tencent.wesing.background.lib.R;
import com.tencent.wesing.background.lib.TMEBackgroundContext;
import com.tencent.wesing.background.lib.bean.AttributeMask;
import com.tencent.wesing.background.lib.bean.TMEBackgroundMap;
import com.tencent.wesing.background.lib.util.DimensionUtil;
import java.lang.reflect.Field;
import static android.graphics.drawable.GradientDrawable.LINEAR_GRADIENT;


/**
 * create by zlonghuang on 2021/5/10
 *
 * 生成drawable 给View，现只支持GradientDrawable
 **/

public class GradientDrawableFactory {

    private GradientDrawableFactory() {
    }

    private static class SingleHolder {
        private static GradientDrawableFactory mFactory = new GradientDrawableFactory();
    }

    public static GradientDrawableFactory getInstance() {
        return SingleHolder.mFactory;
    }

    private LruCache<Integer, GradientDrawable> mCacheDrawable = new LruCache<>(20);

    public GradientDrawable getNeedGradientDrawable(TypedArray ta) {
        //优先使用drawableId
        if (ta.hasValue(R.styleable.TMEBackground_tme_background)) {
            int drawableId = ta.getResourceId(R.styleable.TMEBackground_tme_background, View.NO_ID);
            if (drawableId > 0) {
                GradientDrawable cache = mCacheDrawable.get(drawableId);
                if (cache != null) {
                    return cache;
                }
                GradientDrawable gradientDrawable = createDrawableById(drawableId);
                if (gradientDrawable != null) {
                    mCacheDrawable.put(drawableId, gradientDrawable);
                }
                return gradientDrawable;
            }
        } else  {
            //使用自定义属性
        }
        return null;
    }

    public GradientDrawable createDrawableById(int drawableId) {

        Object[] values = (TMEBackgroundMap.getBackgroundAttributeMap().get(drawableId));
        try {
            if (values == null || values.length <= 2) {
                return (GradientDrawable) TMEBackgroundContext.getContext().getResources().getDrawable(drawableId);
            }
            GradientDrawable gradientDrawable = new GradientDrawable();
            float[] cornerRadius = new float[8];
            float sizeWidth = -1;
            float sizeHeight = -1;
            float strokeWidth = -1;
            float strokeDashWidth = 0.0f;
            int strokeColor = 0;
            float strokeGap = 0.0f;
            float centerX = -1;
            float centerY = -1;
            int centerColor = 0;
            int startColor = 0;
            int endColor = 0;
            int gradientType = LINEAR_GRADIENT;
            int gradientAngle = -1;
            int gradientRadius = -1;
            RectF padding = new RectF();

            //不能根据是不是使用了整数就是Id，因为颜色也是整数，需要用id的位置来判断
            int attrValue = (int) values[0];
            int idValue = (int) values[1];

            long startTime = System.nanoTime();
            boolean isId;
            int index = 2; //第二个开始是属性值，根据位运算来处理，属性顺序是根据AttributeMask顺序来的
            if (hasAttribute(attrValue, AttributeMask.ditherMask)) {
                boolean dither;
                if (isId(idValue, AttributeMask.ditherMask)) {
                    dither = TMEBackgroundContext.getContext().getResources().getBoolean((int) values[index]);
                } else {
                    dither = Boolean.parseBoolean((String) values[index]);
                }
                gradientDrawable.setDither(dither);
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
                gradientDrawable.setShape(type);
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
                    gradientDrawable.setTint(color);
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
                gradientDrawable.setCornerRadius(radius);
            }
            if (hasAttribute(attrValue, AttributeMask.bottomLeftRadiusMask)) {
                isId = isId(idValue, AttributeMask.bottomLeftRadiusMask);
                float radius;
                if (isId) {
                    radius = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    radius = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
                cornerRadius[6] = radius;
                cornerRadius[7] = radius;
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
                cornerRadius[4] = radius;
                cornerRadius[5] = radius;
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
                cornerRadius[0] = radius;
                cornerRadius[1] = radius;
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
                cornerRadius[2] = radius;
                cornerRadius[3] = radius;
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.typeMask)) {
                isId = isId(idValue, AttributeMask.typeMask);
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
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.angleMask)) {
                isId = isId(idValue, AttributeMask.angleMask);
                if (isId) {
                    gradientAngle = TMEBackgroundContext.getContext().getResources().getInteger((int) values[index]);
                } else {
                    gradientAngle = (int) values[index];
                }
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
                    padding.top = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    padding.top = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.leftMask)) {
                isId = isId(idValue, AttributeMask.leftMask);
                if (isId) {
                    padding.left = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    padding.left = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.bottomMask)) {
                isId = isId(idValue, AttributeMask.bottomMask);
                if (isId) {
                    padding.bottom = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    padding.bottom = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.rightMask)) {
                isId = isId(idValue, AttributeMask.rightMask);
                if (isId) {
                    padding.right = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    padding.right = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.heightMask)) {
                isId = isId(idValue, AttributeMask.heightMask);
                if (isId) {
                    sizeHeight = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    sizeHeight = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.widthMask)) {
                isId = isId(idValue, AttributeMask.widthMask);
                if (isId) {
                    sizeWidth = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    sizeWidth = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
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
                gradientDrawable.setColor(solidColor);
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
                    strokeGap = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    strokeGap = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
                index++;
            }
            if (hasAttribute(attrValue, AttributeMask.dashWidthMask)) {
                isId = isId(idValue, AttributeMask.dashWidthMask);
                if (isId) {
                    strokeDashWidth = TMEBackgroundContext.getContext().getResources().getDimension((int) values[index]);
                } else {
                    strokeDashWidth = DimensionUtil.getDimensionPxByAttrValue((String) values[index]);
                }
            }
            Log.i("longpo", "parse attr costTime: " + (System.nanoTime() - startTime));

            startTime = System.nanoTime();

            //有圆角设置
            for (int i = 0; i < cornerRadius.length; i++) {
                if (cornerRadius[i] > 0) {
                    gradientDrawable.setCornerRadii(cornerRadius);
                    break;
                }
            }
            if (sizeHeight > 0 || sizeWidth > 0) {
                gradientDrawable.setSize((int) sizeWidth, (int) sizeHeight);
            }

            //边框
            if (strokeWidth > 0 || strokeDashWidth > 0 || strokeGap > 0) {
                gradientDrawable.setStroke((int) strokeWidth, strokeColor, strokeDashWidth, strokeGap);
            }

            if (startColor > 0 && endColor > 0) {
                gradientDrawable.setGradientType(gradientType);
                int[] color;
                if (centerColor > 0) {
                    color = new int[]{startColor, centerColor, endColor};
                } else {
                    color = new int[]{startColor, endColor};
                }
                gradientDrawable.setColors(color);

                if (centerX > 0 && centerY > 0) {
                    gradientDrawable.setGradientCenter(centerX, centerY);
                }
                if (gradientAngle > 0) {
                    gradientAngle %= 360;
                    // 取整
                    if (gradientAngle % 45 != 0) {
                        int remind = gradientAngle % 45;
                        gradientAngle = gradientAngle - remind;
                    }
                    GradientDrawable.Orientation mOrientation = GradientDrawable.Orientation.LEFT_RIGHT;
                    switch (gradientAngle) {
                        case 0:
                            mOrientation = GradientDrawable.Orientation.LEFT_RIGHT;
                            break;
                        case 45:
                            mOrientation = GradientDrawable.Orientation.BL_TR;
                            break;
                        case 90:
                            mOrientation = GradientDrawable.Orientation.BOTTOM_TOP;
                            break;
                        case 135:
                            mOrientation = GradientDrawable.Orientation.BR_TL;
                            break;
                        case 180:
                            mOrientation = GradientDrawable.Orientation.RIGHT_LEFT;
                            break;
                        case 225:
                            mOrientation = GradientDrawable.Orientation.TR_BL;
                            break;
                        case 270:
                            mOrientation = GradientDrawable.Orientation.TOP_BOTTOM;
                            break;
                        case 315:
                            mOrientation = GradientDrawable.Orientation.TL_BR;
                            break;
                    }
                    gradientDrawable.setOrientation(mOrientation);
                }
                if (gradientRadius > 0) {
                    gradientDrawable.setGradientRadius(gradientRadius);
                }
            }
            //内边距
            if (padding.right > 0 || padding.left > 0 || padding.bottom > 0 || padding.top > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    gradientDrawable.setPadding((int) padding.left, (int) padding.top, (int) padding.right, (int) padding.bottom);
                } else {
                    try {
                        Field paddingField = gradientDrawable.getClass().getDeclaredField("mPadding");
                        paddingField.setAccessible(true);
                        paddingField.set(gradientDrawable, padding);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.i("longpo", "new  attr costTime: " + (System.nanoTime() - startTime));
            return gradientDrawable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 找不到字节码属性用系统获取
        return (GradientDrawable) TMEBackgroundContext.getContext().getResources().getDrawable(drawableId);
    }

    private boolean isId(int idValue, int mask) {
        return (idValue & mask) > 0;
    }

    private boolean hasAttribute(int attrValue, int mask) {
        return (attrValue & mask) > 0;
    }
}
