plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.barbearia.cortedelite"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.barbearia.cortedelite"
        minSdk = 23
        targetSdk = 36
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Componentes de Design e UI moderna
    implementation("com.google.android.material:material:1.10.0")
// Requisito: Login com Google
    implementation("com.google.android.gms:play-services-auth:20.7.0")
// Preparação para a API (Retrofit para comunicação HTTP/REST)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
// ... Mantenha as dependências básicas (appcompat, constraintlayout)
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
// Adicionar RecyclerView

    // 1. Importe o BoM para gerenciar as versões
    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))

    // 2. Adicione a dependência da biblioteca de Autenticação
    // Com o BoM, você não precisa especificar a versão aqui.
    implementation("com.google.firebase:firebase-auth") // Corrigido para Kotlin DSL
    // 1. Importe o BoM para gerenciar as versões
    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))

    // 2. Adicione a dependência da biblioteca de Autenticação (já adicionada)
    implementation("com.google.firebase:firebase-auth")

    // 3. Adicione a dependência do Cloud Firestore (NOVO)
    implementation("com.google.firebase:firebase-firestore")
}