package com.tencent.wesing.background.lib.bean;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * create by zlonghuang on 2021/4/29
 *
 *  编译期会通过Transform把shape 属性插入到class中，自己通过反射获取所有属性
 *
 **/

public class TMEBackgroundMap {
    public final static String BACKGROUND_PARAMS_TAG = "background_param";
    private final static HashMap<Integer, String> mBackgroundAttributeMap = new HashMap<>();

    static {
        Class c = TMEBackgroundMap.class;
        Field[] fields = c.getFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                if (fields[i].getName().startsWith(BACKGROUND_PARAMS_TAG)) {
                    handleShapeValue((String) fields[i].get(c));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // R.drawable.shape_text_3+","+"shape:oval,"+"bottomLeftRadius:10dp,"+"bottomRightRadius:10dp,"+"topLeftRadius:10dp,"+"topRightRadius:10dp,"+"height:100dp,"+"width:100dp,"+"solidColor:#FFFFFF,";
    private static void handleShapeValue(String value) {
        //第一位是Id
        int index = value.indexOf(",");
        mBackgroundAttributeMap.put(Integer.parseInt(value.substring(0, index)), value.substring(index + 1));
    }

    public static HashMap<Integer, String> getBackgroundAttributeMap() {
        return mBackgroundAttributeMap;
    }
}
