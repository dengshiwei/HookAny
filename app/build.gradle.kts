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
        createInstrumentItem(
            "com/explore/hookany/MainActivity",
            "injectMethod",
            "()V",
            "onMethodEnter"
        ),
        createInstrumentItem("com.example.B", "onClick", "(Landroid/view/View;)V", "onMethodExit")
    )
)
fun createInstrumentItem(
    targetClass: String,
    methodName: String,
    methodDesc: String,
    insertLocation: String
): InstrumentItem = objects.newInstance(InstrumentItem::class.java).apply {
    this.targetClass.set(targetClass)
    this.methodName.set(methodName)
    this.methodDesc.set(methodDesc)
    this.insertLocation.set(insertLocation)
}

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