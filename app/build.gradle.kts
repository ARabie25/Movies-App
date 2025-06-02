import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt.android)
    alias(libs.plugins.navigation.android)
}


android{
    namespace = "com.example.moviesapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.moviesapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        val properties =  Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")
        properties.load(localPropertiesFile.reader())
        val apiKey = properties.getProperty("tmdb_api_key")
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw  GradleException("tmdb_api_key is missing or empty in local.properties. Please add 'tmdb_api_key=your_key_here'.")
        }
        debug {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "TMDB_API_KEY", "\"${apiKey}\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "TMDB_API_KEY", "\"${apiKey}\"")
        }
    }


    buildFeatures {
        viewBinding= true
        buildConfig= true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    }

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.navigation)

    // Navigation Components
    implementation(libs.navigation.components.fragment)
    implementation(libs.navigation.components.ui)

    // Coroutines & Flow
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.core)

    // Paging 3
    implementation(libs.paging3)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room)
    implementation(libs.google.material)
    kapt(libs.kapt)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter)

    //Glide
    implementation(libs.glide)

    //SDP && SSP
    implementation(libs.sdp)
    implementation(libs.ssp)

    testImplementation(libs.junit)
    testImplementation(libs.mockito)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines)
    testImplementation(libs.core.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
