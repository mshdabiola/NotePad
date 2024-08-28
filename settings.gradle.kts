pluginManagement {
    repositories {
        includeBuild("build-logic")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://androidx.dev/storage/compose-compiler/repository/")
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "NotePad"
include(":app")
//include(":benchmark")

include(":modules:data")
include(":modules:datastore")
include(":modules:model")
include(":modules:common")
include(":modules:testing")
//include(":modules:screenshot-testing")
include(":modules:database")
//include(":modules:network")
include(":modules:analytics")
include(":modules:designsystem")
include(":modules:domain")
include(":modules:ui")
//include(":modules:worker")
include(":feature:mainscreen")
include(":feature:editScreen")
include(":feature:labelscreen")
include(":feature:searchscreen")
include(":feature:selectlabelscreen")
include(":feature:gallery")
include(":feature:drawing")
include(":feature:about")
include(":benchmarks")
include(":ui-test-hilt-manifest")
