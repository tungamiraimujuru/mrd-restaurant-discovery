plugins {
    id("mrd.android.library")
    id("mrd.android.hilt")
}

android {
    namespace = "com.mrd.discovery.core.common"
}

dependencies {
    api(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
}
