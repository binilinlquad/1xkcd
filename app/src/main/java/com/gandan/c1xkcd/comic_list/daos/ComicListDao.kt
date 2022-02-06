package com.gandan.c1xkcd.comic_list.daos

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gandan.c1xkcd.RUNTIME
import com.gandan.c1xkcd.entity.Strip
import com.gandan.c1xkcd.usecase.latestStrip
import com.gandan.c1xkcd.usecase.stripAt

class ComicPagedList : PagingSource<Int, Strip>() {
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
