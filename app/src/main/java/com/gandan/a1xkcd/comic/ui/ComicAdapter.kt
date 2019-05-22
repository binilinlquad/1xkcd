package com.gandan.a1xkcd.comic.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.gandan.a1xkcd.R
import com.gandan.a1xkcd.service.Page
import com.squareup.picasso.Picasso

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

