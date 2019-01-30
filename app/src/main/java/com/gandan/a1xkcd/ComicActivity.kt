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
import com.gandan.a1xkcd.service.XkcdService
import com.gandan.a1xkcd.service.createXkcdService
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_comics.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ComicActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var service: XkcdService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comics)

        // basic http client
//        val service = createXkcdService("https://xkcd.com/")
        val uiScope = CoroutineScope(Dispatchers.Main)

        val pageSourceFactory = PageDataSourceFactory(service, uiScope, this)

        val livePages: LiveData<PagedList<Page>> = LivePagedListBuilder(pageSourceFactory, 1)
                .build()
        val pagedPageAdapter = ComicPageAdapter()
        comics.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        comics.adapter = pagedPageAdapter

        livePages.observe(this, Observer { pagedList -> pagedPageAdapter.submitList(pagedList) })
    }
}
