package com.gandan.c1xkcd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import arrow.core.Either
import com.gandan.c1xkcd.entity.Strip
import com.gandan.c1xkcd.ui.screen.ComicStrip
import com.gandan.c1xkcd.ui.screen.Screen
import com.gandan.c1xkcd.ui.theme.C1XkcdTheme
import com.gandan.c1xkcd.usecase.latestStrip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get()
        setContent {
            val loading : Flow<Boolean> = viewModel.loading
            val comicStrip : Flow<Strip?> = viewModel.latest
            val errorFlow : Flow<Throwable?> = viewModel.error

            C1XkcdTheme {
                Screen(errorFlow, loading) { ComicStrip(comicStrip) }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.refresh()
        }
    }
}

@ExperimentalSerializationApi
class MainViewModel : ViewModel() {
    private val _latest: MutableStateFlow<Strip?> = MutableStateFlow(null)
    val latest: Flow<Strip?> = _latest
    private val _error: MutableStateFlow<Throwable?> = MutableStateFlow(null)
    val error: Flow<Throwable?> = _error

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading: Flow<Boolean> = _loading



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
