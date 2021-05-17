package com.tencent.wesing.background.plugin.ams

import com.tencent.wesing.background.plugin.ams.bean.AttributeInfo
import com.tencent.wesing.background.plugin.util.BackgroundUtil
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

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
        createAttributeParams()
        super.visitEnd()
    }

    private void insertAttributeToMap() {
        FieldVisitor fieldVisitor
        MethodVisitor methodVisitor
        // 生成赋值静态方法
        methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "initDrawableDataMap", "()V", null, null)
        for (int i = 0; i < attributeInfoList.size(); i++) {
            AttributeInfo info = attributeInfoList.get(i)
            if (info == null || BackgroundUtil.isEmpty(info.name) || BackgroundUtil.isEmpty(info.value)) {
                continue
            }
        }
    }

    private void createAttributeParams() {
        //生成属性值的方式
        /**
         *  public static final Object[] app_background_param1;
         *  public static final Object[] app_background_param2;
         *  public static final Object[] app_background_param3;
         *
         *  public static void initDrawableDataMap() {
         *      app_background_param1 = new Object[]{"2131165341", "134152442", "79691784", "rectangle", "2131100039", "10dp", "10dp", "10dp", "10dp", "10dp", "10dp", "10dp", "10dp", "80dp", "300dp", "2131034318", "2131034145", "4dp", "3dp", "2131100038"};
         *      app_background_param2 = new Object[]{"2131165339", "5179907", "4721153", "2130968580", "oval", "2131296277", "17170447", "10dp", "10dp", "10dp", "2131100038", "2131034318"};
         *      app_background_param3 = new Object[]{"2131165340", "4194546", "4194304", "oval", "10dp", "10dp", "10dp", "10dp", "17170444"};
         *  }
         */
        FieldVisitor fieldVisitor
        MethodVisitor methodVisitor
        // 生成赋值静态方法
        methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "initDrawableDataMap", "()V", null, null)
        for (int i = 0; i < attributeInfoList.size(); i++) {
            AttributeInfo info = attributeInfoList.get(i)
            if (info == null || BackgroundUtil.isEmpty(info.name) || BackgroundUtil.isEmpty(info.value)) {
                continue
            }
            //生成对应的静态属性
            fieldVisitor = classWriter.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC, info.name, "[Ljava/lang/Object;", null, null)
            fieldVisitor.visitEnd()


            String[] values = info.value.split(",")
            int length = values.length
            // 根据长度创建数组
            visitInsn(methodVisitor, length)
            methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object")
            for (int j = 0; j < values.length; j++) {
                //把每个分割的字符串存储到数组
                methodVisitor.visitInsn(Opcodes.DUP)
                visitInsn(methodVisitor, j)  //数组下标
                methodVisitor.visitLdcInsn(values[j])
                methodVisitor.visitInsn(Opcodes.AASTORE)

                //整数存储方式
                /*
                methodVisitor.visitInsn(Opcodes.DUP);
                methodVisitor.visitInsn(Opcodes.ICONST_0);
                methodVisitor.visitIntInsn(Opcodes.SIPUSH, 11293);
                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                methodVisitor.visitInsn(Opcodes.AASTORE);


                methodVisitor.visitInsn(Opcodes.DUP);
                methodVisitor.visitInsn(Opcodes.ICONST_3);
                methodVisitor.visitLdcInsn(new Integer(2130968580));
                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                methodVisitor.visitInsn(Opcodes.AASTORE);
                */
            }
            //把值赋值给定义的属性
            methodVisitor.visitFieldInsn(Opcodes.PUTSTATIC, "com/tencent/wesing/background/lib/bean/TMEBackgroundMap", info.name, "[Ljava/lang/Object;")
        }
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitMaxs(4, 0)
        methodVisitor.visitEnd()
    }

    /*
       取值-1~5采用iconst指令　　
       取值-128~127采用bipush指令
       取值-32768~32767采用sipush指令
       取值-2147483648~2147483647采用 ldc 指令。
       比如对应int型才该方式只能把-1,0,1,2,3,4,5（分别采用iconst_m1,iconst_0, iconst_1, iconst_2, iconst_3, iconst_4, iconst_5）
    */
    private void visitInsn(MethodVisitor methodVisitor, int value) {
        if (value == -1) {
            methodVisitor.visitInsn(Opcodes.ICONST_M1)
            return
        }
        if (value == 0) {
            methodVisitor.visitInsn(Opcodes.ICONST_0)
            return
        }
        if (value == 1) {
            methodVisitor.visitInsn(Opcodes.ICONST_1)
            return
        }
        if (value == 2) {
            methodVisitor.visitInsn(Opcodes.ICONST_2)
            return
        }
        if (value == 3) {
            methodVisitor.visitInsn(Opcodes.ICONST_3)
            return
        }
        if (value == 4) {
            methodVisitor.visitInsn(Opcodes.ICONST_4)
            return
        }
        if (value == 5) {
            methodVisitor.visitInsn(Opcodes.ICONST_5)
            return
        }
        if ((value > 5 && value <= 127) || (value < -1 && value >= -128)) {
            methodVisitor.visitIntInsn(Opcodes.BIPUSH, value)
            return
        }
        if ((value > 127 && value <= 32767) || (value < -128 && value >= -32768)) {
            methodVisitor.visitIntInsn(Opcodes.SIPUSH, value)
            return
        }
        methodVisitor.visitLdcInsn(new Integer(value))
    }
}
