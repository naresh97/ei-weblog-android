package com.nareshkumarrao.eiweblog

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.util.Xml
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.nareshkumarrao.eiweblog.ui.main.Article
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader

internal object Utilities {
    fun weblogXML(context: Context, function: (d: List<Article>) -> Unit) {

        val queue = Volley.newRequestQueue(context)

        val url = "https://www.google.com"

        val stringRequest = StringRequest(Request.Method.GET, url,
                { response ->
                    Log.d("XMLLIST", "got response!")
                    // Display the first 500 characters of the response string.
                    Log.d("XMLLIST", "$response")
                },
                { error -> Log.e("XMLLIST", error.toString()) })


        queue.add(stringRequest)
        Log.e("XMLLIST", "Adding request to queue from: $url")
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
                articles.add(parseZROW(parser))
            } else {
                parseSkip(parser)
            }
        }
        return articles
    }

    private fun parseZROW(parser: XmlPullParser): Article {
        parser.require(XmlPullParser.START_TAG, null, "z:row")
        val title = parser.getAttributeValue(null, "ows_Title")
        val content = parser.getAttributeValue(null, "ows_Body")
        val date = parser.getAttributeValue(null, "ows_Created")
        val author = parser.getAttributeValue(null, "ows_Autor2")
        parser.nextTag()
        parser.require(XmlPullParser.END_TAG, null, "link")

        return Article(title, content, date, author)
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
}
