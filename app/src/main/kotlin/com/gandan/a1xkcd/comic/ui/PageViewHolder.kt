package com.gandan.a1xkcd.comic.ui

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.gandan.a1xkcd.R
import com.gandan.a1xkcd.service.Page

class PageViewHolder(root: View) : RecyclerView.ViewHolder(root) {
    private val comicTitle: TextView = root.findViewById(R.id.comic_title)
    private val comicPage: ImageView = root.findViewById(R.id.comic_page)
    private val comicAlt: TextView = root.findViewById(R.id.comic_alt)
    private val comicLoading: ProgressBar = root.findViewById(R.id.comic_loading)
    private val comicRetry: TextView = root.findViewById(R.id.comic_retry)
    private val titlePlaceHolder: String by lazy(LazyThreadSafetyMode.NONE) {
        root.context.getString(
            R.string.getting_comic_title_placeholder
        )
    }
    private var animator: PageAnimator? = null
    private var page: Page? = null

    fun bind(page: Page?) {
        this.page = page

        if (page == null) {
            showPlaceholder()
            return
        }

        page.run {
            comicTitle.apply {
                text = resources.getString(R.string.comic_title, page.num, page.title)
                contentDescription =
                    resources.getString(R.string.comic_title_description, page.num, page.title)
            }

            comicPage.apply {
                contentDescription = page.alt
                setOnClickListener { toggleComitPageAndAltText() }
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
        comicPage.load(page.img) {
            this.listener(
                onStart = { comicLoading.visibility = View.VISIBLE },
                onSuccess = { _, _ ->
                    comicLoading.visibility = View.GONE
                    comicRetry.visibility = View.GONE
                },
                onError = { _, _ ->
                    comicLoading.visibility = View.GONE
                    comicRetry.visibility = View.VISIBLE
                }
            )
        }
    }
}