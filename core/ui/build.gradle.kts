plugins {
    id("mrd.android.compose")
}

android {
    namespace = "com.mrd.discovery.core.ui"
}

dependencies {
    api(libs.androidx.activity.compose)
}
