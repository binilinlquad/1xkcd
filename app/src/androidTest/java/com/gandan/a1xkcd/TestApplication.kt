package com.gandan.a1xkcd

import com.gandan.a1xkcd.di.AppComponent
import com.gandan.a1xkcd.di.DaggerTestAppComponent
import com.gandan.a1xkcd.di.TestAppComponent

class TestApplication : ComicApplication() {
    lateinit var component: TestAppComponent
    override fun applicationInjector(): AppComponent {
        // follow sample https://google.github.io/dagger/testing.html
        component = DaggerTestAppComponent.create()
        return component
    }
}
