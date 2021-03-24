package com.nareshkumarrao.eiweblog

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar


class AboutActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val aboutToolbar = this.findViewById<Toolbar>(R.id.about_toolbar)
        setSupportActionBar(aboutToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        val version = pInfo.versionName
        findViewById<TextView>(R.id.app_version_view).text = "App Version: $version"
    }

    fun sendBeer(view: View) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://paypal.me/naresh97")
            )
        )
    }

    fun githubRepo(view: View){
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/naresh97/ei-weblog-android")
            )
        )
    }
}