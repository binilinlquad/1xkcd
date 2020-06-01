package com.gandan.a1xkcd.comic.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.gandan.a1xkcd.comic.model.MainMsg
import com.gandan.a1xkcd.comic.model.MainState
import com.gandan.a1xkcd.comic.model.reduce
import com.gandan.a1xkcd.comic.ui.PageDataSourceFactory
import com.gandan.a1xkcd.service.Page
import com.gandan.a1xkcd.service.XkcdService
import com.gandan.a1xkcd.ui.FetchListener

class MainViewModel : ViewModel() {
    lateinit var pages: LiveData<PagedList<Page>>
        private set

    private val fetchListener = FetchListener.create(
        onSuccess = { pages ->
            _state.value = reduce(_state.value!!, MainMsg.SuccessMsg(pages))
        },

        onError = {
            _state.value = reduce(_state.value!!, MainMsg.ErrorMsg(it))
        })

    private val _state = MutableLiveData<MainState>(MainState.Initial)
    val state: LiveData<MainState> = _state

    // service is not part of bind so should not put as parameter, but we can ignore it for now
    fun bind(service: XkcdService) {
        _state.value = reduce(_state.value!!, MainMsg.LoadingMsg)

        // next creation extract it to outside view model by using dagger
        val pageSourceFactory = PageDataSourceFactory(service, viewModelScope, fetchListener)
        pages = LivePagedListBuilder(pageSourceFactory, 1).build()
    }
}

