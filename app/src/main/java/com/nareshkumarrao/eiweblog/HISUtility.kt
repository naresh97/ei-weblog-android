package com.nareshkumarrao.eiweblog

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.net.SocketTimeoutException

data class ExamRow(val name: String, val grade: String, val attempt: String, val date: String)

internal object HISUtility {

    fun setUsernamePassword(context: Context?, username: String, password: String) {
        val sharedPref = context?.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        if (sharedPref != null) {
            with(sharedPref.edit()) {
                putString(context.getString(R.string.username_key), username)
                putString(context.getString(R.string.password_key), password)
                apply()
            }
        }
    }

    fun checkForUpdates(context: Context?, callback: (examRows: List<ExamRow>?) -> Unit) {
        val savedRows = getSavedExamRows(context) ?: run {
            callback(null)
            return
        }

        val newRows: MutableList<ExamRow> = mutableListOf()
        fetchExamRows(context) { examRows ->
            if (examRows != null) {
                for (examRow in examRows) {
                    if (!savedRows.contains(examRow)) {
                        newRows.add(examRow)
                    }
                }
            }
            callback(newRows)
        }


    }

    fun getSavedExamRows(context: Context?): List<ExamRow>? {
        val sharedPref = context?.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val examRowsJson = sharedPref?.getString(context.getString(R.string.exam_rows_key), null)
                ?: return null
        val examRowsType = object : TypeToken<List<ExamRow>>() {}.type
        return Gson().fromJson(examRowsJson, examRowsType)
    }

    private fun saveExamRows(context: Context?, examRows: List<ExamRow>) {
        val examRowsJson = Gson().toJson(examRows)
        val sharedPref = context?.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        if (sharedPref != null) {
            with(sharedPref.edit()) {
                putString(context.getString(R.string.exam_rows_key), examRowsJson)
                apply()
            }
        }
    }

    @Throws(LoginFailedException::class)
    fun fetchExamRows(context: Context?, callback: (examRows: List<ExamRow>?) -> Unit): Unit? {
        val sharedPref = context?.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val username = sharedPref?.getString(context.getString(R.string.username_key), null)
                ?: return null
        val password = sharedPref.getString(context.getString(R.string.password_key), null)
                ?: return null

        val runnable = Runnable {

            try {
                val postData: MutableMap<String, String> = mutableMapOf()
                postData["asdf"] = username
                postData["fdsa"] = password

                val loginPage = Jsoup.connect(context.getString(R.string.ossc_login_post))
                    .method(Connection.Method.POST)
                    .userAgent("Mozilla")
                    .data(postData)
                    .timeout(60000)
                    .execute()

                val selectNotenspiegel =
                    Jsoup.connect(context.getString(R.string.ossc_select_noten))
                        .userAgent("Mozilla")
                        .cookies(loginPage.cookies())
                        .timeout(60000)
                        .get()

                val notenspiegelURL =
                    selectNotenspiegel.select("a[href]:containsOwn(Notenspiegel)").first()
                        ?.attr("href")
                        ?: kotlin.run {
                            callback(null)
                            return@Runnable
                        }

                val selectStudiengangUnhide = Jsoup.connect(notenspiegelURL)
                    .userAgent("Mozilla")
                    .cookies(loginPage.cookies())
                    .timeout(60000)
                    .get()
                val selectStudiengangUnhideURL =
                    selectStudiengangUnhide.select("a[href]:containsOwn(Abschluss)").first()
                        .attr("href")

                val selectStudiengang = Jsoup.connect(selectStudiengangUnhideURL)
                    .userAgent("Mozilla")
                    .cookies(loginPage.cookies())
                    .timeout(60000)
                    .get()
                val studiengangURL =
                    selectStudiengang.select("a[href]:containsOwn(Leistungen anzeigen)").first()
                        .attr("href")


                val notenSpiegelPage = Jsoup.connect(studiengangURL)
                    .userAgent("Mozilla")
                    .cookies(loginPage.cookies())
                    .timeout(60000)
                    .get()

                val allGradesRows =
                    notenSpiegelPage.select("div.fixedContainer > table > tbody > tr")
                val examRows: MutableList<ExamRow> = mutableListOf()
                for (row in allGradesRows) {
                    if (row.select("td.tabelle1_alignleft").size < 1) {
                        continue
                    }
                    val columns = row.select("td")
                    if (columns.size < 1) {
                        continue
                    }
                    val examRow = ExamRow(
                        columns[1].text(),
                        columns[3].text(),
                        columns[6].text(),
                        columns[7].text()
                    )
                    examRows.add(examRow)
                }
                saveExamRows(context, examRows)
                callback(examRows)
            } catch (e: Exception) {
                if (context is Activity) context.runOnUiThread {
                    when (e) {
                        is SocketTimeoutException -> {
                            Toast.makeText(
                                context,
                                context.getString(R.string.ossc_timeout_message),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> {
                            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                    context.finish()
                }
            }
        }

        return Thread(runnable).start()
    }

    fun sendNotification(context: Context?, examRow: ExamRow, id: Int) {
        val intent = Intent(context, NotificationSettingsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(
            context!!,
            context.getString(R.string.grades_notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(context.getString(R.string.exam_results_notification))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${examRow.name}: ${examRow.grade}")
            )
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
        with(NotificationManagerCompat.from(context)) {
            notify(id, builder.build())
        }

    }

    fun createNotificationChannel(context: Context?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context?.getString(R.string.grades_notification_channel_name)
            val descriptionText =
                context?.getString(R.string.grades_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                context?.getString(R.string.grades_notification_channel_id),
                name,
                importance
            ).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}


class LoginFailedException : Throwable()
