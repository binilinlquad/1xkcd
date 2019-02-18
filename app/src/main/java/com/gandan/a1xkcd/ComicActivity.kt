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
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_comics.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import javax.inject.Inject

class ComicActivity : DaggerAppCompatActivity() {
    private val job = Job() + Dispatchers.Main
    @Inject
    lateinit var service: XkcdService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comics)

        val uiScope = CoroutineScope(job)

        val pageSourceFactory = PageDataSourceFactory(service, uiScope, this)

        val livePages: LiveData<PagedList<Page>> = LivePagedListBuilder(pageSourceFactory, 1)
                .build()
        val pagedPageAdapter = ComicPageAdapter()
        comics.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        comics.adapter = pagedPageAdapter

        livePages.observe(this, Observer { pagedList -> pagedPageAdapter.submitList(pagedList) })
    }

    override fun onDestroy() {
        job.cancelChildren()
        super.onDestroy()
    }
}
