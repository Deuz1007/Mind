import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.mindapps.mind"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mindapps.mind"
        minSdk = 26
        targetSdk = 33
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0")

    // Firebase
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-database:20.2.2")

    // Text Extractor
    implementation("com.itextpdf:itextg:5.5.10")
    implementation("org.zwobble.mammoth:mammoth:1.5.0")
    implementation("com.google.mlkit:text-recognition:16.0.0")

    // HTTP Request
    implementation("io.socket:socket.io-client:2.0.0") {
        exclude("org.json", "json")
    }


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.23")
    implementation ("com.google.android.material:material:1.5.0")


    implementation ("androidx.core:core-ktx:1.6.0")
}