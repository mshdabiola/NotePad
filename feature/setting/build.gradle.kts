plugins {
    id("mshdabiola.android.feature")
}

android {
    namespace = "com.mshdabiola.setting"
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.lottie.compose)

}