plugins {
    id("mshdabiola.android.feature")
}

android {
    namespace = "com.mshdabiola.editscreen"
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.play.services.mlkit.text.recognition)
}

