package com.tencent.wesing.background.plugin.ams

import com.tencent.wesing.background.plugin.ams.bean.DrawableEntity
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.MethodInsnNode

/**
 * create by zlonghuang on 2021/5/30
 **/

class DrawableMethodCreator {

    private static final List<DrawableEntity> DRAWABLE_LIST = new ArrayList<>()
    private static final List<DrawableEntity> TARGET_DRAWABLE_LIST = new ArrayList<>()

    private static initDrawableList() {
        DrawableEntity resDrawableEntity = new DrawableEntity()
        resDrawableEntity.opcode = Opcodes.INVOKEVIRTUAL
        resDrawableEntity.owner = "android/content/res/Resources"
        resDrawableEntity.name = "getDrawable"
        resDrawableEntity.desc = "(I)Landroid/graphics/drawable/Drawable;"
        DRAWABLE_LIST.add(resDrawableEntity)

        DrawableEntity compatDrawableEntity = new DrawableEntity()
        compatDrawableEntity.opcode = Opcodes.INVOKESTATIC
        compatDrawableEntity.owner = "androidx/core/content/ContextCompat"
        compatDrawableEntity.name = "getDrawable"
        compatDrawableEntity.desc = "(Landroid/content/Context;I)Landroid/graphics/drawable/Drawable;"
        DRAWABLE_LIST.add(compatDrawableEntity)
    }

    static boolean isDrawableMethodCreator(MethodInsnNode node) {
        if (node == null) {
            return false
        }
        if (DRAWABLE_LIST.isEmpty()) {
            initDrawableList()
        }
        for (int i = 0; i < DRAWABLE_LIST.size(); i++) {
            DrawableEntity entity = DRAWABLE_LIST.get(i)
            if (node.opcode == entity.opcode && node.name == entity.name && node.owner == entity.owner && node.desc == entity.desc) {
                return true
            }
        }
        return false
    }


    static List<DrawableEntity> getTargetDrawableEntityList() {
        if (TARGET_DRAWABLE_LIST.isEmpty()) {
            DrawableEntity instanceDrawable = new DrawableEntity()
            instanceDrawable.opcode = Opcodes.INVOKESTATIC
            instanceDrawable.name = "getInstance"
            instanceDrawable.owner = "com/tencent/wesing/background/lib/drawable/TMEBackgroundDrawableFactory"
            instanceDrawable.desc = "()Lcom/tencent/wesing/background/lib/drawable/TMEBackgroundDrawableFactory;"
            TARGET_DRAWABLE_LIST.add(instanceDrawable)

            DrawableEntity targetDrawable = new DrawableEntity()
            targetDrawable.opcode = Opcodes.INVOKEVIRTUAL
            targetDrawable.name = "createDrawableById"
            targetDrawable.owner = "com/tencent/wesing/background/lib/drawable/TMEBackgroundDrawableFactory"
            targetDrawable.desc = "(I)Landroid/graphics/drawable/Drawable;"
            TARGET_DRAWABLE_LIST.add(targetDrawable)
        }
        return TARGET_DRAWABLE_LIST
    }
}