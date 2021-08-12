package com.tencent.wesing.background.plugin.util

import com.android.build.gradle.AppPlugin
import org.gradle.api.Project

import java.util.regex.Matcher
import java.util.regex.Pattern

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

    //是否是数字
    static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*")
        Matcher isNum = pattern.matcher(str)
        if( !isNum.matches() ){
            return false
        }
        return true
    }

    /**
     * 获取app project
     */
    static Project getAppProject(Project project) {
        if (project == null) {
            return null
        }
        Iterator<Project> iterator = project.rootProject.allprojects.iterator()
        while (iterator.hasNext()) {
            Project p = iterator.next()
            if (p.plugins.hasPlugin(AppPlugin)) {
                return p
            }
        }
        return null
    }
}