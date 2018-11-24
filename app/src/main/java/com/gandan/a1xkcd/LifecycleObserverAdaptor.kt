package com.gandan.a1xkcd

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.OnLifecycleEvent

class LifecycleObserverAdaptor(private val lifecycle: Lifecycle) : LifecycleObserver {
    val state: MutableLiveData<Lifecycle.State> = MutableLiveData()

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun AnyEvent() {
        val newState = lifecycle.currentState
        if (state.value != newState) {
            state.value = newState
        }
    }
}