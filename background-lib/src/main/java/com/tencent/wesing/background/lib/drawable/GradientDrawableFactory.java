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
            GradientDrawableInfo gradientDrawableInfo = new GradientDrawableInfo();
        }
        return null;
    }

    public GradientDrawable createDrawableById(int drawableId) {
        GradientDrawableInfo gradientDrawableInfo = (TMEBackgroundMap.getBackgroundAttributeMap().get(drawableId));
        if (gradientDrawableInfo == null || gradientDrawableInfo.isDisable) {
            return (GradientDrawable) TMEBackgroundContext.getContext().getResources().getDrawable(drawableId);
        }
        GradientDrawable drawable = createDrawableByGradientInfo(gradientDrawableInfo);
        if (drawable != null) {
            return drawable;
        } else {
            // 找不到字节码属性用系统获取
            return (GradientDrawable) TMEBackgroundContext.getContext().getResources().getDrawable(drawableId);
        }
    }


    private GradientDrawable createDrawableByGradientInfo(GradientDrawableInfo gradientDrawableInfo) {
        if (gradientDrawableInfo == null) {
            return null;
        }
        GradientDrawable gradientDrawable = new GradientDrawable();
        if (gradientDrawableInfo.dither) {
            gradientDrawable.setDither(true);  //默认false
        }
        if (gradientDrawableInfo.shape != 0) {  //默认是0
            gradientDrawable.setShape(gradientDrawableInfo.shape);
        }
        if (gradientDrawableInfo.tint != 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            gradientDrawable.setTint(gradientDrawableInfo.tint);
        }

        //圆角
        if (gradientDrawableInfo.radiusArray != null) {
            gradientDrawable.setCornerRadii(gradientDrawableInfo.radiusArray);
        }

        //填充
        if (gradientDrawableInfo.solidColor != 0) {
            gradientDrawable.setColor(gradientDrawableInfo.solidColor);
        }

        //渐变
        if (gradientDrawableInfo.startColor != 0 && gradientDrawableInfo.endColor != 0) {
            if (gradientDrawableInfo.type != 0) {
                gradientDrawable.setGradientType(gradientDrawableInfo.type);
            }
            gradientDrawable.setColors(gradientDrawableInfo.colorArray);
            if (gradientDrawableInfo.centerX > 0 && gradientDrawableInfo.centerY > 0) {
                gradientDrawable.setGradientCenter(gradientDrawableInfo.centerX, gradientDrawableInfo.centerY);
            }
            if (gradientDrawableInfo.angle > 0) {
                gradientDrawable.setOrientation(gradientDrawableInfo.mOrientation);
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
                    Log.i(TAG, "createDrawableByGradientInfo set padding ex: " +  e);
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
    }
}
