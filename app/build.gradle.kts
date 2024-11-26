plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {

    namespace = "com.example.globego"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.globego"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.play.services.fido)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation (libs.xchip.navigation.bar)
    implementation (libs.xviewpager2)
    implementation(libs.glide)

    implementation (libs.checkout)
    implementation (libs.gson)
    implementation (libs.material.v180)
    implementation (libs.material.v190)

    implementation (libs.firebase.database.v2025)
    implementation (libs.firebase.storage)
    implementation (libs.firebase.auth.v2310)

    implementation (libs.play.services.auth.v2060)




}