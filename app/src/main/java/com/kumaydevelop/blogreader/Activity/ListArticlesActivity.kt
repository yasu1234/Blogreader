package com.kumaydevelop.blogreader.Activity

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kumaydevelop.blogreader.Adapter.ArticlesAdapter
import com.kumaydevelop.blogreader.Dialog.AlertDialog
import com.kumaydevelop.blogreader.Entity.BlogDetailEntity
import com.kumaydevelop.blogreader.General.Util
import com.kumaydevelop.blogreader.R
import kotlinx.android.synthetic.main.activity_article_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ListArticlesActivity : AppCompatActivity() {

    private var mAdapter: ArticlesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_list)
        val rssUrl = intent.getStringExtra("RSSURL_KEY")
        val displayCount = intent.getIntExtra("DISPLAYCOUNT", 50)

        articlRecyclerView.layoutManager = LinearLayoutManager(this)

        GlobalScope.launch(Dispatchers.Main) {
            async(Dispatchers.Default) {Util.getRssData(rssUrl)}.await().let {
                val articles = arrayListOf<BlogDetailEntity>()
                if (it != null) {
                    val blog = it.articleEntities
                    if (blog!!.size != 0) {
                        for (i in 0 until displayCount) {
                            if (i > blog.size - 1) {
                                break
                            }
                            articles.add(blog[i])
                        }

                        mAdapter = ArticlesAdapter(this@ListArticlesActivity, articles) {
                            val intent = CustomTabsIntent.Builder().build()
                            intent.launchUrl(this@ListArticlesActivity, Uri.parse(it.link))
                        }

                        articlRecyclerView.adapter = mAdapter

                        // 区切り線を入れる
                        val itemDecoration = DividerItemDecoration(this@ListArticlesActivity, DividerItemDecoration.VERTICAL)
                        articlRecyclerView.addItemDecoration(itemDecoration)
                        progressBarBlogDetail.visibility = android.widget.ProgressBar.INVISIBLE
                    }
                } else {
                    val dialog = AlertDialog()
                    dialog.title = "記事を取得できませんでした。"
                    dialog.onOkClickListener = DialogInterface.OnClickListener { dialog, which ->
                        finish()
                    }
                    dialog.show(supportFragmentManager, null)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}