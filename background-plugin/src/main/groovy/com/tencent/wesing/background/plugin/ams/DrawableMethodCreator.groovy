package com.tencent.wesing.background.plugin.ams

import com.tencent.wesing.background.plugin.ams.bean.DrawableEntity
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.MethodInsnNode

/**
 * create by zlonghuang on 2021/5/30
 **/

class DrawableMethodCreator {

    private static DrawableEntity CONTEXT_COMPAT_DRAWABLE
    private static DrawableEntity RESOURCE_DRAWABLE

    static {
        //用于替换ContextCompat.getDrawable
        CONTEXT_COMPAT_DRAWABLE = new DrawableEntity()
        CONTEXT_COMPAT_DRAWABLE.name = "createDrawableById"
        CONTEXT_COMPAT_DRAWABLE.opcode = Opcodes.INVOKESTATIC
        CONTEXT_COMPAT_DRAWABLE.owner = "com/tencent/wesing/background/lib/drawable/TMEBackgroundDrawableFactory"
        CONTEXT_COMPAT_DRAWABLE.desc = "(Landroid/content/Context;I)Landroid/graphics/drawable/Drawable;"

        //用于替换 mContext.getResources().getDrawable   mContext.getDrawable
        RESOURCE_DRAWABLE = new DrawableEntity()
        RESOURCE_DRAWABLE.name = "createDrawableById"
        RESOURCE_DRAWABLE.opcode = Opcodes.INVOKESTATIC
        RESOURCE_DRAWABLE.owner = "com/tencent/wesing/background/lib/drawable/TMEBackgroundDrawableFactory"
        RESOURCE_DRAWABLE.desc = "(I)Landroid/graphics/drawable/Drawable;"
    }

    static DrawableEntity getContextCompatDrawable() {
        return CONTEXT_COMPAT_DRAWABLE
    }

    static DrawableEntity getResourceDrawable() {
        return RESOURCE_DRAWABLE
    }

    static boolean isContextCompatDrawable(MethodInsnNode node) {
        if (node == null) {
            return false
        }
        if (Opcodes.INVOKESTATIC == node.opcode && "getDrawable" == node.name && "androidx/core/content/ContextCompat" == node.owner && "(Landroid/content/Context;I)Landroid/graphics/drawable/Drawable;" == node.desc) {
            return true
        }
        return false
    }


    static boolean isResourceDrawable(MethodInsnNode node) {
        if (node == null) {
            return false
        }
        if (Opcodes.INVOKEVIRTUAL == node.opcode && "getDrawable" == node.name && "android/content/res/Resources" == node.owner && "(I)Landroid/graphics/drawable/Drawable;" == node.desc) {
            return true
        }
        return false
    }

    static boolean isContextDrawable(MethodInsnNode node) {
        if (node == null) {
            return false
        }
        if (Opcodes.INVOKEVIRTUAL == node.opcode && "getDrawable" == node.name && "android/content/Context" == node.owner && "(I)Landroid/graphics/drawable/Drawable;" == node.desc) {
            return true
        }
        return false
    }
}