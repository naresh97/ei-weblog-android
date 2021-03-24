package com.nareshkumarrao.eiweblog.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nareshkumarrao.eiweblog.R
import com.nareshkumarrao.eiweblog.Utilities

class SectionsFragment : Fragment() {

    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_sections, container, false)

        this.swipeRefreshLayout = rootView.findViewById(R.id.sectionsSwipeRefresh)
        this.swipeRefreshLayout?.setOnRefreshListener {
            Utilities.fetchWeblogXML(this.context, ::updateView)
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.sectionsRecylerView).apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ItemArticleAdapter(listOf())
        }
        Utilities.weblogList(this.context, ::updateView)
    }

    companion object {
        private const val ARG_SECTION_NAME = "section_title"

        @JvmStatic
        fun newInstance(title: String): SectionsFragment {
            return SectionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SECTION_NAME, title)
                }
            }
        }
    }

    private fun updateView(get_articles: List<Article>){
        this.swipeRefreshLayout?.isRefreshing=false
        val articles: MutableList<Article> = mutableListOf()
        val title = arguments?.getString(ARG_SECTION_NAME) ?: return
        for (article in get_articles){
            if(article.category == title){
                articles.add(article)
            }
        }
        articles.sortByDescending { it.date }

        view?.findViewById<RecyclerView>(R.id.sectionsRecylerView)?.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ItemArticleAdapter(articles)
        }
    }
}