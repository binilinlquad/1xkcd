package com.gandan.a1xkcd

import android.view.View
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import com.gandan.a1xkcd.rule.ComicAcceptanceTestFixture
import com.gandan.a1xkcd.rule.MockWebServerRule
import com.gandan.a1xkcd.util.RecyclerViewMatcher
import com.gandan.a1xkcd.util.WaitUntilAdapterHasItems
import com.gandan.a1xkcd.util.waitUntilNotDisplayed
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComicAcceptanceTest {

    @Rule
    @JvmField
    val mockWebServerRule = MockWebServerRule(MOCKWEBSERVER_PORT)

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(ComicActivity::class.java, true, false)

    @Rule
    @JvmField
    val testFixture = ComicAcceptanceTestFixture(mockWebServerRule)

    @Test
    fun given_opening_app_and_success_load__then_user_should_able_see_comic_page() {
        testFixture.responseWithSuccessOnlyFirstPage()
        activityRule.launchActivity(null)
        waitComicPagesPopulated()

        val firstComic = comicContainerAt(0)
        onView(pageImage(firstComic)).check(matches(isDisplayed()))
    }

    @Test
    fun given_opening_app_and_refresh_failed__then_user_should_able_reload_whole_pages() {
        testFixture.responseWithFailAll()
        activityRule.launchActivity(null)
        testFixture.responseWithSuccessOnlyFirstPage()

        onView(withId(R.id.manual_refresh)).perform(click())

        val firstComic = comicContainerAt(0)
        onView(pageImage(firstComic)).check(matches(isDisplayed()))
    }

    @Test
    fun given_opening_app_and_failed_load_one_of_page__then_user_should_able_to_reload_that_page_only() {
        testFixture.responseWithFailLoadPage()
        activityRule.launchActivity(null)
        waitComicPagesPopulated()
        val firstComic = comicContainerAt(0)
        onView(pageRetry(firstComic)).check(matches(isDisplayed()))
        testFixture.responseWithSuccessOnlyFirstPage()

        onView(pageRetry(firstComic)).perform(click())

        onView(pageImage(firstComic)).check(matches(isDisplayed()))
    }

    @Test
    fun given_click_refresh_menu__then_user_should_able_to_reload_whole_pages() {
        testFixture.responseWithFailAll()
        activityRule.launchActivity(null)

        testFixture.responseWithSuccessOnlyFirstPage()

        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText(R.string.menu_refresh)).perform(click())

        waitComicPagesPopulated()

        val firstComic = comicContainerAt(0)
        onView(pageImage(firstComic)).check(matches(isDisplayed()))
    }

    @Test
    fun given_click_goto__then_user_should_able_choose_other_page() {
        val totalPages = testFixture.totalSamplePages
        val targetPage = 2105   // hardcoded for simplify setup
        val targetItemPosition = totalPages - targetPage
        testFixture.responseWithSuccessAll()

        activityRule.launchActivity(null)
        onView(withText(R.string.menu_goto)).perform(click())

        onData(allOf(`is`(instanceOf(String::class.java)), `is`("$targetPage")))
            .perform(click())

        val targetComic = comicContainerAt(targetItemPosition)
        onView(pageRetry(targetComic)).perform(waitUntilNotDisplayed())
        onView(pageImage(targetComic)).check(matches(isDisplayed()))
    }

    //region test utilities
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
    //endregion
}
