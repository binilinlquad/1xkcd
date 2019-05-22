package com.gandan.a1xkcd.rule

import android.app.Activity
import androidx.test.espresso.IdlingRegistry
import androidx.test.platform.app.InstrumentationRegistry
import com.gandan.a1xkcd.MOCKWEBSERVER_PORT
import com.gandan.a1xkcd.TestApplication
import com.gandan.a1xkcd.util.ComicDispatcher
import com.jakewharton.espresso.OkHttp3IdlingResource
import okio.Buffer
import org.junit.rules.ExternalResource
import java.io.InputStream

class ComicAcceptanceTestFixture<T : Activity>(private val rule: AcceptanceTestRule<T>) : ExternalResource() {

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

    private fun setUp() {
        idlingRegistry.register(okHttp3IdlingResource)
    }

    private fun tearDown() {
        idlingRegistry.unregister(okHttp3IdlingResource)
    }

    override fun before() {
        setUp()
        super.before()
    }

    override fun after() {
        super.after()
        tearDown()
    }

    fun responseWithSuccessOnlyFirstStrip() {
        val dispatcher = ComicDispatcher().apply {
            whenPathContains("/info.0.json")
                .thenResponseSuccess(sampleLatestPage("https://localhost:$MOCKWEBSERVER_PORT/sample.jpg"))

            whenPathContains("/sample.jpg")
                .thenResponseSuccess(openTestAsset("sample.jpg")
                    .let { Buffer().readFrom(it) })
        }

        mockWebServer.dispatcher = dispatcher
    }


    fun responseWithFailAll() {
        mockWebServer.dispatcher = ComicDispatcher()
    }


    private fun openTestAsset(filename: String): InputStream {
        return testContext.assets.open(filename)
    }


    private fun sampleLatestPage(imageUrl: String): String {
        return """
                {"month": "2",
                "num": 2110,
                "link": "",
                "year": "2019",
                "news": "",
                "safe_title":
                "Error Bars",
                "transcript": "",
                "alt": "...an effect size of 1.68 (95% CI: 1.56 (95% CI: 1.52 (95% CI: 1.504 (95% CI: 1.494 (95% CI: 1.488 (95% CI: 1.485 (95% CI: 1.482 (95% CI: 1.481 (95% CI: 1.4799 (95% CI: 1.4791 (95% CI: 1.4784...",
                "img": "$imageUrl",
                "title": "Error Bars",
                "day": "11"}
            """.trimIndent()
    }

}