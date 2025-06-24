package com.explore.hook

import org.gradle.api.provider.ListProperty
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

class ClassInsertVisitor(
    api: Int,
    cv: ClassVisitor,
    private val config: ListProperty<InstrumentItem>
) : ClassVisitor(api, cv) {

    private var className: String? = null

    override fun visit(version: Int, access: Int, name: String, signature: String?, superName: String?, interfaces: Array<out String>?) {
        className = name
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(access: Int, name: String, descriptor: String, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        config.get().forEach { itemConfig ->
            if (name == itemConfig.methodName.get() && descriptor == itemConfig.methodDesc.get() && className == itemConfig.targetClass.get()) {
                return MethodInsertAdapter(api, mv, access, name, descriptor, itemConfig)
            }
        }

        return mv
    }
}
