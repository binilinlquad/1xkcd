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
import com.gandan.a1xkcd.experiment.ComicMain
import com.gandan.a1xkcd.service.Page
import com.gandan.a1xkcd.service.XkcdService
import com.gandan.a1xkcd.ui.DisabledGoToButtonHandler
import com.gandan.a1xkcd.ui.GoToButtonHandler
import com.gandan.a1xkcd.ui.PageGoToButtonHandler
import com.gandan.a1xkcd.util.AppDispatchers.main
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_comics.*
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ComicActivity : DaggerAppCompatActivity(),
    CoroutineScope,
    RefreshListener,
    ComicMain.View,
    ComicMain.Screen {

    @Inject
    lateinit var service: XkcdService

    @Inject
    lateinit var imageDownloader: Picasso

    private val job = Job()

    private var goToButtonHandler: GoToButtonHandler = DisabledGoToButtonHandler(this)

    private val mainApp = ComicMain()
    private lateinit var listener: ComicMain.Screen.ScreenEventListener

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comics)

        comics_refresher.setOnRefreshListener { resetPagesAndRefresh() }
        manual_refresh.setOnClickListener { resetPagesAndRefresh() }

        mainApp.bind(this, this)
    }

    override fun onResume() {
        super.onResume()
        listener.onEvent(ComicMain.Screen.Event.STARTED)
    }

    override fun onBind(listener: ComicMain.Screen.ScreenEventListener) {
        this.listener = listener
    }

    override fun showComics() {
        resetPagesAndRefresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancelChildren()
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
        val pageSourceFactory = PageDataSourceFactory(service, this, this)
        val livePages: LiveData<PagedList<Page>> = LivePagedListBuilder(pageSourceFactory, 1)
            .build()

        comics.layoutManager = LinearLayoutManager(this)
        val pagedPageAdapter = ComicPageAdapter(imageDownloader)
        comics.swapAdapter(pagedPageAdapter, false)

        livePages.observe(this, Observer { pagedList ->
            pagedPageAdapter.submitList(pagedList)
            comics_refresher.isRefreshing = false
            goToButtonHandler = DisabledGoToButtonHandler(this@ComicActivity)
        })
    }

    override fun onError(error: Throwable) {
        launch(main) {
            Toast.makeText(this@ComicActivity, error.message, Toast.LENGTH_LONG).show()

            manual_refresh.visibility = View.VISIBLE
            comics.visibility = View.GONE
            goToButtonHandler = DisabledGoToButtonHandler(this@ComicActivity)
        }
    }

    override fun onRefreshed(totalPages: Int) {
        launch(main) {
            manual_refresh.visibility = View.GONE
            comics.visibility = View.VISIBLE
            goToButtonHandler = PageGoToButtonHandler(
                this@ComicActivity,
                totalPages,
                comics::scrollToPosition
            )
        }
    }
}


interface RefreshListener {
    fun onError(error: Throwable)

    fun onRefreshed(totalPages: Int)
}
