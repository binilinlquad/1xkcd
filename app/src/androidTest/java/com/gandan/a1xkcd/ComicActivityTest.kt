package com.gandan.a1xkcd

import android.os.SystemClock
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.gandan.a1xkcd.di.localhostCertificate
import com.gandan.a1xkcd.di.mockWebServer
import okhttp3.mockwebserver.MockResponse
import okhttp3.tls.HandshakeCertificates
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ComicActivityTest {

    @Rule
    @JvmField
    val activityRule = ActivityTestRule<ComicActivity>(ComicActivity::class.java)

    @Before
    fun setUp() {
        val serverCertificates = HandshakeCertificates.Builder()
            .heldCertificate(localhostCertificate)
            .build()
        mockWebServer.useHttps(serverCertificates.sslSocketFactory(), false)
        mockWebServer.start(8080)
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                    {"month": "2", "num": 2110, "link": "", "year": "2019", "news": "", "safe_title": "Error Bars", "transcript": "", "alt": "...an effect size of 1.68 (95% CI: 1.56 (95% CI: 1.52 (95% CI: 1.504 (95% CI: 1.494 (95% CI: 1.488 (95% CI: 1.485 (95% CI: 1.482 (95% CI: 1.481 (95% CI: 1.4799 (95% CI: 1.4791 (95% CI: 1.4784...", "img": "https://imgs.xkcd.com/comics/error_bars.png", "title": "Error Bars", "day": "11"}
                """.trimIndent()
                )
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun test_check_comic_shown() {
//        SystemClock.sleep(10000)
        onView(withId(R.id.comic_strip)).check(matches(isDisplayed()))
    }

}