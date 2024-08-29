/*
 *abiola 2024
 */
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("mshdabiola.android.library")
    id("mshdabiola.android.library.compose")
    alias(libs.plugins.kotlin.serialization)

}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    namespace = "com.mshdabiola.ui"
}

dependencies {
    api(libs.androidx.metrics)
    api(project(":modules:analytics"))
    api(project(":modules:designsystem"))
    api(project(":modules:model"))

    implementation(libs.androidx.browser)
    implementation(libs.coil.kt)
    api(libs.coil.kt.compose)
    implementation(libs.kotlinx.datetime)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.activity.compose)


    androidTestImplementation(project(":modules:testing"))
}
