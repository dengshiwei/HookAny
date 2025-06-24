package com.explore.hook

import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.internal.impldep.kotlinx.serialization.Serializable

abstract class HookConfig {
    abstract val configs: ListProperty<InstrumentItem>
}

@Serializable
class InstrumentItem {
    @get:Input
    lateinit var targetClass: String

    @get:Input
    lateinit var methodName: String

    @get:Input
    lateinit var methodDesc: String

    @get:Input
    lateinit var insertLocation: String // "onMethodEnter" or "onMethodExit"
}

interface ConfigInstrumentParams : InstrumentationParameters {
    @get:Input
    var configs: ListProperty<InstrumentItem>
}