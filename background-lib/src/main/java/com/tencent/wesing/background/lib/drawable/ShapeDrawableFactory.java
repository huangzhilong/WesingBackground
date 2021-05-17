package com.tencent.wesing.background.lib.drawable;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.View;

import com.tencent.wesing.background.lib.R;
import com.tencent.wesing.background.lib.TMEBackgroundContext;
import com.tencent.wesing.background.lib.bean.TMEBackgroundMap;
import com.tencent.wesing.background.lib.util.DimensionUtil;
import java.lang.reflect.Field;

import static android.graphics.drawable.GradientDrawable.LINEAR_GRADIENT;


/**
 * create by zlonghuang on 2021/5/10
 *
 * 生成drawable 给View，现只支持GradientDrawable
 **/

public class ShapeDrawableFactory {

    private ShapeDrawableFactory() {
    }

    private static class SingleHolder {
        private static ShapeDrawableFactory mFactory = new ShapeDrawableFactory();
    }

    public static ShapeDrawableFactory getInstance() {
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

        String attribute = TMEBackgroundMap.getBackgroundAttributeMap().get(drawableId);
        //不支持走系统的getDrawable
        if (!isSupportAttribute(attribute)) {
            return (GradientDrawable) TMEBackgroundContext.getContext().getResources().getDrawable(drawableId);
        }
        try {
            String[] data = attribute.split(",");
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
            for (int i = 0; i < data.length; i++) {
                if (TextUtils.isEmpty(data[i])) {
                    continue;
                }
                String[] values = data[i].split(":");
                if (values.length > 0) {
                    String name = values[0];
                    boolean isId = values[1].equals("1");
                    String value = values[2];

                    if (name.equals("shape")) {
                        int type = 0;
                        if (!isId) {
                            if (value.equals("rectangle")) {
                                type = 0;
                            } else if (value.equals("oval")) {
                                type = 1;
                            } else if (value.equals("line")) {
                                type = 2;
                            } else if (value.equals("ring")) {
                                type = 3;
                            }
                        } else {
                            type = TMEBackgroundContext.getContext().getResources().getInteger(getId(value));
                        }
                        gradientDrawable.setShape(type);
                    } else if (name.equals("radius")) {
                        float mRadius = getDimension(isId, value);
                        gradientDrawable.setCornerRadius(mRadius);
                    } else if (name.equals("bottomLeftRadius")) {
                        float radius = getDimension(isId, value);
                        cornerRadius[6] = radius;
                        cornerRadius[7] = radius;
                    } else if (name.equals("bottomRightRadius")) {
                        float radius = getDimension(isId, value);
                        cornerRadius[4] = radius;
                        cornerRadius[5] = radius;
                    } else if (name.equals("topLeftRadius")) {
                        float radius = getDimension(isId, value);
                        cornerRadius[0] = radius;
                        cornerRadius[1] = radius;
                    } else if (name.equals("topRightRadius")) {
                        float radius = getDimension(isId, value);
                        cornerRadius[2] = radius;
                        cornerRadius[3] = radius;
                    } else if (name.equals("solidColor")) {
                        int solidColor = getColor(isId, value);
                        gradientDrawable.setColor(solidColor);
                    } else if (name.equals("height")) {
                        sizeHeight = getDimension(isId, value);
                    } else if (name.equals("width")) {
                        sizeWidth = getDimension(isId, value);
                    } else if (name.equals("type")) {
                        if (value.equals("radial")) {
                            gradientType = GradientDrawable.RADIAL_GRADIENT;
                        } else if (value.equals("sweep")) {
                            gradientType = GradientDrawable.SWEEP_GRADIENT;
                        }
                    } else if (name.equals("angle")) {
                        if (!isId) {
                            gradientAngle = Integer.parseInt(value);
                        } else {
                            gradientAngle = TMEBackgroundContext.getContext().getResources().getInteger(getId(value));
                        }
                    } else if (name.equals("centerColor")) {
                        centerColor = getColor(isId, value);
                    } else if (name.equals("startColor")) {
                        startColor = getColor(isId, value);
                    } else if (name.equals("endColor")) {
                        endColor = getColor(isId, value);
                    } else if (name.equals("centerX")) {
                        if (!isId) {
                            centerX = Float.parseFloat(value);
                        } else {
                            centerX = TMEBackgroundContext.getContext().getResources().getFraction(getId(value), 1, 1);
                        }
                    } else if (name.equals("centerY")) {
                        if (!isId) {
                            centerY = Float.parseFloat(value);
                        } else {
                            centerY = TMEBackgroundContext.getContext().getResources().getFraction(getId(value), 1, 1);
                        }
                    } else if (name.equals("gradientRadius")) {
                        if (!isId) {
                            gradientRadius = Integer.parseInt(value);
                        } else {
                            gradientRadius = TMEBackgroundContext.getContext().getResources().getInteger(getId(value));
                        }
                    } else if (name.equals("strokeColor")) {
                        strokeColor = getColor(isId, value);
                    } else if (name.equals("strokeWidth")) {
                        strokeWidth = getDimension(isId, value);
                    } else if (name.equals("dashGap")) {
                        strokeGap = getDimension(isId, value);
                    } else if (name.equals("dashWidth")) {
                        strokeDashWidth = getDimension(isId, value);
                    } else if (name.equals("bottom")) {
                        padding.bottom = getDimension(isId, value);
                    } else if (name.equals("left")) {
                        padding.left = getDimension(isId, value);
                    } else if (name.equals("right")) {
                        padding.right = getDimension(isId, value);
                    } else if (name.equals("top")) {
                        padding.top = getDimension(isId, value);
                    } else if (name.equals("dither")) {
                        boolean dither;
                        if (!isId) {
                            dither = Boolean.parseBoolean(value);
                        } else {
                            dither = TMEBackgroundContext.getContext().getResources().getBoolean(getId(value));
                        }
                        gradientDrawable.setDither(dither);
                    } else if (name.equals("tint")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            gradientDrawable.setTint(getColor(isId, value));
                        }
                    }
                }
            }
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
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            return gradientDrawable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 找不到字节码属性用系统获取
        return (GradientDrawable) TMEBackgroundContext.getContext().getResources().getDrawable(drawableId);
    }


    private int getId(String value) {
        return Integer.parseInt(value);
    }

    private float getDimension(boolean isId, String value) {
        float dimen;
        if (!isId) {
            dimen = DimensionUtil.getDimensionPxByAttrValue(value);
        } else {
            dimen = TMEBackgroundContext.getContext().getResources().getDimension(getId(value));
        }
        return dimen;
    }

    private int getColor(boolean isId, String value) {
        int color;
        if (!isId) {
            color = Color.parseColor(value);
        } else {
            color = TMEBackgroundContext.getContext().getResources().getColor(getId(value));
        }
        return color;
    }

    private boolean isSupportAttribute(String attr) {
        if (TextUtils.isEmpty(attr)) {
            return false;
        }
        //不支持的属性
        if (attr.contains("innerRadius") || attr.contains("thickness") || attr.contains("visible")) {
            return false;
        }
        return true;
    }
}
