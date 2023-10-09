import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.mshdabiola.app.configureGradleManagedDevices
import com.mshdabiola.app.configureKotlinAndroid
import com.mshdabiola.app.configurePrintApksTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
//                compileSdk=33
//                defaultConfig.targetSdk = 33
//                compileSdkPreview = "UpsideDownCake"
                defaultConfig.minSdk = 24
                defaultConfig.versionName = "1.1.8"
                defaultConfig.versionCode = 16

                defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                defaultConfig.vectorDrawables {
                    useSupportLibrary = true
                }
                // defaultConfig.resourceConfigurations+= listOf("en")
                // configureFlavors(this)
                configureGradleManagedDevices(this)

            }
            extensions.configure<ApplicationAndroidComponentsExtension> {
                configurePrintApksTask(this)
            }

            tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
                kotlinOptions {
                    jvmTarget = "17"
                }
            }

        }
    }

}