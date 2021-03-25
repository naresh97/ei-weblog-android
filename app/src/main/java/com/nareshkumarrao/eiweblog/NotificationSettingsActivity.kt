package com.nareshkumarrao.eiweblog

import android.content.Context
import android.os.Bundle
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

        val weblogResponse = sharedPref?.getBoolean(getString(R.string.enable_weblog_notifications_key), true)
        val weblogNotificationSwitch = findViewById<SwitchCompat>(R.id.weblog_notification_switch)
        weblogNotificationSwitch.isChecked = weblogResponse!!
        weblogNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean(getString(R.string.enable_weblog_notifications_key), isChecked)
                apply()
            }
        }

        val gradeResponse = sharedPref.getBoolean(getString(R.string.enable_grades_notifications_key), true)
        val gradeNotificationSwitch = findViewById<SwitchCompat>(R.id.grades_notification_switch)
        gradeNotificationSwitch.isChecked = gradeResponse
        gradeNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean(getString(R.string.enable_grades_notifications_key), isChecked)
                apply()
            }
        }


    }

}