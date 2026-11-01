plugins {
    id("mrd.android.compose")
}

android {
    namespace = "com.mrd.discovery.core.designsystem"
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}
