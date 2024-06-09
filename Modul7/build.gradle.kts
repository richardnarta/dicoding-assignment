plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.googleAndroidLibrariesMapsplatformSecretsGradlePlugin) apply false
    alias(libs.plugins.devtoolsKsp) apply false
}

buildscript {
    dependencies {
        classpath (libs.com.google.devtools.ksp.gradle.plugin)
    }
}