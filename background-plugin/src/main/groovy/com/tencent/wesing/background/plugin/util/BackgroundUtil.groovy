package com.tencent.wesing.background.plugin.util

/**
 * create by zlonghuang on 2021/4/19
 **/

class BackgroundUtil {

    static boolean isEmpty(String tag) {
        if (tag == null || tag.size() == 0) {
            return true
        }
        return false
    }

    static int getCollectSize(Collection list) {
        if (list == null) {
            return 0
        }
        return list.size()
    }

    //判断属性值是不是Id类型
    static boolean isIdValue(String attrValue) {
        if (isEmpty(attrValue)) {
            return false
        }
        if (attrValue.startsWith("@")) {
            return true
        }
    }
}