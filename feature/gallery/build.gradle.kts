plugins {
    id("mshdabiola.android.feature")
}

android {
    namespace = "com.mshdabiola.gallery"
}

dependencies {
    implementation(libs.telephoto.zoomable.image)
    googlePlayImplementation(libs.play.services.mlkit.text.recognition)
    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.modules.testing)
    implementation(libs.kotlinx.datetime)


    androidTestImplementation(projects.modules.testing)

}