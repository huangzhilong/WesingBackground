package com.tencent.wesing.background.plugin

/**
 * create by zlonghuang on 2021/6/5
 **/

class WesingExtensionContainer {

    /**
     * 该模块是否开启插件，默认开启
     */
    boolean isOpen = true

    /**
     * 是否只解析shape属性，不hook 修改xml
     */
    boolean isOnlyAnalysisShape = false

    WesingExtensionContainer() {
        isOpen = true
        isOnlyAnalysisShape = false
    }
}