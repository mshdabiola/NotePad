/*
 *abiola 2024
 */
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("mshdabiola.android.library")
    id("mshdabiola.android.library.jacoco")

    id("mshdabiola.android.hilt")
    id("mshdabiola.android.room")
}

android {
    namespace = "com.mshdabiola.database"


}

dependencies {
    implementation(project(":modules:model"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)

    androidTestImplementation(project(":modules:testing"))
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")

}