package com.gandan.a1xkcd.comic.ui

import android.util.Log
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.gandan.a1xkcd.service.Page
import com.gandan.a1xkcd.service.XkcdService
import com.gandan.a1xkcd.ui.FetchListener
import com.gandan.a1xkcd.util.AppDispatchers.network
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PageDataSourceFactory(
    private val service: XkcdService,
    private val coroutineScope: CoroutineScope,
    private val fetchListener: FetchListener
) : DataSource.Factory<Int, Page>() {
    override fun create(): DataSource<Int, Page> {
        return PageDataSource(service, coroutineScope, fetchListener)
    }

}

class PageDataSource(
    private val service: XkcdService,
    private val uiScope: CoroutineScope,
    private val fetchListener: FetchListener
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
                fetchListener.onSuccess(totalPages)
            } catch (e: Throwable) {
                Log.i(TAG, "fail loading latest page with reason ${e.message}")
                fetchListener.onError(e)
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

