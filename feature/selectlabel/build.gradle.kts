plugins {
    id("mshdabiola.android.feature")
}

android {
    namespace = "com.mshdabiola.selectlabel"
}

dependencies {
    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.modules.testing)
    implementation(libs.kotlinx.datetime)


    androidTestImplementation(projects.modules.testing)

}