plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialozation)
}

android {
    namespace = "space.trian.modulecommon"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        val tmdbApiKey = project.findProperty("TMDB_API_KEY") as? String ?: ""
        buildConfigField("String", "API_KEY", "\"$tmdbApiKey\"")
    }
    
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    api(platform(libs.koin.bom))
    api(libs.koin.core)
    api(libs.koin.android)
    api(libs.koin.compose)
    api(libs.koin.compose.viewmodel)
    api(libs.koin.ktor)
    api(platform(libs.ktor.client.bom))
    api(libs.ktor.client.core)
    api(libs.ktor.client.okhttp)
    api(libs.ktor.client.content.negotiation)
    api(libs.ktor.client.serialization.json)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}