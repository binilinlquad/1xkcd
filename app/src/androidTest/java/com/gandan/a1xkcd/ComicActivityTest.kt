package com.gandan.a1xkcd

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.gandan.a1xkcd.rule.AcceptanceTestRule
import com.gandan.a1xkcd.rule.ComicAcceptanceTestFixture
import com.gandan.a1xkcd.util.RecyclerViewMatcher
import com.gandan.a1xkcd.util.WaitUntilAdapterHasItems
import com.gandan.a1xkcd.util.waitUntilNotDisplayed
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test

class ComicActivityTest {

    @Rule
    @JvmField
    val activityRule = AcceptanceTestRule(ComicActivity::class.java, true, false, MOCKWEBSERVER_PORT)

    @Rule
    @JvmField
    val testFixture = ComicAcceptanceTestFixture(activityRule)

    @Test
    fun given_opening_app_and_success_load__then_user_should_able_see_comic_strip() {
        testFixture.responseWithSuccessOnlyFirstStrip()
        activityRule.launchActivity(null)
        onView(withId(R.id.comics)).perform(WaitUntilAdapterHasItems())

        val firstComic = RecyclerViewMatcher(R.id.comics).atPosition(0)
        onView(allOf(isDescendantOfA(firstComic), withId(R.id.comic_loading))).perform(waitUntilNotDisplayed())
        onView(allOf(isDescendantOfA(firstComic), withId(R.id.comic_strip))).check(matches(isDisplayed()))
    }

    @Test
    fun given_opening_app_and_refresh_failed__then_user_should_able_reload_whole_strips() {
        testFixture.responseWithFailAll()
        activityRule.launchActivity(null)

        onView(withId(R.id.comics)).perform(waitUntilNotDisplayed())

        testFixture.responseWithSuccessOnlyFirstStrip()
        onView(withId(R.id.manual_refresh)).perform(click())
        val firstComic = RecyclerViewMatcher(R.id.comics).atPosition(0)
        onView(allOf(isDescendantOfA(firstComic), withId(R.id.comic_loading))).perform(waitUntilNotDisplayed())

        onView(allOf(isDescendantOfA(firstComic), withId(R.id.comic_strip))).check(matches(isDisplayed()))
    }

    private val mockWebServer
        get() = testFixture.mockWebServer

}
