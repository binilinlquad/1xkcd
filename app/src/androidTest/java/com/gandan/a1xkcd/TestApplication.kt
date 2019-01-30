package com.gandan.a1xkcd

import android.os.StrictMode
import com.gandan.a1xkcd.di.AppComponent
import com.gandan.a1xkcd.di.DaggerTestAppComponent

class TestApplication : ComicApplication() {
    override fun onCreate() {
        super.onCreate()
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .permitNetwork()
                .build()
        )
    }

    override fun applicationInjector(): AppComponent {
        // follow sample https://google.github.io/dagger/testing.html
        return DaggerTestAppComponent.create()
    }
}
