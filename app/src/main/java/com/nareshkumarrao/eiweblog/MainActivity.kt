package com.nareshkumarrao.eiweblog

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.google.android.material.tabs.TabLayout
import com.nareshkumarrao.eiweblog.ui.main.SectionsPagerAdapter
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        val myToolbar = findViewById<View>(R.id.about_toolbar) as Toolbar
        setSupportActionBar(myToolbar)

        Utilities.createNotificationChannel(this)

        val uploadWorkRequest: WorkRequest =
            PeriodicWorkRequestBuilder<UpdateWorker>(1, TimeUnit.HOURS)
                .build()
        WorkManager.getInstance(this).enqueue(uploadWorkRequest)



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    fun showNotificationSettings(item: MenuItem){
        val intent = Intent(this, NotificationSettingsActivity::class.java)
        startActivity(intent)
    }

    fun showAbout(item: MenuItem){
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }
}