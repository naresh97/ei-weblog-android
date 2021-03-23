package com.nareshkumarrao.eiweblog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar


class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val aboutToolbar = this.findViewById<Toolbar>(R.id.about_toolbar)
        setSupportActionBar(aboutToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    fun sendBeer(view: View) {
        startActivity( Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://paypal.me/naresh97")
        ))
    }

    fun githubRepo(view: View){
        startActivity( Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/naresh97/ei-weblog-android")
        ))
    }
}