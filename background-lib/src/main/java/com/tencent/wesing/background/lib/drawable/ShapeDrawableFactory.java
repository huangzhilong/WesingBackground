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

    private GradientDrawable createDrawableById(int drawableId) {
        try {
            String attribute = TMEBackgroundMap.getBackgroundAttributeMap().get(drawableId);
            if (!TextUtils.isEmpty(attribute)) {
                String[] data = attribute.split(",");
                GradientDrawable gradientDrawable = new GradientDrawable();
                float[] cornerRadius = new float[8];
                float sizeWidth = -1;
                float sizeHeight = -1;
                float strokeWidth = -1;
                float strokeDashWidth = 0.0f;
                int strokeColor = 0;
                float strokeGap = 0.0f;
                float centerX = 0;
                float centerY = 0;
                int centerColor = 0;
                int startColor = 0;
                int endColor = 0;
                int gradientType = LINEAR_GRADIENT;
                int gradientAngle = 0;
                int gradientRadius = 0;
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
                        }


                        else if (name.equals("centerX")) {
                            if (!isId) {
                                centerX = Float.parseFloat(value);
                            }
                        } else if (name.equals("centerY")) {
                            if (!isId) {
                                centerY = Float.parseFloat(value);
                            }
                        } else if (name.equals("gradientRadius")) {
                            if (!isId) {
                                gradientRadius = Integer.parseInt(value);
                            }
                        }

                        else if (name.equals("strokeColor")) {
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

                //内边距
                if (padding.right > 0 || padding.left > 0 || padding.bottom > 0 || padding.top > 0) {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
}
