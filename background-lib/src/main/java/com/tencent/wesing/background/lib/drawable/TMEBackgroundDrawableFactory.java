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
    private LruCache<GradientDrawableInfo, Drawable> mAttributeDrawableCacheMap = new LruCache<>(10);

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
            } else  {
                //使用自定义属性
                GradientDrawableInfo gradientDrawableInfo = new GradientDrawableInfo();
                for (int i = 0; i < ta.getIndexCount(); i++) {
                    int attr = ta.getIndex(i);
                    if (attr == R.styleable.TMEBackground_tme_shape) {
                        gradientDrawableInfo.shape = ta.getInt(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_solid_color) {
                        gradientDrawableInfo.solidColor = ta.getColor(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_corners_radius) {
                        gradientDrawableInfo.radius = ta.getDimension(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_corners_bottomLeftRadius) {
                        gradientDrawableInfo.bottomLeftRadius = ta.getDimension(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_corners_bottomRightRadius) {
                        gradientDrawableInfo.bottomRightRadius = ta.getDimension(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_corners_topLeftRadius) {
                        gradientDrawableInfo.topLeftRadius = ta.getDimension(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_corners_topRightRadius) {
                        gradientDrawableInfo.topRightRadius = ta.getDimension(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_gradient_angle) {
                        gradientDrawableInfo.angle = (int) ta.getFloat(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_gradient_centerX) {
                        gradientDrawableInfo.centerX = DimensionUtil.getFloatOrFractionOrDimension(ta, attr, 0.5f);
                    } else if (attr == R.styleable.TMEBackground_tme_gradient_centerY) {
                        gradientDrawableInfo.centerY = DimensionUtil.getFloatOrFractionOrDimension(ta, attr, 0.5f);
                    } else if (attr == R.styleable.TMEBackground_tme_gradient_centerColor) {
                        gradientDrawableInfo.centerColor = ta.getColor(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_gradient_endColor) {
                        gradientDrawableInfo.endColor = ta.getColor(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_gradient_startColor) {
                        gradientDrawableInfo.startColor = ta.getColor(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_gradient_gradientRadius) {
                        gradientDrawableInfo.gradientRadius = DimensionUtil.getFloatOrFractionOrDimension(ta, attr, 0.5f);
                    } else if (attr == R.styleable.TMEBackground_tme_gradient_type) {
                        gradientDrawableInfo.type = ta.getInt(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_gradient_useLevel) {
                        //暂时没支持实现
                    } else if (attr == R.styleable.TMEBackground_tme_padding_left) {
                        gradientDrawableInfo.left = ta.getDimension(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_padding_right) {
                        gradientDrawableInfo.right = ta.getDimension(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_padding_top) {
                        gradientDrawableInfo.top = ta.getDimension(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_padding_bottom) {
                        gradientDrawableInfo.bottom = ta.getDimension(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_size_height) {
                        gradientDrawableInfo.height = ta.getDimension(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_size_width) {
                        gradientDrawableInfo.width = ta.getDimension(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_stroke_width) {
                        gradientDrawableInfo.strokeWidth = ta.getDimension(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_stroke_color) {
                        gradientDrawableInfo.strokeColor = ta.getColor(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_stroke_dashWidth) {
                        gradientDrawableInfo.dashWidth = ta.getDimension(attr, 0);
                    } else if (attr == R.styleable.TMEBackground_tme_stroke_dashGap) {
                        gradientDrawableInfo.dashGap = ta.getDimension(attr, 0);
                    }
                }
                Drawable cacheDrawable = mAttributeDrawableCacheMap.get(gradientDrawableInfo);
                if (cacheDrawable != null && cacheDrawable.getConstantState() != null) {
                    return cacheDrawable.getConstantState().newDrawable(); //源码也是这个，它创建了新的实例（newDrawble方法). 但是复用了ConstantState
                }
                gradientDrawableInfo.parseAttribute();
                cacheDrawable = createDrawableByGradientInfo(gradientDrawableInfo, View.NO_ID);
                if (cacheDrawable != null) {
                    mAttributeDrawableCacheMap.put(gradientDrawableInfo, cacheDrawable);
                }
                return cacheDrawable;
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
        if (TMEBackgroundContext.getDrawableMonitor() != null && TMEBackgroundMap.isContainsDrawableId(drawableId)) {
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
            Log.d(TAG, "not found useSystemCreateDrawable drawableId: " + drawableId);
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
                Log.d(TAG, "has padding drawableId: " + drawableId);
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
