package com.tencent.wesing.background.plugin.ams

import com.tencent.wesing.background.plugin.ams.bean.AttributeInfo
import com.tencent.wesing.background.plugin.util.BackgroundUtil
import com.tencent.wesing.background.plugin.util.LogUtil
import org.objectweb.asm.*

/**
 * create by zlonghuang on 2021/5/7
 **/

class ClassShapeXmlAdapterVisitor extends ClassVisitor {

    private ClassWriter classWriter
    private IVisitListener mListener
    private List<AttributeInfo> attributeInfoList = new ArrayList<>()

    void setVisitListener(IVisitListener listener) {
        mListener = listener
    }

    ClassShapeXmlAdapterVisitor(ClassWriter classWriter) {
        super(Opcodes.ASM7, classWriter)
        this.classWriter = classWriter
    }

    @Override
    FieldVisitor visitField(int i, String s, String s1, String s2, Object o) {
        LogUtil.logI("ClassShapeXmlAdapterVisitor", "visitField i: $i  s: $s  s1: $s1  s2: $s2  o: $o")
        if (o != null && !BackgroundUtil.isEmpty(s)) {
            AttributeInfo attributeInfo = new AttributeInfo()
            attributeInfo.name = s
            attributeInfo.value = (String) o
            attributeInfoList.add(attributeInfo)
        }
        return super.visitField(i, s, s1, s2, o)
    }

    @Override
    void visitEnd() {
        if (mListener != null) {
            mListener.onGetShapeXmlAttribute(attributeInfoList)
        }
        super.visitEnd()
    }

    interface IVisitListener {

        void onGetShapeXmlAttribute(List<AttributeInfo> attributeInfoList)
    }
}
