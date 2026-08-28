import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.dagger.hilt.android") version "2.55" apply false
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
}


fun getLocalProperty(propertyName: String, defaultValue: String = ""): String {
    val properties = Properties()
    val file = project.rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { properties.load(it) }
    }
    return properties.getProperty(propertyName, defaultValue)
}

extra.apply {
    set("mapkitApiKey", getLocalProperty("MAPKIT_API_KEY"))
    set("cloudinaryCloudName", getLocalProperty("CLOUDINARY_CLOUD_NAME"))
}
