plugins {
    id("mshdabiola.android.library")
    id("mshdabiola.android.library.compose")
}

android {
    namespace = "com.mshdabiola.bottomsheet"

}
dependencies {
    implementation("androidx.compose.material:material:1.3.1")

    implementation(libs.androidx.activity.compose)

}
