package com.tencent.wesing.background.plugin.ams

import com.tencent.wesing.background.plugin.util.LogUtil
import jdk.internal.org.objectweb.asm.Label
import org.objectweb.asm.Attribute
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes


/**
 * create by zlonghuang on 2021/5/7
 **/

class ClassAdapterVisitor extends ClassVisitor {

    public static final String TAG = "ClassAdapterVisitor"

    private ClassWriter classWriter

    ClassAdapterVisitor(ClassWriter classWriter) {
        super(Opcodes.ASM7, classWriter)
        this.classWriter = classWriter
    }

    @Override
    void visitAttribute(Attribute attribute) {
        super.visitAttribute(attribute)
    }

    @Override
    MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
        LogUtil.logI(TAG, "visitMethod $i  $s  $s1   $s2  $strings")
        return super.visitMethod(i, s, s1, s2, strings)
    }

    @Override
    FieldVisitor visitField(int i, String s, String s1, String s2, Object o) {
        LogUtil.logI(TAG, "visitField $i  $s  $s1   $s2  $o")
        return super.visitField(i, s, s1, s2, o)
    }

    @Override
    void visitEnd() {
        LogUtil.logI(TAG, "visitEnd!!!")
        classWriter.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC,
                "USR", "Ljava/lang/String;", null, "CrazyMo_")

        //添加hashMap
        classWriter.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC, "shapeMap", "Ljava/util/HashMap;", "Ljava/util/HashMap<Ljava/lang/Integer;Ljava/lang/String;>;", null)
        super.visitEnd()
    }
}
