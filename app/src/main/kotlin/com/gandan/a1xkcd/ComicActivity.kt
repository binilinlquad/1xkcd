package com.gandan.a1xkcd

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import coil.Coil
import coil.ImageLoaderBuilder
import com.gandan.a1xkcd.comic.ui.MainView
import com.gandan.a1xkcd.comic.viewModel.MainViewModel
import com.gandan.a1xkcd.service.XkcdService
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import okhttp3.OkHttpClient
import javax.inject.Inject

@AndroidEntryPoint
class ComicActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    @Inject
    lateinit var okHttp3Client: OkHttpClient

    @Inject
    lateinit var service: XkcdService

    private lateinit var mainViewModel: MainViewModel
    private lateinit var mainView: MainView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeCoil()
        setContentView(R.layout.activity_comics)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainView = MainView(this, this.findViewById(android.R.id.content), service)
        mainView.bind(mainViewModel)

    }

    private fun initializeCoil() {
        Coil.setDefaultImageLoader(
            ImageLoaderBuilder(this)
                .okHttpClient(okHttp3Client)
                .build()
        )
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
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_refresh -> {
                mainView.resetPagesAndRefresh()
                true
            }
            R.id.menu_goto -> {
                mainView.showPages()
                true
            }
            R.id.menu_open_source_licenses -> {
                startActivity(Intent(this, OssLicensesMenuActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
