package com.gandan.a1xkcd.comic.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.gandan.a1xkcd.comic.ui.PageDataSourceFactory
import com.gandan.a1xkcd.service.Page
import com.gandan.a1xkcd.service.XkcdService
import com.gandan.a1xkcd.ui.RefreshListener
import kotlinx.coroutines.CoroutineScope

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

    fun showEmptyPage() {
        empty.postValue(true)
        totalPage.postValue(TOTAL_PAGE_EMPTY)
    }

    fun showComicPages(pages: Int) {
        empty.postValue(false)
        totalPage.postValue(pages)
    }

    // main scope is related with Main Thread. If we look at TiVi, TiVi have viewModel scope
    // TODO : why need different scope?
    fun setPageProvider(
        service: XkcdService,
        mainScope: CoroutineScope,
        refreshListener: RefreshListener
    ) {
        val pageSourceFactory = PageDataSourceFactory(service, mainScope, refreshListener)
        pages = LivePagedListBuilder(pageSourceFactory, 1)
            .build()

    }
}