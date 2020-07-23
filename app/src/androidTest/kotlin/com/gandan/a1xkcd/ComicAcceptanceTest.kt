package com.gandan.a1xkcd

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.gandan.a1xkcd.di.ProductionServiceModule
import com.gandan.a1xkcd.rule.ComicAcceptanceTestFixture
import com.gandan.a1xkcd.rule.MockWebServerRule
import com.gandan.a1xkcd.util.RecyclerViewMatcher
import com.gandan.a1xkcd.util.WaitUntilAdapterHasItems
import com.gandan.a1xkcd.util.waitUntilNotDisplayed
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import okhttp3.OkHttpClient
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@UninstallModules(ProductionServiceModule::class)
@HiltAndroidTest
open class ComicAcceptanceTest {

    @Rule
    @JvmField
    val hiltRule = HiltAndroidRule(this)

    @Rule
    @JvmField
    val mockWebServerRule = MockWebServerRule(MOCKWEBSERVER_PORT)

    private val testFixture = ComicAcceptanceTestFixture(mockWebServerRule)

    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Before
    fun setUp() {
        hiltRule.inject()
        testFixture.okHttpClient = okHttpClient
        testFixture.before()
    }

    @After
    fun tearDown() {
        testFixture.after()
    }

    @Test
    fun given_opening_app_and_success_load__then_user_should_able_see_comic_page() {
        testFixture.responseWithSuccessOnlyFirstPage()
        ActivityScenario.launch(ComicActivity::class.java)
        waitComicPagesPopulated()

        val firstComic = comicContainerAt(0)
        onView(pageImage(firstComic)).check(matches(isDisplayed()))
    }

    @Test
    fun given_opening_app_and_refresh_failed__then_user_should_able_reload_whole_pages() {
        testFixture.responseWithFailAll()
        ActivityScenario.launch(ComicActivity::class.java)

        onView(withId(R.id.manual_refresh)).check(matches(isDisplayed()))

        testFixture.responseWithSuccessOnlyFirstPage()

        onView(withId(R.id.manual_refresh)).perform(click())

        val firstComic = comicContainerAt(0)
        onView(pageImage(firstComic)).check(matches(isDisplayed()))
    }

    @Test
    fun given_opening_app_and_failed_load_one_of_page__then_user_should_able_to_reload_that_page_only() {
        testFixture.responseWithFailLoadPage()
        ActivityScenario.launch(ComicActivity::class.java)
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
        ActivityScenario.launch(ComicActivity::class.java)

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

        ActivityScenario.launch(ComicActivity::class.java)
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
