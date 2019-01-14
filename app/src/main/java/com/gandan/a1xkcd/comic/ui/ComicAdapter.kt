package com.gandan.a1xkcd.comic.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gandan.a1xkcd.R
import com.gandan.a1xkcd.service.Page
import com.squareup.picasso.Picasso

class ComicPageAdapter : PagedListAdapter<Page, StripViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StripViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false)
        return StripViewHolder(itemView)
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

class StripViewHolder(root: View) : RecyclerView.ViewHolder(root) {
    private val comicTitle: TextView = root.findViewById(R.id.comic_title)
    private val comicStrip: ImageView = root.findViewById(R.id.comic_strip)
    private val comicAlt: TextView = root.findViewById(R.id.comic_alt)

    fun bind(page: Page) {
        comicTitle.apply {
            text = page.title
            contentDescription = "Title is ${page.title}"
        }
        comicStrip.apply {
            Picasso.get().load(page.img).into(comicStrip)
            contentDescription = page.alt
            setOnClickListener {
                if (animation?.hasStarted() == true && animation?.hasEnded() == false) {
                    postDelayed({ toggleComitStripAndAltText() }, 500)
                } else {
                    toggleComitStripAndAltText()
                }
            }
        }
        comicAlt.text = page.alt
    }


    fun showPlaceholder() {
        comicTitle.apply {
            text = "Loading"
            contentDescription = "Loading"
        }
        comicStrip.apply {
            clearAnimation()
            setImageBitmap(null)
            contentDescription = "Loading"
            setOnClickListener(null)
        }
        comicAlt.text = "Loading"

    }

    private fun toggleComitStripAndAltText() {
        if (comicAlt.visibility != View.VISIBLE) {
            showAltText()
        } else {
            showComicStrip()
        }

    }

    private fun showAltText() {
        val comicStripPaleAnimator = comicStrip.animate()
            .alpha(0.1f)
            .setDuration(500)

        val altTextRevealAnimator = comicAlt.animate()
            .alpha(1f)
            .setDuration(250)
            .setInterpolator(LinearInterpolator())
            .setStartDelay(150)
            .withStartAction {
                comicAlt.alpha = 0.0f
                comicAlt.visibility = View.VISIBLE
            }

        comicStripPaleAnimator.start()
        altTextRevealAnimator.start()
    }

    private fun showComicStrip() {
        val hideAltTextAnimation = comicAlt.animate()
            .alpha(0.1f)
            .setDuration(100)
            .withEndAction {
                comicAlt.visibility = View.INVISIBLE
            }

        val comicStripRevealAnimation = comicStrip.animate()
            .alpha(1f)
            .setDuration(350)

        hideAltTextAnimation.start()
        comicStripRevealAnimation.start()
    }

}