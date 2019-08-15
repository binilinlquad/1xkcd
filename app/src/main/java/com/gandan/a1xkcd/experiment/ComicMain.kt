package com.gandan.a1xkcd.experiment

class ComicMain {
    private lateinit var view: View
    private lateinit var screen: Screen
    private var listener: Screen.ScreenEventListener = createListener()

    fun bind(screen: Screen, view: View) {
        this.screen = screen
        this.view = view
        this.screen.onBind(listener)
    }

    private fun showComics() {
        view.showComics()
    }

    private fun createListener(): Screen.ScreenEventListener {
        return object : Screen.ScreenEventListener {
            override fun onEvent(event: Screen.Event) {
                if (event != Screen.Event.STARTED) return

                showComics()
            }
        }
    }

    interface View {
        fun showComics()

    }

    interface Screen {
        enum class Event {
            UNKNOWN,
            STARTED
        }

        interface ScreenEventListener {
            fun onEvent(event: Event)
        }

        fun onBind(listener: ScreenEventListener)

    }

}


