plugins {
    id("com.android.library") version "8.0.2"
    id("org.jetbrains.kotlin.android") version "1.9.22"
    id("maven-publish")
}
android {
    namespace = "com.mahimeta.sdk"
    compileSdk = 34

    buildFeatures {
        buildConfig = true  // ← Add this
    }

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // Version information
        buildConfigField("String", "SDK_VERSION", "\"1.0.9\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false  // Disable minification for now to debug
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }
    
    // Configure source sets to include both Java and Kotlin sources
    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/java", "src/main/kotlin")
            res.srcDirs("src/main/res")
            manifest.srcFile("src/main/AndroidManifest.xml")
        }
    }
    
    // Enable Java 8 features
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    
    // Configure Kotlin options
    kotlinOptions {
        jvmTarget = "1.9"
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
    api("androidx.core:core-ktx:1.16.0")
    api("androidx.appcompat:appcompat:1.7.1")
    api("com.google.android.material:material:1.12.0")
    api("com.google.android.gms:play-services-ads:22.6.0")
    api("androidx.lifecycle:lifecycle-runtime-ktx:2.9.1")
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.1")

    // Retrofit and Coroutines
    api("com.squareup.retrofit2:retrofit:2.9.0")
    api("com.squareup.retrofit2:converter-gson:2.9.0")
    api("com.squareup.okhttp3:logging-interceptor:4.12.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                groupId = "com.github.syedtehrimabbas"
                artifactId = "MahimetaSDK"
                version = "1.0.8"
                
                from(components["release"])

                pom {
                    name.set("Mahimeta Ad SDK")
                    description.set("A lightweight Android SDK for displaying ads with dynamic configuration from Mahimeta dashboard")
                    url.set("https://github.com/syedtehrimabbas/MahimetaSDK")
                    licenses {
                        license {
                            name.set("Apache License 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("syedtehrimabbas")
                            name.set("Syed Tehrim Abbas")
                            email.set("syedtehrimabbas@gmail.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:github.com/syedtehrimabbas/MahimetaSDK.git")
                        developerConnection.set("scm:git:ssh://github.com/syedtehrimabbas/MahimetaSDK.git")
                        url.set("https://github.com/syedtehrimabbas/MahimetaSDK/tree/main")
                    }
                }
            }
        }
    }
}
