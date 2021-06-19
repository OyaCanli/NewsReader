package com.example.oya.newsreader.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.canlioya.core.model.NewsArticle
import com.example.oya.newsreader.common.bindImage
import com.example.oya.newsreader.databinding.ItemArticleBinding

class ArticleAdapter(private val listener: ListItemClickListener) : ListAdapter<NewsArticle, ArticleAdapter.ViewHolder>(NewsDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentArticle = getItem(position)
        holder.bind(currentArticle)
        holder.binding.root.setOnClickListener { view -> listener.onListItemClick(view, currentArticle) }
    }

    class ViewHolder private constructor(val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(currentArticle: NewsArticle){
            binding.title.text = currentArticle.title
            binding.trail.text = currentArticle.articleTrail
            binding.author.text = currentArticle.author
            binding.date.text = splitDateAndTime(currentArticle.date)
            binding.thumbnail.bindImage(currentArticle.thumbnailUrl)
            binding.section.text = currentArticle.section
        }

        private fun splitDateAndTime(dateTime : String) : String{
            val parts = dateTime.split("T")
            return "${parts[0]}\n${parts[1]}"
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
    fun onListItemClick(view: View?, article : NewsArticle)
}