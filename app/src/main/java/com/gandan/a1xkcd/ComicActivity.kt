package com.gandan.a1xkcd

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.gandan.a1xkcd.comic.ui.ComicPageAdapter
import com.gandan.a1xkcd.comic.ui.PageDataSourceFactory
import com.gandan.a1xkcd.service.Page
import com.gandan.a1xkcd.service.XkcdService
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_comics.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ComicActivity : DaggerAppCompatActivity(), CoroutineScope {

    @Inject
    lateinit var service: XkcdService

    @Inject
    lateinit var imageDownloader: Picasso

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private lateinit var pagedPageAdapter: ComicPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comics)

        pagedPageAdapter = ComicPageAdapter(imageDownloader)
        comics.layoutManager = LinearLayoutManager(this)
        comics.adapter = pagedPageAdapter

        comics_refresher.setOnRefreshListener { refreshComics() }
        manual_refresh.setOnClickListener { refreshComics() }

        refreshComics()
    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancelChildren()
    }

    private fun refreshComics() {
        val pageSourceFactory = PageDataSourceFactory(service, this, this)
        val livePages: LiveData<PagedList<Page>> = LivePagedListBuilder(pageSourceFactory, 1)
            .build()
        livePages.observe(this, Observer { pagedList ->
            pagedPageAdapter.submitList(pagedList)
            comics_refresher.isRefreshing = false
        })
    }
}
