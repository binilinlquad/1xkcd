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

    private val empty = MutableLiveData(false)
    private val totalPage = MutableLiveData(TOTAL_PAGE_EMPTY)

    val comicIsEmpty: LiveData<Boolean> = empty
    val totalPages: LiveData<Int> = totalPage
    lateinit var pages: LiveData<PagedList<Page>>
        private set
    private val _lastError = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = _lastError
    private val fetchListener = FetchListener.create(
        onSuccess = { pages ->
            if (pages > 0) showComicPages(pages) else showEmptyPage()
        },
        onError = {
            showEmptyPage()
            _lastError.value = it
        })

    private fun showEmptyPage() {
        empty.postValue(true)
        totalPage.postValue(TOTAL_PAGE_EMPTY)
    }

    private fun showComicPages(pages: Int) {
        empty.postValue(false)
        totalPage.postValue(pages)
    }

    fun setPageProvider(service: XkcdService) {
        // next creation extract it to outside view model by using dagger
        val pageSourceFactory = PageDataSourceFactory(service, viewModelScope, fetchListener)
        pages = LivePagedListBuilder(pageSourceFactory, 1).build()
    }
}