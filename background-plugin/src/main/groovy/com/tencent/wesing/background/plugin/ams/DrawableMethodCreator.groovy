package com.tencent.wesing.background.plugin.ams

import com.tencent.wesing.background.plugin.ams.bean.DrawableEntity
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.MethodInsnNode

/**
 * create by zlonghuang on 2021/5/30
 **/

class DrawableMethodCreator {

    private static final List<DrawableEntity> DRAWABLE_LIST = new ArrayList<>()
    private static DrawableEntity TARGET_DRAWABLE = null

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


    static DrawableEntity getTargetDrawableEntity() {
        if (TARGET_DRAWABLE == null) {
            TARGET_DRAWABLE = new DrawableEntity()
            TARGET_DRAWABLE.opcode = Opcodes.INVOKEVIRTUAL
            TARGET_DRAWABLE.name = "createDrawableById"
            TARGET_DRAWABLE.owner = "com/tencent/wesing/background/lib/drawable/TMEBackgroundDrawableFactory"
            TARGET_DRAWABLE.desc = "(I)Landroid/graphics/drawable/GradientDrawable;"
        }
        return TARGET_DRAWABLE
    }
}