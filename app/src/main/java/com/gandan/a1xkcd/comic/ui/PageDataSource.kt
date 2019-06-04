package com.gandan.a1xkcd.comic.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import await
import com.gandan.a1xkcd.ComicActivity
import com.gandan.a1xkcd.RefreshListener
import com.gandan.a1xkcd.service.Page
import com.gandan.a1xkcd.service.XkcdService
import com.gandan.a1xkcd.util.AppDispatchers.network
import kotlinx.coroutines.*

class PageDataSourceFactory(
    private val service: XkcdService,
    private val uiScope: CoroutineScope,
    private val activity: ComicActivity
) : DataSource.Factory<Int, Page>() {
    private val sourceLiveData = MutableLiveData<PageDataSource>()
    override fun create(): DataSource<Int, Page> {
        val source = PageDataSource(service, uiScope, activity)
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

    @Suppress("DeferredResultUnused")
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Page>) {
        uiScope.launch {
            async(network) {
                try {
                    val latestPage = service.latestPage().await()
                    val totalPages = latestPage.num

                    Log.i(TAG, "load latest page successfully")
                    callback.onResult(listOf(latestPage), 0, totalPages)

                    refreshListener.onRefresh()

                } catch (e: Throwable) {
                    Log.i(TAG, "fail loading latest page with reason ${e.message}")

                    refreshListener.onError(e)
                }

            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Page>) {
        uiScope.launch {
            async(network) {
                val num = params.key - 1
                try {
                    val page = service.at(num).await()
                    callback.onResult(listOf(page))

                    Log.i(TAG, "load page $num successfully")
                } catch (e: Throwable) {
                    Log.e(TAG, "fail loading page $num with reason ${e.message}")
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Page>) = Unit

    override fun getKey(item: Page): Int = item.num

}

