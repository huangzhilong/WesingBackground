package com.tencent.wesing.background;

import java.util.HashMap;

/**
 * create by zlonghuang on 2021/4/20
 **/

class ShapeTest {

    public static final int textId = R.bool.yes;

    public static final int androidId = android.R.color.background_dark;

    public static final HashMap<String, ValueInfo> shapeMap = new HashMap<>();

    public static final Object[] test = {"ffhfh", R.id.btn_send_gift, "kfkfk"};

    static {
        shapeMap.put("test_shape", new ValueInfo("color", textId));
        shapeMap.put("test_shape", new ValueInfo("color", textId));
    }

    static class ValueInfo {
        //属性名
        public String tag;

        // 属性值是 id值
        public int id;

        //属性值是定义的值
        public String value = null;

        public ValueInfo(String s, int v) {
            tag = s;
            id = v;
        }

        public ValueInfo(String s, String v) {
            tag = s;
            value = v;
        }
    }

}
