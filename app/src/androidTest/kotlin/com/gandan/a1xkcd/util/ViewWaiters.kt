package com.gandan.a1xkcd.util

import android.os.SystemClock
import android.view.View
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.util.HumanReadables
import org.hamcrest.Matcher

fun waitUntilNotDisplayed() = NotDisplayedWaiter()

class NotDisplayedWaiter : ViewAction {
    private companion object {
        private const val timeout = 30_000L
    }

    override fun getDescription(): String = "Wait until view is not displayed"

    override fun getConstraints(): Matcher<View> = ViewMatchers.isAssignableFrom(View::class.java)

    override fun perform(uiController: UiController?, view: View?) {
        // if view is not displayed
        if (!ViewMatchers.isDisplayed().matches(view)) {
            uiController?.loopMainThreadUntilIdle()

            // recheck that view is not displayed and do nothing
            if (!ViewMatchers.isDisplayed().matches(view)) return
        }

        val start = SystemClock.currentThreadTimeMillis()
        do {
            uiController?.loopMainThreadUntilIdle()
            val now = SystemClock.currentThreadTimeMillis()
            if (timeout < now - start) {
                throw PerformException.Builder()
                    .withActionDescription(this.getDescription())
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(RuntimeException("View is never not displayed after wait for $timeout ms"))
                    .build()
            }
        } while (ViewMatchers.isDisplayed().matches(view))
    }
}

fun waitUntilDisplayed(): ViewAction = DisplayedWaiter()

class DisplayedWaiter : ViewAction {
    private companion object {
        private const val timeout = 30_000L
    }

    override fun getDescription(): String = "Wait until view is displayed"

    override fun getConstraints(): Matcher<View> = ViewMatchers.isAssignableFrom(View::class.java)

    override fun perform(uiController: UiController?, view: View?) {
        // if view is not displayed
        if (ViewMatchers.isDisplayed().matches(view)) {
            uiController?.loopMainThreadUntilIdle()

            // recheck that view is displayed and do nothing
            if (ViewMatchers.isDisplayed().matches(view)) return
        }

        val start = SystemClock.currentThreadTimeMillis()
        do {
            uiController?.loopMainThreadUntilIdle()
            val now = SystemClock.currentThreadTimeMillis()
            if (timeout < now - start) {
                throw PerformException.Builder()
                    .withActionDescription(this.getDescription())
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(RuntimeException("View is never displayed after wait for $timeout ms"))
                    .build()
            }
        } while (!ViewMatchers.isDisplayed().matches(view))
    }
}