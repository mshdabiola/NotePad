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
    }
}
rootProject.name = "NotePad"
include(":app")
include(":benchmark")
include(":core:database")
include(":core:designsystem")
include(":core:model")
include(":feature:mainscreen")
include(":feature:editScreen")
include(":core:common")
include(":core:bottomsheet")
include(":feature:labelscreen")
include(":feature:searchscreen")
include(":feature:settingscreen")
include(":feature:helpscreeen")
include(":feature:selectlabelscreen")
