package com.explore.hook

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

class MethodInsertAdapter(
    api: Int,
    mv: MethodVisitor,
    access: Int,
    name: String,
    descriptor: String,
    private val config: InstrumentItem
) : AdviceAdapter(api, mv, access, name, descriptor) {

    override fun onMethodEnter() {
        if (config.insertLocation == "onMethodEnter") {
            injectCode()
        }
    }

    override fun onMethodExit(opcode: Int) {
        if (config.insertLocation == "onMethodExit") {
            injectCode()
        }
    }

    private fun injectCode() {
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitLdcInsn(">> Inserted by ASM for ${config.methodName}")
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
    }
}
