package com.tencent.wesing.background.lib.drawable;

import android.content.res.TypedArray;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import com.tencent.wesing.background.lib.R;
import com.tencent.wesing.background.lib.TMEBackgroundContext;
import com.tencent.wesing.background.lib.bean.AttributeMask;
import com.tencent.wesing.background.lib.bean.GradientDrawableInfo;
import com.tencent.wesing.background.lib.bean.TMEBackgroundMap;

import java.lang.reflect.Field;


/**
 * create by zlonghuang on 2021/5/10
 *
 * 生成drawable 给View，现只支持GradientDrawable
 **/

public class GradientDrawableFactory {

    private static final String TAG = "GradientDrawableFactory";

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
        GradientDrawableInfo gradientDrawableInfo = (TMEBackgroundMap.getBackgroundAttributeMap().get(drawableId));
        try {
            if (gradientDrawableInfo == null || gradientDrawableInfo.isDisable || gradientDrawableInfo.attrMask <= 0) {
                return (GradientDrawable) TMEBackgroundContext.getContext().getResources().getDrawable(drawableId);
            }
            GradientDrawable gradientDrawable = new GradientDrawable();
            if (gradientDrawableInfo.hasAttribute(gradientDrawableInfo.attrMask, AttributeMask.ditherMask)) {
                gradientDrawable.setDither(gradientDrawableInfo.dither);
            }
            if (gradientDrawableInfo.hasAttribute(gradientDrawableInfo.attrMask, AttributeMask.shapeMask)) {
                gradientDrawable.setShape(gradientDrawableInfo.shape);
            }
            if (gradientDrawableInfo.hasAttribute(gradientDrawableInfo.attrMask, AttributeMask.tintMask) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                gradientDrawable.setTint(gradientDrawableInfo.tint);
            }

            //圆角
            if (gradientDrawableInfo.hasAttribute(gradientDrawableInfo.attrMask, AttributeMask.radiusMask) && gradientDrawableInfo.radius > 0) {
                gradientDrawable.setCornerRadius(gradientDrawableInfo.radius);
            } else if (gradientDrawableInfo.topLeftRadius > 0 || gradientDrawableInfo.topRightRadius > 0 || gradientDrawableInfo.bottomLeftRadius > 0 || gradientDrawableInfo.bottomRightRadius > 0) {
                float[] cornerRadius = new float[8];
                cornerRadius[0] = gradientDrawableInfo.topLeftRadius;
                cornerRadius[1] = gradientDrawableInfo.topLeftRadius;
                cornerRadius[2] = gradientDrawableInfo.topRightRadius;
                cornerRadius[3] = gradientDrawableInfo.topRightRadius;
                cornerRadius[4] = gradientDrawableInfo.bottomRightRadius;
                cornerRadius[5] = gradientDrawableInfo.bottomRightRadius;
                cornerRadius[6] = gradientDrawableInfo.bottomLeftRadius;
                cornerRadius[7] = gradientDrawableInfo.bottomLeftRadius;
                gradientDrawable.setCornerRadii(cornerRadius);
            }

            //渐变
            if (gradientDrawableInfo.startColor > 0 && gradientDrawableInfo.endColor > 0) {
                gradientDrawable.setGradientType(gradientDrawableInfo.type);
                int[] color;
                if (gradientDrawableInfo.centerColor > 0) {
                    color = new int[]{gradientDrawableInfo.startColor, gradientDrawableInfo.centerColor, gradientDrawableInfo.endColor};
                } else {
                    color = new int[]{gradientDrawableInfo.startColor, gradientDrawableInfo.endColor};
                }
                gradientDrawable.setColors(color);

                if (gradientDrawableInfo.centerX > 0 && gradientDrawableInfo.centerY > 0) {
                    gradientDrawable.setGradientCenter(gradientDrawableInfo.centerX, gradientDrawableInfo.centerY);
                }
                if (gradientDrawableInfo.angle > 0) {
                    gradientDrawableInfo.angle %= 360;
                    // 取整
                    if (gradientDrawableInfo.angle % 45 != 0) {
                        int remind = gradientDrawableInfo.angle % 45;
                        gradientDrawableInfo.angle = gradientDrawableInfo.angle - remind;
                    }
                    GradientDrawable.Orientation mOrientation = GradientDrawable.Orientation.LEFT_RIGHT;
                    switch (gradientDrawableInfo.angle) {
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
                if (gradientDrawableInfo.gradientRadius > 0) {
                    gradientDrawable.setGradientRadius(gradientDrawableInfo.gradientRadius);
                }
            }

            //内边距
            if (gradientDrawableInfo.right > 0 || gradientDrawableInfo.left > 0 || gradientDrawableInfo.bottom > 0 || gradientDrawableInfo.top > 0) {
                RectF padding = new RectF();
                padding.left = gradientDrawableInfo.left;
                padding.right = gradientDrawableInfo.right;
                padding.top = gradientDrawableInfo.top;
                padding.bottom = gradientDrawableInfo.bottom;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    gradientDrawable.setPadding((int) padding.left, (int) padding.top, (int) padding.right, (int) padding.bottom);
                } else {
                    try {
                        Field paddingField = gradientDrawable.getClass().getDeclaredField("mPadding");
                        paddingField.setAccessible(true);
                        paddingField.set(gradientDrawable, padding);
                    } catch (Exception e) {
                        Log.i(TAG, "set padding ex: " +  e);

                    }
                }
            }
            //高度
            if (gradientDrawableInfo.height > 0 || gradientDrawableInfo.width > 0) {
                gradientDrawable.setSize((int) gradientDrawableInfo.width, (int) gradientDrawableInfo.height);
            }
            //边框
            if (gradientDrawableInfo.strokeWidth > 0 || gradientDrawableInfo.dashWidth > 0 || gradientDrawableInfo.dashGap > 0) {
                gradientDrawable.setStroke((int) gradientDrawableInfo.strokeWidth, gradientDrawableInfo.strokeColor, gradientDrawableInfo.dashWidth, gradientDrawableInfo.dashGap);
            }

            return gradientDrawable;
        } catch (Exception e) {
            Log.i(TAG, "create ex: " +  e);
        }
        // 找不到字节码属性用系统获取
        return (GradientDrawable) TMEBackgroundContext.getContext().getResources().getDrawable(drawableId);
    }
}
