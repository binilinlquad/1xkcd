package com.gandan.a1xkcd

import android.content.Context
import android.view.View
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import com.gandan.a1xkcd.di.ProductionServiceModule
import com.gandan.a1xkcd.di.ServiceModule
import com.gandan.a1xkcd.https.TestCertificate
import com.gandan.a1xkcd.rule.ComicAcceptanceTestFixture
import com.gandan.a1xkcd.rule.MockWebServerRule
import com.gandan.a1xkcd.service.XkcdService
import com.gandan.a1xkcd.service.createXkcdService
import com.gandan.a1xkcd.util.RecyclerViewMatcher
import com.gandan.a1xkcd.util.WaitUntilAdapterHasItems
import com.gandan.a1xkcd.util.waitUntilNotDisplayed
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.tls.HandshakeCertificates
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.Executors
import java.util.concurrent.Future
import javax.inject.Inject

@UninstallModules(ProductionServiceModule::class)
@HiltAndroidTest
open class ComicAcceptanceTest {

    @Rule
    @JvmField
    val hiltRule = HiltAndroidRule(this)

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(ComicActivity::class.java, true, false)

    @Rule
    @JvmField
    val mockWebServerRule = MockWebServerRule(MOCKWEBSERVER_PORT)

    val testFixture = ComicAcceptanceTestFixture(mockWebServerRule)

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

    @Module
    @InstallIn(ApplicationComponent::class)
    object FakeServiceModule : ServiceModule {

        @Provides
        override fun webClient(@ApplicationContext context: Context): OkHttpClient {
            val futureTask: Future<HandshakeCertificates> =
                Executors.newSingleThreadExecutor().submit<HandshakeCertificates> {
                    HandshakeCertificates.Builder()
                        .addTrustedCertificate(TestCertificate.localhostCertificate.certificate)
                        .build()
                }
            val clientCertificates = futureTask.get()

            return OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .sslSocketFactory(
                    clientCertificates.sslSocketFactory(),
                    clientCertificates.trustManager
                )
                .cache(null)
                .build()
        }

        @Provides
        override fun service(okHttpClient: OkHttpClient): XkcdService {
            return createXkcdService(okHttpClient, "https://localhost:$MOCKWEBSERVER_PORT")
        }
    }


}
