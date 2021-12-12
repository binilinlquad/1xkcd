package com.gandan.c1xkcd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
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

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get()
        setContent {
            val loading: Flow<Boolean> = viewModel.loading
            val errorFlow: Flow<Throwable?> = viewModel.error
            val lazyComics = viewModel.comics.collectAsLazyPagingItems()

            C1XkcdTheme {
                Screen(errorFlow, loading) {
                    /*
                       we are not using reverseLayout in LazyColumn because it will start draw from from bottom to top
                       i.e
                      | Title   |
                      | Comic   |
                      | Alt Text|

                      becomes
                      | Alt Text|
                      | Comic   |
                      | Title   |
                     */

                    LazyColumn {
                        items(lazyComics, key = { it.num }, itemContent = {
                            it?.let {
                                Surface(
                                    modifier = Modifier.wrapContentWidth(),
                                    shape = RoundedCornerShape(2.dp),
                                    elevation = 2.dp
                                ) {
                                    Column(
                                        modifier = Modifier.padding(
                                            horizontal = 4.dp,
                                            vertical = 8.dp
                                        )
                                    ) {
                                        Title(it.title)
                                        Spacer(modifier = Modifier.size(4.dp))
                                        ComicImage(it.img, it.alt)
                                        Spacer(modifier = Modifier.size(2.dp))
                                        AltText(it.alt)
                                    }
                                }

                                Spacer(modifier = Modifier.size(8.dp))
                            }
                        })
                    }

                    lazyComics.apply {
                        when {
                            loadState.refresh is LoadState.Loading -> {
                                InfiniteCircularProgressAnimation()
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

@ExperimentalSerializationApi
class MainViewModel : ViewModel() {
    private val _error: MutableStateFlow<Throwable?> = MutableStateFlow(null)
    val error: Flow<Throwable?> = _error

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading: Flow<Boolean> = _loading

    val comics: Flow<PagingData<Strip>> = Pager(PagingConfig(1)) {
        ComicSource()
    }.flow
}
