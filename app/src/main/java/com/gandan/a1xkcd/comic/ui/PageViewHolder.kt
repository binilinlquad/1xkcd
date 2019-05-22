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

class PageViewHolder(root: View, private val imageLoader: Picasso) : RecyclerView.ViewHolder(root) {
    private val comicTitle: TextView = root.findViewById(R.id.comic_title)
    private val comicPage: ImageView = root.findViewById(R.id.comic_page)
    private val comicAlt: TextView = root.findViewById(R.id.comic_alt)
    private val comicLoading: ProgressBar = root.findViewById(R.id.comic_loading)
    private val comicRetry: TextView = root.findViewById(R.id.comic_retry)

    private val titlePlaceHolder: String by lazy(LazyThreadSafetyMode.NONE) { root.context.getString(R.string.getting_comic_title_placeholder) }
    private var animator: PageAnimator? = null
    private var page: Page? = null

    fun bind(page: Page?) {
        this.page = page

        if (this.page == null) {
            showPlaceholder()
            return
        }

        page?.run {
            comicTitle.apply {
                text = page.title
                contentDescription = "Title is ${page.title}"
            }

            comicPage.apply {
                contentDescription = page.alt

                setOnClickListener {
                    toggleComitPageAndAltText()
                }

                loadComicImage(this@run)
            }

            comicAlt.text = page.alt

            comicRetry.setOnClickListener {
                comicLoading.visibility = View.VISIBLE
                loadComicImage(this@run)
            }
        }
    }


    private fun showPlaceholder() {
        comicTitle.apply {
            text = titlePlaceHolder
            contentDescription = titlePlaceHolder
        }
        comicPage.apply {
            clearAnimation()
            setImageBitmap(null)
            contentDescription = titlePlaceHolder
            setOnClickListener(null)
        }
        comicAlt.text = titlePlaceHolder

    }

    private fun toggleComitPageAndAltText() {
        if (animator == null) {
            animator = PageAnimator(comicPage, comicAlt)
        }

        if (comicAlt.visibility != View.VISIBLE) {
            animator!!.showAltText()
        } else {
            animator!!.showComic()
        }

    }

    private fun loadComicImage(page: Page) {
        imageLoader.load(page.img)
            .showProgressBar()
            .intoAndHideProgressBar(
                comicPage
            )
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
            comicRetry.visibility = View.VISIBLE
        }

        override fun onSuccess() {
            comicLoading.visibility = View.GONE
            comicRetry.visibility = View.GONE
        }
    }
}