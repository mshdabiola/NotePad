@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("mshdabiola.android.library")
    id("mshdabiola.android.hilt")
    id("mshdabiola.android.room")
}

android {
    namespace = "com.mshdabiola.database"


}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.androidx.core.ktx)

//    ksp(libs.room.compiler)
//    implementation(libs.bundles.room.bundle)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)

}