plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.rafaelhibene.safewalk"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rafaelhibene.safewalk"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // Pega a chave API do gradle.properties e lança exceção se não encontrar
        val mapsApiKey = project.findProperty("MAPS_API_KEY") as? String
            ?: throw GradleException("MAPS_API_KEY não encontrado no gradle.properties")

        // Define para o AndroidManifest.xml (placeholders)
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey

        // Removido o buildConfigField (não será mais usado)
        // buildConfigField("String", "MAPS_API", "\"$mapsApiKey\"")

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
    implementation("com.airbnb.android:lottie:6.1.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.libraries.places:places:3.4.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
