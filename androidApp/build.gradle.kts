plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

val javaVersion = libs.versions.java.get()

android {
    namespace = "com.hendramarihot.currencyconverter.android"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "com.hendramarihot.currencyconverter"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(javaVersion)
        targetCompatibility = JavaVersion.toVersion(javaVersion)
    }
    buildFeatures {
        compose = true
    }
}

// Kotlin comes from AGP's built-in support.
// Pin the JDK that runs the compilers so builds match across machines and CI.
kotlin {
    jvmToolchain(javaVersion.toInt())
}

dependencies {
    implementation(project(":shared"))
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.kotlinx.coroutines.android)
    debugImplementation(libs.compose.ui.tooling)
}
