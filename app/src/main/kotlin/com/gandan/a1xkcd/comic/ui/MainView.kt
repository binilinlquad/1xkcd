package com.gandan.a1xkcd.comic.ui

import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.gandan.a1xkcd.R
import com.gandan.a1xkcd.comic.model.MainState
import com.gandan.a1xkcd.comic.viewModel.MainViewModel
import com.gandan.a1xkcd.service.XkcdService
import com.gandan.a1xkcd.ui.GoToButtonHandler
import com.gandan.a1xkcd.ui.PageGoToButtonHandler

@Suppress("PrivatePropertyName")
class MainView(
    private val owner: LifecycleOwner,
    private val root: View,
    private val service: XkcdService
) {

    private lateinit var comics_refresher: SwipeRefreshLayout
    private lateinit var  manual_refresh: TextView
    private lateinit var  comics: RecyclerView
    private lateinit var mainViewModel: MainViewModel
    private lateinit var goToButtonHandler: GoToButtonHandler

    var gotoMenu: MenuItem? = null
    set(value) {
        field = value
        if (field != null) {
            mainViewModel.state.value?.let { render(it) }
        }
    }

    init {
        root.post {
            comics_refresher  = root.findViewById(R.id.comics_refresher)
            manual_refresh = root.findViewById(R.id.manual_refresh)
            comics = root.findViewById(R.id.comics)
        }
    }

    fun bind(viewModel: MainViewModel) {
        mainViewModel = viewModel
        root.post {
            comics_refresher.setOnRefreshListener { resetPagesAndRefresh() }
            manual_refresh.setOnClickListener { resetPagesAndRefresh() }

            resetPagesAndRefresh()
        }
    }

    private fun render(state: MainState) {
        root.post {
            when (state) {
                is MainState.Empty -> {
                    renderEmpty()
                }
                is MainState.ShowComic -> {
                    renderPages(state)
                }
                is MainState.Error -> {
                    renderError(state)
                }
                is MainState.Refresh -> {
                    renderRefresh()
                }
            }
        }
    }

    private fun renderEmpty() {
        showEmptyPlaceholder()
        disableGoToButton()
    }

    private fun renderPages(state: MainState.ShowComic) {
        showComicPages()
        enableGoToButton(state)
    }

    private fun renderError(state: MainState.Error) {
        showError(state)
        disableGoToButton()
    }

    private fun renderRefresh() {
        comics_refresher.isRefreshing = true
    }

    fun resetPagesAndRefresh() {
        mainViewModel.state.observe(owner, Observer { event -> render(event) })
        mainViewModel.bind(service)

        comics.layoutManager = LinearLayoutManager(root.context)
        val pagedPageAdapter = ComicPageAdapter()
        comics.swapAdapter(pagedPageAdapter, false)

        // still looking how to put it nicely with viewmodel
        mainViewModel.pages.observe(owner, Observer { pagedList ->
            pagedPageAdapter.submitList(pagedList)
        })
    }

    private fun showEmptyPlaceholder() {
        comics_refresher.isRefreshing = false
        manual_refresh.visibility = View.VISIBLE
        comics.visibility = View.GONE
    }

    private fun disableGoToButton() {
        gotoMenu?.isEnabled = false
    }

    private fun enableGoToButton(state: MainState.ShowComic) {
        goToButtonHandler = PageGoToButtonHandler(root.context, state.totalPages, comics::scrollToPosition)
        gotoMenu?.isEnabled = true
    }

    private fun showComicPages() {
        comics_refresher.isRefreshing = false
        manual_refresh.visibility = View.GONE
        comics.visibility = View.VISIBLE
    }

    private fun showError(state: MainState.Error) {
        Toast.makeText(root.context, state.error.message, Toast.LENGTH_LONG).show()
    }

    fun showPages() {
        goToButtonHandler.onClick()
    }

}