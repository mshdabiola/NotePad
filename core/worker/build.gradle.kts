@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("mshdabiola.android.library")
    id("mshdabiola.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.mshdabiola.worker"
    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
}
dependencies {
    implementation(project(":core:database"))
    implementation(project(":core:model"))

    implementation(project(":core:common"))

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.work.ktx)
    implementation(libs.hilt.ext.work)
    implementation(libs.kotlinx.serialization.json)
    //kapt(libs.hilt.ext.compiler)

}
