plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.fptu.fevent"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fptu.fevent"
        minSdk = 26
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
    packaging {
        resources {
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/LICENSE.md"
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Room (Java support)
    implementation (libs.core.ktx)  // hoặc phiên bản mới nhất
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler) // dùng cho Java

// ViewModel & LiveData (Jetpack Lifecycle)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
    implementation("com.itextpdf:itext7-core:7.2.5")

// Optional - RecyclerView, Navigation (nếu bạn dùng)
    implementation(libs.recyclerview)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation (libs.material)

    //JAVAMAIL API
    implementation ("com.sun.mail:android-mail:1.6.7")
    implementation ("com.sun.mail:android-activation:1.6.7")

    //MPAndroidChart for pie charts
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation ("com.cloudinary:cloudinary-android:2.4.0")
    dependencies {
        implementation ("com.github.bumptech.glide:glide:4.16.0") // Đảm bảo phiên bản Glide khớp
        annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0") // Thêm compiler
    }


    // Thư viện hỗ trợ ActivityResultLauncher (quan trọng cho Camera/Gallery API mới)
    implementation ("androidx.activity:activity-ktx:1.9.0")
    implementation ("androidx.fragment:fragment-ktx:1.8.0")
}