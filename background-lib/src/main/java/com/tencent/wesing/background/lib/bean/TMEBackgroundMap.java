package com.tencent.wesing.background.lib.bean;

import java.util.HashMap;

/**
 * create by zlonghuang on 2021/4/29
 *
 *  编译期会通过Transform把shape 属性插入到class中，自己通过反射获取所有属性
 *
 **/

public class TMEBackgroundMap {

    private final static HashMap<Integer, Object[]> mBackgroundAttributeMap = new HashMap<>();

    public static HashMap<Integer, Object[]> getBackgroundAttributeMap() {
        return mBackgroundAttributeMap;
    }

    public static void initDrawableDataMap() {
    }
}
