package com.nareshkumarrao.eiweblog.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nareshkumarrao.eiweblog.R

class SectionsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sections, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.sectionsRecylerView).apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ItemArticleAdapter(listOf())
        }

    }
    fun updateView(get_articles: List<Article>){
        view?.findViewById<RecyclerView>(R.id.sectionsRecylerView)?.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ItemArticleAdapter(get_articles)
        }
    }
}