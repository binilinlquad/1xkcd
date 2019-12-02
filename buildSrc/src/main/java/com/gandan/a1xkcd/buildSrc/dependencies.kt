package com.gandan.a1xkcd.buildSrc

object AndroidX {
    const val appCompat = "androidx.appcompat:appcompat:1.1.0"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
    const val recyclerView = "androidx.recyclerview:recyclerview:1.0.0"
    const val cardView = "androidx.cardview:cardview:1.0.0"
    const val lifecyleExtension = "android.arch.lifecycle:extensions:1.1.1"
    const val pagingRuntime = "androidx.paging:paging-runtime-ktx:2.1.0"

    object Test {
        const val runner = "androidx.test:runner:1.1.1"
        const val rules = "androidx.test:rules:1.1.1"
        const val espressoCore = "androidx.test.espresso:espresso-core:3.1.1"
        const val espressoContrib = "androidx.test.espresso:espresso-contrib:3.1.1"
    }
}