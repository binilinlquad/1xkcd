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
import com.gandan.a1xkcd.comic.viewModel.MainState
import com.gandan.a1xkcd.comic.viewModel.MainViewModel
import com.gandan.a1xkcd.service.XkcdService
import com.gandan.a1xkcd.ui.GoToButtonHandler
import com.gandan.a1xkcd.ui.PageGoToButtonHandler
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_comics.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import javax.inject.Inject

class ComicActivity : DaggerAppCompatActivity(), CoroutineScope by MainScope() {
    @Inject
    lateinit var service: XkcdService

    private lateinit var goToButtonHandler: GoToButtonHandler

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comics)
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        comics_refresher.setOnRefreshListener { resetPagesAndRefresh() }
        manual_refresh.setOnClickListener { resetPagesAndRefresh() }

        resetPagesAndRefresh()
    }

    private fun showEmptyPage() {
        comics_refresher.isRefreshing = false
        manual_refresh.visibility = View.VISIBLE
        comics.visibility = View.GONE
    }

    private fun showComicPage() {
        comics_refresher.isRefreshing = false
        manual_refresh.visibility = View.GONE
        comics.visibility = View.VISIBLE
    }

    private fun showRefresh() {
        comics_refresher.isRefreshing = true
    }

    private fun showError(t: Throwable) {
        Toast.makeText(this, t.message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menus, menu)
        return true
    }

    private var gotoMenu: MenuItem? = null
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        gotoMenu = menu?.findItem(R.id.menu_goto)
        gotoMenu?.run {
            mainViewModel.gotoIsEnabled.observe(this@ComicActivity, Observer { isEnabled = it })
        }
        return super.onPrepareOptionsMenu(menu)
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

    private fun render(state: MainState) {
        when (state) {
            is MainState.Empty -> {
                showEmptyPage()
            }
            is MainState.ShowComic -> {
                showComicPage()
                setupGotoButtonHandler(state.totalPages)
            }
            is MainState.Error -> {
                showError(state.error)
            }
            is MainState.Refresh -> {
                showRefresh()
            }
        }
    }

    private fun setupGotoButtonHandler(totalPages: Int) {
        goToButtonHandler = PageGoToButtonHandler(this, totalPages, comics::scrollToPosition)
    }

    private fun resetPagesAndRefresh() {
        mainViewModel.state.observe(this, Observer { event -> render(event) })
        mainViewModel.bind(service)

        comics.layoutManager = LinearLayoutManager(this)
        val pagedPageAdapter = ComicPageAdapter()
        comics.swapAdapter(pagedPageAdapter, false)

        // still looking how to put it nicely with viewmodel
        mainViewModel.pages.observe(this, Observer { pagedList ->
            pagedPageAdapter.submitList(pagedList)
        })
    }

}
