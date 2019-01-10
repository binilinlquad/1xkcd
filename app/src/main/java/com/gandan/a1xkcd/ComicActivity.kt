package com.gandan.a1xkcd

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import await
import com.gandan.a1xkcd.comic.ui.ComicAdapter
import com.gandan.a1xkcd.service.createXkcdClient
import kotlinx.android.synthetic.main.activity_comics.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ComicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comics)

        val adapter = ComicAdapter()
        comics.layoutManager = LinearLayoutManager(this)
        comics.adapter = adapter

        // basic http client
        val xkcdClient = createXkcdClient("https://xkcd.com/")

        // get latest strip
        val uiScope = CoroutineScope(Dispatchers.Main)
        uiScope.launch {
            try {
                val page = xkcdClient.latestStrip().await()
                adapter.pages.add(page)
                adapter.notifyDataSetChanged()
            } catch (e: Throwable) {
                Toast.makeText(this@ComicActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
