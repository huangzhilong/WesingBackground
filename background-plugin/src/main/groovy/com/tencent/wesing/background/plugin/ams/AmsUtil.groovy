package com.tencent.wesing.background.plugin.ams

import com.tencent.wesing.background.plugin.ams.bean.AttributeInfo
import com.tencent.wesing.background.plugin.util.LogUtil
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

/**
 * create by zlonghuang on 2021/5/7
 **/

class AmsUtil {

    private static final String TAG = "AmsUtil"

    static void getParseXmlAttributeInfoByClass(File f, ClassShapeXmlAdapterVisitor.IVisitListener listener) {
        try {
            FileInputStream fis = new FileInputStream(f)
            ClassReader classReader = new ClassReader(fis)
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES)
            ClassShapeXmlAdapterVisitor classShapeXmlAdapterVisitor = new ClassShapeXmlAdapterVisitor(classWriter)
            classShapeXmlAdapterVisitor.setVisitListener(listener)
            //分析，处理结果写入cw EXPAND_FRAMES：栈图以扩展格式进行访问
            classReader.accept(classShapeXmlAdapterVisitor, ClassReader.EXPAND_FRAMES);

            fis.close()
        } catch (Exception e) {
            LogUtil.logI(TAG, "getParseXmlAttributeInfoByClass ex: $e")
        }
    }

    static byte[] InsertTMEBackgroundMapClassAttribute(InputStream inputStream, String path, List<AttributeInfo> attributeInfoList) {
        try {
            ClassReader classReader = new ClassReader(inputStream)
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES)
            InsertBackgroundAttributeClassAdapterVisitor insertBackgroundAttributeClassAdapterVisitor = new InsertBackgroundAttributeClassAdapterVisitor(classWriter, attributeInfoList)
            //分析，处理结果写入cw EXPAND_FRAMES：栈图以扩展格式进行访问
            classReader.accept(insertBackgroundAttributeClassAdapterVisitor, ClassReader.EXPAND_FRAMES)

            byte [] newClassByte = classWriter.toByteArray()
            return newClassByte
        } catch (Exception e) {
            LogUtil.logI(TAG, "InsertTMEBackgroundMapClassAttribute fileName: $path  ex: ${e.printStackTrace()}")
        }
        return null
    }
}
