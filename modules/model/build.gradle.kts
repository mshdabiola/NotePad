plugins {
    id("mshdabiola.jvm.library")
    alias(libs.plugins.kotlin.serialization)

}

dependencies{
    api(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)

}