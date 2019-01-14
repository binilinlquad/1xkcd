package com.gandan.a1xkcd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.gandan.a1xkcd.comic.ui.ComicPageAdapter
import com.gandan.a1xkcd.comic.ui.PageDataSourceFactory
import com.gandan.a1xkcd.service.Page
import com.gandan.a1xkcd.service.createXkcdClient
import kotlinx.android.synthetic.main.activity_comics.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ComicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comics)

        // basic http client
        val xkcdClient = createXkcdClient("https://xkcd.com/")
        val uiScope = CoroutineScope(Dispatchers.Main)

        val pageSourceFactory = PageDataSourceFactory(xkcdClient, uiScope, this)

        val livePages: LiveData<PagedList<Page>> = LivePagedListBuilder(pageSourceFactory, 1)
                .build()
        val pagedPageAdapter = ComicPageAdapter()
        comics.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        comics.adapter = pagedPageAdapter

        livePages.observe(this, Observer { pagedList -> pagedPageAdapter.submitList(pagedList) })
    }
}
