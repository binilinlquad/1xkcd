package com.gandan.a1xkcd.rule

import android.app.Activity
import androidx.test.espresso.IdlingRegistry
import androidx.test.platform.app.InstrumentationRegistry
import com.gandan.a1xkcd.TestApplication
import com.jakewharton.espresso.OkHttp3IdlingResource
import java.io.InputStream

class ComicAcceptanceTestFixture<T : Activity>(private val rule: AcceptanceTestRule<T>) {

    val mockWebServer
        get() = rule.mockWebServer

    private val testApplication
        get() = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApplication

    private val okHttpClient
        get() = testApplication.component.okHttpClient()

    private val okHttp3IdlingResource by lazy {
        OkHttp3IdlingResource.create("OkHttpIdlingResource", okHttpClient)
    }

    private val testContext = InstrumentationRegistry.getInstrumentation().context

    private val idlingRegistry
        get() = IdlingRegistry.getInstance()

    fun setUp() {
        idlingRegistry.register(okHttp3IdlingResource)
    }

    fun tearDown() {
        idlingRegistry.unregister(okHttp3IdlingResource)
    }

    fun openTestAsset(filename: String): InputStream {
        return testContext.assets.open(filename)
    }

}