plugins {
    id("mshdabiola.android.feature")
}

android {
    namespace = "com.mshdabiola.drawing"
}
dependencies {
    implementation(project(":core:worker"))
}

