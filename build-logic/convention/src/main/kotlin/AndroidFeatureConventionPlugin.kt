import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("mrd.android.compose")
            pluginManager.apply("mrd.android.hilt")

            dependencies {
                add("implementation", libs.findLibrary("androidx-hilt-navigation-compose").get())
            }
        }
    }
}
