package com.gandan.a1xkcd.util

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher
import java.lang.RuntimeException

class WaitUntilAdapterHasItems : ViewAction {
    override fun getDescription(): String {
        return "wait for adapter has item more than 0"
    }

    override fun getConstraints(): Matcher<View> {
        return ViewMatchers.isAssignableFrom(RecyclerView::class.java)
    }

    override fun perform(uiController: UiController?, view: View?) {
        val recyclerView = view as? RecyclerView
        var counter = 0
        var success = false
        do {
            uiController?.loopMainThreadForAtLeast(300)
            val hasItem = recyclerView?.adapter?.itemCount ?: 0 > 0
            if (hasItem) {
                success = true
                break
            }
        } while (counter++ < 100)

        if (!success) throw RuntimeException("$view never has items until 30 seconds")
    }
}
