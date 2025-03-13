plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "fr.isen.boussougou.socialsphere"
    compileSdk = 35

    defaultConfig {
        applicationId = "fr.isen.boussougou.socialsphere"
        minSdk = 25
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation ("androidx.compose.material:material-icons-extended:1.7.8")


    implementation ("androidx.navigation:navigation-compose:2.5.3")
    implementation ("androidx.compose.material3:material3:1.2.0")
    implementation ("androidx.activity:activity-compose:1.6.1")
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.6.0-alpha01")

    // Accompanist libraries for Compose
    //implementation ("com.google.accompanist:accompanist-imageloading-coil:2.2.2")
    implementation ("com.google.accompanist:accompanist-permissions:0.37.2")
    implementation("com.google.accompanist:accompanist-drawablepainter:0.37.2")



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.navigation:navigation-compose:2.6.0-alpha01")

    // Ajoutez la dépendance pour le BoM de Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))

    //Dépendances pour les produits Firebase
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")

    implementation("io.coil-kt:coil-compose:2.5.0") // Or the latest version of Coil
    implementation("com.google.firebase:firebase-firestore-ktx:24.1.0") // Or the latest version
    implementation("com.google.firebase:firebase-storage-ktx:20.0.0") // Or the latest version
    implementation("androidx.compose.ui:ui:1.5.4") // or whatever version of compose you use

}