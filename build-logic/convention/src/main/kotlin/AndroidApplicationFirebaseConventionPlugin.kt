
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import com.mshdabiola.app.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationFirebaseConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.gms.google-services")
                apply("com.google.firebase.firebase-perf")
                apply("com.google.firebase.crashlytics")
            }


            extensions.configure<ApplicationAndroidComponentsExtension> {
                finalizeDsl {
                    it.buildTypes.forEach { buildType ->
                        // Disable the Crashlytics mapping file upload. This feature should only be
                        // enabled if a Firebase backend is available and configured in
                        // google-services.json.
                        buildType.configure<CrashlyticsExtension> {
                            mappingFileUploadEnabled = !buildType.isDebuggable
                        }
                    }
                }
            }
        }
    }
}
