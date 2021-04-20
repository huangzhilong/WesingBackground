package com.tencent.wesing.background.plugin.util

/**
 * create by zlonghuang on 2021/4/19
 **/

class BackgroundUtil {

    static boolean isEmpty(String tag) {
        if (tag == null || tag.size() == 0) {
            return true
        }
        reurn false
    }

    static int getCollectSize(Collection list) {
        if (list == null) {
            return 0
        }
        return list.size()
    }
}