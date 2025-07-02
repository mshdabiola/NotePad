/*
 *abiola 2024
 */
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("mshdabiola.android.library")
    id("mshdabiola.android.library.compose")

    id("mshdabiola.android.hilt")
}

android {
    namespace = "com.mshdabiola.analytics"
}

dependencies {
    implementation(libs.androidx.compose.runtime)

    //  prodImplementation(platform(libs.firebase.bom))
    googlePlayImplementation(platform(libs.firebase.bom))

    googlePlayImplementation(libs.firebase.analytics)
}
