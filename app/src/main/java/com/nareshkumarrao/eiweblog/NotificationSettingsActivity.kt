package com.nareshkumarrao.eiweblog

import android.content.Context
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar


class NotificationSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)

        val notificationsToolbar = this.findViewById<Toolbar>(R.id.notification_toolbar)
        setSupportActionBar(notificationsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val weblogResponse = sharedPref?.getBoolean(getString(R.string.enable_notifications_key), true)

        val notificationSwitch = findViewById<SwitchCompat>(R.id.notification_switch)
        notificationSwitch.isChecked = weblogResponse!!
        notificationSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            with (sharedPref.edit()) {
                putBoolean(getString(R.string.enable_notifications_key), isChecked)
                apply()
            }
            //Toast.makeText(this, "Notifications are set to $isChecked", Toast.LENGTH_SHORT).show()
        })


    }

}