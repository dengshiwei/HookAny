### 背景
在做技术调研时，针对一些三方 SDK 的细节逻辑无法判断，所以萌生在指定方法通过插桩的方式来实现某些函数的调研，或者日志打印。辅助开发中的应用问题分析。

### 使用步骤
目前是在 AGP8.X 环境发布到 Portal gradle 环境，针对 maven 的发布可自行使用源码发布离线包来实现。
下载 repo 包放到工程目录下。分别在 gradle 配置中集成插件。集成完成后再对应的 app  build.gradle 中配置 hook 的函数信息。

```
hookConfig {
    debug = true
    configs.set(
        listOf(
            objects.newInstance(InstrumentItem::class.java).apply {
                targetClass.set("com/explore/hookany/MainActivity")
                methodName.set("injectMethod")
                methodDesc.set("()V")
                insertLocation.set("onMethodEnter")
                injectClass.set("com/explore/hookany/MainActivity")
                injectMethod.set("logSomething")
                injectMethodDesc.set("()V") // 如果有参数就用 "(Ljava/lang/String;)V"
                injectCallType.set("virtual") // 非静态方法
            },
            objects.newInstance(InstrumentItem::class.java).apply {
                targetClass.set("com/explore/hookany/MainActivity")
                methodName.set("injectMethodParams")
                methodDesc.set("(I)V")
                insertLocation.set("onMethodEnter")
                injectClass.set("java/lang/System")
                injectMethod.set("currentTimeMillis")
                injectMethodDesc.set("()V") // 如果有参数就用 "(Ljava/lang/String;)V"
                injectCallType.set("static") // 静态方法
            },
        )
    )
}
```

详细信息参照 demo
