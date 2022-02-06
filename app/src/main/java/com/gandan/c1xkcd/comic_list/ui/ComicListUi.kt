package com.gandan.c1xkcd.comic_list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.gandan.c1xkcd.entity.Strip
import com.gandan.c1xkcd.ui.common.InfiniteHorizontalProgressAnimation
import com.gandan.c1xkcd.ui.common.Screen
import kotlinx.coroutines.flow.Flow

@Composable
internal fun ComicListUi(error: Flow<Throwable?>, lazyComics: Flow<PagingData<Strip>>) {
    val errorState = error.collectAsState(initial = null)
    val lazyComicsState = lazyComics.collectAsLazyPagingItems()

    Screen(errorState) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(
                lazyComicsState,
                key = { it.num },
                itemContent = { ComicCard(it) }
            )

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
