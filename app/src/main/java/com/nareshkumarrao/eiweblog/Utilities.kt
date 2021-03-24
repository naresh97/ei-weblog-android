package com.nareshkumarrao.eiweblog

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.util.Xml
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.text.HtmlCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.nareshkumarrao.eiweblog.ui.main.Article
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader


internal object Utilities {

    fun weblogList(context: Context?, function: (d: List<Article>) -> Unit){
        val sharedPref = context?.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val weblogResponse = sharedPref?.getString(context.getString(R.string.weblog_response_key), null)
        if (weblogResponse == null){
            fetchWeblogXML(context, function)
            return
        }

        val parser: XmlPullParser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(  StringReader(weblogResponse) )
        parser.nextTag()
        function(parseXML(parser))
    }

    fun fetchWeblogXML(context: Context?, function: (d: List<Article>) -> Unit) {

        val queue = Volley.newRequestQueue(context)

        val url = context?.getString(R.string.weblog_xml_url)

        val stringRequest = StringRequest(Request.Method.GET, url,
                { response ->
                    val responseStr = String(response.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)

                    val sharedPref = context?.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                    if (sharedPref != null) {
                        with (sharedPref.edit()) {
                            putString(context.getString(R.string.weblog_response_key), responseStr)
                            apply()
                        }
                    }



                    val parser: XmlPullParser = Xml.newPullParser()
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                    //Log.d("XMLLIST", responseStr )
                    parser.setInput(  StringReader(responseStr) )
                    parser.nextTag()

                    val articles = parseXML(parser)
                    function(articles)

                },
                { error -> Log.e("XMLLIST", error.toString()) })

        queue.add(stringRequest)
    }

    private fun parseXML(parser: XmlPullParser): List<Article> {
        parser.require(XmlPullParser.START_TAG, null, "xml")
        var articles: List<Article> = listOf()
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == "rs:data") {
                articles = parseRSDATA(parser)
            } else {
                parseSkip(parser)
            }
        }
        return articles
    }

    private fun parseRSDATA(parser: XmlPullParser): List<Article> {
        val articles = mutableListOf<Article>()
        parser.require(XmlPullParser.START_TAG, null, "rs:data")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == "z:row") {
                parseZROW(parser)?.let { articles.add(it) }
            } else {
                parseSkip(parser)
            }
        }
        return articles
    }

    private fun parseZROW(parser: XmlPullParser): Article? {
        parser.require(XmlPullParser.START_TAG, null, "z:row")
        val title = parser.getAttributeValue(null, "ows_Title")
        val content = parser.getAttributeValue(null, "ows_Body")
        val date = parser.getAttributeValue(null, "ows_Created")
        val author = parser.getAttributeValue(null, "ows_Autor2")
        val category = parser.getAttributeValue(null, "ows_Kategorie")
        parser.nextTag()
        parser.require(XmlPullParser.END_TAG, null, "z:row")

        if(title == null || content == null || date == null || author == null || category == null){
            return null
        }

        return Article(title, content, date, author, category)
    }

    private fun parseSkip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    fun sendNotification(context: Context?, article: Article, id:Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context!!, context.getString(R.string.channel_id))
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(article.title)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(HtmlCompat.fromHtml(article.content, HtmlCompat.FROM_HTML_MODE_COMPACT)))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(id, builder.build())
        }

    }

    fun createNotificationChannel(context: Context?){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context?.getString(R.string.channel_name)
            val descriptionText = context?.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(context?.getString(R.string.channel_id), name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getLatestRelevantArticle(articles: List<Article>): Article? {
        val sortedArticles = articles.sortedByDescending { it.date }
        for (article in sortedArticles){
            if( article.category == "Lehre" || article.category == "Prüfung" || article.category == "Sonstiges"){
                return article
            }
        }
        return null
    }

    fun fetchRepoReleaseInformation(context: Context?, callback: (v: String, log:String, url:String?) -> Unit){
        val queue = Volley.newRequestQueue(context)
        val url = context?.getString(R.string.github_api_releases)
        val jsonObjectRequest = JsonArrayRequest(Request.Method.GET, url, null,
                { response ->
                    val latestRelease = response.getJSONObject(0)
                    val releaseVersion = latestRelease.getString("tag_name")
                    val releaseLog = latestRelease.getString("body")
                    val releaseAssets = latestRelease.getJSONArray("assets")
                    var releaseAPK: String? = null
                    for(i in 0 until releaseAssets.length()){
                        val asset = releaseAssets.getJSONObject(0)
                        val contentType = asset.getString("content_type")
                        if( contentType == "application/vnd.android.package-archive" ){
                            releaseAPK = asset.getString("browser_download_url")
                            break
                        }
                    }
                    Log.d("RESTAPI", "v: $releaseVersion, log: $releaseLog, apk: $releaseAPK")
                    callback(releaseVersion, releaseLog, releaseAPK)
                },
                {  error -> Log.e("RESTAPI", error.toString())
                }
        )
        queue.add(jsonObjectRequest)
    }
}
