/*
 *abiola 2024
 */
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("mshdabiola.android.library")
    id("mshdabiola.android.library.compose")
    id("mshdabiola.android.library.jacoco")

    alias(libs.plugins.roborazzi)
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    namespace = "com.mshdabiola.designsystem"
}

dependencies {
    // lintPublish(projects.lint)

    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.material.iconsExtended)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material3.adaptive)
    api(libs.androidx.compose.material3.navigationSuite)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui.util)

    api(libs.kotlinx.collection.immutable)

    implementation(libs.coil.kt.compose)
    implementation(libs.androidx.ui.text.google.fonts)

    testImplementation(libs.androidx.compose.ui.test)
    testImplementation(libs.androidx.compose.ui.testManifest)

    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(projects.modules.testing)
    testImplementation(projects.modules.screenshotTesting)


    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(projects.modules.testing)

}
