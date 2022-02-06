package com.gandan.c1xkcd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.paging.*
import com.gandan.c1xkcd.entity.Strip
import com.gandan.c1xkcd.ui.comic_list.ComicListUi
import com.gandan.c1xkcd.usecase.latestStrip
import com.gandan.c1xkcd.usecase.stripAt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val error: Flow<Throwable?> = MutableStateFlow(null)
        val lazyComics: Flow<PagingData<Strip>> =
            Pager(PagingConfig(pageSize = 1, prefetchDistance = 1)) {
                ComicSource()
            }.flow

        setContent {
            ComicListUi(error, lazyComics)
        }
    }
}

class ComicSource : PagingSource<Int, Strip>() {
    override fun getRefreshKey(state: PagingState<Int, Strip>): Int? {
        // ignore refresh key for now
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Strip> {
        // load each page one by one for now
        return try {
            val page = params.key // initial page value is unknown until we got first response
            val comic = if (page == null) RUNTIME.latestStrip() else RUNTIME.stripAt(page)
            val curPage = comic.num

            // swap prevKey and nextKey to mimic reverseLayout.
            LoadResult.Page(
                data = listOf(comic),
                prevKey = curPage + 1,
                nextKey = if (curPage == 1) null else curPage - 1
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}
