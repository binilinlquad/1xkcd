package com.gandan.a1xkcd.comic.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.gandan.a1xkcd.service.Page
import com.gandan.a1xkcd.service.XkcdService
import com.gandan.a1xkcd.ui.RefreshListener
import com.gandan.a1xkcd.util.AppDispatchers.network
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PageDataSourceFactory(
    private val service: XkcdService,
    private val uiScope: CoroutineScope,
    private val refreshListener: RefreshListener
) : DataSource.Factory<Int, Page>() {
    private val sourceLiveData = MutableLiveData<PageDataSource>()
    override fun create(): DataSource<Int, Page> {
        val source = PageDataSource(service, uiScope, refreshListener)
        sourceLiveData.postValue(source)
        return source
    }

}

class PageDataSource(
    private val service: XkcdService,
    private val uiScope: CoroutineScope,
    private val refreshListener: RefreshListener
) : ItemKeyedDataSource<Int, Page>() {

    companion object {
        private val TAG = PageDataSource::class.java.simpleName
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Page>) {
        uiScope.launch {
            try {
                val latestPage = loadLatestPage()
                val totalPages = latestPage.num

                Log.i(TAG, "load latest page successfully")
                callback.onResult(listOf(latestPage), 0, totalPages)
                refreshListener.onRefreshed(totalPages)
            } catch (e: Throwable) {
                Log.i(TAG, "fail loading latest page with reason ${e.message}")
                refreshListener.onError(e)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Page>) {
        uiScope.launch {
            val num = params.key - 1
            try {
                val page = loadPageAt(num)
                callback.onResult(listOf(page))
                Log.i(TAG, "load page $num successfully")
            } catch (e: Throwable) {
                Log.e(TAG, "fail loading page $num with reason ${e.message}")
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Page>) = Unit

    override fun getKey(item: Page): Int = item.num

    private suspend fun loadLatestPage(): Page {
        return withContext(network) {
            service.latestPage()
        }
    }

    private suspend fun loadPageAt(pageNum: Int): Page {
        return withContext(network) {
            service.at(pageNum)
        }
    }
}

