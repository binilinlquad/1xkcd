package com.gandan.c1xkcd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.*
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.gandan.c1xkcd.entity.Strip
import com.gandan.c1xkcd.ui.screen.*
import com.gandan.c1xkcd.ui.theme.C1XkcdTheme
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
            val errorState = error.collectAsState(initial = null)
            val lazyComicsState = lazyComics.collectAsLazyPagingItems()

            C1XkcdTheme {
                Screen(errorState) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(
                            lazyComicsState,
                            key = { it.num },
                            itemContent = { comic ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = 8.dp
                                ) {
                                    Column(
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 8.dp
                                        ),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        comic?.let {
                                            Title(comic.title)
                                            Spacer(modifier = Modifier.size(4.dp))
                                            ComicImage(comic.img, comic.alt)
                                            Spacer(modifier = Modifier.size(8.dp))
                                            AltText(comic.alt)
                                        } ?: InfiniteHorizontalProgressAnimation()
                                    }
                                }
                            })

                        lazyComicsState.apply {
                            if (loadState.append is LoadState.Loading ||
                                loadState.refresh is LoadState.Loading
                            ) {
                                item { InfiniteHorizontalProgressAnimation() }
                            }
                        }
                    }
                }
            }
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

