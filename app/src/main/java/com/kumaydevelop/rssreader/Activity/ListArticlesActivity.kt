package com.kumaydevelop.rssreader.Activity

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.kumaydevelop.rssreader.Adapter.ArticlesAdapter
import com.kumaydevelop.rssreader.Dialog.AlertDialog
import com.kumaydevelop.rssreader.Entity.BlogDetailEntity
import com.kumaydevelop.rssreader.General.Util
import com.kumaydevelop.rssreader.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_article_list.*

class ListArticlesActivity : AppCompatActivity() {

    private var compositeDisposable : CompositeDisposable? = null

    private var mAdapter: ArticlesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_list)
        compositeDisposable = CompositeDisposable()
        val rssUrl = intent.getStringExtra("RSSURL_KEY")
        val displayCount = intent.getIntExtra("DISPLAYCOUNT", 50)

        articlRecyclerView.layoutManager = LinearLayoutManager(this)

        val splitedUrl = Util.splitUrl(rssUrl)
        val response = Util.createRetrofit(splitedUrl)

        // 非同期で記事を取得し、一覧表示する
        compositeDisposable?.add(response.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    // 記事を表示件数で設定した分取得する
                    val articles = arrayListOf<BlogDetailEntity>()
                    val blog = it.articleEntities
                    if (blog!!.size != 0) {
                        for (i in 0 until displayCount) {
                            if (i > blog!!.size -1) {
                                break
                            }
                            articles.add(blog[i])
                        }

                        mAdapter = ArticlesAdapter(this, articles) {
                            val intent = CustomTabsIntent.Builder().build()
                            intent.launchUrl(this@ListArticlesActivity, Uri.parse(it.link))
                        }

                        articlRecyclerView.adapter = mAdapter

                        // 区切り線を入れる
                        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
                        articlRecyclerView.addItemDecoration(itemDecoration)
                        progressBarBlogDetail.visibility = android.widget.ProgressBar.INVISIBLE
                    }

                }, {
                    var dialog = AlertDialog()
                    dialog.title = "記事を取得できませんでした。"
                    dialog.onOkClickListener = DialogInterface.OnClickListener { dialog, which ->
                        finish()
                    }
                    dialog.show(supportFragmentManager, null)
                    Log.e("ERROR", it.cause.toString())
                }))

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable?.clear()
    }
}