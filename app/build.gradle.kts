import com.mshdabiola.app.BuildType


@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
//    alias(libs.plugins.kotlinAndroid)


    id("mshdabiola.android.application")
    id("mshdabiola.android.application.compose")
    id("mshdabiola.android.application.flavor")
    id("mshdabiola.android.hilt")
    alias(libs.plugins.baselineprofile)

}

android {
    namespace = "com.mshdabiola.playnotepad"

    defaultConfig {
        applicationId = "com.mshdabiola.playnotepad"
        versionCode = libs.versions.versionCode.get().toIntOrNull()
        versionName = System.getenv("VERSION_NAME") ?: libs.versions.versionName.get()

        testInstrumentationRunner = "com.mshdabiola.testing.TestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = BuildType.DEBUG.applicationIdSuffix
            versionNameSuffix=BuildType.DEBUG.versionNameSuffix

        }
        val release = getByName("release") {
            isMinifyEnabled = true
            applicationIdSuffix = BuildType.RELEASE.applicationIdSuffix
            versionNameSuffix=BuildType.RELEASE.versionNameSuffix

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // To publish on the Play store a private signing key is required, but to allow anyone
            // who clones the code to sign and run the release variant, use the debug signing key.
            // TODO: Abstract the signing configuration to a separate file to avoid hardcoding this.
            // signingConfig = signingConfigs.getByName("debug")
            // Ensure Baseline Profile is fresh for release builds.
            baselineProfile.automaticGenerationDuringBuild = true
        }
        create("benchmark") {
            // Enable all the optimizations from release build through initWith(release).
            initWith(release)
            matchingFallbacks.add("release")
            // Debug key signing is available to everyone.
            signingConfig = signingConfigs.getByName("debug")
            // Only use benchmark proguard rules
            proguardFiles("benchmark-rules.pro")
            isMinifyEnabled = true
            applicationIdSuffix = BuildType.BENCHMARK.applicationIdSuffix
        }
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(projects.modules.designsystem)
    implementation(projects.modules.data)
    implementation(projects.modules.ui)

//    implementation(project(":feature:editScreen"))
//    implementation(project(":feature:labelscreen"))
//    implementation(project(":feature:selectlabelscreen"))
//    implementation(project(":feature:searchscreen"))
//    implementation(project(":feature:gallery"))
//    implementation(project(":feature:drawing"))
//    implementation(project(":feature:about"))
//    implementation(project(":feature:mainscreen"))

    implementation(projects.feature.main)
    implementation(projects.feature.detail)




    implementation(libs.kotlinx.serialization.json)


    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.tracing.ktx)
    implementation(libs.androidx.window.core)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.coil.kt)
    implementation(libs.timber)

    ksp(libs.hilt.compiler)

    debugImplementation(libs.androidx.compose.ui.testManifest)
    debugImplementation(projects.uiTestHiltManifest)

    kspTest(libs.hilt.compiler)

    testImplementation(projects.modules.testing)
    testImplementation(libs.androidx.compose.ui.test)
    testImplementation(libs.androidx.work.testing)
    testImplementation(libs.hilt.android.testing)



    androidTestImplementation(projects.modules.testing)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.hilt.android.testing)

    baselineProfile(projects.benchmarks)


    googlePlayImplementation(platform(libs.firebase.bom))
    googlePlayImplementation(libs.firebase.analytics)
    googlePlayImplementation(libs.firebase.performance)
    googlePlayImplementation(libs.firebase.crashlytics)

    googlePlayImplementation(libs.firebase.cloud.messaging)
    googlePlayImplementation(libs.firebase.remoteconfig)
    googlePlayImplementation(libs.firebase.message)
    googlePlayImplementation(libs.firebase.auth)

    googlePlayImplementation(libs.play.game)
    googlePlayImplementation(libs.play.update)
    googlePlayImplementation(libs.play.update.kts)
    googlePlayImplementation(libs.play.review)
    googlePlayImplementation(libs.play.review.kts)
}

baselineProfile {
    // Don't build on every iteration of a full assemble.
    // Instead enable generation directly for the release build variant.
    automaticGenerationDuringBuild = false
}

dependencyGuard {
    configuration("fossReliantReleaseRuntimeClasspath")
    configuration("googlePlayReleaseRuntimeClasspath")
}
