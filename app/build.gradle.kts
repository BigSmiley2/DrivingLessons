plugins {
    id("com.android.application")

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.drivinglessons"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.drivinglessons"
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
}

dependencies {

    val glideVersion = "4.12.0"
    val firebaseVersion = "32.7.2"
    val firebaseUiVersion = "8.0.2"

    //firebase

    implementation(platform("com.google.firebase:firebase-bom:$firebaseVersion"))

    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-auth")

    implementation("com.firebaseui:firebase-ui-storage:$firebaseUiVersion")
    implementation("com.firebaseui:firebase-ui-database:$firebaseUiVersion")

    // Glide v4

    implementation ("com.github.bumptech.glide:glide:$glideVersion")
    annotationProcessor ("com.github.bumptech.glide:compiler:$glideVersion")

    // gif

    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.17")

    // view pager

    implementation ("androidx.viewpager2:viewpager2:1.1.0-beta02")

    //recycler view

    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // mail

    implementation(fileTree(mapOf(
        "dir" to "libs",
        "include" to listOf("*.aar", "*.jar")
    )))

    implementation(files("libs/activation.jar"))
    implementation(files("libs/additional.jar"))
    implementation(files("libs/mail.jar"))

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}