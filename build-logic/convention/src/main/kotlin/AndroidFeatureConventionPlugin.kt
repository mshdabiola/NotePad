/*
 * Copyright 2022 The Android Open Source Project
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.project

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("mshdabiola.android.library")
                apply("mshdabiola.android.library.compose")
                apply("mshdabiola.android.hilt")
            }
            extensions.configure<LibraryExtension> {

                defaultConfig {
//                    testInstrumentationRunner =
//                        "com.google.samples.apps.nowinandroid.core.testing.NiaTestRunner"
                }
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                add("implementation", project(":core:model"))
                add("implementation", project(":core:database"))
                add("testImplementation", kotlin("test"))
                add("androidTestImplementation", kotlin("test"))
                add("androidTestImplementation", libs.findLibrary("androidx-compose-ui-test").get())
                add(
                    "androidTestImplementation",
                    libs.findLibrary("androidx-test-espresso-core").get()
                )
                add("androidTestImplementation", libs.findLibrary("androidx-test-ext").get())


                add("implementation", libs.findLibrary("androidx.navigation.compose").get())
                add("implementation", libs.findLibrary("androidx.hilt.navigation.compose").get())
                // add("implementation", libs.findLibrary("androidx.constraintlayout").get())
                add("implementation", libs.findLibrary("kotlinx-collection-immutable").get())
                // add("implementation", libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
                // add("implementation", libs.findLibrary("androidx.lifecycle.viewModelCompose").get())


            }
        }
    }
}
