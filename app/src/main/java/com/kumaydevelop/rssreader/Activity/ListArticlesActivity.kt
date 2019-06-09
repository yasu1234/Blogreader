package com.kumaydevelop.rssreader.Activity

import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.kumaydevelop.rssreader.Adapter.ArticlesAdapter
import com.kumaydevelop.rssreader.R
import com.kumaydevelop.rssreader.Rss
import com.kumaydevelop.rssreader.RssLoader
import kotlinx.android.synthetic.main.activity_article_list.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton

class ListArticlesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_list)
        val rssUrl = intent.getStringExtra("RSSURL_KEY")
        val displayCount = intent.getIntExtra("DISPLAYCOUNT", 50)
        val args: Bundle = Bundle().also {
            it.putString("url", rssUrl)
            it.putInt("displayCount", displayCount)
        }
        supportLoaderManager.initLoader(0, args, articleListCallback)

    }

    private val articleListCallback : LoaderManager.LoaderCallbacks<Rss> = object : LoaderManager.LoaderCallbacks<Rss> {

        override fun onLoaderReset(loader: Loader<Rss>?) {
        }

        override fun onLoadFinished(loader: Loader<Rss>?, data: Rss?) {
            if (data?.articles == null) {
                alert("記事が取得できません") {
                    yesButton {}
                }.show()
            } else {
                articlRecyclerView.layoutManager = LinearLayoutManager(this@ListArticlesActivity) as RecyclerView.LayoutManager?
                articlRecyclerView.adapter = ArticlesAdapter(this@ListArticlesActivity, data.articles) {
                    val intent = CustomTabsIntent.Builder().build()
                    intent.launchUrl(this@ListArticlesActivity, Uri.parse(it.link))
                }
                // 区切り線を入れる
                val itemDecoration = DividerItemDecoration(this@ListArticlesActivity, DividerItemDecoration.VERTICAL)
                articlRecyclerView.addItemDecoration(itemDecoration)
            }
        }

        override fun onCreateLoader(id: Int, args: Bundle?)
                = RssLoader(this@ListArticlesActivity, args!!.getString("url"), args!!.getInt("displayCount"))

    }
}