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
            LogUtil.logI(TAG, "onHookCodeCreateDrawable111 fileName: ${f.absolutePath}")
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
                List<AbstractInsnNode> mLineList = new ArrayList<>()
                AbstractInsnNode[] abstractInsnNodeList = node.instructions.toArray()
                for (int i = 0; i < abstractInsnNodeList.length; i++) {
                    AbstractInsnNode abstractInsnNode = abstractInsnNodeList[i]
                    mLineList.add(abstractInsnNode)
                    if (abstractInsnNode instanceof MethodInsnNode) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode
                        // hook ContextCompatDrawable
                        if (DrawableMethodCreator.isContextCompatDrawable(methodInsnNode)) {
                            DrawableEntity compatDrawableEntity = DrawableMethodCreator.getContextCompatDrawable()
                            methodInsnNode.opcode = compatDrawableEntity.opcode
                            methodInsnNode.desc = compatDrawableEntity.desc
                            methodInsnNode.name = compatDrawableEntity.name
                            methodInsnNode.owner = compatDrawableEntity.owner
                            isHook = true
                        }
                    } else if (abstractInsnNode instanceof LineNumberNode)  {
                        if (!mLineList.isEmpty()) {
                            if (onHandleLineListNode(mLineList, node.instructions)) {
                                isHook = true
                            }
                        }
                        mLineList.clear()
                    }
                }
                if (!mLineList.isEmpty()) {
                    if(onHandleLineListNode(mLineList, node.instructions)) {
                        isHook = true
                    }
                    mLineList.clear()
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

    private static boolean onHandleLineListNode(List<AbstractInsnNode> mLineList, InsnList instructions) {
        if (mLineList.isEmpty()) {
            return false
        }
        int getDrawableIndex = -1
        for (int i = 0; i < mLineList.size(); i++) {
            AbstractInsnNode abstractInsnNode = mLineList.get(i)
            if (abstractInsnNode instanceof MethodInsnNode) {
                MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode
                if (DrawableMethodCreator.isResourceDrawable(methodInsnNode)) {
                    getDrawableIndex = i
                    break
                }
            }
        }
        //把getDrawable上面的getResource以及getContext去除(使用变量的不会有),再把getDrawable替换成自己的
        if (getDrawableIndex > 0) {
            MethodInsnNode drawableInsnNode = (MethodInsnNode) mLineList.get(getDrawableIndex)
            DrawableEntity drawableEntity = DrawableMethodCreator.getResourceDrawable()
            drawableInsnNode.opcode = drawableEntity.opcode
            drawableInsnNode.name = drawableEntity.name
            drawableInsnNode.owner = drawableEntity.owner
            drawableInsnNode.desc = drawableEntity.desc
            for (int i = 0; i < getDrawableIndex; i++) {
                AbstractInsnNode abstractInsnNode = mLineList.get(i)
                if (abstractInsnNode instanceof MethodInsnNode) {
                    MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode
                    if (methodInsnNode.desc == "()Landroid/content/res/Resources;" || methodInsnNode.desc == "()Landroid/content/Context;") {
                        instructions.remove(methodInsnNode)
                    }
                }
            }
            return true
        }
        return false
    }
}
