package com.tencent.wesing.background.lib.drawable;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import com.tencent.wesing.background.lib.R;
import com.tencent.wesing.background.lib.TMEBackgroundContext;
import com.tencent.wesing.background.lib.bean.GradientDrawableInfo;
import com.tencent.wesing.background.lib.bean.TMEBackgroundMap;
import com.tencent.wesing.background.lib.util.DimensionUtil;
import java.lang.reflect.Field;


/**
 * create by zlonghuang on 2021/5/10
 *
 * 生成drawable 给View，现只支持GradientDrawable
 **/

public class TMEBackgroundDrawableFactory {

    private static final String TAG = "TMEBackgroundDrawableFactory";

    private TMEBackgroundDrawableFactory() {
    }

    private static class SingleHolder {
        private static TMEBackgroundDrawableFactory mFactory = new TMEBackgroundDrawableFactory();
    }

    public static TMEBackgroundDrawableFactory getInstance() {
        return SingleHolder.mFactory;
    }

    private LruCache<Integer, Drawable> mCacheDrawable = new LruCache<>(10);

    // xml属性解析过来的
    public Drawable getNeedGradientDrawable(TypedArray ta) {
        //优先使用drawableId
        if (ta.hasValue(R.styleable.TMEBackground_tme_background)) {
            int drawableId = ta.getResourceId(R.styleable.TMEBackground_tme_background, View.NO_ID);
            if (drawableId > 0) {
                if (TMEBackgroundContext.isAvailable()) {
                    return createDrawableById(drawableId);
                } else {
                    //插件禁用，使用系统原本的获取
                    return useSystemCreateDrawable(drawableId);
                }
            }
        }
        return null;
    }

    private static Drawable useSystemCreateDrawable(int drawableId) {
        long startTime = System.nanoTime();
        if (drawableId <= 0) {
            return null;
        }
        Drawable drawable = TMEBackgroundContext.getDrawable(drawableId);
        if (TMEBackgroundContext.getDrawableMonitor() != null) {
            TMEBackgroundContext.getDrawableMonitor().onGetDrawable(true, System.nanoTime() - startTime, drawableId);
        }
        return drawable;
    }

    /**
     *  代码中使用，通过Id。用户字节码替换代码的drawable获取
     */
    @SuppressLint("LongLogTag")
    public static Drawable createDrawableById(int drawableId) {
        //校验是否有可用的drawableInfo
        if (TMEBackgroundMap.getBackgroundAttributeMap() == null) {
            return useSystemCreateDrawable(drawableId);
        }
        GradientDrawableInfo gradientDrawableInfo = (TMEBackgroundMap.getBackgroundAttributeMap().get(drawableId));
        if (gradientDrawableInfo == null || gradientDrawableInfo.isDisable) {
            return useSystemCreateDrawable(drawableId);
        }
        Log.d(TAG, "createDrawableById drawableId: " + drawableId);
        Drawable cache = getInstance().mCacheDrawable.get(drawableId);
        if (cache != null && cache.getConstantState() != null) {
            long startTime = System.nanoTime();
            Drawable drawable = cache.getConstantState().newDrawable();
            if (TMEBackgroundContext.getDrawableMonitor() != null) {
                TMEBackgroundContext.getDrawableMonitor().onGetDrawable(false, System.nanoTime() - startTime, drawableId);
            }
            return drawable;
        }

        // 当前只解析GradientDrawable
        Drawable drawable = getInstance().createDrawableByGradientInfo(gradientDrawableInfo, drawableId);
        getInstance().mCacheDrawable.put(drawableId, drawable);
        return drawable;
    }


    @SuppressLint("LongLogTag")
    private Drawable createDrawableByGradientInfo(GradientDrawableInfo gradientDrawableInfo, int drawableId) {
        try {
            long startTime = System.nanoTime();
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
                Rect padding = new Rect();
                padding.left = (int) gradientDrawableInfo.left;
                padding.right = (int) gradientDrawableInfo.right;
                padding.top = (int) gradientDrawableInfo.top;
                padding.bottom = (int) gradientDrawableInfo.bottom;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    gradientDrawable.setPadding(padding.left, padding.top, padding.right, padding.bottom);
                } else {
                    Field paddingField = gradientDrawable.getClass().getDeclaredField("mPadding");
                    paddingField.setAccessible(true);
                    paddingField.set(gradientDrawable, padding);
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
            if (TMEBackgroundContext.getDrawableMonitor() != null) {
                TMEBackgroundContext.getDrawableMonitor().onGetDrawable(false, System.nanoTime() - startTime, drawableId);
            }
            return gradientDrawable;
        } catch (Exception e) {
            Log.i(TAG, "createDrawableByGradientInfo happend ex: " + e);
            if (TMEBackgroundContext.getDrawableMonitor() != null) {
                TMEBackgroundContext.getDrawableMonitor().onGetDrawableError(drawableId, e.getMessage());
            }
        }
        return useSystemCreateDrawable(drawableId);
    }
}
