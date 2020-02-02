package com.gandan.a1xkcd.di

import com.gandan.a1xkcd.MOCKWEBSERVER_PORT
import com.gandan.a1xkcd.https.TestCertificate
import com.gandan.a1xkcd.service.XkcdService
import com.gandan.a1xkcd.service.createXkcdService
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.tls.HandshakeCertificates
import java.util.concurrent.Executors
import java.util.concurrent.Future
import javax.inject.Singleton


@Component(
    modules = [AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityModule::class,
        FakeServiceModule::class
    ]
)

@Singleton
interface TestAppComponent : AppComponent {

    fun okHttpClient(): OkHttpClient
}

@Module
class FakeServiceModule : ServiceModule {

    @Singleton
    @Provides
    override fun webClient(): OkHttpClient {
        val futureTask: Future<HandshakeCertificates> =
            Executors.newSingleThreadExecutor().submit<HandshakeCertificates> {
                HandshakeCertificates.Builder()
                    .addTrustedCertificate(TestCertificate.localhostCertificate.certificate)
                    .build()
            }
        val clientCertificates = futureTask.get()

        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .sslSocketFactory(
                clientCertificates.sslSocketFactory(),
                clientCertificates.trustManager
            )
            .cache(null)
            .build()
    }

    @Singleton
    @Provides
    override fun service(okHttpClient: dagger.Lazy<OkHttpClient>): XkcdService {
        return createXkcdService(okHttpClient.get(), "https://localhost:$MOCKWEBSERVER_PORT")
    }
}

