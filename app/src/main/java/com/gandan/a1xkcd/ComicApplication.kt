package com.gandan.a1xkcd

import android.app.Activity
import android.app.Application
import com.gandan.a1xkcd.di.AppComponent
import com.gandan.a1xkcd.di.AppModule
import com.gandan.a1xkcd.di.DaggerAppComponent
import com.gandan.a1xkcd.di.ProductionServiceModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

open class ComicApplication : Application(), HasActivityInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        applicationInjector().inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }

    protected open fun applicationInjector(): AppComponent {
        return DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .productionServiceModule(ProductionServiceModule(this))
            .build()
    }

}