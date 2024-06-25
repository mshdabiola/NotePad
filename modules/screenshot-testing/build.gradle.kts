/*
 *abiola 2022
 */
plugins {
    id("mshdabiola.android.library")
    id("mshdabiola.android.library.compose")
    id("mshdabiola.android.hilt")
}

android {
    namespace = "com.mshdabiola.screenshottesting"
}

dependencies {
    api(libs.roborazzi)
    implementation(libs.androidx.compose.ui.test)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui.test)
    implementation(libs.robolectric)
    implementation(projects.modules.common)
    implementation(projects.modules.designsystem)
}
