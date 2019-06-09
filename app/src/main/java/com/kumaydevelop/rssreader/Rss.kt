package com.kumaydevelop.rssreader

import android.content.Context
import android.support.v4.content.AsyncTaskLoader
import org.jsoup.Jsoup
import org.w3c.dom.NodeList
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

data class Article(val title: String, val link: String, val date: Date)

data class Rss(val title: String, val date: Date, val articles: List<Article>, val rssUrl: String)

data class Feed(val feedUrl: String)

fun parseRss(stream: InputStream, rssUrl: String, displayCount: Int): Rss {
    val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream)
    stream.close()

    val xPath = XPathFactory.newInstance().newXPath()

    val formatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US)

    val items = xPath.evaluate("rss/channel//item", doc, XPathConstants.NODESET) as NodeList

    val articles = arrayListOf<Article>()

    // 記事一覧表示のときのみ取得処理を行う
    if (displayCount != 0) {
        for (i in 0 until displayCount) {
            if (i > items.length -1) {
                break
            }
            val item = items.item(i)
            val article =  Article(
                    title = xPath.evaluate("./title/text()", item),
                    link = xPath.evaluate("./link/text()", item),
                    date = formatter.parse(xPath.evaluate("./pubDate/text()", item)))

            articles.add(article)
        }
    }

    return Rss(title = xPath.evaluate("/rss/channel/title/text()", doc),
            date = formatter.parse(xPath.evaluate("/rss/channel/lastBuildDate/text()", doc)),
            articles = articles,
            rssUrl = rssUrl)
}

// URLからRSSのURLを取得する
class getRssUrl(context: Context, url: String): AsyncTaskLoader<Feed>(context) {
    private var cache : Feed? = null
    val http = url
    override fun loadInBackground(): Feed? {
        val response = getHttp(http)
        if (response != null) {
            Jsoup.connect(http).get().run {
                val linkTag = select("link")
                val aaList = linkTag.stream().filter { x ->
                    x.toString().contains("application/rss+xml") && x.toString().contains("RSS2.0")
                }.map { x -> x.attr("href") }.findFirst()

                val aa = aaList.orElse("")

                if (!aa.isNullOrBlank()) {
                    return Feed(feedUrl = aa)
                }
            }
            return null
        }
        return null
    }

    override fun deliverResult(data: Feed?) {
        if (isReset || data == null) return

        cache = data
        super.deliverResult(data)
    }

    override fun onStartLoading() {
        if (cache != null) {
            deliverResult(cache)
        }

        if (takeContentChanged() || cache == null) {
            forceLoad()
        }
    }

    override fun onStopLoading() {
        cancelLoad()
    }

    override fun onReset() {
        super.onReset()
        onStopLoading()
        cache = null
    }
}

class RssLoader(context: Context, url: String, displayCount: Int): AsyncTaskLoader<Rss>(context) {
    private var cache : Rss? = null
    var rssUrl = url
    val displayCount = displayCount

    override fun loadInBackground(): Rss? {
        if (rssUrl == null) {
            return null
        }

        val response = getHttp(rssUrl)

        if (response != null) {
            return parseRss(response, rssUrl, displayCount)
        }

        return null
    }

    override fun deliverResult(data: Rss?) {
        if (isReset || data == null) return

        cache = data
        super.deliverResult(data)
    }

    override fun onStartLoading() {
        if (cache != null) {
            deliverResult(cache)
        }

        if (takeContentChanged() || cache == null) {
            forceLoad()
        }
    }

    override fun onStopLoading() {
        cancelLoad()
    }

    override fun onReset() {
        super.onReset()
        onStopLoading()
        cache = null
    }
}