package com.gandan.a1xkcd

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.gandan.a1xkcd.rule.AcceptanceTestRule
import com.gandan.a1xkcd.rule.ComicAcceptanceTestFixture
import com.gandan.a1xkcd.util.ComicDispatcher
import com.gandan.a1xkcd.util.RecyclerViewMatcher
import com.gandan.a1xkcd.util.WaitUntilAdapterHasItems
import com.gandan.a1xkcd.util.waitUntilNotDisplayed
import okio.Buffer
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ComicActivityTest {

    @Rule
    @JvmField
    val activityRule = AcceptanceTestRule(ComicActivity::class.java, true, false, MOCKWEBSERVER_PORT)

    private val testFixture = ComicAcceptanceTestFixture(activityRule)

    @Test
    fun test_check_comic_shown() {
        activityRule.launchActivity(null)
        onView(withId(R.id.comics)).perform(WaitUntilAdapterHasItems())

        val firstComic = RecyclerViewMatcher(R.id.comics).atPosition(0)
        onView(allOf(isDescendantOfA(firstComic), withId(R.id.comic_loading))).perform(waitUntilNotDisplayed())
        onView(allOf(isDescendantOfA(firstComic), withId(R.id.comic_strip))).check(matches(isDisplayed()))
    }


    @Before
    fun setUp() {
        testFixture.setUp()

        mockWebServer.dispatcher = ComicDispatcher().apply {
            whenPathContains("/info.0.json")
                .thenResponseSuccess(sampleLatestPage("https://localhost:$MOCKWEBSERVER_PORT/sample.jpg"))
            whenPathContains("/sample.jpg")
                .thenResponseSuccess(
                    testFixture.openTestAsset("sample.jpg")
                    .let { Buffer().readFrom(it) })
        }
    }


    private val mockWebServer
        get() = testFixture.mockWebServer

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
        testFixture.tearDown()
    }
}
