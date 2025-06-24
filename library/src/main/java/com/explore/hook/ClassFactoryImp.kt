package com.explore.hook

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

abstract class ClassFactoryImp : AsmClassVisitorFactory<ConfigInstrumentParams> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return ClassInsertVisitor(Opcodes.ASM9, nextClassVisitor, parameters.get().configs)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return true
    }
}