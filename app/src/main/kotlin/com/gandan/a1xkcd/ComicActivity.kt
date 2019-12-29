package com.gandan.a1xkcd

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.gandan.a1xkcd.comic.ui.ComicPageAdapter
import com.gandan.a1xkcd.comic.viewModel.MainViewModel
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

class ComicActivity : DaggerAppCompatActivity(), CoroutineScope {
    @Inject
    lateinit var service: XkcdService

    private var goToButtonHandler: GoToButtonHandler = DisabledGoToButtonHandler(this)

    private var refreshListener: RefreshListener =
        RefreshListener.create({ showPages(it) }, { showEmptyPage(it) })

    private val job = Job()

    private lateinit var mainViewModel: MainViewModel

    override val coroutineContext: CoroutineContext
        get() = job + AppDispatchers.main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comics)
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        mainViewModel.comicIsEmpty.observe(this, Observer {
            manual_refresh.visibility = if (it) View.VISIBLE else View.GONE
            comics.visibility = if (!it) View.VISIBLE else View.GONE
        })

        mainViewModel.totalPages.observe(this, Observer { totalPages ->
            goToButtonHandler =
                if (totalPages == MainViewModel.TOTAL_PAGE_EMPTY)
                    DisabledGoToButtonHandler(this@ComicActivity)
                else PageGoToButtonHandler(
                    this@ComicActivity,
                    totalPages,
                    comics::scrollToPosition
                )
        })

        comics_refresher.setOnRefreshListener { resetPagesAndRefresh() }
        manual_refresh.setOnClickListener { resetPagesAndRefresh() }

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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
        mainViewModel.setPageProvider(service, this, refreshListener)

        comics.layoutManager = LinearLayoutManager(this)
        val pagedPageAdapter = ComicPageAdapter()
        comics.swapAdapter(pagedPageAdapter, false)

        mainViewModel.pages.observe(this, Observer { pagedList ->
            pagedPageAdapter.submitList(pagedList)
            comics_refresher.isRefreshing = false
            goToButtonHandler = DisabledGoToButtonHandler(this@ComicActivity)
        })
    }

    private fun showEmptyPage(error: Throwable) {
        launch(coroutineContext) {
            Toast.makeText(this@ComicActivity, error.message, Toast.LENGTH_LONG).show()

            mainViewModel.showEmptyPage()
        }
    }

    private fun showPages(totalPages: Int) {
        launch(coroutineContext) {
            mainViewModel.showComicPages(totalPages)
        }
    }

}
