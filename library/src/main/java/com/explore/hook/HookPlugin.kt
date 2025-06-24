package com.explore.hook

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import kotlin.reflect.full.declaredMemberProperties

class HookPlugin : Plugin<Project> {
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
                    if (property.name == "configs") {
                        val result = property.getter.call(hookConfig)
                        it.configs = result as ListProperty<InstrumentItem>
                    }
                }
            }

            variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
        }
    }
}