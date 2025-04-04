import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android) version "1.9.20"
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.compose)
    id("com.google.protobuf") version "0.9.4"
}

val bundleId = "com.singhDevs.chezz"

android {
    namespace = "com.singhDevs.chezz"
    compileSdk = 35

    defaultConfig {
        val secretsFile = project.rootProject.file("secrets.properties")
        val properties = Properties()
        properties.load(secretsFile.inputStream())

        val googleWebClientId = properties.getProperty("google_web_client_id") ?: ""
        val googleAndroidClientId = properties.getProperty("google_android_client_id") ?: ""
        val googleSignInNonce = properties.getProperty("google_sign_in_nonce") ?: ""

        buildConfigField(
            type = "String",
            name = "googleWebClientId",
            value = googleWebClientId
        )
        buildConfigField(
            type = "String",
            name = "googleAndroidClientId",
            value = googleAndroidClientId
        )
        buildConfigField(
            type = "String",
            name = "googleSignInNonce",
            value = googleSignInNonce
        )

        applicationId = "com.singhDevs.chezz"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        android.buildFeatures.buildConfig = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("build/generated/source/proto/main/java")
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.1"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
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
    implementation(libs.androidx.espresso.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.11.0")

    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.material3:material3")
    implementation("io.coil-kt.coil3:coil-compose:3.1.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.1.0")

    implementation("com.github.bhlangonijr:chesslib:1.3.3")
    implementation("com.airbnb.android:lottie:6.6.2")
    implementation("com.airbnb.android:lottie-compose:6.6.2")


    // Google Credentials
    implementation("androidx.credentials:credentials:1.5.0-rc01")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    // optional - needed for credentials support from play services, for devices running
    // Android 13 and below.
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0-rc01")


    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Secure Storage
    implementation("androidx.security:security-crypto:1.0.0")

    // DataStore
    implementation("com.google.protobuf:protobuf-javalite:3.25.1") // Update to latest
    implementation("androidx.datastore:datastore:1.1.4")
}