plugins {
    id("mshdabiola.android.feature")
}

android {
    namespace = "com.mshdabiola.editscreen"
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:18.0.2")
}

