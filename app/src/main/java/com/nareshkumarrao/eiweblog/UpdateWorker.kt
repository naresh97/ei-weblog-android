package com.nareshkumarrao.eiweblog

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.math.BigInteger
import java.security.MessageDigest

class UpdateWorker(private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {

        val sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPref?.getBoolean(context.getString(R.string.enable_notifications_key), true)
        if(!notificationsEnabled!!){
            return Result.success();
        }

        Utilities.weblogList(context) { articles ->
            val lastArticle = Utilities.getLatestRelevantArticle(articles)!!
            val hashString = lastArticle.title + lastArticle.content + lastArticle.date
            val oldHash = md5(hashString)

            Utilities.fetchWeblogXML(applicationContext){newArticles ->
                val lastNewArticle = Utilities.getLatestRelevantArticle(newArticles)!!
                val newHashString = lastNewArticle.title + lastNewArticle.content + lastNewArticle.date
                val newHash = md5(newHashString)

                if(oldHash != newHash){
                    Utilities.sendNotification(context, lastNewArticle, newArticles.size)
                }
            }
        }

        return Result.success()
    }

    private fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

}