plugins {
    id("mrd.android.library")
}

android {
    namespace = "com.mrd.discovery.core.domain"
}

dependencies {
    api(projects.core.common)

    testImplementation(libs.junit)
}
