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
    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


}
// Applying the Google services plugin
apply(plugin = "com.google.gms.google-services")