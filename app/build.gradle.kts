import com.explore.hook.InstrumentItem

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hook.any)
}

android {
    namespace = "com.explore.hookany"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.explore.hookany"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}
hookConfig.configs.set(
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

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}