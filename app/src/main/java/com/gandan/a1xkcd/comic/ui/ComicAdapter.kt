package com.gandan.a1xkcd.comic.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gandan.a1xkcd.R
import com.gandan.a1xkcd.service.Page
import com.squareup.picasso.Picasso
import kotlin.LazyThreadSafetyMode.NONE

class ComicPageAdapter(private val imageLoader: Picasso) : PagedListAdapter<Page, StripViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StripViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false)
        return StripViewHolder(itemView, imageLoader)
    }

    override fun onBindViewHolder(holder: StripViewHolder, position: Int) {
        val page: Page? = getItem(position)
        if (page != null) {
            holder.bind(page)
        } else {
            holder.showPlaceholder()
        }
    }

    companion object {

        private val diffUtil = object : DiffUtil.ItemCallback<Page>() {
            override fun areItemsTheSame(oldItem: Page, newItem: Page): Boolean {
                return oldItem.num == newItem.num
            }

            override fun areContentsTheSame(oldItem: Page, newItem: Page): Boolean {
                return oldItem == newItem
            }
        }


    }
}

class StripViewHolder(root: View, private val imageLoader: Picasso) : RecyclerView.ViewHolder(root) {
    private val comicTitle: TextView = root.findViewById(R.id.comic_title)
    private val comicStrip: ImageView = root.findViewById(R.id.comic_strip)
    private val comicAlt: TextView = root.findViewById(R.id.comic_alt)

    private val loadingText: String by lazy(NONE) { root.context.getString(R.string.loading) }
    private var animator: PageAnimator? = null

    fun bind(page: Page) {
        comicTitle.apply {
            text = page.title
            contentDescription = "Title is ${page.title}"
        }
        comicStrip.apply {
            imageLoader.load(page.img).into(comicStrip)
            contentDescription = page.alt
            setOnClickListener {
                toggleComitStripAndAltText()
            }
        }
        comicAlt.text = page.alt
    }


    fun showPlaceholder() {
        comicTitle.apply {
            text = loadingText
            contentDescription = loadingText
        }
        comicStrip.apply {
            clearAnimation()
            setImageBitmap(null)
            contentDescription = loadingText
            setOnClickListener(null)
        }
        comicAlt.text = loadingText

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
}