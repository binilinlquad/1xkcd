package com.gandan.a1xkcd.experiment

import org.junit.Test

class ComicMainTest {

    @Test
    fun given_screen_is_never_started__app_should_not_show_comics() {
        val screen = FakeScreen()
        val view = AssertCapableView()
        val server = ComicMain()
        server.bind(screen, view)

        view.assertComicsIsNotShown()
    }

    @Test
    fun given_screen_start__app_should_show_comics() {
        val screen = FakeScreen()
        val view = AssertCapableView()
        val server = ComicMain()
        server.bind(screen, view)

        screen.start()

        view.assertComicsIsShown()
    }

    class AssertCapableView : ComicMain.View {
        private var hasShown = false

        fun assertComicsIsNotShown() {
            assert(!hasShown) { "Show comics function should not invoked" }
        }

        fun assertComicsIsShown() {
            assert(hasShown) { "Show comics function should be invoked" }
        }

        override fun showComics() {
            hasShown = true
        }

    }

    class FakeScreen : ComicMain.Screen {

        private lateinit var listener: ComicMain.Screen.ScreenEventListener

        fun start() {
            listener.onEvent(ComicMain.Screen.Event.STARTED)
        }

        override fun onBind(listener: ComicMain.Screen.ScreenEventListener) {
            this.listener = listener
            this.listener.onEvent(ComicMain.Screen.Event.UNKNOWN)
        }

    }
}