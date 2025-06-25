package com.explore.hook

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import kotlin.reflect.full.declaredMemberProperties

class HookPlugin : Plugin<Project> {
    private val TAG = "HookAny"
    lateinit var hookConfig: HookConfig
    override fun apply(project: Project) {
        println("My plugin applied")
        createExtension(project)
        injectTransform(project)
    }

    private fun createExtension(project: Project) {
        hookConfig = project.extensions.create("hookConfig", HookConfig::class.java)
    }

    private fun injectTransform(project: Project) {
        val androidApp = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidApp.onVariants { variant ->
            variant.instrumentation.transformClassesWith(
                ClassFactoryImp::class.java, // 显式传入工厂类
                InstrumentationScope.ALL
            ) {
                val memberProperties = hookConfig::class.declaredMemberProperties
                memberProperties.forEach { property ->
                    when (property.name) {
                        "debug" -> {
                            HookLogger.ENABLE_HOOK_LOG = (property.getter.call(hookConfig) as Property<Boolean>).getOrElse(false)
                        }

                        "configs" -> {
                            val result = property.getter.call(hookConfig)
                            it.configs = result as ListProperty<InstrumentItem>
                        }
                    }
                }
                printConfig(it.configs)
            }

            variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
        }
    }

    private fun printConfig(configs: ListProperty<InstrumentItem>) {
        configs.get().forEachIndexed { index, item ->
            HookLogger.log(TAG, "第 ${index} 配置：")
            HookLogger.log(TAG, "targetClass = ${item.targetClass.getOrNull()}")
            HookLogger.log(TAG, "methodName  = ${item.methodName.getOrNull()}")
            HookLogger.log(TAG, "methodDesc  = ${item.methodDesc.getOrNull()}")
            HookLogger.log(TAG, "insertLocation = ${item.insertLocation.getOrNull()}")
            HookLogger.log(TAG, "injectClass = ${item.injectClass.getOrNull()}")
            HookLogger.log(TAG, "injectMethod = ${item.injectMethod.getOrNull()}")
            HookLogger.log(TAG, "injectMethodDesc = ${item.injectMethodDesc.getOrNull()}")
            HookLogger.log(TAG, "injectCallType = ${item.injectCallType.getOrNull()}")
        }

    }
}