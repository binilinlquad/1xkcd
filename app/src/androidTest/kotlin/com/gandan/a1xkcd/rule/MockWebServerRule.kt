package com.gandan.a1xkcd.rule

import com.gandan.a1xkcd.https.TestCertificate
import okhttp3.mockwebserver.MockWebServer
import okhttp3.tls.HandshakeCertificates
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockWebServerRule(private val port: Int) : TestRule {
    val mockWebServer = MockWebServer()

    override fun apply(base: Statement?, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                try {
                    mockWebServer.useHttps(createHandshakeCertificate().sslSocketFactory(), false)
                    mockWebServer.start(port)
                    base?.evaluate()
                } finally {
                    mockWebServer.shutdown()
                }
            }
        }
    }

    private fun createHandshakeCertificate(): HandshakeCertificates {
        return HandshakeCertificates.Builder()
            .heldCertificate(TestCertificate.localhostCertificate)
            .build()
    }


}