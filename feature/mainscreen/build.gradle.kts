plugins {
    id("mshdabiola.android.feature")
}

android {
    namespace = "com.mshdabiola.mainscreen"
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.lottie.compose)

}