package com.mshdabiola.app

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.Project

@Suppress("EnumEntryName")
enum class FlavorDimension {
    //    contentType,
    store
}

// The content for the app can either come from local static data which is useful for demo
// purposes, or from a production backend server which supplies up-to-date, real content.
// These two product flavors reflect this behaviour.
@Suppress("EnumEntryName")
enum class Flavor(
    val dimension: FlavorDimension,
    val applicationIdSuffix: String? = null,
    val versionNameSuffix: String? = null,
) {
    //    demo(FlavorDimension.contentType, applicationIdSuffix = ".demo", "-demo"),
//    prod(FlavorDimension.contentType),
    fossReliant(FlavorDimension.store,applicationIdSuffix=".foss"),
    googlePlay(FlavorDimension.store, applicationIdSuffix = ".play", versionNameSuffix = "-play")
}

fun Project.configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: Flavor) -> Unit = {},
) {
    commonExtension.apply {
//        flavorDimensions += FlavorDimension.contentType.name
        flavorDimensions += FlavorDimension.store.name

        productFlavors {
            Flavor.values().forEach {
                create(it.name) {
                    dimension = it.dimension.name
                    flavorConfigurationBlock(this, it)
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (it.applicationIdSuffix != null) {
                            this.applicationIdSuffix = it.applicationIdSuffix
                        }
                        if (it.versionNameSuffix != null) {
                            this.versionNameSuffix = it.versionNameSuffix
                        }
                        if(it ==Flavor.googlePlay){
                            with(pluginManager) {
                                apply("mshdabiola.android.application.firebase")
                            }
                        }
                    }

                }
            }
        }
    }
}
