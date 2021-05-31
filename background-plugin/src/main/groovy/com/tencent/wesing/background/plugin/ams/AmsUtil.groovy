package com.tencent.wesing.background.plugin.ams

import com.tencent.wesing.background.plugin.ams.ClassShapeXmlAdapterVisitor.IVisitListener
import com.tencent.wesing.background.plugin.ams.bean.AttributeInfo
import com.tencent.wesing.background.plugin.ams.bean.DrawableEntity
import com.tencent.wesing.background.plugin.util.LogUtil
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode


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

    static void InsertTMEBackgroundMapClassAttribute(File f, List<AttributeInfo> attributeInfoList) {
        try {
            FileInputStream fis = new FileInputStream(f)
            ClassReader classReader = new ClassReader(fis)
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES)
            InsertBackgroundAttributeClassAdapterVisitor insertBackgroundAttributeClassAdapterVisitor = new InsertBackgroundAttributeClassAdapterVisitor(classWriter, attributeInfoList)
            //分析，处理结果写入cw EXPAND_FRAMES：栈图以扩展格式进行访问
            classReader.accept(insertBackgroundAttributeClassAdapterVisitor, ClassReader.EXPAND_FRAMES);

            //覆盖自己
            byte [] newClassByte = classWriter.toByteArray()
            FileOutputStream fos = new FileOutputStream(f)
            fos.write(newClassByte)
            fos.close()

            fis.close()
        } catch (Exception e) {
            LogUtil.logI(TAG, "InsertTMEBackgroundMapClassAttribute ex: $e")
        }
    }

    static void onHookCodeCreateDrawable(File f) {
        try {
            String path = f.absolutePath
            boolean isHook = false
            FileInputStream fis = new FileInputStream(f)
            ClassReader classReader = new ClassReader(fis)
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES)
            ClassNode classNode = new ClassNode(Opcodes.ASM7)
            classReader.accept(classNode, 0)
            Iterator<MethodNode> iterator = classNode.methods.iterator()
            while (iterator.hasNext()) {
                MethodNode node = iterator.next()
                if (node == null || node.instructions == null || node.instructions.iterator()== null) {
                    continue
                }
                Iterator<AbstractInsnNode> eIterator = node.instructions.iterator().iterator()
                while (eIterator.hasNext()) {
                    AbstractInsnNode abstractInsnNode = eIterator.next()
                    if (abstractInsnNode instanceof MethodInsnNode) {
                        MethodInsnNode kk = (MethodInsnNode) abstractInsnNode
                        if (DrawableMethodCreator.isDrawableMethodCreator(kk)) {
                            DrawableEntity targetEntity = DrawableMethodCreator.getTargetDrawableEntity()
                            kk.name = targetEntity.name
                            kk.desc = targetEntity.desc
                            kk.opcode = targetEntity.opcode
                            kk.owner = targetEntity.owner
                            isHook = true
                        }
                    }
                }
            }
            if (isHook) {
                LogUtil.logI(TAG, "onHookCodeCreateDrawable fileName: $path")
            }
            classNode.accept(classWriter)
            //覆盖自己
            byte [] newClassByte = classWriter.toByteArray()
            FileOutputStream fos = new FileOutputStream(f)
            fos.write(newClassByte)
            fos.close()

            fis.close()
        } catch (Exception e) {
            LogUtil.logI(TAG, "onHookCodeCreateDrawable ex: $e")
        }
    }
}
