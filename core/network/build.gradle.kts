plugins {
    id("mrd.android.library")
    id("mrd.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.mrd.discovery.core.network"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp)

    testImplementation(libs.junit)
}
