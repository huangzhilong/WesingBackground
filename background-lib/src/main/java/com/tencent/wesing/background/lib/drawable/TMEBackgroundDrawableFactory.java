package com.tencent.wesing.background.lib.drawable;

import android.content.res.TypedArray;
import android.graphics.RectF;
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

    private static final String TAG = "GradientDrawableFactory";

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
                return createDrawableById(drawableId);
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
            cacheDrawable = createDrawableByGradientInfo(gradientDrawableInfo);
            if (cacheDrawable != null) {
                mAttributeDrawableCacheMap.put(gradientDrawableInfo, cacheDrawable);
            }
            return cacheDrawable;
        }
        return null;
    }

    /**
     *  代码中使用，通过Id。用户字节码替换代码的drawable获取
     */
    public static Drawable createDrawableById(int drawableId) {
        //校验是否有可用的drawableInfo
        if (TMEBackgroundMap.getBackgroundAttributeMap() == null) {
            return TMEBackgroundContext.getDrawable(drawableId);
        }
        GradientDrawableInfo gradientDrawableInfo = (TMEBackgroundMap.getBackgroundAttributeMap().get(drawableId));
        if (gradientDrawableInfo == null || gradientDrawableInfo.isDisable) {
            return TMEBackgroundContext.getDrawable(drawableId);
        }
        Log.d(TAG, "createDrawableById drawableId: " + drawableId);
        Drawable cache = getInstance().mCacheDrawable.get(drawableId);
        if (cache != null && cache.getConstantState() != null) {
            return cache.getConstantState().newDrawable();
        }
        // 当前只解析GradientDrawable
        GradientDrawable drawable = getInstance().createDrawableByGradientInfo(gradientDrawableInfo);
        if (drawable != null) {
            getInstance().mCacheDrawable.put(drawableId, drawable);
            return drawable;
        } else {
            // 找不到字节码属性用系统获取
            return TMEBackgroundContext.getDrawable(drawableId);
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
