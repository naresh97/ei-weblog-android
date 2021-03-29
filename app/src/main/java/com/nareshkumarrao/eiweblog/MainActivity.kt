package com.nareshkumarrao.eiweblog

import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
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
        HISUtility.createNotificationChannel(this)

        val updateWeblogWorkerRequest: WorkRequest =
                PeriodicWorkRequestBuilder<UpdateWeblogWorker>(1, TimeUnit.HOURS)
                        .build()
        val updateGradesWorkerRequest: WorkRequest =
                PeriodicWorkRequestBuilder<UpdateGradesWorker>(3, TimeUnit.HOURS)
                        .build()

        WorkManager.getInstance(this).enqueue(updateWeblogWorkerRequest)
        WorkManager.getInstance(this).enqueue(updateGradesWorkerRequest)

        Utilities.fetchRepoReleaseInformation(this, ::repoReleaseCallback)
    }

    private fun repoReleaseCallback(version: String, log: String, url: String?){
        var version = version
        val url = url ?: getString(R.string.github_repository)
        val log = "Changelog $version: \n\n$log"

        if(version.substring(0,1) == "v"){
            version = version.substring(1)
        }

        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        val pVersion = pInfo.versionName

        if( version != pVersion){
            val builder = AlertDialog.Builder(this)

            builder.apply {
                setPositiveButton(getString(R.string.download)) { _, _ ->
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(url)
                        )
                    )
                }
                setNegativeButton(getString(R.string.cancel)){ dialog, _ ->
                    dialog.dismiss()
                }
            }
            builder.setMessage(log)
                .setTitle(getString(R.string.update_dialog))


            builder.create().show()

        }
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

    fun showGrades(item: MenuItem){
        startActivity(Intent(this, GradesActivity::class.java))
    }

    fun showReportIssue(item: MenuItem){
        startActivity(
                Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/naresh97/ei-weblog-android/issues/new")
                )
        )
    }
}