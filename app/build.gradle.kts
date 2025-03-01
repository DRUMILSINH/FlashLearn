plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.rana.flashlearn"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rana.flashlearn"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Core AndroidX libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // ✅ MVVM Architecture Components
    implementation(libs.androidx.lifecycle.viewmodel.ktx)  // ViewModel
    implementation(libs.androidx.lifecycle.livedata.ktx)  // LiveData

    // ✅ Navigation Component (For Fragment Navigation)
    implementation(libs.androidx.navigation.fragment.ktx)  // Navigation between fragments
    implementation(libs.androidx.navigation.ui.ktx)  // Navigation UI (for BottomNavView, etc.)

    // ✅ Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}


