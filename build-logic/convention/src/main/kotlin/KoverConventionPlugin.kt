import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class KoverConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val koverPlugin = "org.jetbrains.kotlinx.kover"
            pluginManager.apply(koverPlugin)

            rootProject.subprojects {
                if (this@subprojects.name == target.name) return@subprojects

                this@subprojects.beforeEvaluate {
                    this@subprojects.pluginManager.apply(koverPlugin)
                }
                target.dependencies.add("kover", this@subprojects)
            }

            configure<KoverProjectExtension> {
//                currentProject {
//                    createVariant("unit") {
//                        this.
//                        sources.includedSourceSets.add("unitTest")
//                    }
//
//                    createVariant("integration") {
//                        sources.includedSourceSets.add("integrationTest")
//                    }
//                }

//                reports {
//                    filters {
//                        includes.packages("dev.dai.android.architecture.template.*")
//                    }
//                }
            }
        }
    }
}