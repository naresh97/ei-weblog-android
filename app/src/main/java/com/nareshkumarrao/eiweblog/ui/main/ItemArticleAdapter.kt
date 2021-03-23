package com.nareshkumarrao.eiweblog.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nareshkumarrao.eiweblog.R

data class Article(val title: String, val content: String, val date: String, val author: String)

class ItemArticleAdapter(private val articles: List<Article>) : RecyclerView.Adapter<ItemArticleAdapter.ViewHolder>() {
    inner class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_article, parent, false)) {
        private var title: TextView? = null
        private var content: TextView? = null

        init {
            title = itemView.findViewById(R.id.titleText)
            content = itemView.findViewById(R.id.contentText)
        }

        fun bind(article: Article) {
            title?.text = article.title
            content?.text = article.content
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemArticleAdapter.ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article: Article = articles[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int {
        return articles.size
    }
}