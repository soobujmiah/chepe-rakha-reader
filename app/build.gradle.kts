// Chepe Rakha Reader — App module build configuration
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.sobuj.cheperakha.reader"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sobuj.cheperakha.reader"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk { abiFilters += listOf("arm64-v8a") }

        // AdMob App ID — override via local.properties admobAppId or CI secrets
        manifestPlaceholders["ADMOB_APP_ID"] = project.findProperty("admobAppId") as String? 
            ?: "ca-app-pub-3940256099942544~3347511713"
        
        buildConfigField("String", "ADMOB_APP_ID", "\"${manifestPlaceholders["ADMOB_APP_ID"]}\"")
        buildConfigField("boolean", "IS_TEST_BUILD", "true")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders["ADMOB_APP_ID"] = project.findProperty("admobAppId") as String?
                ?: "ca-app-pub-3940256099942544~3347511713"
            buildConfigField("String", "ADMOB_APP_ID", "\"${manifestPlaceholders["ADMOB_APP_ID"]}\"")
            buildConfigField("boolean", "IS_TEST_BUILD", "false")
        }
        debug {
            manifestPlaceholders["ADMOB_APP_ID"] = project.findProperty("admobAppId") as String?
                ?: "ca-app-pub-3940256099942544~3347511713"
            buildConfigField("String", "ADMOB_APP_ID", "\"${manifestPlaceholders["ADMOB_APP_ID"]}\"")
            buildConfigField("boolean", "IS_TEST_BUILD", "true")
            isMinifyEnabled = false
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
        buildConfig = true
    }
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.9.2")
    implementation("androidx.fragment:fragment-ktx:1.8.3")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.5")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")

    // ViewPager2 for chapter pagination
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // AdMob (Google Mobile Ads SDK)
    implementation("com.google.android.gms:play-services-ads:23.5.0")

    // UMP Consent SDK
    implementation("com.google.android.ump:user-messaging-platform:3.1.0")

    // OkHttp for HTTP requests (optional, if needed later)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
