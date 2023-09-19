package com.mshdabiola.app

@Suppress("unused")
enum class BuildType(val applicationIdSuffix: String? = null, val versionNameSuffix: String = "") {
    DEBUG(".debug", "-debug"),
    RELEASE,
    BENCHMARK(".benchmark", "-benchmark")
}
