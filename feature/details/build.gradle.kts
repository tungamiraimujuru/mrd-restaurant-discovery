plugins {
    id("mrd.android.feature")
}

android {
    namespace = "com.mrd.discovery.feature.details"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.designsystem)
    implementation(projects.core.ui)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
