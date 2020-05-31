package com.kumaydevelop.blogreader.General

import org.jsoup.Jsoup

data class RssData(val feedUrl: String)


object Rss {
    fun getRssUrl(url: String): RssData? {
        val response = getHttp(url)
        if (response != null) {
            // タグからRSSURLを取得する
            Jsoup.connect(url).get().run {
                val linkTag = select("link")
                // application/rss+xml属性があるlinkタグのhrefを取得
                val rssList = linkTag.stream().filter { x ->
                    x.toString().contains("application/rss+xml")
                }.map { x -> x.attr("href") }.findFirst()

                val rssLink = rssList.orElse("")

                if (!rssLink.isNullOrBlank()) {
                    return RssData(feedUrl = rssLink)
                }
            }

            return null
        }
        return null
    }
}