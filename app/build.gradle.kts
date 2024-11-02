plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "org.cygnus.smarttubemanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.cygnus.smarttubemanager"
        buildConfigField("String", "APPLICATION_ID", "\"${applicationId}\"")
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.tv:tv-foundation:1.0.0-alpha11")
    implementation("androidx.tv:tv-material:1.0.0")

    implementation("androidx.core:core-ktx:1.12.0") // Check for the latest stable version
    implementation("androidx.appcompat:appcompat:1.7.0") // Check for the latest stable version
    implementation("androidx.compose.ui:ui") // No version required when using BOM
    implementation("androidx.compose.material3:material3:1.3.1") // Check for the latest stable version
    implementation("androidx.compose.foundation:foundation") // No version required when using BOM
    implementation("androidx.activity:activity-compose:1.8.0") // Check for the latest stable version
}