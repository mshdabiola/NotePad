/*
 *abiola 2024
 */
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("mshdabiola.android.library")

    id("mshdabiola.android.hilt")
    id("kotlinx-serialization")
}

android {
    namespace = "com.mshdabiola.data"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    api(project(":modules:common"))
    api(project(":modules:database"))
    api(project(":modules:datastore"))
    api(project(":modules:model"))
    implementation(projects.modules.common)

    implementation(project(":modules:analytics"))

    implementation(libs.kotlinx.datetime)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(project(":modules:testing"))

}
