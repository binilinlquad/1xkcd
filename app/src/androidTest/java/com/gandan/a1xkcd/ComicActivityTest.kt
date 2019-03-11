package com.gandan.a1xkcd

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import com.gandan.a1xkcd.rule.AcceptanceTestRule
import com.jakewharton.espresso.OkHttp3IdlingResource
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import okio.Buffer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ComicActivityTest {

    @Rule
    @JvmField
    val activityRule = AcceptanceTestRule(ComicActivity::class.java, MOCKWEBSERVER_PORT)

    @Test
    fun test_check_comic_shown() {
        // wait until sample image is load
        latch.await(3, TimeUnit.SECONDS)

        onView(withId(R.id.comic_strip))
            .check(matches(isDisplayed()))
    }


    @Before
    fun setUp() {
        idlingRegistry.register(okHttp3IdlingResource)

        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                requireNotNull(request)

                return when {
                    request.path.contains("/info.0.json") -> {
                        latch.countDown()
                        MockResponse()
                            .setResponseCode(200)
                            .setBody(sampleLatestPage("${mockWebServer.url("/sample.jpg")}"))
                    }
                    request.path.contains("/sample.jpg") -> {
                        latch.countDown()
                        MockResponse()
                            .setResponseCode(200)
                            .setBody(
                                testContext.assets.open("sample.jpg")
                                    .let { Buffer().readFrom(it) }
                            )
                    }
                    else -> throw IllegalArgumentException("request $request is not handled")
                }
            }

        }
    }

    private val testApplication
        get() = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApplication

    private val okHttpClient
        get() = testApplication.component.okHttpClient()

    private val okHttp3IdlingResource
        get() = OkHttp3IdlingResource.create("OkHttpIdlingResource", okHttpClient)

    private val latch = CountDownLatch(2)

    private val testContext = InstrumentationRegistry.getInstrumentation().context

    private val mockWebServer
        get() = activityRule.mockWebServer

    private val idlingRegistry
        get() = IdlingRegistry.getInstance()


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

    @After
    fun tearDown() {
        idlingRegistry.unregister(okHttp3IdlingResource)
    }
}
