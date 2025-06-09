plugins {
    id("mshdabiola.android.feature")
}

android {
    namespace = "com.mshdabiola.main"
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.lottie.compose)
    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.modules.testing)
    implementation(libs.kotlinx.datetime)


    androidTestImplementation(projects.modules.testing)

}