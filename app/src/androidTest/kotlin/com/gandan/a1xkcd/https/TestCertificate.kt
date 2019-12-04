package com.gandan.a1xkcd.https

import okhttp3.tls.HeldCertificate
import java.net.InetAddress

class TestCertificate {
    companion object {
        private val localhost: String = InetAddress.getLocalHost().canonicalHostName
        val localhostCertificate: HeldCertificate = HeldCertificate.Builder()
            .addSubjectAlternativeName(localhost)
            .build()

    }
}