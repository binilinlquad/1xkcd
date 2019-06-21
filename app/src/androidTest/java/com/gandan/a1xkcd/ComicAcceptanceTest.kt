package com.gandan.a1xkcd

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.gandan.a1xkcd.rule.AcceptanceTestRule
import com.gandan.a1xkcd.rule.ComicAcceptanceTestFixture
import com.gandan.a1xkcd.util.RecyclerViewMatcher
import com.gandan.a1xkcd.util.WaitUntilAdapterHasItems
import com.gandan.a1xkcd.util.waitUntilNotDisplayed
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test

class ComicAcceptanceTest {

    @Rule
    @JvmField
    val activityRule = AcceptanceTestRule(ComicActivity::class.java, true, false, MOCKWEBSERVER_PORT)

    @Rule
    @JvmField
    val testFixture = ComicAcceptanceTestFixture(activityRule)

    @Test
    fun given_opening_app_and_success_load__then_user_should_able_see_comic_page() {
        testFixture.responseWithSuccessOnlyFirstPage()
        activityRule.launchActivity(null)
        waitComicPagesPopulated()

        val firstComic = comicContainerAt(0)
        onView(pageProgressBar(firstComic)).perform(waitUntilNotDisplayed())
        onView(pageImage(firstComic)).check(matches(isDisplayed()))
    }

    @Test
    fun given_opening_app_and_refresh_failed__then_user_should_able_reload_whole_pages() {
        testFixture.responseWithFailAll()
        activityRule.launchActivity(null)
        onView(withId(R.id.comics)).perform(waitUntilNotDisplayed())
        testFixture.responseWithSuccessOnlyFirstPage()

        onView(withId(R.id.manual_refresh)).perform(click())

        val firstComic = comicContainerAt(0)
        onView(pageProgressBar(firstComic)).perform(waitUntilNotDisplayed())
        onView(pageImage(firstComic)).check(matches(isDisplayed()))
    }

    @Test
    fun given_opening_app_and_failed_load_one_of_page__then_user_should_able_to_reload_that_page_only() {
        testFixture.responseWithFailLoadPage()
        activityRule.launchActivity(null)
        waitComicPagesPopulated()
        val firstComic = comicContainerAt(0)
        onView(pageProgressBar(firstComic)).perform(waitUntilNotDisplayed())
        onView(pageRetry(firstComic)).check(matches(isDisplayed()))
        testFixture.responseWithSuccessOnlyFirstPage()

        onView(pageRetry(firstComic)).perform(click())
        onView(pageProgressBar(firstComic)).perform(waitUntilNotDisplayed())

        onView(pageImage(firstComic)).check(matches(isDisplayed()))
    }

    @Test
    fun given_click_refresh_menu__then_user_should_able_to_reload_whole_pages() {
        testFixture.responseWithFailAll()
        activityRule.launchActivity(null)
        onView(withId(R.id.comics)).perform(waitUntilNotDisplayed())

        testFixture.responseWithSuccessOnlyFirstPage()

        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText(R.string.menu_refresh)).perform(click())

        waitComicPagesPopulated()

        val firstComic = comicContainerAt(0)
        onView(pageImage(firstComic)).check(matches(isDisplayed()))
    }

    private fun waitComicPagesPopulated() {
        onView(withId(R.id.comics)).perform(WaitUntilAdapterHasItems())
    }

    private fun pageProgressBar(pageContainerMatcher: Matcher<View>): Matcher<View> {
        return allOf(isDescendantOfA(pageContainerMatcher), withId(R.id.comic_loading))
    }

    private fun pageImage(pageContainerMatcher: Matcher<View>): Matcher<View> {
        return allOf(isDescendantOfA(pageContainerMatcher), withId(R.id.comic_page))
    }

    private fun pageRetry(pageContainerMatcher: Matcher<View>): Matcher<View> {
        return allOf(isDescendantOfA(pageContainerMatcher), withId(R.id.comic_retry))
    }

    private fun comicContainerAt(pos: Int): Matcher<View> {
        return RecyclerViewMatcher(R.id.comics).atPosition(pos)
    }
}
