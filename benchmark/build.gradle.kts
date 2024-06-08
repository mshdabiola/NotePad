plugins {
    id("com.android.test")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.mshdabiola.benchmark"
    compileSdk = 33

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }


    defaultConfig {
        minSdk = 24
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        // This benchmark buildType is used for benchmarking, and should function like your
        // release build (for example, with minification on). It's signed with a debug key
        // for easy local/CI testing.
        val benchmark by creating {
            // Keep the build type debuggable so we can attach a debugger if needed.
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks.add("release")
        }
    }


// Use the same flavor dimensions as the application to allow generating Baseline Profiles on prod,
    // which is more close to what will be shipped to users (no fake data), but has ability to run the
    // benchmarks on demo, so we benchmark on stable data.
    configureFlavors(this) { flavor ->
        buildConfigField(
            "String",
            "APP_FLAVOR_SUFFIX",
            "\"${flavor.applicationIdSuffix ?: ""}\""
        )
    }

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true

//    testOptions {
//        managedDevices {
//            devices {
//                create<ManagedVirtualDevice>("pixel4Api33") {
//                    device = "Pixel 4"
//                    apiLevel = 33
//                    systemImageSource = "aosp"
//                }
//            }
//        }
//    }
//    targetProjectPath = ":app"
//    experimentalProperties["android.experimental.self-instrumenting"] = true
}

dependencies {
    implementation(libs.androidx.test.core)
    implementation(libs.androidx.test.espresso.core)
    implementation(libs.androidx.test.ext)
    implementation(libs.androidx.test.runner)
    implementation(libs.androidx.test.rules)
    implementation(libs.androidx.test.uiautomator)
    implementation(libs.androidx.benchmark.macro)
    implementation(libs.androidx.profileinstaller)
}

androidComponents {
    beforeVariants {
        it.enable = it.buildType == "benchmark"
    }
}
