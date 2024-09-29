plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    //noinspection UseTomlInstead,UseTomlInstead
    implementation("org.bouncycastle:bcprov-jdk15to18:1.70")
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-auth:23.0.0")
    //noinspection UseTomlInstead
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    //noinspection UseTomlInstead
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Retrofit and Gson converters for network calls
    //noinspection UseTomlInstead
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

    //noinspection UseTomlInstead
    implementation("com.google.maps.android:maps-ktx:2.3.0")
    //noinspection UseTomlInstead
    implementation("com.google.maps.android:android-maps-utils:3.3.0")


    implementation(libs.firebase.database)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.preference.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Google Maps SDK
    implementation(libs.play.services.maps)

    // Google Location Services
    implementation(libs.play.services.location)

    // Retrofit for API calls
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Add Google Directions API dependency
    //noinspection UseTomlInstead
    implementation("com.google.maps:google-maps-services:0.18.0") // Add this line for route calculation

    // Add OkHttp dependency for handling network requests (Optional but recommended for API calls)
    //noinspection UseTomlInstead
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
}

// Applying the Google services plugin
apply(plugin = "com.google.gms.google-services")