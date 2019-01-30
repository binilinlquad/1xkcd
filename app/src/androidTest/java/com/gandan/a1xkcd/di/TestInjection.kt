package com.gandan.a1xkcd.di

import com.gandan.a1xkcd.service.XkcdService
import com.gandan.a1xkcd.service.createXkcdService
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import java.net.InetAddress


@Component(
    modules = [AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityModule::class,
        FakeServiceModule::class
    ]
)
interface TestAppComponent : AppComponent

val mockWebServer = MockWebServer()

val localhost = InetAddress.getByName("localhost").canonicalHostName
val localhostCertificate = HeldCertificate.Builder()
    .addSubjectAlternativeName(localhost)
    .build()

@Module
class FakeServiceModule : ServiceModule {

    @Provides
    override fun webClient(): OkHttpClient {
        val clientCertificates = HandshakeCertificates.Builder()
            .addTrustedCertificate(localhostCertificate.certificate())
            .build();
        return OkHttpClient.Builder()
            .sslSocketFactory(clientCertificates.sslSocketFactory(), clientCertificates.trustManager())
            .build()
    }

    @Provides
    override fun service(okHttpClient: OkHttpClient): XkcdService {
        return createXkcdService(okHttpClient, "https://localhost:8080")
    }
}
