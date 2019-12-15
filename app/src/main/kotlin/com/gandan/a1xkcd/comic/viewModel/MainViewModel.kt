package com.gandan.a1xkcd.comic.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    companion object {
        const val TOTAL_PAGE_EMPTY = 0
    }

    private val empty = MutableLiveData(false)
    private val totalPage = MutableLiveData(TOTAL_PAGE_EMPTY)

    val comicIsEmpty: LiveData<Boolean> = empty
    val totalPages: LiveData<Int> = totalPage

    fun showEmptyPage() {
        empty.postValue(true)
        totalPage.postValue(TOTAL_PAGE_EMPTY)
    }

    fun showComicPages(pages: Int) {
        empty.postValue(false)
        totalPage.postValue(pages)
    }
}