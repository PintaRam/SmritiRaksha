plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.smritiraksha"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.smritiraksha"
        minSdk = 23
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.fragment)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.constraintlayout:constraintlayout-core:1.0.4")
    implementation("androidx.appcompat:appcompat:1.7.0") // Use the latest version
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.3")
    implementation ("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.airbnb.android:lottie:6.4.1")
    implementation ("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.android.volley:volley:1.2.1")
    implementation ("com.github.dangiashish:Google-Direction-Api:1.6")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("androidx.work:work-runtime:2.9.0")
    implementation("com.google.guava:guava:31.0.1-android")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.android.gms:play-services-fitness:21.1.0")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    



}