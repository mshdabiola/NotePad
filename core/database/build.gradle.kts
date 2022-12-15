@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("mshdabiola.android.library")
    id("mshdabiola.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mshdabiola.database"

    defaultConfig {

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }

    }

}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.androidx.core.ktx)

    ksp(libs.room.compiler)
    implementation(libs.bundles.room.bundle)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)

}