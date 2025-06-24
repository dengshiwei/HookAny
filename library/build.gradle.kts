plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

//apply(from = "../maven_publish.gradle")  // 发布本地 maven 时，由于与 gradle_portal 的自动生成 resources 目录冲突，需要将项目中的 resources 文件拷贝到 main 目录下，然后发布到 maven
apply(from = "../gradle_portal.gradle")

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    implementation(libs.asm)
    implementation(libs.asmCommons)
    implementation(libs.asmAnalysis)
    implementation(libs.asmUtil)
    compileOnly(libs.gradle)
    compileOnly(libs.gradle.api)
    implementation(gradleApi())
}
