package com.explore.hook

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

class MethodInsertAdapter(
    api: Int,
    mv: MethodVisitor,
    access: Int,
    name: String,
    descriptor: String,
    private val config: InstrumentItem
) : AdviceAdapter(api, mv, access, name, descriptor) {

    var isInjected = false

    override fun onMethodEnter() {
        super.onMethodEnter()
        if (config.insertLocation.get() == "onMethodEnter") {
            injectCode()
        }
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        if (config.insertLocation.get() == "onMethodExit") {
            injectCode()
        }
        if (isInjected) {
            showParams(opcode)
        }
    }

    private fun injectCode() {
        val injectClass = config.injectClass.get()        // 当前类 internalName，例如 com/example/Foo
        val injectMethod = config.injectMethod.get()      // 方法名
        val injectMethodDesc = config.injectMethodDesc.get() // 方法描述符，例如 ()V
        val callType = config.injectCallType.orNull ?: "static"

        if (callType == "virtual") {
            // 1. 加载 this
            mv.visitVarInsn(ALOAD, 0)

            // 2. 调用当前类的实例方法
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                injectClass,        // class internal name (e.g., com/example/Foo)
                injectMethod,       // method name
                injectMethodDesc,   // method descriptor
                false               // isInterface
            )
        } else {
            // 默认静态调用
            mv.visitMethodInsn(
                INVOKESTATIC,
                injectClass,
                injectMethod,
                injectMethodDesc,
                false
            )
        }
        isInjected = true
    }

    fun showParams(opcode: Int) {
        val argTypes = Type.getArgumentTypes(methodDesc)
        if (argTypes.isEmpty()) return

        // 1. 准备 Log.d 的参数
        mv.visitFieldInsn(
            GETSTATIC,
            "android/util/Log",
            "d",
            "Ljava/io/PrintStream;"
        ) // or use Log.d below
        mv.visitLdcInsn("Hook")

        // 2. 构建 StringBuilder
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder")
        mv.visitInsn(DUP)
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false)

        var index = if ((access and ACC_STATIC) == 0) 1 else 0 // 非静态方法，第一个 slot 是 this

        argTypes.forEachIndexed { i, type ->
            mv.visitLdcInsn("arg$i=")
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                false
            )

            when (type.sort) {
                Type.BOOLEAN, Type.CHAR, Type.BYTE, Type.SHORT, Type.INT -> {
                    mv.visitVarInsn(ILOAD, index)
                    mv.visitMethodInsn(
                        INVOKEVIRTUAL,
                        "java/lang/StringBuilder",
                        "append",
                        "(I)Ljava/lang/StringBuilder;",
                        false
                    )
                }

                Type.LONG -> {
                    mv.visitVarInsn(LLOAD, index)
                    mv.visitMethodInsn(
                        INVOKEVIRTUAL,
                        "java/lang/StringBuilder",
                        "append",
                        "(J)Ljava/lang/StringBuilder;",
                        false
                    )
                    index += 1
                }

                Type.FLOAT -> {
                    mv.visitVarInsn(FLOAD, index)
                    mv.visitMethodInsn(
                        INVOKEVIRTUAL,
                        "java/lang/StringBuilder",
                        "append",
                        "(F)Ljava/lang/StringBuilder;",
                        false
                    )
                }

                Type.DOUBLE -> {
                    mv.visitVarInsn(DLOAD, index)
                    mv.visitMethodInsn(
                        INVOKEVIRTUAL,
                        "java/lang/StringBuilder",
                        "append",
                        "(D)Ljava/lang/StringBuilder;",
                        false
                    )
                    index += 1
                }

                Type.ARRAY, Type.OBJECT -> {
                    mv.visitVarInsn(ALOAD, index)
                    mv.visitMethodInsn(
                        INVOKEVIRTUAL,
                        "java/lang/StringBuilder",
                        "append",
                        "(Ljava/lang/Object;)Ljava/lang/StringBuilder;",
                        false
                    )
                }
            }
            mv.visitLdcInsn(", ") // append comma
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                false
            )

            index += 1
        }

        // 3. 转成 String
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "toString",
            "()Ljava/lang/String;",
            false
        )

        // 4. 调用 Log.d(tag, message)
        mv.visitMethodInsn(
            INVOKESTATIC,
            "android/util/Log",
            "d",
            "(Ljava/lang/String;Ljava/lang/String;)I",
            false
        )

        // 5. 丢弃返回值
        mv.visitInsn(POP)
    }
}
