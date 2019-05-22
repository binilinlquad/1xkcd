package com.gandan.a1xkcd.comic.ui

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gandan.a1xkcd.R
import com.gandan.a1xkcd.service.Page
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import java.lang.Exception

class StripViewHolder(root: View, private val imageLoader: Picasso) : RecyclerView.ViewHolder(root) {
    private val comicTitle: TextView = root.findViewById(R.id.comic_title)
    private val comicStrip: ImageView = root.findViewById(R.id.comic_strip)
    private val comicAlt: TextView = root.findViewById(R.id.comic_alt)
    private val comicLoading: ProgressBar = root.findViewById(R.id.comic_loading)

    private val titlePlaceHolder: String by lazy(LazyThreadSafetyMode.NONE) { root.context.getString(R.string.getting_comic_title_placeholder) }
    private var animator: PageAnimator? = null

    fun bind(page: Page) {
        comicTitle.apply {
            text = page.title
            contentDescription = "Title is ${page.title}"
        }
        comicStrip.apply {
            imageLoader.load(page.img)
                .showProgressBar()
                .intoAndHideProgressBar(
                    comicStrip
                )
            contentDescription = page.alt

            setOnClickListener {
                toggleComitStripAndAltText()
            }
        }
        comicAlt.text = page.alt
    }


    fun showPlaceholder() {
        comicTitle.apply {
            text = titlePlaceHolder
            contentDescription = titlePlaceHolder
        }
        comicStrip.apply {
            clearAnimation()
            setImageBitmap(null)
            contentDescription = titlePlaceHolder
            setOnClickListener(null)
        }
        comicAlt.text = titlePlaceHolder

    }

    private fun toggleComitStripAndAltText() {
        if (animator == null) {
            animator = PageAnimator(comicStrip, comicAlt)
        }

        if (comicAlt.visibility != View.VISIBLE) {
            animator!!.showAltText()
        } else {
            animator!!.showComic()
        }

    }

    private fun RequestCreator.showProgressBar(): RequestCreator {
        return this.also { comicLoading.visibility = View.VISIBLE }
    }

    private fun RequestCreator.intoAndHideProgressBar(target: ImageView) {
        this.into(target, HideProgressBar())
    }

    inner class HideProgressBar : Callback {
        override fun onError(e: Exception?) {
            comicLoading.visibility = View.GONE
            comicTitle.setText(R.string.cannot_load_comic_title_placeholder)
        }

        override fun onSuccess() {
            comicLoading.visibility = View.GONE
        }
    }
}