plugins {
    id("mrd.android.compose")
}

android {
    namespace = "com.mrd.discovery.core.ui"
}

dependencies {
    api(libs.androidx.activity.compose)
    api(projects.core.common)

    testImplementation(libs.junit)
}
