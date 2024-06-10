/*
 *abiola 2022
 */
import com.android.build.api.dsl.ManagedVirtualDevice
import com.mshdabiola.app.BuildType
import com.mshdabiola.app.configureFlavors
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("mshdabiola.android.test")
    alias(libs.plugins.androidx.baselineprofile)
}

android {
    namespace = "com.mshdabiola.benchmarks"

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "APP_BUILD_TYPE_SUFFIX", "\"\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        // This benchmark buildType is used for benchmarking, and should function like your
        // release build (for example, with minification on). It's signed with a debug key
        // for easy local/CI testing.
        create("benchmark") {
            // Keep the build type debuggable so we can attach a debugger if needed.
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks.add("release")
            buildConfigField(
                "String",
                "APP_BUILD_TYPE_SUFFIX",
                "\"${BuildType.BENCHMARK.applicationIdSuffix ?: ""}\""
            )
        }
    }

    // Use the same flavor dimensions as the application to allow generating Baseline Profiles on prod,
    // which is more close to what will be shipped to users (no fake data), but has ability to run the
    // benchmarks on demo, so we benchmark on stable data.
//    configureFlavors(this) { flavor ->
//        buildConfigField(
//            "String",
//            "APP_FLAVOR_SUFFIX",
//            "\"${flavor.applicationIdSuffix ?: ""}\""
//        )
//    }

//    testOptions.managedDevices.devices {
//        create<ManagedVirtualDevice>("pixel6Api34") {
//            device = "Pixel 6"
//            apiLevel = 34
//            systemImageSource = "google"
//        }
//    }
    testOptions.managedDevices.devices {
        create<com.android.build.api.dsl.ManagedVirtualDevice>("pixel6Api33") {
            device = "Pixel 6"
            apiLevel = 33
            systemImageSource = "aosp"
        }
    }

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

baselineProfile {
    // This specifies the managed devices to use that you run the tests on.
    managedDevices += "pixel6Api33"

    // Don't use a connected device but rely on a GMD for consistency between local and CI builds.
    useConnectedDevices = false

}

dependencies {
    implementation(libs.androidx.benchmark.macro)
    implementation(libs.androidx.test.core)
    implementation(libs.androidx.test.espresso.core)
    implementation(libs.androidx.test.ext)
    implementation(libs.androidx.test.rules)
    implementation(libs.androidx.test.runner)
    implementation(libs.androidx.test.uiautomator)
}
