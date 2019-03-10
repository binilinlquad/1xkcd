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
import okhttp3.tls.HandshakeCertificates


@Component(
    modules = [AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityModule::class,
        FakeServiceModule::class
    ]
)
interface TestAppComponent : AppComponent {

    fun okHttpClient(): OkHttpClient
}

@Module
class FakeServiceModule : ServiceModule {
    @Provides
    override fun webClient(): OkHttpClient {
        val clientCertificates = HandshakeCertificates.Builder()
            .addTrustedCertificate(TestCertificate.localhostCertificate.certificate())
            .build();
        return OkHttpClient.Builder()
            .sslSocketFactory(clientCertificates.sslSocketFactory(), clientCertificates.trustManager())
            .build()
    }

    @Provides
    override fun service(okHttpClient: OkHttpClient): XkcdService {
        return createXkcdService(okHttpClient, "https://localhost:$MOCKWEBSERVER_PORT")
    }
}

