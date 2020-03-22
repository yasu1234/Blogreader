package com.kumaydevelop.blogreader.General

import android.content.Context
import androidx.loader.content.AsyncTaskLoader
import org.jsoup.Jsoup

data class Rss(val feedUrl: String)

// URLからRSSのURLを取得する
class getRssUrl(context: Context, url: String): AsyncTaskLoader<Rss>(context) {
    private var cache : Rss? = null
    val http = url
    override fun loadInBackground(): Rss? {
        val response = getHttp(http)
        if (response != null) {
            // タグからRSSURLを取得する
            Jsoup.connect(http).get().run {
                val linkTag = select("link")
                // application/rss+xml属性があるlinkタグのhrefを取得
                val rssList = linkTag.stream().filter { x ->
                    x.toString().contains("application/rss+xml")
                }.map { x -> x.attr("href") }.findFirst()

                val rssLink = rssList.orElse("")

                if (!rssLink.isNullOrBlank()) {
                    return Rss(feedUrl = rssLink)
                }
            }

            return null
        }
        return null
    }

    override fun deliverResult(data: Rss?) {
        if (isReset) return
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