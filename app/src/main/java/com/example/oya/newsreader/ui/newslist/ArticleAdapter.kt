package com.example.oya.newsreader.ui.newslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.canlioya.core.model.NewsArticle
import com.example.oya.newsreader.R
import com.example.oya.newsreader.common.bindImage
import com.example.oya.newsreader.common.fromHtml
import com.example.oya.newsreader.common.splitDateAndTime
import com.example.oya.newsreader.databinding.ItemArticleBinding

class ArticleAdapter(private val listener: ListItemClickListener) : ListAdapter<NewsArticle, ArticleAdapter.ViewHolder>(
    NewsDiffCallback()
){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentArticle = getItem(position)
        holder.bind(currentArticle)
        holder.binding.articleItemRoot.setOnClickListener {listener.onListItemClick(currentArticle) }
        holder.binding.share.setOnClickListener { listener.onShareClick(currentArticle.webUrl) }
        holder.binding.bookmark.setOnClickListener { listener.onBookmarkClick(currentArticle) }
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
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
            return oldItem == newItem
        }
    }
}

interface ListItemClickListener {
    fun onListItemClick(article : NewsArticle)
    fun onBookmarkClick(article : NewsArticle)
    fun onShareClick(url : String)
}