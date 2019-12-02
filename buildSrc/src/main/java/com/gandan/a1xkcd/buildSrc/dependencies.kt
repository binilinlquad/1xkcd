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
}