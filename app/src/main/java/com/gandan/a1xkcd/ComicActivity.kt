package com.gandan.a1xkcd

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.gandan.a1xkcd.comic.ui.ComicPageAdapter
import com.gandan.a1xkcd.comic.ui.PageDataSourceFactory
import com.gandan.a1xkcd.service.Page
import com.gandan.a1xkcd.service.XkcdService
import com.gandan.a1xkcd.ui.DisabledGoToButtonHandler
import com.gandan.a1xkcd.ui.GoToButtonHandler
import com.gandan.a1xkcd.ui.PageGoToButtonHandler
import com.gandan.a1xkcd.ui.RefreshListener
import com.gandan.a1xkcd.util.AppDispatchers
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_comics.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ComicActivity : DaggerAppCompatActivity(),
    CoroutineScope {

    @Inject
    lateinit var service: XkcdService

    private var goToButtonHandler: GoToButtonHandler = DisabledGoToButtonHandler(this)

    private var refreshListener: RefreshListener =
        RefreshListener.create({ showPages(it) }, { showEmptyScreen(it) })

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + AppDispatchers.main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comics)

        comics_refresher.setOnRefreshListener { resetPagesAndRefresh() }
        manual_refresh.setOnClickListener { resetPagesAndRefresh() }
    }

    override fun onResume() {
        super.onResume()
        showComics()
    }

    fun showComics() {
        resetPagesAndRefresh()
    }

    override fun onDestroy() {
        job.cancelChildren()
        super.onDestroy()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menus, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_refresh -> {
                resetPagesAndRefresh()
                true
            }
            R.id.menu_goto -> {
                goToButtonHandler.onClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun resetPagesAndRefresh() {
        val pageSourceFactory = PageDataSourceFactory(service, this, refreshListener)
        val livePages: LiveData<PagedList<Page>> = LivePagedListBuilder(pageSourceFactory, 1)
            .build()

        comics.layoutManager = LinearLayoutManager(this)
        val pagedPageAdapter = ComicPageAdapter()
        comics.swapAdapter(pagedPageAdapter, false)

        livePages.observe(this, Observer { pagedList ->
            pagedPageAdapter.submitList(pagedList)
            comics_refresher.isRefreshing = false
            goToButtonHandler = DisabledGoToButtonHandler(this@ComicActivity)
        })
    }

    private fun showEmptyScreen(error: Throwable) {
        launch(coroutineContext) {
            Toast.makeText(this@ComicActivity, error.message, Toast.LENGTH_LONG).show()

            manual_refresh.visibility = View.VISIBLE
            comics.visibility = View.GONE
            goToButtonHandler = DisabledGoToButtonHandler(this@ComicActivity)
        }
    }

    private fun showPages(totalPages: Int) {
        launch(coroutineContext) {
            showComicPages()
            enableGotoButton(totalPages)
        }
    }

    private fun enableGotoButton(totalPages: Int) {
        goToButtonHandler = PageGoToButtonHandler(
            this@ComicActivity,
            totalPages,
            comics::scrollToPosition
        )
    }

    private fun showComicPages() {
        manual_refresh.visibility = View.GONE
        comics.visibility = View.VISIBLE
    }

}
