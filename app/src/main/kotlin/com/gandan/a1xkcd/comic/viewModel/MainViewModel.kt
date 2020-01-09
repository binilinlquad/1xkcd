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
    companion object {
        const val TOTAL_PAGE_EMPTY = 0
    }

    var currentTotalPages = TOTAL_PAGE_EMPTY
        private set

    lateinit var pages: LiveData<PagedList<Page>>
        private set

    private val fetchListener = FetchListener.create(
        onSuccess = { pages ->
            if (pages > 0) {
                currentTotalPages = pages

                _state.value = MainState.ShowComic
            } else {
                _state.value = MainState.Empty
            }
        },

        onError = {
            currentTotalPages = TOTAL_PAGE_EMPTY
            _state.value = MainState.Error(it)
        })

    private val _state = MutableLiveData<MainState>()
    val event: LiveData<MainState> = _state

    // service is not part of bind so should not put as parameter, but we can ignore it for now
    fun bind(service: XkcdService) {
        if (_state.value == MainState.ShowComic) {
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
    object ShowComic : MainState()
    object Empty : MainState()
    object Refresh : MainState()
    class Error(val error: Throwable) : MainState()
}