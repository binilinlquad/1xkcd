package com.gandan.a1xkcd

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import coil.Coil
import coil.ImageLoaderBuilder
import com.gandan.a1xkcd.comic.model.MainState
import com.gandan.a1xkcd.comic.ui.ComicPageAdapter
import com.gandan.a1xkcd.comic.viewModel.MainViewModel
import com.gandan.a1xkcd.service.XkcdService
import com.gandan.a1xkcd.ui.GoToButtonHandler
import com.gandan.a1xkcd.ui.PageGoToButtonHandler
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_comics.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import okhttp3.OkHttpClient
import javax.inject.Inject

@AndroidEntryPoint
class ComicActivity :AppCompatActivity(), CoroutineScope by MainScope() {

    @Inject
    lateinit var okHttp3Client: OkHttpClient

    @Inject
    lateinit var service: XkcdService

    private lateinit var goToButtonHandler: GoToButtonHandler

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Coil.setDefaultImageLoader(
            ImageLoaderBuilder(this)
                .okHttpClient(okHttp3Client)
                .build()
        )

        setContentView(R.layout.activity_comics)
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        comics_refresher.setOnRefreshListener { resetPagesAndRefresh() }
        manual_refresh.setOnClickListener { resetPagesAndRefresh() }

        resetPagesAndRefresh()
    }

    private fun renderEmpty() {
        showEmptyPlaceholder()
        disableGoToButton()
    }

    private fun showEmptyPlaceholder() {
        comics_refresher.isRefreshing = false
        manual_refresh.visibility = View.VISIBLE
        comics.visibility = View.GONE
    }

    private fun renderPages(state: MainState.ShowComic) {
        showComicPages()
        enableGoToButton(state)
    }

    private fun enableGoToButton(state: MainState.ShowComic) {
        goToButtonHandler = PageGoToButtonHandler(this, state.totalPages, comics::scrollToPosition)
        gotoMenu?.isEnabled = true
    }

    private fun showComicPages() {
        comics_refresher.isRefreshing = false
        manual_refresh.visibility = View.GONE
        comics.visibility = View.VISIBLE
    }

    private fun renderRefresh() {
        comics_refresher.isRefreshing = true
    }

    private fun renderError(state: MainState.Error) {
        showError(state)
        disableGoToButton()
    }

    private fun showError(state: MainState.Error) {
        Toast.makeText(this, state.error.message, Toast.LENGTH_LONG).show()
    }

    private fun disableGoToButton() {
        gotoMenu?.isEnabled = false
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
            mainViewModel.state.value?.let { render(it) }
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
            R.id.menu_open_source_licenses -> {
                startActivity(Intent(this, OssLicensesMenuActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun render(state: MainState) {
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
