package com.gandan.a1xkcd.comic.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.gandan.a1xkcd.comic.ui.PageDataSourceFactory
import com.gandan.a1xkcd.service.Page
import com.gandan.a1xkcd.service.XkcdService
import com.gandan.a1xkcd.ui.FetchListener

class MainViewModel : ViewModel() {
    lateinit var pages: LiveData<PagedList<Page>>
        private set

    private val fetchListener = FetchListener.create(
        onSuccess = { pages ->
            if (pages > 0) {
                _state.value = MainState.ShowComic(pages)
            } else {
                _state.value = MainState.Empty
            }
        },

        onError = {
            _state.value = MainState.Error(it)
        })

    private val _state = MutableLiveData<MainState>()
    val state: LiveData<MainState> = _state

    // service is not part of bind so should not put as parameter, but we can ignore it for now
    fun bind(service: XkcdService) {
        if (_state.value is MainState.ShowComic) {
            _state.value = MainState.Refresh
        } else {
            _state.value = MainState.Filling
        }

        // next creation extract it to outside view model by using dagger
        val pageSourceFactory = PageDataSourceFactory(service, viewModelScope, fetchListener)
        pages = LivePagedListBuilder(pageSourceFactory, 1).build()
    }
}

sealed class MainState {
    object Filling : MainState()
    class ShowComic(val totalPages: Int) : MainState()
    object Empty : MainState()
    object Refresh : MainState()
    class Error(val error: Throwable) : MainState()
}