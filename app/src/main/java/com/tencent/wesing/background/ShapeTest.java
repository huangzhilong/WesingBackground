package com.tencent.wesing.background;

import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.tencent.wesing.background.lib.TMEBackgroundContext;
import com.tencent.wesing.background.lib.drawable.TMEBackgroundDrawableFactory;

import java.util.HashMap;

/**
 * create by zlonghuang on 2021/4/20
 **/

class ShapeTest {


    private final static HashMap<Integer, Object> mBackgroundAttributeMap = new HashMap<>();


    public static final Object[] param1  = { 1, 2, 3, 4, 6, 7, "shape_text_2.xml", "dither", R.bool.yes};


    public Drawable drawable = TMEBackgroundContext.getContext().getResources().getDrawable(R.drawable.ic_launcher);


    public static final Object[] param2  = { 24444, "shape_text_3.xml", "shape"};
    public static final Object[] param4  = { 24444, "shape_text_3.xml", "shape",222};
    public static final Object[] param3  = { 24444, "shape_text_3.xml", "shape", 111, 555};
    public static final Object[] param6  = { 24444, "shape_text_3.xml", "shape", 111, 555, 233344};
    public static final Object[] param7  = { 24444, "shape_text_3.xml", "shape", 111, 555,4444, 5555};


    public static final Object[] test = {"ffhfh", R.id.btn_send_gift, "kfkfk", "ffhfh", R.id.btn_send_gift, "kfkfk", "ffhfh", R.id.btn_send_gift, "kfkfk", "ffhfh", R.id.btn_send_gift, "kfkfk", "ffhfh", R.id.btn_send_gift, "kfkfk","ffhfh", R.id.btn_send_gift, "kfkfk"};


    public static void  initData() {
        Integer id = 2113456777;
        Object[] param888  = { 24444, "shape_text_3.xml", "shape", 111, 555,4444, 5555};
        mBackgroundAttributeMap.put(id, param888);
        mBackgroundAttributeMap.put(66666666, param888);
    }


    public void testDrawable() {
        Drawable drawable = TMEBackgroundContext.getContext().getResources().getDrawable(R.drawable.ic_launcher);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Drawable drawable1 = TMEBackgroundContext.getContext().getResources().getDrawable(R.drawable.ic_launcher, TMEBackgroundContext.getContext().getTheme());
        }
        Drawable drawable2 = ContextCompat.getDrawable(TMEBackgroundContext.getContext(), R.drawable.ic_launcher);

        Drawable drawable3 = TMEBackgroundDrawableFactory.getInstance().createDrawableById(R.drawable.shape_text_2);
    }


    private final static HashMap<Integer, GradientDrawableInfo> mGradientDrawableMap = new HashMap<>();


    public static void initGradientDrawable() {
        Object[] param888  = { 24444, "shape_text_3.xml", "shape", 111, 555,4444, 5555};
        mGradientDrawableMap.put(2535336, new GradientDrawableInfo(param888));
    }


}
