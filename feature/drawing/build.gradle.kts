plugins {
    id("mshdabiola.android.feature")
}

android {
    namespace = "com.mshdabiola.drawing"
}
dependencies {
    implementation(project(":modules:worker"))
}

