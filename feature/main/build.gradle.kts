plugins {
    id("mshdabiola.android.feature")
}

android {
    namespace = "com.mshdabiola.main"
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.lottie.compose)

}