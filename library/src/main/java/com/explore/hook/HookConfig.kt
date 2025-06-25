package com.explore.hook

import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import javax.inject.Inject

abstract class HookConfig {
    abstract val configs: ListProperty<InstrumentItem>
    abstract val debug: Property<Boolean>
}

abstract class InstrumentItem @Inject constructor(objectFactory: ObjectFactory) {

    @get:Input
    abstract val targetClass: Property<String>

    @get:Input
    abstract val methodName: Property<String>

    @get:Input
    abstract val methodDesc: Property<String>

    @get:Input
    abstract val insertLocation: Property<String>  // e.g. "onMethodEnter" or "onMethodExit"

    @get:Input
    abstract val injectCallType: Property<String>  // e.g. "static" or "virtual"

    // 用于指定插入的方法
    @get:Input
    abstract val injectClass: Property<String> // e.g. "com/example/InjectUtils"

    @get:Input
    abstract val injectMethod: Property<String> // e.g. "logEntry"

    @get:Input
    abstract val injectMethodDesc: Property<String> // e.g. "()V"
}

interface ConfigInstrumentParams : InstrumentationParameters {
    @get:Input
    var configs: ListProperty<InstrumentItem>

    @get:Input
    @get:Optional
    val debug: Property<Boolean>
}