package com.gandan.c1xkcd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gandan.c1xkcd.comic_list.daos.ComicPagedList
import com.gandan.c1xkcd.comic_list.ui.ComicListUi
import com.gandan.c1xkcd.entity.Strip
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
                ComicPagedList()
            }.flow

        setContent {
            ComicListUi(error, lazyComics)
        }
    }
}
