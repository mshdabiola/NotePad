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
rootProject.name = "NotePad"
include(":app")
//include(":benchmark")
include(":core:database")
include(":core:designsystem")
include(":core:model")
include(":core:worker")
include(":feature:mainscreen")
include(":feature:editScreen")
include(":core:common")
include(":feature:labelscreen")
include(":feature:searchscreen")
include(":feature:selectlabelscreen")
include(":feature:gallery")
include(":feature:drawing")
include(":feature:about")
include(":app:baselineprofile")
