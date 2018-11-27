package com.gandan.a1xkcd

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import com.gandan.a1xkcd.service.Strip
import com.gandan.a1xkcd.service.createXkcdClient
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel: MainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        // basic http client
        val xkcdClient = createXkcdClient("https://xkcd.com/")

        // get latest strip
        xkcdClient.latestStrip().enqueue(object : Callback<Strip> {
            override fun onFailure(call: Call<Strip>, t: Throwable) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Failed because $t", Toast.LENGTH_LONG).show()
                    comic_title.text = "Cannot get latest strip :("
                }
            }

            override fun onResponse(call: Call<Strip>, response: Response<Strip>) {
                if (response.isSuccessful.not()) {
                    throw IllegalStateException("Request failed with response ${response.code()}")
                }

                val strip = response.body()!!

                updateMainViewModel(viewModel, strip)
            }

        })

        viewModel.title.observe(this, Observer { title ->
            comic_title.text = title
        })

        viewModel.img.observe(this, Observer { uri ->
            Picasso.get().load(uri).into(comic_strip)
        })

        viewModel.alt.observe(this, Observer { alt ->
            comic_alt.text = alt
        })

        comic_strip.setOnClickListener { v ->
            if (comic_strip.animation?.hasStarted() == true && comic_strip.animation?.hasEnded() == false) {
                comic_strip.postDelayed({ toggleComitStripAndAltText() }, 500)
            } else {
                toggleComitStripAndAltText()
            }
        }
    }

    private fun toggleComitStripAndAltText() {
        if (comic_alt.visibility != View.VISIBLE) {
            showAltText()
        } else {
            showComicStrip()
        }

    }

    private fun showAltText() {
        val comicStripPaleAnimator = comic_strip.animate()
                .alpha(0.1f)
                .setDuration(500)

        val altTextRevealAnimator = comic_alt.animate()
                .alpha(1f)
                .setDuration(250)
                .setInterpolator(LinearInterpolator())
                .setStartDelay(150)
                .withStartAction {
                    comic_alt.alpha = 0.0f
                    comic_alt.visibility = View.VISIBLE
                }

        comicStripPaleAnimator.start()
        altTextRevealAnimator.start()
    }

    private fun showComicStrip() {
        val hideAltTextAnimation = comic_alt.animate()
                .alpha(0.1f)
                .setDuration(450)
                .setInterpolator(LinearInterpolator())
                .withEndAction {
                    comic_alt.visibility = View.INVISIBLE
                }

        val comicStripRevealAnimation = comic_strip.animate()
                .alpha(1f)
                .setDuration(500)

        hideAltTextAnimation.start()
        comicStripRevealAnimation.start()
    }

    private fun updateMainViewModel(viewModel: MainViewModel, strip: Strip) {
        viewModel.alt.postValue(strip.alt)
        viewModel.img.postValue(Uri.parse(strip.img))
        viewModel.day.postValue(strip.day)
        viewModel.link.postValue(strip.link)
        viewModel.news.postValue(strip.news)
        viewModel.num.postValue(strip.num)
        viewModel.safeTitle.postValue(strip.safeTitle)
        viewModel.title.postValue(strip.title)
        viewModel.transcript.postValue(strip.transcript)
        viewModel.year.postValue(strip.year)
    }

}
