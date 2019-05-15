package com.gandan.a1xkcd.comic.ui

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import await
import com.gandan.a1xkcd.ComicActivity
import com.gandan.a1xkcd.service.Page
import com.gandan.a1xkcd.service.XkcdService
import kotlinx.android.synthetic.main.activity_comics.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

class PageDataSource(private val service: XkcdService,
                     private val uiScope: CoroutineScope,
                     private val activity: ComicActivity) : ItemKeyedDataSource<Int, Page>() {

    companion object {
        private val TAG = PageDataSource::class.java.simpleName
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Page>) {
        uiScope.launch {
            try {
                val latestPage = service.latestStrip().await()
                val totalPages = latestPage.num
                callback.onResult(listOf(latestPage), 0, totalPages)

                Log.i(TAG, "load latest page successfully")

                activity.manual_refresh.visibility = View.GONE
                activity.comics.visibility = View.VISIBLE

            } catch (e: Throwable) {
                Log.i(TAG, "fail loading latest page with reason ${e.message}")
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()

                activity.manual_refresh.visibility = View.VISIBLE
                activity.comics.visibility = View.GONE
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Page>) {
        uiScope.launch {
            val num = params.key - 1
            try {
                val page = service.at(num).await()
                callback.onResult(listOf(page))

                Log.i(TAG, "load page $num successfully")
            } catch (e: Throwable) {
                Log.e(TAG, "fail loading page $num with reason ${e.message}")
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Page>) = Unit

    override fun getKey(item: Page): Int = item.num

}

