/*
 *abiola 2024
 */
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("mshdabiola.android.library")
    id("mshdabiola.android.hilt")
}

android {
    namespace = "com.mshdabiola.domain"
}
dependencies {
    api(projects.modules.data)
    api(projects.modules.model)


    testImplementation(projects.modules.testing)
}