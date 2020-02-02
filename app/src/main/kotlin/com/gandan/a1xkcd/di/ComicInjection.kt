package com.gandan.a1xkcd.di

import com.gandan.a1xkcd.ComicActivity
import com.gandan.a1xkcd.ComicApplication
import com.gandan.a1xkcd.service.XkcdService
import com.gandan.a1xkcd.service.createXkcdService
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import okhttp3.Cache
import okhttp3.OkHttpClient
import javax.inject.Singleton


@Component(
    modules = [AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityModule::class,
        ProductionServiceModule::class]
)

@Singleton
interface AppComponent {
    fun inject(app: ComicApplication)
}

@Module
class AppModule

@Module
interface ActivityModule {

    @ActivtyScope
    @ContributesAndroidInjector()
    fun contributeYourActivityInjector(): ComicActivity
}

@Module
class ProductionServiceModule(private val application: ComicApplication) : ServiceModule {

    @Singleton
    @Provides
    override fun webClient(): OkHttpClient {
        val cacheSize = 10L * 1024 * 1024 // 10 MB
        val localCache = Cache(application.cacheDir, cacheSize)

        return OkHttpClient.Builder()
            .cache(localCache)
            .build()
    }

    @Singleton
    @Provides
    override fun service(okHttpClient: dagger.Lazy<OkHttpClient>): XkcdService {
        return createXkcdService(okHttpClient.get(), "https://xkcd.com/")
    }
}

interface ServiceModule {

    fun webClient(): OkHttpClient

    fun service(okHttpClient: dagger.Lazy<OkHttpClient>): XkcdService
}
