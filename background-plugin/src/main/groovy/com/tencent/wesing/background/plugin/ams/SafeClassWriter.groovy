package com.tencent.wesing.background.plugin.ams

import com.tencent.wesing.background.plugin.util.LogUtil
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

/**
 * create by zlonghuang on 2021/6/7
 * 主要解决getCommonSuperClass 找不到类的异常
 **/

class SafeClassWriter extends ClassWriter {

    SafeClassWriter(int flags) {
        super(flags)
        //LogUtil.logI("longpo", "${getClassLoader().class.name}   ${getClassLoader()}")
    }

    SafeClassWriter(ClassReader classReader, int flags) {
        super(classReader, flags)
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        return "java/lang/Object";
    }

    @Override
    protected ClassLoader getClassLoader() {
        return super.getClassLoader()
    }
}