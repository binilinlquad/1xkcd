package com.gandan.a1xkcd

import android.content.Context
import com.gandan.a1xkcd.di.ServiceModule
import com.gandan.a1xkcd.https.TestCertificate
import com.gandan.a1xkcd.service.XkcdService
import com.gandan.a1xkcd.service.createXkcdService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.tls.HandshakeCertificates
import java.util.concurrent.Executors
import java.util.concurrent.Future

@Module
@InstallIn(ApplicationComponent::class)
object FakeServiceModule : ServiceModule {

    @Provides
    override fun webClient(@ApplicationContext context: Context): OkHttpClient {
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

    @Provides
    override fun service(okHttpClient: OkHttpClient): XkcdService {
        return createXkcdService(okHttpClient, "https://localhost:$MOCKWEBSERVER_PORT")
    }
}
