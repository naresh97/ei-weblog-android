package com.nareshkumarrao.eiweblog

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.android.volley.VolleyLog
import com.google.android.material.tabs.TabLayout
import com.nareshkumarrao.eiweblog.ui.main.Article
import com.nareshkumarrao.eiweblog.ui.main.SectionsPagerAdapter
import okhttp3.OkHttpClient


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        val myToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(myToolbar)

        VolleyLog.DEBUG = true

        /*val queue = Volley.newRequestQueue(this)
        val url = "https://www.reddit.com"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                Log.d("XMLLIST", "got response!")
                Log.d("XMLLIST", "$response")
            },
            { error -> Log.e("XMLLIST", error.toString()) })
        stringRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue.add(stringRequest)*/

        val thread = Thread {
            try {
                Log.d("XMLLIST", "Starting network request thread.")
                val client = OkHttpClient()
                val request = okhttp3.Request.Builder().url("https://google.com/").build()
                val response = client.newCall(request).execute()
                Log.d("XMLLIST", response.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        thread.start()

    }

    fun logArticles(articles: List<Article>){
        Log.i("XMLLIST", articles.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Utilities.weblogXML(baseContext, ::logArticles)
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }
}