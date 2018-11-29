package com.gandan.a1xkcd

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent

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