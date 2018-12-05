package com.gandan.a1xkcd

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gandan.a1xkcd.service.Strip
import com.gandan.a1xkcd.service.createXkcdClient
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_latest_strip.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LatestStripActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_strip)
        val viewModel: MainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        viewModel.title.observe(this, Observer { title ->
            comic_title.text = title
            comic_title.contentDescription = "Title is $title"
        })

        viewModel.img.observe(this, Observer { uri ->
            Picasso.get().load(uri).into(comic_strip)
        })

        viewModel.alt.observe(this, Observer { alt ->
            comic_alt.text = alt
            // i think put alternative text as comic strip content description is more appropriate
            // rather than put it in comic_alt content description
            comic_strip.contentDescription = alt
        })

        comic_strip.setOnClickListener { v ->
            if (comic_strip.animation?.hasStarted() == true && comic_strip.animation?.hasEnded() == false) {
                comic_strip.postDelayed({ toggleComitStripAndAltText() }, 500)
            } else {
                toggleComitStripAndAltText()
            }
        }

        // basic http client
        val xkcdClient = createXkcdClient("https://xkcd.com/")

        // get latest strip
        val uiScope = CoroutineScope(Dispatchers.Main)
        uiScope.launch {
            val strip = xkcdClient.latestStrip().await()
            updateMainViewModel(viewModel, strip)
        }
    }

    private suspend fun Call<Strip>.await(): Strip {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<Strip> {
                override fun onFailure(call: Call<Strip>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

                override fun onResponse(call: Call<Strip>, response: Response<Strip>) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    } else {
                        continuation.resumeWithException(HttpException(response))
                    }
                }

            })
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
            .setDuration(100)
                .withEndAction {
                    comic_alt.visibility = View.INVISIBLE
                }

        val comicStripRevealAnimation = comic_strip.animate()
                .alpha(1f)
            .setDuration(350)

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
