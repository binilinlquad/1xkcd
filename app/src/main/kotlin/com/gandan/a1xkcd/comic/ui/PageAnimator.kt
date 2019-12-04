package com.gandan.a1xkcd.comic.ui

import android.view.View
import android.view.animation.LinearInterpolator

class PageAnimator(private val comicPage: View, private val comicAlt: View) {

    companion object Duration {
        private const val DELAY_POST_ANIMATION = 500L

        private const val DELAY_SHOW_ALT_TEXT = 150L
        private const val SHOW_ALT_TEXT = 250L
        private const val HIDE_ALT_TEXT = 100L

        private const val FADE_PAGE_IN = 350L
        private const val FADE_PAGE_OUT = 500L

        private val linearInterpolator = LinearInterpolator()
    }

    fun showComic() {
        val hideAltTextAnimation = comicAlt.animate()
                .alpha(0.1f)
                .setDuration(HIDE_ALT_TEXT)
                .withEndAction {
                    comicAlt.visibility = View.INVISIBLE
                }

        val comicPageRevealAnimation = comicPage.animate()
                .alpha(1f)
            .setDuration(FADE_PAGE_IN)

        hideAltTextAnimation.start()
        comicPageRevealAnimation.start()
    }

    fun showAltText() {
        val comicPageFadeOutAnimator = comicPage.animate()
                .alpha(0.1f)
            .setDuration(FADE_PAGE_OUT)

        val altTextRevealAnimator = comicAlt.animate()
                .alpha(1f)
                .setDuration(SHOW_ALT_TEXT)
                .setInterpolator(linearInterpolator)
                .setStartDelay(DELAY_SHOW_ALT_TEXT)
                .withStartAction {
                    comicAlt.alpha = 0.0f
                    comicAlt.visibility = View.VISIBLE
                }

        comicPageFadeOutAnimator.start()
        altTextRevealAnimator.start()
    }

    private fun runImmediateOrPostAnimation(animator: Runnable) {
        if (comicPage.animation.hasStarted() || !comicPage.animation.hasEnded()) {
            comicPage.postDelayed(animator, DELAY_POST_ANIMATION)
        } else if (comicAlt.animation.hasStarted() || !comicAlt.animation.hasEnded()) {
            comicAlt.postDelayed(animator, DELAY_POST_ANIMATION)
        } else {
            // immediate run
            animator.run()
        }
    }
}