package com.gandan.a1xkcd.buildSrc

object Libs {
    object AndroidX {
        const val appCompat = "androidx.appcompat:appcompat:1.1.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
        const val recyclerView = "androidx.recyclerview:recyclerview:1.0.0"
        const val cardView = "androidx.cardview:cardview:1.0.0"
        const val lifecyleExtension = "android.arch.lifecycle:extensions:1.1.1"
        const val pagingRuntime = "androidx.paging:paging-runtime-ktx:2.1.0"

        object Test {
            private const val version = "1.1.1"
            const val runner = "androidx.test:runner:$version"
            const val rules = "androidx.test:rules:$version"
            const val orchestrator = "androidx.test:orchestrator:$version"

            object Espresso {
                private const val version = "3.1.1"
                const val core = "androidx.test.espresso:espresso-core:$version"
                const val contrib = "androidx.test.espresso:espresso-contrib:$version"
            }

        }
    }

    object Dagger {
        private const val version = "2.21"
        const val core = "com.google.dagger:dagger:$version"
        const val android = "com.google.dagger:dagger-android:$version"
        const val androidSupport = "com.google.dagger:dagger-android-support:$version"
        const val compiler = "com.google.dagger:dagger-compiler:$version"
        const val processor = "com.google.dagger:dagger-android-processor:$version"
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
        private const val version = "4.2.2"
        const val core = "com.squareup.okhttp3:okhttp:$version"
        const val mockWebServer = "com.squareup.okhttp3:mockwebserver:$version"
        const val tls = "com.squareup.okhttp3:okhttp-tls:$version"

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
}