package com.gandan.a1xkcd.buildSrc

object Libs {
    object AndroidX {
        private const val lifecycleVersion = "2.1.0"

        const val appCompat = "androidx.appcompat:appcompat:1.1.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.0-beta3"
        const val recyclerView = "androidx.recyclerview:recyclerview:1.1.0"
        const val cardView = "androidx.cardview:cardview:1.0.0"
        const val pagingRuntime = "androidx.paging:paging-runtime-ktx:2.1.0"
        const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.0.0"
        const val lifecyleExtension = "androidx.lifecycle:lifecycle-extensions:$lifecycleVersion"
        const val lifecycleCompiler = "androidx.lifecycle:lifecycle-compiler:$lifecycleVersion"
        const val viewModelScope = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.1.0-beta01"
        const val lifeCycleScope = "androidx.lifecycle:lifecycle-runtime-ktx:2.2.0-alpha01"

        object Test {
            private const val version = "1.1.1"
            const val runner = "androidx.test:runner:$version"
            const val rules = "androidx.test:rules:$version"
            const val orchestrator = "androidx.test:orchestrator:$version"
            const val extJUnit = "androidx.test.ext:junit:$version"

            object Espresso {
                private const val version = "3.1.1"
                const val core = "androidx.test.espresso:espresso-core:$version"
                const val contrib = "androidx.test.espresso:espresso-contrib:$version"
            }

        }
    }

    object Hilt {
        private const val version = "2.28.1-alpha"
        private const val kaptVersion = "2.28-alpha"

        const val hilt = "com.google.dagger:hilt-android:$version"
        const val hiltTesting = "com.google.dagger:hilt-android-testing:$version"
        const val kapt = "com.google.dagger:hilt-android-compiler:$kaptVersion"
    }

    const val coil = "io.coil-kt:coil:0.7.0"

    const val junit = "junit:junit:4.12"

    object Coroutine {
        private const val version = "1.2.1"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object OkHttp {
        private const val version = "4.7.2"
        const val core = "com.squareup.okhttp3:okhttp:$version"
        const val mockWebServer = "com.squareup.okhttp3:mockwebserver:$version"
        const val tls = "com.squareup.okhttp3:okhttp-tls:$version"
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:$version"

        const val idlingResource = "com.jakewharton.espresso:okhttp3-idling-resource:1.0.0"
    }

    const val gson = "com.google.code.gson:gson:2.8.5"

    object Retrofit {
        private const val version = "2.6.2"
        const val core = "com.squareup.retrofit2:retrofit:$version"
        const val convertGson = "com.squareup.retrofit2:converter-gson:$version"
    }

    const val photoView = "com.github.chrisbanes:PhotoView:2.2.0"

    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0"

    const val ossLicenses = "com.google.android.gms:play-services-oss-licenses:17.0.0"
}