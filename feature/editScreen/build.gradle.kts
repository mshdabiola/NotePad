plugins {
    id("mshdabiola.android.feature")
}

android {
    namespace = "com.mshdabiola.editscreen"
}

dependencies {
    implementation(libs.kotlinx.datetime)
    googlePlayImplementation(libs.play.services.mlkit.text.recognition)
}

