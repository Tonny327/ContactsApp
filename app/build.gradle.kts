plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.contactsapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.contactsapp"
        minSdk = 24
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
    buildFeatures {
        viewBinding = true
        aidl = true
    }
}

dependencies {
    //UI-компоненты и поддержка
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)


    //UI-компоненты (прямое подключение)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.cardview:cardview:1.0.0")
    //Работа со списками
    implementation ("me.zhanghai.android.fastscroll:library:1.3.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    //Загрузка изображений
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    //Жизненный цикл и LiveData
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")
    //KTX и утилиты
    implementation("androidx.core:core-ktx:1.13.1")

    //Unit-тесты (на JVM, без Android)
    testImplementation(libs.junit)
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.2.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.robolectric:robolectric:4.11.1")
//Android-инструментальные тесты (на устройстве/эмуляторе)
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}