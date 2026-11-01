plugins {
    id("mrd.android.library")
    id("mrd.android.hilt")
}

android {
    namespace = "com.mrd.discovery.core.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.network)
    implementation(projects.core.database)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
