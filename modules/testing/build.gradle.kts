/*
 *abiola 2024
 */
plugins {
    id("mshdabiola.android.library")
    id("mshdabiola.android.library.compose")
    id("mshdabiola.android.hilt")
}

android {
    namespace = "com.mshdabiola.testing"
}
dependencies {
    api(kotlin("test"))
    api(libs.androidx.compose.ui.test)
    api(projects.modules.analytics)
    api(projects.modules.data)
    api(projects.modules.model)

    debugApi(libs.androidx.compose.ui.testManifest)

    api(libs.turbine)
    implementation(libs.androidx.test.rules)
    implementation(libs.hilt.android.testing)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.kotlinx.datetime)
    implementation(projects.modules.common)
    implementation(projects.modules.designsystem)
}