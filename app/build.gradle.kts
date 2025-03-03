plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")  
}

android {
    namespace = "com.rana.flashlearn"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rana.flashlearn"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")


    implementation("com.google.firebase:firebase-auth-ktx:21.0.1") // Reverted to a stable version
    implementation("com.google.firebase:firebase-firestore-ktx:24.0.0") // Reverted to a stable version
    implementation("com.google.android.gms:play-services-auth:20.0.0") // Updated version


    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.0") // Added coroutines dependency
    implementation("androidx.security:security-crypto:1.1.0-alpha03") 

    // Jetpack Navigation (Optional)
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
