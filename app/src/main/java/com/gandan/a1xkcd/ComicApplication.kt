package com.gandan.a1xkcd

import android.app.Activity
import android.app.Application
import coil.Coil
import coil.ImageLoaderBuilder
import com.gandan.a1xkcd.di.AppComponent
import com.gandan.a1xkcd.di.DaggerAppComponent
import com.gandan.a1xkcd.di.ProductionServiceModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import okhttp3.OkHttpClient
import javax.inject.Inject

open class ComicApplication : Application(), HasActivityInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var okHttp3Client: OkHttpClient

    override fun onCreate() {
        super.onCreate()
        applicationInjector().inject(this)
        Coil.setDefaultImageLoader(
            ImageLoaderBuilder(this)
                .okHttpClient(okHttp3Client)
                .build()
        )
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }

    protected open fun applicationInjector(): AppComponent {
        return DaggerAppComponent.builder()
            .productionServiceModule(ProductionServiceModule(this))
            .build()
    }

}