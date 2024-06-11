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

    debugApi(libs.androidx.compose.ui.testManifest)
    api(kotlin("test"))
    api(libs.androidx.compose.ui.test)
    api(libs.roborazzi)

    api(project(":modules:analytics"))
    api(project(":modules:data"))
    api(project(":modules:model"))


    debugApi(libs.androidx.compose.ui.testManifest)

    implementation(libs.accompanist.testharness)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.test.rules)
    implementation(libs.hilt.android.testing)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.kotlinx.datetime)
    implementation(libs.robolectric.shadows)
    implementation(project(":modules:common"))
    implementation(project(":modules:designsystem"))
}