package com.kumaydevelop.rssreader.General

import android.content.Context
import android.support.v4.content.AsyncTaskLoader
import org.jsoup.Jsoup

data class Rss(val feedUrl: String)

// URLからRSSのURLを取得する
class getRssUrl(context: Context, url: String): AsyncTaskLoader<Rss>(context) {
    private var cache : Rss? = null
    val http = url
    override fun loadInBackground(): Rss? {
        val response = getHttp(http)
        if (response != null) {
            Jsoup.connect(http).get().run {
                val linkTag = select("link")
                val aaList = linkTag.stream().filter { x ->
                    x.toString().contains("application/rss+xml")
                }.map { x -> x.attr("href") }.findFirst()

                val rssLink = aaList.orElse("")

                if (!rssLink.isNullOrBlank()) {
                    return Rss(feedUrl = rssLink)
                }
            }
            return null
        }
        return null
    }

    override fun deliverResult(data: Rss?) {
        // 結果が破棄されていたり取得できない場合は返さない
        if (isReset || data == null) return

        cache = data
        super.deliverResult(data)
    }

    override fun onStartLoading() {
        // キャッシュがあれば、キャッシュを返す
        if (cache != null) {
            deliverResult(cache)
        }

        if (takeContentChanged() || cache == null) {
            forceLoad()
        }
    }

    // 実行中の処理をキャンセルする
    override fun onStopLoading() {
        cancelLoad()
    }

    override fun onReset() {
        super.onReset()
        onStopLoading()
        cache = null
    }
}