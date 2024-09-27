plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "za.co.varsitycollege.syntechsoftware.wingwatch"
    compileSdk = 34

    defaultConfig {
        applicationId = "za.co.varsitycollege.syntechsoftware.wingwatch"
        minSdk = 26
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    //noinspection UseTomlInstead
    implementation ("org.bouncycastle:bcprov-jdk15to18:1.70")
    // Firebase Auth and Realtime Database dependencies
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-auth:23.0.0")
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.preference.ktx)
    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Google Maps SDK
    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    // Google Location Services
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    // Retrofit for API calls
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

}
// Applying the Google services plugin
apply(plugin = "com.google.gms.google-services")