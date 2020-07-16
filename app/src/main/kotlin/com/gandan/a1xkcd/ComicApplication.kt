package com.gandan.a1xkcd

import android.app.Application
import coil.Coil
import coil.ImageLoaderBuilder
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import javax.inject.Inject

@HiltAndroidApp
open class ComicApplication : Application() {

    @Inject
    lateinit var okHttp3Client: OkHttpClient

    override fun onCreate() {
        super.onCreate()
        initializeCoil()
    }

    protected open fun initializeCoil() {
        Coil.setDefaultImageLoader(
            ImageLoaderBuilder(this)
                .okHttpClient(okHttp3Client)
                .build()
        )
    }

}