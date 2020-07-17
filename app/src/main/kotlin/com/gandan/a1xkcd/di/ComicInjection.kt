package com.gandan.a1xkcd.di

import android.content.Context
import com.gandan.a1xkcd.service.XkcdService
import com.gandan.a1xkcd.service.createXkcdService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object ProductionServiceModule : ServiceModule {

    @Provides
    @Singleton
    override fun webClient(@ApplicationContext context: Context): OkHttpClient {
        val cacheSize = 10L * 1024 * 1024 // 10 MB
        val localCache = Cache(context.cacheDir, cacheSize)

        return OkHttpClient.Builder()
            .cache(localCache)
            .build()
    }

    @Provides
    @Singleton
    override fun service(okHttpClient: OkHttpClient): XkcdService {
        return createXkcdService(okHttpClient, "https://xkcd.com/")
    }
}

interface ServiceModule {

    fun webClient(context: Context): OkHttpClient

    fun service(okHttpClient: OkHttpClient): XkcdService
}
