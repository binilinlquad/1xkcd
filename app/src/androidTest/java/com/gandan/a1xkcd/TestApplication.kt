package com.gandan.a1xkcd

import com.gandan.a1xkcd.di.AppComponent
import com.gandan.a1xkcd.di.DaggerTestAppComponent

class TestApplication : ComicApplication() {
    override fun applicationInjector(): AppComponent {
        // follow sample https://google.github.io/dagger/testing.html
        return DaggerTestAppComponent.create()
    }
}
