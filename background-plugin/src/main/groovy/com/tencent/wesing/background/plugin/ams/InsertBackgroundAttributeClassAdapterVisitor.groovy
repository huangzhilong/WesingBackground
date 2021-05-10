package com.tencent.wesing.background.plugin.ams

import com.tencent.wesing.background.plugin.ams.bean.AttributeInfo
import org.objectweb.asm.*

/**
 * create by zlonghuang on 2021/5/7
 **/

class InsertBackgroundAttributeClassAdapterVisitor extends ClassVisitor {

    private ClassWriter classWriter
    private List<AttributeInfo> attributeInfoList

    InsertBackgroundAttributeClassAdapterVisitor(ClassWriter classWriter, List<AttributeInfo> attributeInfoList) {
        super(Opcodes.ASM7, classWriter)
        this.classWriter = classWriter
        this.attributeInfoList = attributeInfoList
    }

    @Override
    void visitEnd() {
        if (attributeInfoList != null && attributeInfoList.size() > 0) {
            for (int i = 0; i < attributeInfoList.size(); i++) {
                AttributeInfo info = attributeInfoList.get(i)
                classWriter.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC,
                        info.name, "Ljava/lang/String;", null, info.value)
            }
        }
        super.visitEnd()
    }
}
