package com.gandan.a1xkcd.util

import android.os.SystemClock
import android.view.View
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.util.HumanReadables
import org.hamcrest.Matcher

fun waitUntilNotDisplayed(): ViewAction {
    return NotDisplayedWaiter()
}

class NotDisplayedWaiter : ViewAction {
    private companion object {
        private const val timeout = 30_000L
    }

    override fun getDescription(): String = "Wait until view is not displayed"

    override fun getConstraints(): Matcher<View> = ViewMatchers.isDisplayed()

    override fun perform(uiController: UiController?, view: View?) {
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