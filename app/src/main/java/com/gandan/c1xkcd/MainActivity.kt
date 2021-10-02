package com.gandan.c1xkcd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.*
import arrow.core.Either
import com.gandan.c1xkcd.entity.Strip
import com.gandan.c1xkcd.ui.screen.Screen
import com.gandan.c1xkcd.ui.theme.C1XkcdTheme
import com.gandan.c1xkcd.usecase.latestStrip
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get()
        setContent {
            C1XkcdTheme {
                Screen(viewModel)
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.refresh()
        }
    }
}

@ExperimentalSerializationApi
class MainViewModel : ViewModel() {
    private val _latest: MutableLiveData<Strip> = MutableLiveData()
    val latest: LiveData<Strip> = _latest
    // CH: Make it public for testing
    val _error: MutableLiveData<Throwable?> = MutableLiveData()
    val error: LiveData<Throwable?> = _error

    val _loading: MutableLiveData<Boolean> = MutableLiveData()
    val loading: LiveData<Boolean> = _loading



    suspend fun refresh() {
        _loading.value = true
        when(val result  = Either.catch { RUNTIME.latestStrip() }) {
            is Either.Right -> {
                _error.value = null
                _latest.value = result.value
            }
            is Either.Left -> _error.value = result.value
        }
        _loading.value = false
    }
}
