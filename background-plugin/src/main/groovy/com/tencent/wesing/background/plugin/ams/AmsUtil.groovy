package com.tencent.wesing.background.plugin.ams

import com.tencent.wesing.background.plugin.ams.bean.AttributeInfo
import com.tencent.wesing.background.plugin.ams.bean.DrawableEntity
import com.tencent.wesing.background.plugin.util.LogUtil
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.LineNumberNode
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

    static void doHookCodeCreateDrawable(File f) {
        try {
            boolean isHook = false
            FileInputStream fis = new FileInputStream(f)
            ClassReader classReader = new ClassReader(fis)
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES)
            ClassNode classNode = new ClassNode(Opcodes.ASM7)
            classReader.accept(classNode, 0)
            Iterator<MethodNode> iterator = classNode.methods.iterator()
            while (iterator.hasNext()) {
                MethodNode node = iterator.next()
                if (node == null || node.instructions == null || node.instructions.iterator() == null) {
                    continue
                }
                AbstractInsnNode[] abstractInsnNodeList = node.instructions.toArray()
                //需要用行数来区分getDrawable方法，如 TMEBackgroundContext.getContext().getResources().getDrawable(2131165295);
                // 会有3个MethodInsnNode
                // getContext  com/tencent/wesing/background/lib/TMEBackgroundContext  ()Landroid/content/Context;   5
                // getResources  android/content/Context  ()Landroid/content/res/Resources;   5
                // getDrawable  android/content/res/Resources  (I)Landroid/graphics/drawable/Drawable;   5

                List<MethodInsnNode> MethodInsnNodeList = new ArrayList<>()
                boolean isDrawableMethodCreator = false

                for (int i = 0; i < abstractInsnNodeList.length; i++) {
                    AbstractInsnNode abstractInsnNode = abstractInsnNodeList[i]
                    if (abstractInsnNode instanceof MethodInsnNode) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode
                        if (DrawableMethodCreator.isDrawableMethodCreator(methodInsnNode)) {
                            isDrawableMethodCreator = true
                        }
                        MethodInsnNodeList.add(methodInsnNode)
                    } else if (abstractInsnNode instanceof LineNumberNode) {
                        if (!MethodInsnNodeList.isEmpty() && isDrawableMethodCreator) {
                            isHook = true
                            hookDrawable(MethodInsnNodeList, node.instructions)
                        }
                        isDrawableMethodCreator = false
                        MethodInsnNodeList.clear()
                    }
                }
                if (!MethodInsnNodeList.isEmpty() && isDrawableMethodCreator) {
                    isHook = true
                    hookDrawable(MethodInsnNodeList, node.instructions)
                    isDrawableMethodCreator = false
                    MethodInsnNodeList.clear()
                }
            }
            if (isHook) {
                LogUtil.logI(TAG, "onHookCodeCreateDrawable fileName: ${f.absolutePath}")
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

    private static void hookDrawable(List<MethodInsnNode> nodeList, InsnList instructions) {
        if (nodeList == null || nodeList.isEmpty()) {
            return
        }
        DrawableEntity targetDrawableEntityList = DrawableMethodCreator.getTargetDrawableEntityList()
        //留下最后一个
        for (int i = 0; i < nodeList.size() - 1; i++) {
            MethodInsnNode methodInsnNode = nodeList.get(i)
            instructions.remove(methodInsnNode)
        }
        MethodInsnNode lastMethodInsnNode = nodeList.get(nodeList.size() - 1)
        lastMethodInsnNode.name = targetDrawableEntityList.name
        lastMethodInsnNode.desc = targetDrawableEntityList.desc
        lastMethodInsnNode.opcode = targetDrawableEntityList.opcode
        lastMethodInsnNode.owner = targetDrawableEntityList.owner
    }
}
