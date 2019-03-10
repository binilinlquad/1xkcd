package com.gandan.a1xkcd

import android.util.Log
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

    private val testApplication
        get() = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApplication

    private val okHttpClient
        get() = testApplication.component.okHttpClient()

    private val okHttp3IdlingResource
        get() = OkHttp3IdlingResource.create("OkHttpIdlingResource", okHttpClient)

    private val latch = CountDownLatch(2)
    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(okHttp3IdlingResource)

        activityRule.mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                Log.e("Heroo", "$request")
                requireNotNull(request)

                return if (request.path.contains("/info.0.json")) {
                    latch.countDown()
                    MockResponse()
                        .setResponseCode(200)
                        .setBody(
                            """
                    {"month": "2",
                    "num": 2110,
                    "link": "",
                    "year": "2019",
                    "news": "",
                    "safe_title":
                    "Error Bars",
                    "transcript": "",
                    "alt": "...an effect size of 1.68 (95% CI: 1.56 (95% CI: 1.52 (95% CI: 1.504 (95% CI: 1.494 (95% CI: 1.488 (95% CI: 1.485 (95% CI: 1.482 (95% CI: 1.481 (95% CI: 1.4799 (95% CI: 1.4791 (95% CI: 1.4784...",
                    "img": "./sample.jpg",
                    "title": "Error Bars",
                    "day": "11"}
                """.trimIndent()
                        )
                } else if (request.path.contains("sample.jpg")) {
                    latch.countDown()
                    MockResponse().setResponseCode(200)
                        .setBody(
                            Buffer().readFrom(
                                InstrumentationRegistry.getInstrumentation().targetContext.assets.open(
                                    "sample.jpg"
                                )
                            )
                        )
                } else {
                    throw IllegalArgumentException("request $request is not handled")
                }
            }

        }
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(okHttp3IdlingResource)
    }

    @Test
    fun test_check_comic_shown() {
        latch.await(3, TimeUnit.SECONDS)

        onView(withId(R.id.comic_strip))
            .check(matches(isDisplayed()))
    }

}