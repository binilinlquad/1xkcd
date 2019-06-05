package com.gandan.a1xkcd.di

import com.gandan.a1xkcd.ComicActivity
import com.gandan.a1xkcd.ComicApplication
import com.gandan.a1xkcd.service.XkcdService
import com.gandan.a1xkcd.service.createXkcdService
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import okhttp3.Cache
import okhttp3.OkHttpClient


@Component(
    modules = [AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityModule::class,
        ProductionServiceModule::class]
)
interface AppComponent {
    fun inject(app: ComicApplication)

}

@Module
class AppModule(private val application: ComicApplication) {

    @Provides
    fun imageDownloader(okHttpClient: OkHttpClient): Picasso {
        return Picasso.Builder(application).downloader(OkHttp3Downloader(okHttpClient)).build()
    }
}

@Module
interface ActivityModule {

    @ActivtyScope
    @ContributesAndroidInjector()
    abstract fun contributeYourActivityInjector(): ComicActivity
}

@Module
class ProductionServiceModule(private val application: ComicApplication) : ServiceModule {

    @Provides
    override fun webClient(): OkHttpClient {
        val cacheSize = 10L * 1024 * 1024 // 10 MB
        val localCache = Cache(application.cacheDir, cacheSize)

        return OkHttpClient.Builder()
            .cache(localCache)
            .build()
    }

    @Provides
    override fun service(okHttpClient: OkHttpClient): XkcdService {
        return createXkcdService(okHttpClient, "https://xkcd.com/")
    }
}

interface ServiceModule {

    fun webClient(): OkHttpClient

    fun service(okHttpClient: OkHttpClient): XkcdService
}
