plugins {
    id("mshdabiola.android.feature")
}

android {
    namespace = "com.mshdabiola.gallery"
}
dependencies {
    implementation(libs.telephoto.zoomable.image)
    googlePlayImplementation(libs.play.services.mlkit.text.recognition)


}
