package com.canli.oya.newsreader.ui.newslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.canli.oya.newsreader.R
import com.canli.oya.newsreader.common.bindImage
import com.canli.oya.newsreader.common.fromHtml
import com.canli.oya.newsreader.common.splitDateAndTime
import com.canli.oya.newsreader.databinding.ItemArticleBinding
import com.canlioya.core.model.NewsArticle
import timber.log.Timber

const val BOOKMARK_ITEM = 37913
const val REMOVE_BOOKMARK = 12796

class ArticleAdapter(private val listener: ListItemClickListener) : ListAdapter<NewsArticle, ArticleAdapter.ViewHolder>(
    NewsDiffCallback()
){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentArticle = getItem(position)
        holder.bind(currentArticle)
        holder.bindListener(currentArticle, listener, position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if(payloads.isNotEmpty()){
            when(payloads[0] as? Int) {
                BOOKMARK_ITEM -> holder.binding.bookmark.setImageResource(R.drawable.ic_bookmark_filled)
                REMOVE_BOOKMARK -> holder.binding.bookmark.setImageResource(R.drawable.ic_bookmark_outlined)
                else -> super.onBindViewHolder(holder, position, payloads)
            }
            val currentArticle = getItem(position)
            holder.bindListener(currentArticle, listener, position)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    class ViewHolder private constructor(val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(currentArticle: NewsArticle){
            binding.title.text = currentArticle.title
            binding.trail.text = fromHtml(currentArticle.articleTrail)
            binding.author.text = currentArticle.author
            binding.date.text = splitDateAndTime(currentArticle.date)
            binding.thumbnail.bindImage(currentArticle.thumbnailUrl)
            binding.section.text = currentArticle.section
            if(currentArticle.isBookmarked){
                binding.bookmark.setImageResource(R.drawable.ic_bookmark_filled)
            } else {
                binding.bookmark.setImageResource(R.drawable.ic_bookmark_outlined)
            }
        }

        fun bindListener(currentArticle: NewsArticle, listener: ListItemClickListener, position: Int){
            binding.articleItemRoot.setOnClickListener {listener.onListItemClick(currentArticle) }
            binding.share.setOnClickListener { listener.onShareClick(currentArticle.webUrl) }
            binding.bookmark.setOnClickListener { listener.onBookmarkClick(position, currentArticle) }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemArticleBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    private class NewsDiffCallback : DiffUtil.ItemCallback<NewsArticle>() {
        override fun areItemsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
            return oldItem.articleId == newItem.articleId
        }

        override fun areContentsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: NewsArticle, newItem: NewsArticle): Any? {
            Timber.d("getChangePayLoad is called.")
            return when {
                !oldItem.isBookmarked && newItem.isBookmarked -> BOOKMARK_ITEM
                oldItem.isBookmarked && !newItem.isBookmarked -> REMOVE_BOOKMARK
                else -> super.getChangePayload(oldItem, newItem)
            }
        }
    }
}

interface ListItemClickListener {
    fun onListItemClick(article : NewsArticle)
    fun onBookmarkClick(position : Int, article : NewsArticle)
    fun onShareClick(url : String)
}