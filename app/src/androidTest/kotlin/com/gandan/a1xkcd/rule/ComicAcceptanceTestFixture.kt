package com.gandan.a1xkcd.rule

import androidx.test.espresso.IdlingRegistry
import androidx.test.platform.app.InstrumentationRegistry
import coil.Coil
import com.gandan.a1xkcd.MOCKWEBSERVER_PORT
import com.gandan.a1xkcd.util.ComicDispatcher
import com.jakewharton.espresso.OkHttp3IdlingResource
import okhttp3.OkHttpClient
import okio.Buffer
import java.io.InputStream


class ComicAcceptanceTestFixture(private val rule: MockWebServerRule) {

    val mockWebServer
        get() = rule.mockWebServer

    lateinit var okHttpClient: OkHttpClient

    private val okHttp3IdlingResource by lazy {
        OkHttp3IdlingResource.create("OkHttpIdlingResource", okHttpClient)
    }

    private val testContext = InstrumentationRegistry.getInstrumentation().context

    private val idlingRegistry
        get() = IdlingRegistry.getInstance()

    val totalSamplePages = 2110

    private fun setUp() {
        idlingRegistry.register(okHttp3IdlingResource)
    }

    private fun tearDown() {
        idlingRegistry.unregister(okHttp3IdlingResource)
        // clear coil cache inside image loader so every load will trigger network request
        Coil.loader().clearMemory()
    }

    fun before() {
        setUp()
    }

    fun after() {
        tearDown()
    }

    fun responseWithSuccessOnlyFirstPage() {
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


    fun responseWithFailLoadPage() {
        mockWebServer.dispatcher = ComicDispatcher().apply {
            whenPathContains("/info.0.json")
                .thenResponseSuccess(sampleLatestPage("https://localhost:$MOCKWEBSERVER_PORT/sample.jpg"))
        }
    }

    fun responseWithSuccessAll() {
        val dispatcher = ComicDispatcher().apply {
            whenPathContains("/info.0.json")
                .thenResponseSuccess(sampleLatestPage("https://localhost:$MOCKWEBSERVER_PORT/sample.jpg"))

            whenPathContains("/sample.jpg")
                .thenResponseSuccess(openTestAsset("sample.jpg")
                    .let { Buffer().readFrom(it) })

            rest()
                .thenResponseSuccess(sampleLatestPage("https://localhost:$MOCKWEBSERVER_PORT/sample.jpg"))

        }

        mockWebServer.dispatcher = dispatcher
    }

    private fun openTestAsset(filename: String): InputStream {
        return testContext.assets.open(filename)
    }


    private fun sampleLatestPage(imageUrl: String): String {
        return """
                {"month": "2",
                "num": $totalSamplePages,
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