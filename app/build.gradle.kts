plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.eventtracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.eventtracker"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.cardview)

    implementation("androidx.core:core:1.10.1")


    // Room runtime
    implementation("androidx.room:room-runtime:2.7.2")
    implementation(libs.preference)

    // Room annotation processor (Java projelerde annotationProcessor olarak eklenmeli)
    annotationProcessor("androidx.room:room-compiler:2.7.2")

    implementation(libs.room.common.jvm)
    implementation(libs.room.runtime.android)

    implementation("com.google.android.flexbox:flexbox:3.0.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}