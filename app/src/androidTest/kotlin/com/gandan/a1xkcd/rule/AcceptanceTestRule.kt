package com.gandan.a1xkcd.rule

import android.app.Activity
import android.util.Log
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.intercepting.SingleActivityFactory
import com.gandan.a1xkcd.https.TestCertificate
import okhttp3.mockwebserver.MockWebServer
import okhttp3.tls.HandshakeCertificates

class AcceptanceTestRule<T : Activity> : ActivityTestRule<T> {
    private var port: Int

    companion object {
        private val TAG = AcceptanceTestRule::class.java.simpleName
        private const val DEFAULT_PORT = 65000
    }

    val mockWebServer: MockWebServer by lazy { MockWebServer() }

    constructor(activityClass: Class<T>?) : this(activityClass, DEFAULT_PORT)
    constructor(activityClass: Class<T>?, port: Int) : super(activityClass, true) {
        this.port = port
    }

    constructor(activityClass: Class<T>?, initialTouchMode: Boolean, port: Int) : super(
        activityClass,
        initialTouchMode,
        true
    ) {
        this.port = port
    }

    constructor(
        activityClass: Class<T>?,
        initialTouchMode: Boolean,
        launchActivity: Boolean,
        port: Int = DEFAULT_PORT
    ) : super(
        activityClass,
        initialTouchMode,
        launchActivity
    ) {
        this.port = port
    }

    constructor(
        activityFactory: SingleActivityFactory<T>?,
        initialTouchMode: Boolean,
        launchActivity: Boolean,
        port: Int = DEFAULT_PORT
    ) : super(
        activityFactory,
        initialTouchMode,
        launchActivity
    ) {
        this.port = port
    }

    constructor(
        activityClass: Class<T>?,
        targetPackage: String,
        launchFlags: Int,
        initialTouchMode: Boolean,
        launchActivity: Boolean,
        port: Int
    ) : super(activityClass, targetPackage, launchFlags, initialTouchMode, launchActivity) {
        this.port = port
    }

    override fun beforeActivityLaunched() {
        try {
            mockWebServer.useHttps(createHandshakeCertificate().sslSocketFactory(), false)
            mockWebServer.start(this.port)
        } catch (i: IllegalStateException) {
            Log.e(TAG, "Cannot start mockWebserver. Do you launch activity twice?")
            throw i
        } finally {
            super.beforeActivityLaunched()
        }
    }

    override fun afterActivityFinished() {
        super.afterActivityFinished()
        mockWebServer.shutdown()
    }

    private fun createHandshakeCertificate(): HandshakeCertificates {
        return HandshakeCertificates.Builder()
            .heldCertificate(TestCertificate.localhostCertificate)
            .build()
    }
}